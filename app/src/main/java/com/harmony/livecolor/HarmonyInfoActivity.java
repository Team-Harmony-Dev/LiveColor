package com.harmony.livecolor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

public class HarmonyInfoActivity extends AppCompatActivity {

    //In case we want delete option
    private PalettesFragment.OnListFragmentInteractionListener listener;

    private ArrayList<MyPalette> paletteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harmony_info);

        //removes action bar
        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();



        initPalettes();
        initRecycler();

    }


    /**
     *  BACK BUTTON
     *  simple back button
     * @param view view of button
     *
     *
     * part of the refactor
     * set back button to leave activity
     */
    public void onClickBackButton(View view){
        finish();
    }

    /**
     *  EDIT BUTTON
     *  placeholder edit button
     * @param view view of button
     *
     *
     * @author Daniel
     * part of the refactor
     *
     */
    public void onClickEditButton(View view){
        Log.d("DEBUG","edit button pressed in harmony activity");
    }

    public void initPalettes(){
        paletteList = new ArrayList<>();

        float[] hsv = getIntent().getFloatArrayExtra("color_hsv");
        Log.d("S4US4", "Received hsv "+hsv[0]+" "+hsv[1]+" "+hsv[2]);
        float hue = hsv[0];
        float saturation = hsv[1];
        float value = hsv[2];

        //TODO should probably have a helper function for these tests. Also, they're no longer "test"s
        //Note: fragment_palettes.xml limits the number of colors displayed per palette on the menu to 10.
        // also the edit button when you select a palette does nothing.

        //I'm not sure how to fix hue based color scheme for black/white.
        //So if it's super light/dark I'll just not generate them.
        float valueTooMonotonous = (float) 0.01;

        //Check if it's in the range [0.01..0.99] inclusive
        if(1 - valueTooMonotonous >= value && valueTooMonotonous <= value) {
            //Color & Complement
            float[][] testBasicColor = HarmonyGenerator.complementScheme(hue, saturation, value, 2);
            ArrayList<MyColor> testBasicColorMyColors = HarmonyGenerator.colorsToMyColors(testBasicColor, 2);
            MyPalette testBasicPalette = new MyPalette("1", "Color & Complement", testBasicColorMyColors);
            paletteList.add(testBasicPalette);

            //Testing getting the analogous colors each a # of degrees to each side of the given color.
            float[][] testAnalogous = HarmonyGenerator.analogousScheme(hue, saturation, value, 20, 5);
            ArrayList<MyColor> testAnalogousMyColors = HarmonyGenerator.colorsToMyColors(testAnalogous, 5);
            MyPalette testAnalogousPalette = new MyPalette("1", "Analogous", testAnalogousMyColors);
            paletteList.add(testAnalogousPalette);
        } else {
            //Color with no complement
            float[][] testBasicColor = new float[][] {new float[] {hue, saturation, value}};
            ArrayList<MyColor> testBasicColorMyColors = HarmonyGenerator.colorsToMyColors(testBasicColor, 1);
            MyPalette testBasicColorPalette = new MyPalette("1", "Color", testBasicColorMyColors);
            paletteList.add(testBasicColorPalette);
        }

        //TODO to fix this might be harder. Generate more of the other lightness?
        //Testing getting the monochromatic colors to each side of the given color based on percent.
        float[][] testMonochromatic = HarmonyGenerator.monochromaticScheme(hue, saturation, value, (float) 0.50, 5);
        ArrayList<MyColor> testMonochromaticMyColors = HarmonyGenerator.colorsToMyColors(testMonochromatic, 5);
        MyPalette testMonochromaticPalette = new MyPalette("2", "Monochromatic", testMonochromaticMyColors);
        paletteList.add(testMonochromaticPalette);

        if(1 - valueTooMonotonous >= value && valueTooMonotonous <= value) {
            //TODO (refactor) I may have misunderstood triadic. I can just use the evenly spaced for that. This is Split-Complementary.
            float[][] testTriadic = HarmonyGenerator.triadicScheme(hue, saturation, value, 20, 3);
            ArrayList<MyColor> testTriadicMyColors = HarmonyGenerator.colorsToMyColors(testTriadic, 3);
            MyPalette testTriadicPalette = new MyPalette("3", "Split-Complementary", testTriadicMyColors);
            paletteList.add(testTriadicPalette);

            //Testing getting some colors spaced evenly on the color wheel
            float[][] testOddEvenSpaced = HarmonyGenerator.evenlySpacedScheme(hue, saturation, value, 3);
            ArrayList<MyColor> testOddEvenSpacedMyColors = HarmonyGenerator.colorsToMyColors(testOddEvenSpaced, 3);
            MyPalette testOddEvenSpacedPalette = new MyPalette("4", "Three evenly spaced hues", testOddEvenSpacedMyColors);
            paletteList.add(testOddEvenSpacedPalette);

            //Testing getting some colors spaced evenly on the color wheel
            float[][] testEvenEvenSpaced = HarmonyGenerator.evenlySpacedScheme(hue, saturation, value, 4);
            ArrayList<MyColor> testEvenEvenSpacedMyColors = HarmonyGenerator.colorsToMyColors(testEvenEvenSpaced, 4);
            MyPalette testEvenEvenSpacedPalette = new MyPalette("5", "Four evenly spaced hues", testEvenEvenSpacedMyColors);
            paletteList.add(testEvenEvenSpacedPalette);
        }
    }

    public void initRecycler(){
        //get the RecyclerView from the view
        RecyclerView recyclerView = findViewById(R.id.harmonyRecycler);
        //then initialize the adapter, passing in the bookList
        MyPalettesRecyclerViewAdapter adapter = new MyPalettesRecyclerViewAdapter(this, paletteList,listener);
        //and set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
        //and set the layout manager as well
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
