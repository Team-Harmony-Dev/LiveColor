package com.harmony.livecolor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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
    private boolean isButtonClicked = false;
    private boolean isButtonClickedNew = false;
    ImageButton saveNC;
    ToggleButton simpleToggleButton;
    Boolean ToggleButtonState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_color);

        colorNNView = findViewById(R.id.colorNN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        saveNC = findViewById(R.id.saveNewColor);

        simpleToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        ToggleButtonState = simpleToggleButton.isChecked();

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

        updateRGBText();

        simpleToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    convertRGBtoHSV();
                    updateHSVText();
                    updateSeekbarsHSV();
                } else {
                    // The toggle is disabled
                    convertHSVtoRGB();
                    updateRGBText();
                    updateSeekbarsRGB();
                }
            }
        });

        seekRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                updateText(progress, seekGreen.getProgress(), seekBlue.getProgress());
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                ToggleButtonState = simpleToggleButton.isChecked();
                if(!ToggleButtonState){
                    RV = progressChangedValue;
                } else {
                    HV = progressChangedValue;
                }
                updateText(progressChangedValue, seekGreen.getProgress(), seekBlue.getProgress());
                updateColorNew();
                updateColorName();
                resetBookmark();
            }
        });

        seekGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                updateText(seekRed.getProgress(), progress, seekBlue.getProgress());
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                ToggleButtonState = simpleToggleButton.isChecked();
                if(!ToggleButtonState){
                    GV = progressChangedValue;
                } else {
                    SV = progressChangedValue;
                }
                updateText(seekRed.getProgress(), progressChangedValue, seekBlue.getProgress());
                updateColorNew();
                updateColorName();
                resetBookmark();
            }
        });

        seekBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                updateText(seekRed.getProgress(), seekGreen.getProgress(), progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                ToggleButtonState = simpleToggleButton.isChecked();
                if(!ToggleButtonState){
                    BV = progressChangedValue;
                } else {
                    VV = progressChangedValue;
                }
                updateText(seekRed.getProgress(), seekGreen.getProgress(), progressChangedValue);
                updateColorNew();
                updateColorName();
                resetBookmark();
            }
        });

        Button reset = findViewById(R.id.resetColor);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RV = Color.red(colorValue);
                GV = Color.green(colorValue);
                BV = Color.blue(colorValue);

                ToggleButtonState = simpleToggleButton.isChecked();

                if(!ToggleButtonState){
                    updateSeekbarsRGB();
                    updateRGBText();
                } else {
                    convertRGBtoHSV();
                    updateHSVText();
                    updateSeekbarsHSV();
                }

                updateColorNew();
            }
        });

        ImageButton backB = findViewById(R.id.backBut);
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // TODO: ANDREW put your code here
        // |
        // |
        // V
        saveNC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isButtonClickedNew = !isButtonClickedNew;
                saveNC.setImageResource(isButtonClickedNew ? R.drawable.bookmark_selected : R.drawable.ic_action_name);
                if(isButtonClickedNew){
                    ToggleButtonState = simpleToggleButton.isChecked();
                    int colorI = 0;
                    if(ToggleButtonState){
                        convertHSVtoRGB();
                    }
                    colorI = getIntFromColor(RV, GV, BV);
                    saveNC.setColorFilter(colorI);
                }else{
                    saveNC.setColorFilter(null);
                }
            }
        });

        final ImageButton saveOC = findViewById(R.id.saveOldColor);
        saveOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isButtonClicked = !isButtonClicked;
                saveOC.setImageResource(isButtonClicked ? R.drawable.bookmark_selected : R.drawable.ic_action_name);
                if(isButtonClicked){
                    saveOC.setColorFilter(colorValue);
                }else{
                    saveOC.setColorFilter(null);
                }
            }
        });
    }

    // Resets the "save" button for the new color to the "unsaved" state
    public void resetBookmark(){
        if(isButtonClickedNew){
            saveNC.setImageResource(R.drawable.ic_action_name);
            saveNC.setColorFilter(null);
            isButtonClickedNew = false;
        }
    }

    // Updates the seekbars to the current HSV values
    public void updateSeekbarsHSV(){
        seekRed.setMax(360);
        seekRed.setProgress(HV);
        seekGreen.setMax(100);
        seekGreen.setProgress(SV);
        seekBlue.setMax(100);
        seekBlue.setProgress(VV);
    }

    // Updates the seekbars to the current RGB values
    public void updateSeekbarsRGB(){
        seekRed.setMax(255);
        seekRed.setProgress(RV);
        seekGreen.setMax(255);
        seekGreen.setProgress(GV);
        seekBlue.setMax(255);
        seekBlue.setProgress(BV);
    }

    // Updates the text to display Hue, Saturation, and Value and the current progress on the seekbars
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

    //TODO: make a generic updateText function that checks for HSV or RGB state, & takes in the three ints of the seekbars
    public void updateText(int updateR, int updateG, int updateB){
        TextView A = (TextView) findViewById(R.id.textRorH);
        TextView B = (TextView) findViewById(R.id.textGorS);
        TextView C = (TextView) findViewById(R.id.textBorV);
        ToggleButtonState = simpleToggleButton.isChecked();
        if(!ToggleButtonState){
            String fullRedText = String.format("Red: %1$d", updateR);
            A.setText(fullRedText);
            String fullGreenText = String.format("Green: %1$d", updateG);
            B.setText(fullGreenText);
            String fullBlueText = String.format("Blue: %1$d", updateB);
            C.setText(fullBlueText);
        } else {
            String fullHueText = String.format("Hue: %1$d", updateR);
            A.setText(fullHueText);
            String fullSaturationText = String.format("Saturation: %1$d", updateG);
            B.setText(fullSaturationText);
            String fullValueText = String.format("Value: %1$d", updateB);
            C.setText(fullValueText);
        }
    }

    // Updates the text to display Red, Green, and Blue and the current progress on the seekbars
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

    // Converts the current values in RGB to HSV and stores them in HV, SV, and VV
    public void convertRGBtoHSV(){
        float[] hsvArray;
        hsvArray = new float[3];
        RGBToHSV(RV,GV,BV,hsvArray);
        HV = Math.round(hsvArray[0]);
        SV = Math.round((hsvArray[1])*100);
        VV = Math.round((hsvArray[2])*100);
    }

    // Converts the current values in HSV to RGB and stores them in RV, GV, BV
    public void convertHSVtoRGB(){
        float[] hsv = new float[3];
        hsv[0] = HV;
        hsv[1] = ((float) SV) / 100;
        hsv[2] = ((float) VV) / 100;
        int outputColor = Color.HSVToColor(hsv);
        RV = Color.red(outputColor);
        GV = Color.green(outputColor);
        BV = Color.blue(outputColor);
    }

    public void updateColorNew(){
        ImageView colorNewS = (ImageView) findViewById(R.id.colorNewShow);

        //Get the current state of the toggle button - RGB or HSV
        ToggleButtonState = simpleToggleButton.isChecked();

        int colorI = 0;
        if(ToggleButtonState){
            convertHSVtoRGB();
        }
        colorI = getIntFromColor(RV, GV, BV);
        colorNewS.setBackgroundColor(colorI);

        //updateColorPicker();
    }

    public void updateColorName(){
        ToggleButtonState = simpleToggleButton.isChecked();

        int colorI = 0;
        if(ToggleButtonState){
            convertHSVtoRGB();
        }

        colorI = getIntFromColor(RV,GV, BV);

        EditColorActivity.colorNNView = this.findViewById(R.id.colorNN);
        colorNameGetter cng = new colorNameGetter();
        colorNameGetter.updateViewWithColorName(colorNNView, colorI);
        cng.execute(colorI);
    }

    public void updateColorPicker(){
        //TODO: Decide if returning new color to color picker or keeping the old
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}
