package com.example.ars;

import android.util.Log;

import com.unity3d.player.UnityPlayer;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.indexaccum.IAMax;
import org.nd4j.linalg.factory.Nd4j;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.lang.Math;

public class LanderRemoteControl extends Environnement{
    public final ExecutorService executor;
    public final Hp hp;
    public final Policy brain;
    public final Normalizer normalizer;
    public double prevShaping;
    public final double SCALE = 1.0;
    private final int POSX = 0;
    private final int POSY = 1;
    private final int VITX = 2;
    private final int VITY = 3;
    private final int ANGLE = 4;
    private final int ANGULAR_VELOCITY = 5;
    private final int ROUE_GAUCHE = 6;
    private final int ROUE_DROITE = 7;

    double [][] d = {{-0.0535,    0.1348,    0.1338,    0.0094,   -0.3563,    0.4841,    0.2330,    0.0334},
                     {0.2950,    0.6773,    0.2343,    0.2736,   -0.3095,    0.3376,    0.7834,    0.2507},
                     {0.4789,   -0.3162,    0.2109,    0.8221,    0.2770,   -0.9420,   -0.0494,    0.2097},
                     {-0.1046,   -0.0651,   -0.1461,    0.1963,   -0.0989,    0.1930,   -0.4679,   -0.2442}};



    public LanderRemoteControl(Utils utils, Policy brain, Normalizer normalizer, Hp hp, ExecutorService executor){
        super(utils);

        this.executor = executor;
        this.hp = hp;
        this.normalizer = normalizer;
        this.brain = brain;
    }


    public void setPrevShaping(INDArray state){
        double posX = state.getDouble(POSX)/SCALE;
        double posY = state.getDouble(POSY)/SCALE;
        double vitX = state.getDouble(VITX)/SCALE;
        double vitY = state.getDouble(VITY)/SCALE;
        double angle = state.getDouble(ANGLE)/SCALE;
        double angularVelocity = state.getDouble(ANGULAR_VELOCITY)/SCALE;
        double laRoueGaucheToucheLeSol = state.getDouble(ROUE_GAUCHE)/SCALE;
        double laRoueDroiteToucheLeSol = state.getDouble(ROUE_DROITE)/SCALE;

        double normePosition = Math.sqrt((posX - landingFieldX)*(posX - landingFieldX) + (posY - landingFieldY )*(posY - landingFieldY ));
        double normeVitesse = Math.sqrt(vitX * vitX + vitY*vitY);


        this.prevShaping = - 25*(normePosition)
                - 5 * normeVitesse
                - 100 * Math.abs(angle) + 10 * laRoueGaucheToucheLeSol + 10 *laRoueDroiteToucheLeSol;

    }



    public double explore(String direction, INDArray delta){
        double totalReward = 0;
        INDArray state = Nd4j.create(utils.getState());
        boolean done = false;
        setPrevShaping(state);

        while(!done){
            normalizer.observe(state);
            state = normalizer.normalize(state);
            INDArray predictionVector = brain.evaluate(state, delta, direction);
            //Log.i("ARS::", "Decision Vector: " + predictionVector.toString());
            int action =  Nd4j.getExecutioner().execAndReturn(new IAMax(predictionVector)).getFinalResult().intValue();
            state = applyAction(action);
            done = hasGameFinished(state); //Check if simulation has finished
            totalReward += getReward(state, done);


        }

        Log.i("TotalReward", String.valueOf(totalReward));
        return totalReward;
    }


    public double getReward(INDArray state, boolean done){
        double posX = state.getDouble(POSX)/SCALE;
        double posY = state.getDouble(POSY)/SCALE;
        double vitX = state.getDouble(VITX)/SCALE;
        double vitY = state.getDouble(VITY)/SCALE;
        double angle = state.getDouble(ANGLE)/SCALE;
        double angularVelocity = state.getDouble(ANGULAR_VELOCITY)/SCALE;
        double laRoueGaucheToucheLeSol = state.getDouble(ROUE_GAUCHE)/SCALE;
        double laRoueDroiteToucheLeSol = state.getDouble(ROUE_DROITE)/SCALE;

        double normePosition = Math.sqrt((posX - landingFieldX)*(posX - landingFieldX) + (posY - landingFieldY )*(posY - landingFieldY ));
        double normeVitesse = Math.sqrt(vitX * vitX + vitY*vitY);


        double shaping = - 20* (normePosition)
                - 20* normeVitesse
                - 100 * Math.abs(angle) + 10 * laRoueGaucheToucheLeSol + 10 *laRoueDroiteToucheLeSol;

        double reward;

        reward = shaping - this.prevShaping;

        if(Math.abs(posX - landingFieldX) >= 1){
            reward = reward - 30;
        }

        this.prevShaping = shaping;


        if(done) {
            if (hasLandedProperlyInsideLandingField(state)) {
                reward += 200;
            }
        }


        return reward;
    }


    public INDArray applyAction(int action){
        switch(action){
            case Move.UP:
                UnityPlayer.UnitySendMessage("Lander", "appliquerForceY", "400");
                break;
            case Move.LEFT:
                UnityPlayer.UnitySendMessage("Lander", "appliquerForceX", "-80");
                break;
            case Move.RIGHT:
                UnityPlayer.UnitySendMessage("Lander", "appliquerForceX", "80");
                break;
            case Move.NONE:
                break;
        }

        return Nd4j.create(utils.getState());
    }


    public boolean hasGameFinished(INDArray state){
        return  hasLanded(state)  ||
                (state.getDouble(0) > (envWidth + 10) || state.getDouble(0) < -(envWidth+10)) ||
                (state.getDouble(1) > envHeight + 10);
    }


    public boolean hasLandedProperly(INDArray state){
        return (int) state.getDouble(6) == 1 && (int) state.getDouble(7) == 1;
    }

    public boolean hasLanded(INDArray state){
        return (int) state.getDouble(6) == 1 || (int) state.getDouble(7) == 1;
    }


    public boolean hasLandedProperlyInsideLandingField(INDArray state){
        return hasLandedProperly(state) && (Math.abs(state.getDouble(0) - landingFieldX) <= 1);
    }
}
