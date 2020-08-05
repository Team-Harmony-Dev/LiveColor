package com.harmony.livecolor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // first opening night mode check
        // stops white blind splash if dark mode enabled on device
        if (!NightModeUtils.isToogleEnabled(SplashActivity.this)) {
            if (NightModeUtils.isDarkMode(SplashActivity.this)) {
                NightModeUtils.setIsNightModeEnabled(SplashActivity.this, true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                NightModeUtils.setIsNightModeEnabled(SplashActivity.this, false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else {
            if (NightModeUtils.isNightModeEnabled(SplashActivity.this)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }


        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
