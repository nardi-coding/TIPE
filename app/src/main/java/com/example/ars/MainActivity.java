package com.example.ars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

import org.nd4j.linalg.factory.Nd4j;

public class MainActivity extends AppCompatActivity{

    protected UnityPlayer mUnityPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Nd4j.ones(1,1);

        mUnityPlayer = new UnityPlayer(this);

        getWindow().setFormat(PixelFormat.RGBX_8888);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;



        View animation = findViewById(R.id.animation);
        animation.setLayoutParams(new ConstraintLayout.LayoutParams( width/2, height));
        animation.setX(0);
        animation.setY(0);

        View console= findViewById(R.id.console);
        console.setLayoutParams(new ConstraintLayout.LayoutParams(width/2, height/2));
        console.setX((float) width/2);
        console.setY(0);

        View menu = findViewById(R.id.menu);
        menu.setLayoutParams(new ConstraintLayout.LayoutParams( width/2, height/2));
        menu.setX((float) width/2);
        menu.setY((float) height/2);


        FrameLayout unityLayout = (FrameLayout) findViewById(R.id.animation);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        unityLayout.addView(mUnityPlayer.getView(), 0, lp);
        mUnityPlayer.requestFocus();


        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);

    }

    public void checkPermission(String permission, int requestCode){
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }


    @Override
    protected void onDestroy() {
        mUnityPlayer.quit();
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (! (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(MainActivity.this, "You can't save data to csv file", Toast.LENGTH_SHORT).show();
            }

        }

    }

}