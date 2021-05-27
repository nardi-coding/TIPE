package com.example.ars;

import com.unity3d.player.UnityPlayer;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class RemoteControlWithTrainedModels extends LanderRemoteControl{

    public RemoteControlWithTrainedModels(Utils utils, Policy brain, Normalizer normalizer, Hp hp, ExecutorService executor){
        super(utils, brain, normalizer, hp, executor);
    }

    public void start(){
        UnityPlayer.UnitySendMessage("Lander", "setRandomPosition", "");
        executor.execute(() -> explore("null", null));
    }
}
