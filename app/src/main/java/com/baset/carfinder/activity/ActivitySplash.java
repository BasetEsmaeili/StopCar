package com.baset.carfinder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.baset.carfinder.constants.Constants;
import com.baset.carfinder.R;
import com.bumptech.glide.Glide;

public class ActivitySplash extends AppCompatActivity implements Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imageView = findViewById(R.id.img_splash_background);
        Glide.with(getBaseContext()).load(R.drawable.splash_background).into(imageView);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
                if (preferences.contains(KEY_PREFERENCE_PLAQUE)) {
                    startActivity(new Intent(getBaseContext(), ActivityMain.class));
                    finish();
                } else {
                    startActivity(new Intent(getBaseContext(), ActivitySpecifications.class));
                    finish();
                }
            }
        }, SPLASH_DURATION);
    }
}
