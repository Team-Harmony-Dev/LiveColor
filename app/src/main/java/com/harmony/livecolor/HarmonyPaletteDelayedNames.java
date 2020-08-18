package com.harmony.livecolor;

import android.util.Log;
import java.util.ArrayList;

/**
 * Class for HarmonyInfoActivity.java to use to load color names for many palettes without freezing up the app.
 * @author Dustin
 */
public class HarmonyPaletteDelayedNames extends Thread {

    ArrayList<MyPalette> paletteList;

    public HarmonyPaletteDelayedNames(ArrayList<MyPalette> paletteListArr) {
        paletteList = paletteListArr;
    }

    @Override
    public void run() {
        Log.d("Harmony palettes", "Inside thread. Entering loop");
        for(MyPalette pal : paletteList){
            for(MyColor color : pal.getColors()){
                String name = ColorNameGetterCSV.getName(color.getHex());
                color.setName(name);
            }
        }

        Log.d("Harmony palettes", "Inside thread. Done");
    }
}
