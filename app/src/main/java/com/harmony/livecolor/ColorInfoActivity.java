package com.harmony.livecolor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.io.InputStream;
import java.util.ArrayList;

import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.parseColor;
import static com.harmony.livecolor.ColorPickerFragment.colorToHex;

public class ColorInfoActivity extends AppCompatActivity implements SaveListener {

    // ToolBar
    Toolbar toolBar;

    private SavedColorsFragment.OnListFragmentInteractionListener listener;
    int colorValue, RV, GV, BV, hue;
    String hexValue;
    float[] hsvArray;
    private ArrayList<MyColor> colorList;
    ColorDatabase newColorDatabase;

    ScaleAnimation scaleAnimation;
    private boolean isButtonClicked = false;

    //For the save button animation/color fill
    private ImageView saveButtonCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_info);

        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

       ActionBar actionBar = getSupportActionBar();
       //actionBar.hide();

        // dark mode check
        int currentNightMode =  getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // dark mode changes
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayoutColorIID);
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                constraintLayout.setBackground(getDrawable(R.color.colorPrimaryLight));

                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                constraintLayout.setBackground(getDrawable(R.color.colorPrimary));
                break;
        }


        // save color check
        isButtonClicked = false;
        ImageButton saveColorB = (ImageButton) findViewById(R.id.saveButton);
        saveColorB.setImageResource(R.drawable.ic_baseline_bookmark_border_light_grey_48);
        saveColorB.setColorFilter(null);

        // save button setup animation
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f,Animation.RELATIVE_TO_SELF, 0.7f,Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        //TODO: Move SharedPrefs to outside of the method
        // pass colors through MyColor and intent (talk with Gabby)

        // FETCH PICKED COLOR FROM PREFERENCES
        SharedPreferences preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String colorString = preferences.getString("colorString","Default");
        String colorNameT = preferences.getString("colorName","Default");

        Log.d("DEBUG", "Color set to background = " + colorString);
        colorValue = Integer.parseInt(colorString);
        if (intent.getExtras() != null) {
            Log.d("ColorInfoActivity", "BUNDLE!! hex: " + bundle.getString("hex"));
            String hex = bundle.getString("hex");
            colorValue = parseColor(hex);
        }

        colorValue = Integer.parseInt(colorString);

        Log.d("DEBUG", "Color set to background = " + colorString);
        colorValue = Integer.parseInt(colorString);
        if (bundle != null) {
            String hex = bundle.getString("hex");
            colorValue = parseColor(hex);
        }


        // UPDATE VALUES
        ImageView colorD = findViewById(R.id.colorDisplay);
        colorD.setBackgroundColor(colorValue);

        ConstraintLayout wholeLayout = findViewById(R.id.constraintLayoutColor);
        wholeLayout.setBackgroundColor(colorValue);


        TextView colorNameView = findViewById(R.id.colorNameCIA);
        if (intent.getExtras() != null) {
            Log.d("ColorInfoActivity", "BUNDLE!!");
            colorNameT = bundle.getString("name");
        }

        //TODO clean this up a lot. Make functions for this sort of thing, it will be reused.
        final boolean USE_API_FOR_NAMES = false;
        //TODO it looks like we don't actually have 100% of the screen, there's color around the box we're in which takes what %?
        final double viewWidthPercentOfScreen = 0.9;
        final float maxFontSize = 30;
        if(USE_API_FOR_NAMES) {
            ColorNameGetter.updateViewWithColorName(colorNameView, colorValue, viewWidthPercentOfScreen, maxFontSize);
            //colorNameView.setText(colorNameT);
        } else {
            final boolean CHANGE_FONT_SIZE_IF_TOO_LONG = true;
            if(CHANGE_FONT_SIZE_IF_TOO_LONG) {
                //Display the name on one line
                TextView viewToUpdateColorName = colorNameView;
                String hex = "#" + colorToHex(colorValue);
                ColorNameGetterCSV.getAndFitName(viewToUpdateColorName, hex, viewWidthPercentOfScreen, maxFontSize);
            } else {
                //Get the hex, and then name that corresponds to the hex
                String hex = "#" + colorToHex(colorValue);
                String colorName = ColorNameGetterCSV.getName(hex);
                //Display the name
                colorNameView.setText(colorName);

                //Log.d("V2S1 colorname", "Hex " + hex + ": " + colorName);
            }
        }

        //HEX

        hexValue = String.format("HEX: #%06X", (0xFFFFFF & colorValue)); //get the hex representation minus the first ff
        Log.d("ColorInfoActivity", "hexValue: " + hexValue);
        TextView hexDisplay = (TextView) findViewById(R.id.HexText);
        hexDisplay.setText(hexValue);



        //RGB

        RV = Color.red(colorValue);
        GV = Color.green(colorValue);
        BV = Color.blue(colorValue);

        String rgb = String.format("RGB: (%1$d, %2$d, %3$d)", RV, GV, BV);
        TextView rgbDisplay = (TextView) findViewById(R.id.RGBText);//get the textview that displays the RGB value
        rgbDisplay.setText(rgb); //set the textview to the new RGB: rgbvalue


        //HSV
        hsvArray = new float[3];
        RGBToHSV(RV, GV, BV, hsvArray);
        hue = Math.round(hsvArray[0]);
        //String fullHSV = String.format("HSV: (%1$d, %2$.3f, %3$.3f)", hue, saturation, value);
        float saturation = hsvArray[1];
        String saturationStr;
        float value = hsvArray[2];
        String valueStr;
        //We don't want it to display 0.000 if it's just 0.
        if(saturation == 0){
            saturationStr = "0";
        } else {
            saturationStr = String.format("%1$.3f", saturation);
        }
        if(value == 0){
            valueStr = "0";
        } else {
            valueStr = String.format("%1$.3f", value);
        }

        final String PREFIX = "HSV: (";
        final String SEP = ", ";
        final String POSTFIX = ")";
        String fullHSV = PREFIX + hue + SEP + saturationStr + SEP + valueStr + POSTFIX;
        TextView hsvDisplay = (TextView) findViewById(R.id.HSVText);
        hsvDisplay.setText(fullHSV);



        newColorDatabase = new ColorDatabase(ColorInfoActivity.this);

        //initColors();

        //initRecycler();


    }


    /**
     * ALL CAPS METHOD DESCRIPTION
     * lowercase description of the means
     * @param param breif description of param
     * @return description as necessary of the return value
     *
     * @author {someone}, Daniel
     * changed as part of the onCreate inner to outer method refactor
     */

    /**
     * TOOLBAR BACK BUTTON FINISH ACTIVITY
     * call the return to finish activity
     * @param view view
     *
     * @author {someone}, Daniel
     * changed as part of the onCreate inner to outer method refactor
     */
    public void onClickBackButton(View view){
        finish();
    }

    /**
     * TOOLBAR EDIT BUTTON SWITCH TO EDIT COLOR ACTIVITY
     * starts edit color activity with current color
     * @param view view for the button
     *
     * @author {someone}, Daniel
     * changed as part of the oncreate inner to outer method refactor
     */
    public void onClickEditButton(View view){
        Intent intent = getIntent();
        Intent startEditColorActivity = new Intent(view.getContext(), EditColorActivity.class);
        if(intent.getExtras() != null){
            Log.d("ColorInfoActivity", "BUNDLE sending colorValue: " + colorValue);
            startEditColorActivity.putExtra("colorValue", colorValue);
        }
        startActivity(startEditColorActivity);
    }

    //Color the save button in if the save occurred (wasn't cancelled)
    public void saveHappened(){
        saveButtonCB.setImageResource(R.drawable.ic_baseline_bookmark_selected_light_grey_48 );
        saveButtonCB.setColorFilter(colorValue);

        isButtonClicked = true;
        //Log.d("V2S2 bugfix", "callback saveColorB="+saveColorB);

        Log.d("V2S2 bugfix", "Got callback (save happened). isButtonClicked="+isButtonClicked+" colorValue="+colorValue);
    }

    public void onClickSaveButton(View view){

            String hex = "#" + colorToHex(colorValue);
            String name = ColorNameGetterCSV.getName(hex);
            String rgb = String.format("(%1$d, %2$d, %3$d)", RV, GV, BV);
            String hsv = hsvArray.toString();

            if(!isButtonClicked){
                view.startAnimation(scaleAnimation);
                saveButtonCB = (ImageButton) view;

                CustomDialog pickerDialog = new CustomDialog(this,name,hex,rgb,hsv);
                final ColorInfoActivity callbackToHere = this;
                pickerDialog.addListener(callbackToHere);
                pickerDialog.showSaveDialog();
            }



    }




    /**
     * COPY HEX VALUE TO CLIPBOARD
     * call the copy to clip with proper args for the button
     * @param view view of button
     *
     * @author {someone}, Daniel
     * changed as part of the onCreate inner to outer method refactor
     */
    public void onClickCopyHEX(View view){
        copyToClip("HEX");
    }
    /**
     * COPY RGB VALUE TO CLIPBOARD
     * call the copy to clip with proper args for the button
     * @param view view of button
     *
     * @author {someone}, Daniel
     * changed as part of the onCreate inner to outer method refactor
     */
    public void onClickCopyRGB(View view){
        copyToClip("RGB");
    }

    /**
     * COPY HSV VALUE TO CLIPBOARD
     * call the copy to clip with proper args for the button
     * @param view view of button
     *
     * @author {someone}, Daniel
     * changed as part of the onCreate inner to outer method refactor
     */
    public void onClickCopyHSV(View view){
        copyToClip("HSV");
    }

    /**
     * GENERATE AND VIEW COLOR HARMONIES
     * start the harmony info activity using current color hsv as basis
     * @param view view of button
     *
     * @author {someone}, Daniel
     * changed as part of the onCreate inner to outer method refactor
     */
    public void onClickColorHarmonies(View view){
        Intent intent = new Intent(ColorInfoActivity.this, HarmonyInfoActivity.class);
        intent.putExtra("color_hsv", hsvArray);
        startActivity(intent);
    }


    /**
     * copyToClip copies the selected color info to the user's clipboard and produces the custom toast
     * @param type the type of color info being copied - RGB, HEX, or HSV
     * @author Gabby
     */
    void copyToClip(String type){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        final Context cont = getApplicationContext();

        if(type == "HEX"){
            clip = ClipData.newPlainText("copied", String.format("#%06X", (0xFFFFFF & colorValue)));
        } else if (type == "HSV"){
            clip = ClipData.newPlainText("copied", String.format("(%1$d, %2$.3f, %3$.3f)", hue, hsvArray[1], hsvArray[2]));
        } else {
            clip = ClipData.newPlainText("copied", String.format("(%1$d, %2$d, %3$d)", RV, GV, BV));
        }
        clipboard.setPrimaryClip(clip);

        Toast toast = Toast.makeText(cont,
                type + " values copied to clipboard!",
                Toast.LENGTH_SHORT);
        View view = toast.getView();

        view.setBackgroundResource(R.color.colorDark);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);

        toast.show();
    }

}
