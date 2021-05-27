package com.example.ars;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import com.unity3d.player.UnityPlayer;


public class Console extends Fragment {

    public Terminal terminal;
    public Settings settings;

    public Console() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.console,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        Button terminal = view.findViewById(R.id.term);
        Button settings = view.findViewById(R.id.sett);
        settings.setTextColor(getResources().getColor(R.color.black));
        terminal.setTextColor(getResources().getColor(R.color.white));


        View term = view.findViewById(R.id.terminal);
        View sett = view.findViewById(R.id.settings);
        sett.setVisibility(View.GONE);
        term.setVisibility(View.VISIBLE);

        terminal.setOnClickListener(v -> {
            settings.setTextColor(getResources().getColor(R.color.black));
            terminal.setTextColor(getResources().getColor(R.color.white));
            sett.setVisibility(View.GONE);
            term.setVisibility(View.VISIBLE);

            ScrollView scrollView = this.terminal.getActivity().findViewById(R.id.scroll);
            scrollView.fullScroll(View.FOCUS_DOWN);
        });

        settings.setOnClickListener(v -> {
            settings.setTextColor(getResources().getColor(R.color.white));
            terminal.setTextColor(getResources().getColor(R.color.black));
            sett.setVisibility(View.VISIBLE);
            term.setVisibility(View.GONE);
        });

        init();
    }


    public void init(){
        this.terminal = (Terminal) getChildFragmentManager().findFragmentById(R.id.terminal);
        this.settings = (Settings) getChildFragmentManager().findFragmentById(R.id.settings);
    }


}