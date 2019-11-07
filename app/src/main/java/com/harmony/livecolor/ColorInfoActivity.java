package com.harmony.livecolor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import static android.graphics.Color.RGBToHSV;

public class ColorInfoActivity extends AppCompatActivity {

    int colorValue, RV, GV, BV, hue;
    float[] hsvArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_info);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ImageButton button2 = (ImageButton) findViewById(R.id.backButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // FETCH PICKED COLOR FROM PREFERENCES
        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        String colorString = preferences.getString("nameKey","Default");

        Log.d("DEBUG", "Color set to background = " + colorString);
        colorValue = Integer.parseInt(colorString);

        // UPDATE VALUES
        ImageView colorD = (ImageView) findViewById(R.id.colorDisplay);
        colorD.setBackgroundColor(colorValue);

        //HEX
        String hexValue = String.format("HEX: #%06X", (0xFFFFFF & colorValue)); //get the hex representation minus the first ff
        TextView hexDisplay = (TextView) findViewById(R.id.HexText);
        hexDisplay.setText(hexValue);

        //RGB
        RV = Color.red(colorValue);
        GV = Color.green(colorValue);
        BV = Color.blue(colorValue);

        String rgb = String.format("RGB: (%1$d, %2$d, %3$d)",RV,GV,BV);
        TextView rgbDisplay = (TextView) findViewById(R.id.RGBText);//get the textview that displays the RGB value
        rgbDisplay.setText(rgb); //set the textview to the new RGB: rgbvalue

        //HSV
        hsvArray = new float[3];
        RGBToHSV(RV,GV,BV,hsvArray);
        hue = Math.round(hsvArray[0]);
        String fullHSV = String.format("HSV: (%1$d, %2$.3f, %3$.3f)",hue,hsvArray[1],hsvArray[2]);
        TextView hsvDisplay = (TextView) findViewById(R.id.HSVText);
        hsvDisplay.setText(fullHSV);

        Button copy1 = findViewById(R.id.copyHEX);
        copy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied", String.format("#%06X", (0xFFFFFF & colorValue)));
                clipboard.setPrimaryClip(clip);
            }
        });

        Button copy2 = findViewById(R.id.copyRGB);
        copy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied", String.format("(%1$d, %2$d, %3$d)",RV,GV,BV));
                clipboard.setPrimaryClip(clip);
            }
        });

        Button copy3 = findViewById(R.id.copyHSV);
        copy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied", String.format("(%1$d, %2$.3f, %3$.3f)",hue,hsvArray[1],hsvArray[2]));
                clipboard.setPrimaryClip(clip);
            }
        });
    }

}
