package com.harmony.livecolor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import static android.graphics.Color.RGBToHSV;

/**
 * @author Gabby
 */
public class EditColorActivity extends AppCompatActivity {
    int colorValue;
    SeekBar seekRed, seekGreen, seekBlue;
    static TextView colorNNView;
    String name, hex, rgb, hsv;
    private boolean isButtonClicked = false;
    private boolean isButtonClickedNew = false;
    ImageButton saveNC;
    ToggleButton simpleToggleButton;
    Boolean ToggleButtonState;
    private int m_Text = 0;
    String colorNameT;
    ScaleAnimation scaleAnimation;
    ColorDatabase colorDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_color);
        colorDB = new ColorDatabase(this);


        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        colorNNView = findViewById(R.id.colorNN);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        saveNC = findViewById(R.id.saveNewColor);

        simpleToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        ToggleButtonState = simpleToggleButton.isChecked();

        SharedPreferences preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String colorString = preferences.getString("colorString","Default");

        colorValue = Integer.parseInt(colorString);
        if (intent.getExtras() != null) {
            Log.d("EditColorActivity", "BUNDLE!!");
            colorValue = bundle.getInt("colorValue");
            Log.d("EditColorActivity", "BUNDLE getting color: " + colorValue);
        }
        int RV = Color.red(colorValue);
        int GV = Color.green(colorValue);
        int BV = Color.blue(colorValue);

        // UPDATE VALUES
        ImageView colorD = (ImageView) findViewById(R.id.colorShow);
        colorD.setBackgroundColor(colorValue);
        ImageView colorNewS = (ImageView) findViewById(R.id.colorNewShow);
        colorNewS.setBackgroundColor(colorValue);


        seekRed = findViewById(R.id.seekBarRed);
        seekRed.setProgress(RV);
        seekGreen = findViewById(R.id.seekBarGreen);
        seekGreen.setProgress(GV);
        seekBlue = findViewById(R.id.seekBarBlue);
        seekBlue.setProgress(BV);

        TextView colorNameView = findViewById(R.id.colorN);
        colorNameView.setText(colorNameT);
        //When you press edit color on a saved color, the name is incorrect. This should fix it...
        //Actually doesn't work. onCreate isn't called when that happens or something? TODO fix this.
        final double viewWidthPercentOfScreen = 0.50;
        final float maxFontSize = 30;

        TextView colorNameN = findViewById(R.id.colorNN);
        colorNameN.setText(" ");

        TextView colorNameV = findViewById(R.id.colorN);
        updateColorNameWithView(colorNameV);

        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        updateText(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());

        simpleToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled: HSV mode
                    int[] newHSVValues = convertRGBtoHSV(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress()); // Convert the RGB values into the HSV values for the seekbars
                    updateSeekbarsHSV(newHSVValues[0],newHSVValues[1],newHSVValues[2]); // Set the seekbars to their new values
                    updateText(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress()); // Update the text to reflect the new values

                } else {
                    // The toggle is disabled: RGB mode
                    int[] newRGBValues = convertHSVtoRGB(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress()); //Convert teh HSV values to RGB values for the seekbars
                    updateSeekbarsRGB(newRGBValues[0], newRGBValues[1], newRGBValues[2]); // Set the seekbars to their new values
                    updateText(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress()); // Update the text to reflect the new values
                }
            }
        });

        seekRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                updateText(progress, seekGreen.getProgress(), seekBlue.getProgress());
                updateColorNewInput(progress, seekGreen.getProgress(), seekBlue.getProgress());
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateText(progressChangedValue, seekGreen.getProgress(), seekBlue.getProgress());
                updateColorNewInput(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                EditColorActivity.colorNNView = findViewById(R.id.colorNN);
                updateColorNameWithView(colorNNView);
                resetBookmark();
            }
        });

        seekGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                updateText(seekRed.getProgress(), progress, seekBlue.getProgress());
                updateColorNewInput(seekRed.getProgress(), progress, seekBlue.getProgress());
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateText(seekRed.getProgress(), progressChangedValue, seekBlue.getProgress());
                updateColorNewInput(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                EditColorActivity.colorNNView = findViewById(R.id.colorNN);
                updateColorNameWithView(colorNNView);
                resetBookmark();
            }
        });

        seekBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                updateText(seekRed.getProgress(), seekGreen.getProgress(), progress);
                updateColorNewInput(seekRed.getProgress(), seekGreen.getProgress(), progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateText(seekRed.getProgress(), seekGreen.getProgress(), progressChangedValue);
                updateColorNewInput(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                EditColorActivity.colorNNView = findViewById(R.id.colorNN);
                updateColorNameWithView(colorNNView);
                resetBookmark();
            }
        });

        final RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        final ImageButton reset = findViewById(R.id.resetColor);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleButtonState = simpleToggleButton.isChecked();
                reset.startAnimation(rotate);
                if(!ToggleButtonState){
                    updateSeekbarsRGB(Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
                    updateText(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                } else {
                    int[] newHSVValues = convertRGBtoHSV(Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
                    updateSeekbarsHSV(newHSVValues[0], newHSVValues[1], newHSVValues[2]);
                    updateText(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                }

                updateColorNewInput(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                TextView colorNameN = findViewById(R.id.colorNN);
                colorNameN.setText(colorNameT);
                resetBookmark();
            }
        });

        final ImageButton backB = findViewById(R.id.backBut);
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView redText = findViewById(R.id.textRorH);
        redText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditColorActivity.this);
                ToggleButtonState = simpleToggleButton.isChecked();
                if(ToggleButtonState){
                    builder.setTitle("Input a value for Hue in the range (0,360):");
                } else{
                    builder.setTitle("Input a value for Red in the range (0,255):");
                }

                final EditText input = new EditText(EditColorActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = Integer.parseInt(input.getText().toString());
                        seekRed.setProgress(m_Text);
                        EditColorActivity.colorNNView = findViewById(R.id.colorNN);
                        updateColorNameWithView(colorNNView);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        TextView greenText = findViewById(R.id.textGorS);
        greenText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditColorActivity.this);
                ToggleButtonState = simpleToggleButton.isChecked();
                if(ToggleButtonState){
                    builder.setTitle("Input a value for Saturation in the range (0,100):");
                } else{
                    builder.setTitle("Input a value for Green in the range (0,255):");
                }

                final EditText input = new EditText(EditColorActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = Integer.parseInt(input.getText().toString());
                        seekGreen.setProgress(m_Text);
                        EditColorActivity.colorNNView = findViewById(R.id.colorNN);
                        updateColorNameWithView(colorNNView);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        TextView blueText = findViewById(R.id.textBorV);
        blueText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditColorActivity.this);
                ToggleButtonState = simpleToggleButton.isChecked();
                if(ToggleButtonState){
                    builder.setTitle("Input a value for Value in the range (0,100):");
                } else{
                    builder.setTitle("Input a value for Blue in the range (0,255):");
                }

                final EditText input = new EditText(EditColorActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = Integer.parseInt(input.getText().toString());
                        seekBlue.setProgress(m_Text);
                        EditColorActivity.colorNNView = findViewById(R.id.colorNN);
                        updateColorNameWithView(colorNNView);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        saveNC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isButtonClickedNew){
                    view.startAnimation(scaleAnimation);
                    isButtonClickedNew = !isButtonClickedNew;
                    saveNC.setImageResource(R.drawable.bookmark_selected);
                    ToggleButtonState = simpleToggleButton.isChecked();
                    int colorI = 0;
                    if(ToggleButtonState) {
                        int hue = seekRed.getProgress();
                        int sat = seekGreen.getProgress();
                        int val = seekBlue.getProgress();

                        name = colorNNView.getText().toString();

                        hsv = String.format("(%1$d, %2$d, %3$d)",hue,sat,val);
                        int[] newRGBValues = convertHSVtoRGB(hue, sat, val);
                        colorI = getIntFromColor(newRGBValues[0], newRGBValues[1], newRGBValues[2]);
                        rgb = String.format("(%1$d, %2$d, %3$d)",newRGBValues[0],newRGBValues[1],newRGBValues[2]);
                        hex = String.format( "#%02X%02X%02X", newRGBValues[0], newRGBValues[1], newRGBValues[2] );
                        colorDB.addColorInfoData(name, hex, rgb, hsv);
                    } else {
                        colorI = getIntFromColor(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                        int red = seekRed.getProgress();
                        int green = seekGreen.getProgress();
                        int blue = seekBlue.getProgress();
                        name = colorNNView.getText().toString();
                        rgb = String.format("(%1$d, %2$d, %3$d)", red, green, blue);
                        hex = String.format( "#%02X%02X%02X", red, green, blue);
                        int[] hue = convertRGBtoHSV(red,green,blue);
                        hsv = String.format("(%1$d, %2$d, %3$d)",hue[0],hue[1],hue[2]);
                        colorDB.addColorInfoData(name, hex, rgb, hsv);
                    }
                    saveNC.setColorFilter(colorI);
                }

            }
        });

    }

    /**
     * Resets the "save" button for the new color to the "unsaved" state
     */
    public void resetBookmark(){
        if(isButtonClickedNew){
            saveNC.setImageResource(R.drawable.unsaved);
            saveNC.setColorFilter(null);
            isButtonClickedNew = false;
        }
    }

    /**
     * Updates the seekbars to the passed HSV values
     * @param hue
     * @param saturation
     * @param value
     */
    public void updateSeekbarsHSV(int hue, int saturation, int value){
        seekRed.setMax(360);
        seekRed.setProgress(hue);
        seekGreen.setMax(100);
        seekGreen.setProgress(saturation);
        seekBlue.setMax(100);
        seekBlue.setProgress(value);
    }

    /**
     * Updates the seekbars to the passed RGB values, different from updateSeekbarsHSV because the max values are different
     * @param red
     * @param green
     * @param blue
     */
    public void updateSeekbarsRGB(int red, int green, int blue){
        seekRed.setMax(255);
        seekRed.setProgress(red);
        seekGreen.setMax(255);
        seekGreen.setProgress(green);
        seekBlue.setMax(255);
        seekBlue.setProgress(blue);
    }

    /**
     * Updates the RGB/HSV values in the textViews above the seekbars
     * @param updateRH int for the Red or Hue text
     * @param updateGS int for the Green or Saturation text
     * @param updateBV int for the Blue or Value text
     */
    public void updateText(int updateRH, int updateGS, int updateBV){
        TextView A = (TextView) findViewById(R.id.textRorH);
        TextView B = (TextView) findViewById(R.id.textGorS);
        TextView C = (TextView) findViewById(R.id.textBorV);
        ToggleButtonState = simpleToggleButton.isChecked();
        if(!ToggleButtonState){
            String fullRedText = String.format("Red: %1$d", updateRH);
            A.setText(fullRedText);
            String fullGreenText = String.format("Green: %1$d", updateGS);
            B.setText(fullGreenText);
            String fullBlueText = String.format("Blue: %1$d", updateBV);
            C.setText(fullBlueText);
        } else {
            String fullHueText = String.format("Hue: %1$d", updateRH);
            A.setText(fullHueText);
            String fullSaturationText = String.format("Saturation: %1$d", updateGS);
            B.setText(fullSaturationText);
            String fullValueText = String.format("Value: %1$d", updateBV);
            C.setText(fullValueText);
        }
    }

    /**
     * Converts given RGB ints to HSV values and returns them in an array
     * @param red
     * @param green
     * @param blue
     * @return an array of length 3 containing the RGB values, respectively
     */
    public int[] convertRGBtoHSV(int red, int green, int blue){
        float[] hsvArray = new float[3];
        RGBToHSV(red,green,blue,hsvArray);
        int[] convertedHSVForSeekbars = new int[3];
        convertedHSVForSeekbars[0] = Math.round(hsvArray[0]);
        convertedHSVForSeekbars[1] = Math.round((hsvArray[1])*100);
        convertedHSVForSeekbars[2] = Math.round((hsvArray[2])*100);
        return convertedHSVForSeekbars;
    }

    /**
     * Converts given HSV ints to RGB values and returns them in an array
     * @param hue
     * @param saturation
     * @param value
     * @return an array of length 3 containing the HSV values, respectively
     */
    public static int[] convertHSVtoRGB(int hue, int saturation, int value){
        float[] hsv = new float[3];
        hsv[0] = hue;
        hsv[1] = ((float) saturation) / 100;
        hsv[2] = ((float) value) / 100;
        int outputColor = Color.HSVToColor(hsv);
        int[] newRGBValues = new int[3];
        newRGBValues[0] = Color.red(outputColor);
        newRGBValues[1] = Color.green(outputColor);
        newRGBValues[2] = Color.blue(outputColor);
        return newRGBValues;
    }

    /**
     * Update the "new color" image with passed int values (either RGB or HSV)
     * @param redOrHue
     * @param greenOrSat
     * @param blueOrVal
     */
    public void updateColorNewInput(int redOrHue, int greenOrSat, int blueOrVal){
        ImageView colorNewS = (ImageView) findViewById(R.id.colorNewShow);
        ToggleButtonState = simpleToggleButton.isChecked();

        int colorI = 0;
        if(ToggleButtonState){
            int[] getRGBValue = convertHSVtoRGB(redOrHue, greenOrSat, blueOrVal);
            colorI = getIntFromColor(getRGBValue[0], getRGBValue[1], getRGBValue[2]);
        }else {
            colorI = getIntFromColor(redOrHue, greenOrSat, blueOrVal);
        }

        colorNewS.setBackgroundColor(colorI);
    }

    /**
     * Takes a textView and updates it with the current color based on the values of the seekbars
     * @param thisView textView to update
     */
    public void updateColorNameWithView(TextView thisView){
        ToggleButtonState = simpleToggleButton.isChecked();

        int redOrHue = seekRed.getProgress();
        int greenOrSat = seekGreen.getProgress();
        int blueOrValue = seekBlue.getProgress();

        int colorI = 0;
        if(ToggleButtonState){
            int[] getRGBValues = convertHSVtoRGB(redOrHue, greenOrSat, blueOrValue);
            colorI = getIntFromColor(getRGBValues[0], getRGBValues[1], getRGBValues[2]);
        } else {
            colorI = getIntFromColor(redOrHue, greenOrSat, blueOrValue);
        }

        final double viewWidthPercentOfScreen = 1.0;
        final float maxFontSize = 30;
        ColorNameGetter.updateViewWithColorName(thisView, colorI, viewWidthPercentOfScreen, maxFontSize);
    }

    /**
     *  Takes in RGB values and returns the associated color int
     * @param Red red value (R)
     * @param Green green value (G)
     * @param Blue blue value (B)
     * @return the color int of the passed RGB value
     */
    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}
