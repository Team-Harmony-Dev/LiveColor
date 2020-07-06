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

        ImageButton backbut = (ImageButton) findViewById(R.id.backButton);
        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

        // Edit color listener
        ImageButton editColorB = (ImageButton) findViewById(R.id.editButton);
        editColorB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Intent startEditColorActivity = new Intent(view.getContext(), EditColorActivity.class);
                if(intent.getExtras() != null){
                    Log.d("ColorInfoActivity", "BUNDLE sending colorValue: " + colorValue);
                    startEditColorActivity.putExtra("colorValue", colorValue);
                }
                startActivity(startEditColorActivity);
            }
        });

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

        // Copy Button Listeners

        Button copy1 = findViewById(R.id.copyHEX);

        copy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClip("HEX");
            }
        });

        Button copy2 = findViewById(R.id.copyRGB);
        copy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClip("RGB");
            }
        });

        Button copy3 = findViewById(R.id.copyHSV);
        copy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClip("HSV");
            }
        });

        Button harmonyButton = findViewById(R.id.harmonyButton);
        harmonyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ColorInfoActivity.this, HarmonyInfoActivity.class);
                intent.putExtra("color_hsv", hsvArray);
                startActivity(intent);
            }
        });


        newColorDatabase = new ColorDatabase(ColorInfoActivity.this);

        //initColors();

        //initRecycler();


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
