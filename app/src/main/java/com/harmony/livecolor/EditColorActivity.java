package com.harmony.livecolor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.zip.Inflater;

import static android.graphics.Color.RGBToHSV;

public class EditColorActivity extends AppCompatActivity {
    int RV, GV, BV, colorValue, HV, SV, VV;
    SeekBar seekRed, seekGreen, seekBlue;
    static TextView colorNNView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_color);

        colorNNView = findViewById(R.id.colorNN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        String colorString = preferences.getString("colorString","Default");
        String colorNameT = preferences.getString("colorName","Default");
        colorValue = Integer.parseInt(colorString);
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

        seekRed = (SeekBar) findViewById(R.id.seekBarRed);
        seekRed.setProgress(RV);
        seekGreen = (SeekBar) findViewById(R.id.seekBarGreen);
        seekGreen.setProgress(GV);
        seekBlue = (SeekBar) findViewById(R.id.seekBarBlue);
        seekBlue.setProgress(BV);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    convertRGBtoHSV();
                    updateHSVText();
                    seekRed.setMax(360);
                    seekRed.setProgress(HV);
                    seekGreen.setMax(100);
                    seekGreen.setProgress(SV);
                    seekBlue.setMax(100);
                    seekBlue.setProgress(VV);
                } else {
                    // The toggle is disabled
                    convertRGBtoHSV();
                    seekRed.setMax(255);
                    seekRed.setProgress(RV);
                    seekGreen.setMax(255);
                    seekGreen.setProgress(GV);
                    seekBlue.setMax(255);
                    seekBlue.setProgress(BV);
                }
            }
        });

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

        ImageButton reset = findViewById(R.id.resetColor);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RV = Color.red(colorValue);
                GV = Color.green(colorValue);
                BV = Color.blue(colorValue);

                SeekBar seekRed = (SeekBar) findViewById(R.id.seekBarRed);
                seekRed.setProgress(RV);
                SeekBar seekBlue = (SeekBar) findViewById(R.id.seekBarBlue);
                seekBlue.setProgress(BV);
                SeekBar seekGreen = (SeekBar) findViewById(R.id.seekBarGreen);
                seekGreen.setProgress(GV);

                updateColorNew();
            }
        });
    }

    public void updateHSVText(){
        TextView A = (TextView) findViewById(R.id.textRorH);
        String fullHueText = String.format("Hue: %1$d", HV);
        A.setText(fullHueText);
        TextView B = (TextView) findViewById(R.id.textGorS);
        String fullSaturationText = String.format("Saturation: %1$d", SV);
        B.setText(fullSaturationText);
        TextView C = (TextView) findViewById(R.id.textBorV);
        String fullValueText = String.format("Value: %1$d", VV);
        C.setText(fullValueText);
    }

    public void updateRGBText(){
        TextView A = (TextView) findViewById(R.id.textRorH);
        String fullRedText = String.format("Red: %1$d", RV);
        A.setText(fullRedText);
        TextView B = (TextView) findViewById(R.id.textGorS);
        String fullGreenText = String.format("Green: %1$d", GV);
        B.setText(fullGreenText);
        TextView C = (TextView) findViewById(R.id.textBorV);
        String fullBlueText = String.format("Blue: %1$d", BV);
        C.setText(fullBlueText);
    }

    public void convertRGBtoHSV(){
        float[] hsvArray;
        hsvArray = new float[3];
        RGBToHSV(RV,GV,BV,hsvArray);
        HV = Math.round(hsvArray[0]);
        SV = Math.round((hsvArray[1])*100);
        VV = Math.round((hsvArray[2])*100);
    }

    public void convertHSVtoRGB(){
        float[] hsv = new float[3];
        hsv[0] = HV;
        hsv[1] = SV;
        hsv[2] = VV;
        int outputColor = Color.HSVToColor(hsv);
        RV = Color.red(outputColor);
        GV = Color.green(outputColor);
        BV = Color.blue(outputColor);
    }

    public void updateColorNew(){
        ImageView colorNewS = (ImageView) findViewById(R.id.colorNewShow);

        //Get the current state of the toggle button - RGB or HSV
        ToggleButton simpleToggleButton = (ToggleButton) findViewById(R.id.toggleButton); // initiate a toggle button
        Boolean ToggleButtonState = simpleToggleButton.isChecked(); // check current state of a toggle button (true or false).

        int colorI = 0;
        if(!ToggleButtonState){
            colorI = getIntFromColor(RV,GV, BV);
        } else {

        }

        colorNewS.setBackgroundColor(colorI);

        //TODO: Get color name and update the text for "new color name"

        EditColorActivity.colorNNView = this.findViewById(R.id.colorNN);
        colorNameGetter cng = new colorNameGetter();
        colorNameGetter.updateViewWithColorName(colorNNView, colorI);
        cng.execute(colorI);

        updateColorPicker();
    }

    public void updateColorPicker(){
        //View view;
        //view = Inflater.inflate(R.layout.fragment_color_picker, MainActivity, false);
        //ImageView colorDisplay = findViewById(R.id.pickedColorDisplayView);
        //final int TRANSPARENT = 0xFF000000;
        //int colorNew = getIntFromColor(RV,GV, BV);
        //colorNew = colorNew | TRANSPARENT;
        //colorDisplay.setBackgroundColor(colorNew);
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}
