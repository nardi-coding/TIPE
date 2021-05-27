package com.example.ars;

import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class Settings extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        ListPreference listPreference = (ListPreference) findPreference("gravity");

        EditTextPreference lr               = (EditTextPreference) findPreference("lr");
        EditTextPreference directions       = (EditTextPreference) findPreference("directions");
        EditTextPreference bestdirections   = (EditTextPreference) findPreference("bestdirections");
        EditTextPreference steps            = (EditTextPreference) findPreference("steps");
        EditTextPreference noise            = (EditTextPreference) findPreference("noise");

        lr.setSummary("");
        directions.setSummary("");
        bestdirections.setSummary("");
        steps.setSummary("");
        noise.setSummary("");

        double gravity = Double.parseDouble(String.valueOf(listPreference.getValue()));
        Utils.saveToSharedPref(getActivity(), "gravity", gravity);

        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            double gravity1 = Double.parseDouble(String.valueOf(newValue));
            Utils.saveToSharedPref(getActivity(), "gravity", gravity1);
            return true;
        });



        lr.setOnPreferenceChangeListener((preference, newValue) -> {
            lr.setSummary(String.valueOf(newValue));
            Utils.saveToSharedPref(getActivity(), "lr", Double.parseDouble(String.valueOf(newValue)));
            return true;
        });

        directions.setOnPreferenceChangeListener((preference, newValue) -> {
            directions.setSummary(String.valueOf(newValue));
            Utils.saveToSharedPref(getActivity(), "directions", Double.parseDouble(String.valueOf(newValue)));
            return true;
        });

        bestdirections.setOnPreferenceChangeListener((preference, newValue) -> {
            bestdirections.setSummary(String.valueOf(newValue));
            Utils.saveToSharedPref(getActivity(), "bestdirections", Double.parseDouble(String.valueOf(newValue)));
            return true;
        });

        steps.setOnPreferenceChangeListener((preference, newValue) -> {
            steps.setSummary(String.valueOf(newValue));
            Utils.saveToSharedPref(getActivity(), "steps", Double.parseDouble(String.valueOf(newValue)));
            return true;
        });

        noise.setOnPreferenceChangeListener((preference, newValue) -> {
            noise.setSummary(String.valueOf(newValue));
            Utils.saveToSharedPref(getActivity(), "noise", Double.parseDouble(String.valueOf(newValue)));
            return true;
        });



    }
}