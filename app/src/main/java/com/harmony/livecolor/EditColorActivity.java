package com.harmony.livecolor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import java.io.InputStream;

import static android.graphics.Color.RGBToHSV;
import static com.harmony.livecolor.ColorPickerFragment.colorToHex;
import static com.harmony.livecolor.UsefulFunctions.convertHSVtoRGB;
import static com.harmony.livecolor.UsefulFunctions.convertRGBtoHSV;
import static com.harmony.livecolor.UsefulFunctions.getIntFromColor;

/**
 * @author Gabby
 */
public class EditColorActivity extends AppCompatActivity implements SaveListener {

    final static boolean ONLY_SAVE_ONCE_PER_COLOR = false;
    int colorValue;
    //For the save button animation/color fill
    private ImageView saveButtonCB;
    private int colorICB;

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

    RotateAnimation rotate;

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

        // dark theme check




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
        colorNameN.setText("");

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

        rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());


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
                        try {
                            m_Text = Integer.parseInt(input.getText().toString());
                            Log.d("I34", "m_Text="+m_Text);
                            seekRed.setProgress(m_Text);
                        } catch (NumberFormatException e) {
                            Log.d("I34", "Input was empty");
                        }
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
                        try {
                            m_Text = Integer.parseInt(input.getText().toString());
                            Log.d("I34", "m_Text="+m_Text);
                            seekGreen.setProgress(m_Text);
                        } catch (NumberFormatException e) {
                            Log.d("I34", "Input was empty");
                        }
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
                        try {
                            m_Text = Integer.parseInt(input.getText().toString());
                            Log.d("I34", "m_Text="+m_Text);
                            seekBlue.setProgress(m_Text);
                        } catch (NumberFormatException e) {
                            Log.d("I34", "Input was empty");
                        }
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



    }

    /**
     * BACK
     * simple back button
     * @param view view of button
     *
     * @author Gabby
     * changed as part of the onCreate inner to outer method refactor
     */
    public void onClickBack(View view) {
            finish();
        }

    /**
     * RESET COLOR
     * clear new color according to toggle, rotate reset button
     * @param view view of button
     *
     * @author Gabby
     * changed as part of the onCreate inner to outer method refactor
     */
    public void onClickReset(View view) {
        int oldRed = seekRed.getProgress();
        int oldGreen = seekGreen.getProgress();
        int oldBlue = seekBlue.getProgress();

        ImageButton reset = (ImageButton) view;
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
        //For if they hit reset while on the original color, keep the bookmark colored. (only reset if the color changed)
        if(seekRed.getProgress() != oldRed || seekGreen.getProgress() != oldGreen || seekBlue.getProgress() != oldBlue){
            resetBookmark();
        }
    }

    //Color the save button in if the save occurred (wasn't cancelled)
    public void saveHappened(){
        saveButtonCB.setImageResource(R.drawable.bookmark_selected );
        saveButtonCB.setColorFilter(colorICB);

        if(ONLY_SAVE_ONCE_PER_COLOR) {
            isButtonClickedNew = true;
        }

        Log.d("V2S2 bugfix", "Got callback (save happened).");
    }

    /**
     * SAVE COLOR
     * save new color, bounce and recolor button
     * @param view view of button
     *
     * @author Gabby
     * changed as part of the onCreate inner to outer method refactor
     */
    public void onClickSaveNew(View view) {

        if(!isButtonClickedNew){
            view.startAnimation(scaleAnimation);
            
            saveButtonCB = saveNC;

            ToggleButtonState = simpleToggleButton.isChecked();
            int colorI = 0;
            if(ToggleButtonState) {
                int hue = seekRed.getProgress();
                int sat = seekGreen.getProgress();
                int val = seekBlue.getProgress();


                name = colorNNView.getText().toString();
                //If the color is unedited it saves "" as the name. We can get the name from the other view.
                if(name == ""){
                    name = ((TextView) findViewById(R.id.colorN)).getText().toString();
                }

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
                //If the color is unedited it saves "" as the name. We can get the name from the other view.
                if(name == ""){
                    name = ((TextView) findViewById(R.id.colorN)).getText().toString();
                }

                rgb = String.format("(%1$d, %2$d, %3$d)", red, green, blue);
                hex = String.format( "#%02X%02X%02X", red, green, blue);
                int[] hue = convertRGBtoHSV(red,green,blue);
                hsv = String.format("(%1$d, %2$d, %3$d)",hue[0],hue[1],hue[2]);
                colorDB.addColorInfoData(name, hex, rgb, hsv);
            }
            colorICB = colorI;
            CustomDialog saveDialog = new CustomDialog(EditColorActivity.this,name,hex,rgb,hsv);
            final EditColorActivity callbackToHere = this;
            saveDialog.addListener(callbackToHere);
            saveDialog.showSaveDialog();
        }

    }



    /**
     * Resets the "save" button for the new color to the "unsaved" state
     *
     * @author Gabby
     */
    public void resetBookmark(){
        saveNC.setImageResource(R.drawable.unsaved);
        saveNC.setColorFilter(null);
        isButtonClickedNew = false;
    }

    /**
     * Updates the seekbars to the passed HSV values
     * @param hue
     * @param saturation
     * @param value
     *
     * @author Gabby
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
     *
     * @author Gabby
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
     *
     * @author Gabby
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
     * Update the "new color" image with passed int values (either RGB or HSV)
     * @param redOrHue
     * @param greenOrSat
     * @param blueOrVal
     *
     * @author Gabby
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


        //TODO clean this up a lot. Make functions for this sort of thing, it will be reused.
        final boolean USE_API_FOR_NAMES = false;

        final double viewWidthPercentOfScreen = 0.5;
        final int numberOfLines = 2;
        final float maxFontSize = 30;

        if(USE_API_FOR_NAMES) {
            ColorNameGetter.updateViewWithColorName(thisView, colorI, viewWidthPercentOfScreen*numberOfLines, maxFontSize);
        } else {
            final boolean CHANGE_FONT_SIZE_IF_TOO_LONG = true;
            if(CHANGE_FONT_SIZE_IF_TOO_LONG) {
                //Display the name on one line
                TextView viewToUpdateColorName = thisView;
                String hex = "#" + colorToHex(colorI);
                ColorNameGetterCSV.getAndFitName(viewToUpdateColorName, hex, viewWidthPercentOfScreen*numberOfLines, maxFontSize);
            } else {
                //Get the hex, and then name that corresponds to the hex
                String hex = "#" + colorToHex(colorI);
                String colorName = ColorNameGetterCSV.getName(hex);
                //Display the name
                thisView.setText(colorName);

                //Log.d("V2S1 colorname", "Hex " + hex + ": " + colorName);
            }
        }
    }
}
