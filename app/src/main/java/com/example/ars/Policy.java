package com.example.ars;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;

public class Policy {
    INDArray theta;
    Hp hp;
    public Policy(int inputSize, int outputSize, Hp hp){
        this.theta = Nd4j.zeros(DataType.DOUBLE, outputSize, inputSize);
        this.hp = hp;
    }

    public INDArray evaluate(INDArray input, INDArray delta, String direction){
        if (direction.equals("null")){
            return this.theta.mmul(input);
        }else if(direction.equals("positive")){
            //Log.i("Prediction: ", "Vector: " + input.toString())
            return (this.theta.add(delta.mul(hp.noise))).mmul(input);
        }else{
            return (this.theta.sub(delta.mul(hp.noise))).mmul(input);
        }
    }

    public ArrayList<INDArray> sampleDeltas(){
        ArrayList<INDArray> array = new ArrayList<>();
        for(int i = 1; i <= hp.directions; i ++){
            array.add(Nd4j.randn(DataType.DOUBLE, this.theta.shape()));
        }
        return array;
    }


    public void update(double[][] rollouts, INDArray[] deltas, double sigmaR){
        INDArray step = Nd4j.zeros(DataType.DOUBLE, this.theta.shape());
        /*
        int i = 0;
        for(double[] tuple:rollouts){
            if(tuple != null) {
                double rPos = tuple[0];
                double rNeg = tuple[1];
                INDArray d = deltas[i];


                step = step.add(d.mul(rPos - rNeg));

                i++;
            }
        }

        */

        for(int i = 0; i < hp.bestDirections; i++){
            double rPos = rollouts[i][0];
            double rNeg = rollouts[i][1];
            INDArray d = deltas[i];
            step = step.add(d.mul(rPos - rNeg));
        }



        this.theta = this.theta.add(step.mul(hp.learningRate/(hp.bestDirections * sigmaR))); //Changer sigmaR car ca peut etre nul (quand toutes les valeurs rpos et rneg sont egales)
    }
}
