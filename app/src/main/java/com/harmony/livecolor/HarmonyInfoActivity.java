package com.harmony.livecolor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class HarmonyInfoActivity extends AppCompatActivity {

    //In case we want delete option
    private PalettesFragment.OnListFragmentInteractionListener listener;

    private ArrayList<MyPalette> paletteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harmony_info);
        initPalettes();
        initRecycler();
    }


    public void initPalettes(){
        //initialize ArrayList<MyPalette> here
        paletteList = new ArrayList<>();
        //will access palettes from database and put into MyPalette objects

        //TODO should probably have a helper function for these tests.
        //Note: fragment_palettes.xml limits the number of colors displayed per palette on the menu to 10.
        // also the edit button when you select a palette does nothing.

        int[][] testBasicColor = new int[][] {new int[] {180, 100, 100}};
        ArrayList<MyColor> testBasicColorMyColors = harmonyGenerator.colorsToMyColors(testBasicColor, 1);
        MyPalette testBasicColorPalette = new MyPalette("1", "Single color", testBasicColorMyColors);
        paletteList.add(testBasicColorPalette);
        //Testing getting the analogous colors 15 degrees to each side of the given color ().
        int[][] testAnalogous = harmonyGenerator.analogousScheme(180, 100, 100, 20, 5);
        ArrayList<MyColor> testAnalogousMyColors = harmonyGenerator.colorsToMyColors(testAnalogous, 5);
        MyPalette testAnalogousPalette = new MyPalette("1", "Analogous", testAnalogousMyColors);
        paletteList.add(testAnalogousPalette);
        //Testing getting the analogous colors 15 degrees to each side of the given color ().
        int[][] testMonochromatic = harmonyGenerator.monochromaticScheme(180, 100, 100, 50, 3);
        ArrayList<MyColor> testMonochromaticMyColors = harmonyGenerator.colorsToMyColors(testMonochromatic, 3);
        MyPalette testMonochromaticPalette = new MyPalette("2", "Monochromatic", testMonochromaticMyColors);
        paletteList.add(testMonochromaticPalette);
        //TODO triadic test

        //Testing getting the analogous colors 15 degrees to each side of the given color ().
        int[][] testEvenEvenSpaced = harmonyGenerator.evenlySpacedScheme(300, 100, 100, 4);
        ArrayList<MyColor> testEvenEvenSpacedMyColors = harmonyGenerator.colorsToMyColors(testEvenEvenSpaced, 4);
        MyPalette testEvenEvenSpacedPalette = new MyPalette("4", "Four evenly spaced on color wheel", testEvenEvenSpacedMyColors);
        paletteList.add(testEvenEvenSpacedPalette);
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
