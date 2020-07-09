package com.harmony.livecolor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.parseColor;

public class ColorInfoActivity extends AppCompatActivity {
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
        actionBar.hide();

        ImageButton button2 = (ImageButton) findViewById(R.id.backButton);
        button2.setOnClickListener(new View.OnClickListener() {
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

        LinearLayout wholeLayout = findViewById(R.id.colorLayout);
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



        Button copy1 = findViewById(R.id.copyHEX);
        copy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied", String.format("#%06X", (0xFFFFFF & colorValue)));
                clipboard.setPrimaryClip(clip);

                Snackbar.make(view, "HEX value copied to clipboard!", Snackbar.LENGTH_SHORT).show();
            }
        });

        Button copy2 = findViewById(R.id.copyRGB);
        copy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied", String.format("(%1$d, %2$d, %3$d)", RV, GV, BV));
                clipboard.setPrimaryClip(clip);

                Snackbar.make(view, "RGB values copied to clipboard!", Snackbar.LENGTH_SHORT).show();
            }
        });

        Button copy3 = findViewById(R.id.copyHSV);
        copy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied", String.format("(%1$d, %2$.3f, %3$.3f)", hue, hsvArray[1], hsvArray[2]));
                clipboard.setPrimaryClip(clip);

                Snackbar.make(view, "HSV values copied to clipboard!", Snackbar.LENGTH_SHORT).show();
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

    /*public void initColors(){
        //initialize ArrayList<MyColors> here
        String TAG = "COLORS";
        Cursor colorData = newColorDatabase.getColorInfoData();
        colorList = new ArrayList<>();

        if (colorData != null && colorData.getCount() > 0) {
            if (colorData.moveToFirst()) {
                do {
                    Log.d(TAG,  colorData.getString(2));
                    colorList.add(new MyColor(colorData.getString(0) + "",
                            colorData.getString(1) + "", colorData.getString(2) + "",
                            colorData.getString(3) + "", colorData.getString(4) + ""));
                }         while (colorData.moveToNext());

            }
        }
    }

    public void initRecycler(){
        //get the RecyclerView from the view
        RecyclerView recyclerView = findViewById(R.id.colorInfoRecycler);
        MySavedColorsRecyclerViewAdapter adapter = new MySavedColorsRecyclerViewAdapter(this,colorList,listener);
        //and set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
        //and set the layout manager as well
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    } */

}
