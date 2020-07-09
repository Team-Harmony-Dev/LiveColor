package com.harmony.livecolor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.parseColor;

public class ColorInfoActivity extends AppCompatActivity {

    // ToolBar
    Toolbar toolBar;

    private SavedColorsFragment.OnListFragmentInteractionListener listener;
    int colorValue, RV, GV, BV, hue;
    String hexValue;
    float[] hsvArray;
    private ArrayList<MyColor> colorList;
    ColorDatabase newColorDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_info);

        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

       ActionBar actionBar = getSupportActionBar();
       //actionBar.hide();


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

        final double viewWidthPercentOfScreen = 1.0;
        final float maxFontSize = 30;
        ColorNameGetter.updateViewWithColorName(colorNameView, colorValue, viewWidthPercentOfScreen, maxFontSize);
        //colorNameView.setText(colorNameT);

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
        String fullHSV = String.format("HSV: (%1$d, %2$.3f, %3$.3f)", hue, hsvArray[1], hsvArray[2]);
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
     * @author Daniel
     * changed as part of the onCreate inner to outer method refactor
     */

    /**
     * TOOLBAR BACK BUTTON FINISH ACTIVITY
     * call the return to finish activity
     * @param view view
     *
     * @author Daniel
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
     * @author Daniel
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

    /**
     * COPY HEX VALUE TO CLIPBOARD
     * call the copy to clip with proper args for the button
     * @param view view of button
     *
     * @author Daniel
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
     * @author Daniel
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
     * @author Daniel
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
     * @author Daniel
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
