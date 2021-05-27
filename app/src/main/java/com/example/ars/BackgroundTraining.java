package com.example.ars;

import android.os.Environment;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.indexaccum.IAMax;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class BackgroundTraining extends LanderRemoteControl{

    public boolean stop = false;

    public BackgroundTraining(Utils utils, Policy brain, Normalizer normalizer, Hp hp, ExecutorService executor){
        super(utils, brain, normalizer, hp, executor);
    }


    public void train(final TrainingCallback callback){
        this.executor.execute(() -> trainingLoop(callback));
        executor.shutdown();
    }





    private void trainingLoop(TrainingCallback callback){
        double reward;
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/AugmentedRandomSearch");

        boolean var = false;
        if (!folder.exists()) {
            var = folder.mkdir();
        }

        Date currentTime = Calendar.getInstance().getTime();
        final String filename = folder.toString() + "/" + currentTime.toString() + ".csv";

        Log.i("Saving data to: ", filename);

        SaveToCSV.saveToCSV(hp.steps + "," + hp.learningRate + "," + hp.directions + "," + hp.bestDirections + "," + hp.noise + "," + hp.gravity, filename);
        for(int step = 1; step <= hp.steps; step++){
            ArrayList<INDArray> deltas = brain.sampleDeltas();
            double[] rewards = new double[2 * hp.directions];
            HashMap<Integer, Double> maximums = new HashMap<>();

            UnityPlayer.UnitySendMessage("Lander", "setRandomPosition", "");

            for(int k = 0; k < hp.directions; k++){
                if (!stop) {
                    //Log.i("Positive", brain.theta.add(deltas.get(k).mul(hp.noise)).toString());
                    double pRes = explore("positive", deltas.get(k));
                    UnityPlayer.UnitySendMessage("Lander", "setLanderAtLastPosition", "");
                    //Log.i("Negative", brain.theta.sub(deltas.get(k).mul(hp.noise)).toString());
                    double nRes = explore("negative", deltas.get(k));
                    UnityPlayer.UnitySendMessage("Lander", "setLanderAtLastPosition", "");
                    rewards[k] = pRes;
                    rewards[hp.directions + k] = nRes;
                    maximums.put(k, Math.max(pRes, nRes));
                }else{
                    break;
                }

            }

            if(stop){
                break;
            }


            INDArray array = Nd4j.create(rewards);
            double std = array.stdNumber(false).doubleValue();

            //Sort HashMap to get rollouts
            double[][] rollouts = new double[hp.bestDirections][];
            INDArray[] d = new INDArray[hp.bestDirections];

            List<Double> maxList =  new ArrayList<>(maximums.values());
            Collections.sort(maxList, Collections.reverseOrder());

            Log.i("maxList", maxList.toString());

            for(int i = 0; i < hp.bestDirections; i++) {
                double r = maxList.get(i);
                for(int j = 0; j < hp.directions; j++) {
                    if (maximums.containsKey(j) && r == maximums.get(j)) {
                        double[] l = {rewards[j], rewards[hp.directions + j]};
                        rollouts[i] = l;
                        d[i] = deltas.get(j);
                        break;
                    }
                }
            }

            Log.i("brain", brain.theta.toString());

            brain.update(rollouts, d, std);

            Log.i("Maximums", String.valueOf(std));


            reward = explore("null", null);

            SaveToCSV.saveToCSV(step + "," + reward, filename);

            callback.onComplete("Step: " + step + ", Reward: " + reward);



        }
    }


    public void stop(){
        stop = true;
    }


}
