package com.example.ars;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;

public class SaveToCSV {
    public static void saveToCSV(String data, String filename){
        {

            new Thread() {
                public void run() {
                    try {


                        FileWriter fw = new FileWriter(filename, true);
                        fw.append(data);
                        fw.append("\n");

                        fw.flush();
                        fw.close();

                    } catch (Exception e) {
                    }

                }
            }.start();

        }

    }

}
