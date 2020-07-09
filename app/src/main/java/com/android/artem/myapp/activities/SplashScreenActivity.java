package com.android.artem.myapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.artem.myapp.R;
import com.android.artem.myapp.util.mColor;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView l1, l2, l3, l4, l5,l6,l7, l8, l9;
    private Animation rotate, rotateBack, rotate2;
    private RelativeLayout relativeLayout;
    private List list[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);
        getSupportActionBar().hide();

        l1 = findViewById(R.id.logo1);
        l2 = findViewById(R.id.logo2);
        l3= findViewById(R.id.logo3);
        l4 = findViewById(R.id.logo4);
        l5 = findViewById(R.id.logo5);
        l6 = findViewById(R.id.logo6);
        l7 = findViewById(R.id.logo7);
        l8 = findViewById(R.id.logo8);
        l9 = findViewById(R.id.logo9);

        relativeLayout = findViewById(R.id.relative_layout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation();
            }
        });

        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotateBack = AnimationUtils.loadAnimation(this, R.anim.rotate_back);
        rotate2 = AnimationUtils.loadAnimation(this, R.anim.rotate2);



    }
    public void animation(){
        if(l1.getAnimation()==null) l1.startAnimation(rotate);
        if(l2.getAnimation()==null) l2.startAnimation(rotateBack);
        if(l3.getAnimation()==null) l3.startAnimation(rotate2);
        if(l4.getAnimation()==null) l4.startAnimation(rotate);
        if(l5.getAnimation()==null) l5.startAnimation(rotateBack);
        if(l6.getAnimation()==null) l6.startAnimation(rotate);
        if(l7.getAnimation()==null) l7.startAnimation(rotate2);
        if(l8.getAnimation()==null) l8.startAnimation(rotate2);
        if(l9.getAnimation()==null) l9.startAnimation(rotateBack);




        l1.setBackgroundColor(mColor.getGcolors()[1]);
        l2.setBackgroundColor(mColor.getGcolors()[1]);
        l3.setBackgroundColor(mColor.getGcolors()[1]);
        l4.setBackgroundColor(mColor.getGcolors()[1]);
        l5.setBackgroundColor(mColor.getGcolors()[1]);
        l6.setBackgroundColor(mColor.getGcolors()[1]);
        l7.setBackgroundColor(mColor.getGcolors()[1]);
        l8.setBackgroundColor(mColor.getGcolors()[1]);
        l9.setBackgroundColor(mColor.getGcolors()[1]);

    }




    @Override
    protected void onPause() {
        super.onPause();

    }



}
