package com.harmony.livecolor;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//Credit for color name stuff:
//https://github.com/meodai/color-names
//https://github.com/meodai/ClosestVector
//https://github.com/dtao/nearest-color

//Assumption: Names never contain commas, hopefully, because it's a CSV.

//So many probs with API, try CSV instead.
//Load from res/raw/colornames.csv
//https://stackoverflow.com/questions/38415680/how-to-parse-csv-file-into-an-array-in-android-studio#38415815
public class ColorNameGetterCSV extends android.app.Application { //TODO extends gives context for getresources? Is that right?

    private InputStream inputStream;
    //Might be redundant
    private boolean haveAlreadyReadNames = false;
    private ArrayList<String[]> colorNames;

    /*
    //https://stackoverflow.com/a/8238658
    private static ColorNameGetterCSV mApp = null;
    @Override
    public void onCreate()
    {
        super.onCreate();
        mApp = this;
    }
    public static Context context()
    {
        return mApp.getApplicationContext();
    }
     */

    public ColorNameGetterCSV(InputStream inputStream){
        this.inputStream = inputStream;
    }

     private ArrayList<String[]> read(){
         ArrayList<String[]> resultList = new ArrayList<String[]>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                //Ignore any names marked as not a good name
                char badNameChar = 'x';
                if(csvLine.length() > 0 && csvLine.charAt(csvLine.length() - 1) == badNameChar){
                    continue;
                }
                //Each row should contain two elements: A name, and a hex value (including the #)
                String[] row = csvLine.split(",");
                resultList.add(row);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return resultList;
    }

    //TODO private?
    public void readColors(){
        /*
        InputStream inputStream = getResources().openRawResource(R.raw.colornames);
        ColorNameGetterCSV colors = new ColorNameGetterCSV(inputStream);
        */
        //TODO is this the best way to do this?
        this.colorNames = this.read();
        Log.d("V2S1 colorname", "Read: "+this.colorNames);
        Log.d("V2S1 colorname", "type: "+this.colorNames.getClass().getName());
        Log.d("V2S1 colorname", "EachLine: "+this.colorNames.get(0));
        Log.d("V2S1 colorname", "type: "+this.colorNames.get(0).getClass().getName());
        Log.d("V2S1 colorname", "InnerElem: "+this.colorNames.get(0)[0]);
        Log.d("V2S1 colorname", "type: "+this.colorNames.get(0)[0].getClass().getName());

        //printArr();
    }

    //https://github.com/meodai/color-names/blob/master/scripts/server.js
    public String getName(String hex){
        //TODO static?
        /*
        if(!haveAlreadyReadNames) {
            readColors();
            haveAlreadyReadNames = true;
        }
        */
        //Use this list of names, do some rounding to find nearest
        //TODO

        return "Black?";//I bet it's Black
    }

    //For debug
    public void printArr(){
        //Note: Assumes each line has 2 String elements: Name, Hex
        for(int i = 0; i < this.colorNames.size(); i++){
            //TODO check style
            String nameHexGood = this.colorNames.get(i)[0] + ", "
                    + this.colorNames.get(i)[1];
            Log.d("V2S1 colorname", "Color"+i+": "+nameHexGood);
        }
    }
}
