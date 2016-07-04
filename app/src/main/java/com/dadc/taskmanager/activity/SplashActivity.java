package com.dadc.taskmanager.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dadc.taskmanager.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        animation.reset();
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameSplashScreen);
        assert frameLayout != null;
        frameLayout.clearAnimation();
        frameLayout.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        animation.reset();
        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewSplashScreen);

        assert imageViewLogo != null;
        imageViewLogo.clearAnimation();
        imageViewLogo.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            }
        }, 3000);
    }

}



