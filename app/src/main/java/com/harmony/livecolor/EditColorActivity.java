package com.harmony.livecolor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditColorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_color);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        String colorString = preferences.getString("nameKey","Default");
        String colorNameT = preferences.getString("colorName","Default");
        int colorValue = Integer.parseInt(colorString);
        int RV = Color.red(colorValue);
        int GV = Color.green(colorValue);
        int BV = Color.blue(colorValue);

        // UPDATE VALUES
        ImageView colorD = (ImageView) findViewById(R.id.colorShow);
        colorD.setBackgroundColor(colorValue);
        ImageView colorNewS = (ImageView) findViewById(R.id.colorNewShow);
        colorNewS.setBackgroundColor(colorValue);

        TextView colorNameView = findViewById(R.id.colorN);
        colorNameView.setText(colorNameT);
        TextView colorNameN = findViewById(R.id.colorNN);
        colorNameN.setText(colorNameT);

        SeekBar seekRed = (SeekBar) findViewById(R.id.seekBarRed);
        seekRed.setProgress(RV);
        SeekBar seekBlue = (SeekBar) findViewById(R.id.seekBarBlue);
        seekBlue.setProgress(BV);
        SeekBar seekGreen = (SeekBar) findViewById(R.id.seekBarGreen);
        seekGreen.setProgress(GV);
    }
}
