package com.harmony.livecolor;

import android.content.Intent;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

//        if (NightModeUtils.isNightModeEnabled(SplashActivity.this)) {
//            Log.d("DARK", "NIGHT MODE ENABLED ");
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            setContentView(R.layout.activity_splash_night);
//            setTheme(R.style.SplashThemeDark);
//        } else {
//            Log.d("DARK", "NIGHT MODE DISABLED ");
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            setContentView(R.layout.activity_splash);
//            setTheme(R.style.SplashThemeLight);
//        }

        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();
    }
}
