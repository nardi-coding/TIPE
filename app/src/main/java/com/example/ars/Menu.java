package com.example.ars;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.rng.Random;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class Menu extends Fragment {
    TextView results;
    UnityPlayer mUnityPlayer;
    BackgroundTraining bgTraining;
    boolean stop = false;

    public static final float SCALE = 1.0f;

    public Menu() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Console cons = (Console) getActivity().getSupportFragmentManager().findFragmentById(R.id.console);
        Terminal terminal = cons.terminal;

        Button train = view.findViewById(R.id.start_training);
        Button perturbation = view.findViewById(R.id.perturbation);
        Button load = view.findViewById(R.id.open);

        Utils utils = new Utils(getActivity());


        train.setOnClickListener(v -> {

            if(!stop){
                stop = true;
                train.setText("Stop");
                results = terminal.getActivity().findViewById(R.id.training_info);

                ExecutorService executor = Executors.newSingleThreadExecutor();


                Random r = Nd4j.getRandom();
                r.setSeed(3);


                Hp hp = new Hp(utils);

                UnityPlayer.UnitySendMessage("Lander", "setGravity", String.valueOf(-1.0 * hp.gravity).replace(".", ","));

                UnityPlayer.UnitySendMessage("Lander", "setTimeScale", String.valueOf(SCALE).replace(".", ","));


                Policy brain = new Policy(8, 4, hp);
                Normalizer normalizer = new Normalizer(8);

                bgTraining = new BackgroundTraining(utils, brain, normalizer, hp, executor);

                bgTraining.train(result -> {
                    getActivity().runOnUiThread(() -> {
                        results.append("\n" + result);
                        ScrollView scrollView = terminal.getActivity().findViewById(R.id.scroll);
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    });
                });

            }else{
                stop = false;
                bgTraining.stop();
                train.setText("Train");

                Toast.makeText(getContext(), "Last step is finishing, please wait.", Toast.LENGTH_LONG).show();
            }


        });

        perturbation.setOnClickListener(v ->{
            UnityPlayer.UnitySendMessage("Lander", "appliquerForceX", "-80");
        });
        load.setVisibility(View.INVISIBLE);
    }
}