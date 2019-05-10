package com.example.qrfiletransferandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView loading = (ImageView) findViewById(R.id.welcome);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.splash);
        loading.startAnimation(anim);
        checkFilePermission();
    }

    private void startTimer() {
        final Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(2500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }

    private void checkFilePermission() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET
        };

        if(!allGranted(permissions)){
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else {
            startTimer();
        }
    }

    private boolean allGranted(String[] permissions) {
        for(String permission: permissions){
            if(ContextCompat.checkSelfPermission(SplashActivity.this, permission)
                    != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkFilePermission();
    }
}
