package com.harmony.livecolor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ColorInfoActivity extends AppCompatActivity {

    int colorValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_info);

        // FETCH PICKED COLOR FROM PREFERENCES
        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        String colorString = preferences.getString("nameKey","Default");

        Log.d("DEBUG", "Color set to background = " + colorString);
        colorValue = Integer.parseInt(colorString);

        // UPDATE VALUES
        ImageView colorD = (ImageView) findViewById(R.id.colorDisplay);
        colorD.setBackgroundColor(colorValue);

        int RV = Color.red(colorValue);
        int GV = Color.green(colorValue);
        int BV = Color.blue(colorValue);
    }
}
