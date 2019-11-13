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
    int RV;
    int GV;
    int BV;

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
        RV = Color.red(colorValue);
        GV = Color.green(colorValue);
        BV = Color.blue(colorValue);

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

        seekRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                RV = progressChangedValue;
                updateColorNew();
            }
        });

        seekGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                GV = progressChangedValue;
                updateColorNew();
            }
        });

        seekBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                BV = progressChangedValue;
                updateColorNew();
            }
        });
    }

    public void updateColorNew(){
        ImageView colorNewS = (ImageView) findViewById(R.id.colorNewShow);
        int colorI = getIntFromColor(RV,GV, BV);
        colorNewS.setBackgroundColor(colorI);

        //TODO: Get color name and update the text for "new color name"

        /* EditColorActivity.colorNNView = this.findViewById(R.id.colorNN);
        colorNameGetter tmp = new colorNameGetter();
        tmp.execute(colorI); */
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}
