package com.harmony.livecolor;

import android.util.Log;

public class HarmonyPaletteDelayedNames extends Thread {

    @Override
    public void run() {
        String name = ColorNameGetterCSV.getName("#FFFFFF");
        //(Test to make sure this doesn't freeze up the app.)
        for(int i = 0; i < 100; ++i){
            name = ColorNameGetterCSV.getName("#FFFFFF");
        }
        Log.d("Harmony palettes", "Inside thread. "+name);
    }
}
