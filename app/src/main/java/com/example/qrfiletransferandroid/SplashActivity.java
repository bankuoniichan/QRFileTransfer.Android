package com.example.qrfiletransferandroid;

import android.content.Intent;
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
        final Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
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
}
