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

public class Splash extends AppCompatActivity {

    /**
     * Duration of wait
     */
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        Animation mAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        mAnimation.reset();
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameSplashScreen);
        assert frameLayout != null;
        frameLayout.clearAnimation();
        frameLayout.startAnimation(mAnimation);

        mAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        mAnimation.reset();
        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewSplashScreen);

        assert imageViewLogo != null;
        imageViewLogo.clearAnimation();
        imageViewLogo.startAnimation(mAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(Splash.this, MainActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}



