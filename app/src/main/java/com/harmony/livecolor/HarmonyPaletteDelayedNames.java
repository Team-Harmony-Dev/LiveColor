package com.harmony.livecolor;

import android.util.Log;
import java.util.ArrayList;

public class HarmonyPaletteDelayedNames extends Thread {

    ArrayList<MyPalette> paletteList;

    public HarmonyPaletteDelayedNames(ArrayList<MyPalette> paletteListArr) {
        paletteList = paletteListArr;
    }

    @Override
    public void run() {
        //String name = ColorNameGetterCSV.getName("#FFFFFF");
        //(Test to make sure this doesn't freeze up the app.)
//        for(int i = 0; i < 100; ++i){
//            name = ColorNameGetterCSV.getName("#FFFFFF");
//        }
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
