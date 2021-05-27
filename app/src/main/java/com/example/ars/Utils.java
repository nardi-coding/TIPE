package com.example.ars;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.unity3d.player.UnityPlayer;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Utils {

    Activity activity;
    String state;

    public Utils(Activity activity){
        this.activity = activity;

    }

    public static void saveToSharedPref(Activity activity, String key, double variable){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, String.valueOf(variable));
        editor.apply();
    }

    public static void saveToSharedPref(Activity activity, String key, String variable){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, variable);
        editor.apply();
    }
    public static double getDouble(Activity activity, String key){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return Double.parseDouble(sharedPref.getString(key, "-1").replace(",", "."));
    }

    public static double getDouble(Activity activity, String key, String def){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return Double.parseDouble(sharedPref.getString(key, def).replace(",", "."));
    }

    public static String getString(Activity activity, String key){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }


    public  String getString(String key){
        return getString(activity, key);
    }


    public static HashMap<Integer, Double> sortHashMap(HashMap<Integer, Double> hashMap){
        List<Integer> keys = new ArrayList<>(hashMap.keySet());
        List<Double> values = new ArrayList<>(hashMap.values());

        Collections.sort(values);


        HashMap<Integer, Double> sortedHashMap = new HashMap<>();

        for(Double value : values){
            for(int k : keys){
                if(Objects.equals(hashMap.get(k), value)){
                    sortedHashMap.put(k, value);
                    break;
                }
            }
        }


        return sortedHashMap;
    }

    public void receiveState(String state){
        saveToSharedPref(activity, "state", state);
    }

    public double[] getState() {
        UnityPlayer.UnitySendMessage("Lander", "getState", "");
        //En mets un temps d'attente (car il y a un delai entre le moment de l'execution de la fonction et la reponse)
        try {
            Thread.sleep(120);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return stringToDoubleList(getString(activity, "state"));
    }



    public void getGroundPosition(String position){
        saveToSharedPref(activity, "groundPos", position);
    }

    public void getWidth(String width){
        saveToSharedPref(activity, "width", width);
    }

    public void getHeight(String height){
        saveToSharedPref(activity, "height", height);
    }

    public double getDouble(String key){
        return getDouble(activity, key);
    }

    public double getDouble(String key, String defaultValue){
        return getDouble(activity, key, defaultValue);
    }


    public static double[] stringToDoubleList(String list){
        String[] liste = list.split("#");
        double[] state = new double[8];
        int i = 0;
        for(String elem : liste){
            state[i] = Double.parseDouble(elem.replace(",", "."));
            i++;
        }
        return state;
    }


}
