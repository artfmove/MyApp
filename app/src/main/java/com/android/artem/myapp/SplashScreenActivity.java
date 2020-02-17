package com.android.artem.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView logo1ImageView, logo2ImageView, logo3ImageView, logo4ImageView;
    private Animation rotate, rotateBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        logo1ImageView = findViewById(R.id.logo1);
        logo2ImageView = findViewById(R.id.logo2);
        logo3ImageView = findViewById(R.id.logo3);
        logo4ImageView = findViewById(R.id.logo4);
        startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class));

       /* rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotateBack = AnimationUtils.loadAnimation(this, R.anim.rotate_back);

        logo1ImageView.setAnimation(rotateBack);
        logo2ImageView.setAnimation(rotate);
        logo3ImageView.setAnimation(rotate);
        logo4ImageView.setAnimation(rotateBack);

        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(2200);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class));
                }
            }
        };
        thread.start();*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
