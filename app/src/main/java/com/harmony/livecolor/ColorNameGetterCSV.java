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

//A lot of our bugs with color names are from using the async API call, so lets try reading from a local CSV.
//Load from res/raw/colornames.csv
//https://stackoverflow.com/questions/38415680/how-to-parse-csv-file-into-an-array-in-android-studio#38415815
public class ColorNameGetterCSV extends android.app.Application {

    private InputStream inputStream;
    //Might be redundant
    private boolean haveAlreadyReadNames = false;
    private ArrayList<String[]> colorNames;
    private final int NAME_INDEX = 0;
    private final int HEX_INDEX = 1;

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
            //First line is columns, we need to skip that
            boolean firstLine = true;
            while ((csvLine = reader.readLine()) != null) {
                //Ignore the column name line
                if(firstLine){
                    firstLine = false;
                    continue;
                }
                /* API actually seems to give both. https://api.color.pizza/v1/100000 gives "Dark Matter" which is marked
                //Ignore any names marked as not a good name
                char badNameChar = 'x';
                if(csvLine.length() > 0 && csvLine.charAt(csvLine.length() - 1) == badNameChar){
                    continue;
                }
                */
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
        //Debug
        Log.d("V2S1 colorname", "Read: "+this.colorNames);
        Log.d("V2S1 colorname", "type: "+this.colorNames.getClass().getName());
        Log.d("V2S1 colorname", "EachLine: "+this.colorNames.get(0));
        Log.d("V2S1 colorname", "type: "+this.colorNames.get(0).getClass().getName());
        Log.d("V2S1 colorname", "InnerElem: "+this.colorNames.get(0)[0]);
        Log.d("V2S1 colorname", "type: "+this.colorNames.get(0)[0].getClass().getName());

        //printArr();
    }

    //TODO Closest vector doesn't sqrt. Probably unnecessary right? Because we're only checking if one is greater than the other.
    private double getDistanceBetween(int r1, int g1, int b1, int r2, int g2, int b2){
        return Math.sqrt(Math.pow(r1-r2, 2)+Math.pow(g1-g2, 2)+Math.pow(b1-b2, 2));
    }

    //https://github.com/meodai/color-names/blob/master/scripts/server.js
    //TODO static?
    public String getName(String hex){
        //This is returned if something goes wrong
        final String errorColorName = "Error";
        /*
        if(!haveAlreadyReadNames) {
            readColors();
            haveAlreadyReadNames = true;
        }
        */
        //Use the list of names, do some rounding to find nearest
        //Naive approach with no caching to begin with, see how fast that is.

        //Expects #RRGGBB (includes the #, and has  no alpha)
        if(hex.length() != 7) {
            //TODO some sort of error message?
            Log.d("V2S1 colorname", "Hex " + hex + " not valid");
            return errorColorName;
        }
        int red = 0;
        int green = 0;
        int blue = 0;
        for(int i = 1; i < hex.length(); i+=2){
            //Substring excludes the end index itself, so +2 instead of +1.
            String hexPiece = hex.substring(i, i+2);
            int color = Integer.parseInt(hexPiece,16);
            if(i == 1){
                red = color;
            } else if (i == 3) {
                green = color;
            } else if (i == 5) {
                blue = color;
            } else {
                Log.d("V2S1 colorname", "Something weird happened when converting "+hex+"to rgb");
                return errorColorName;
            }
        }
        //Log.d("V2S1 colorname", "Looking for name for color "+hex+" ("+red+" "+green+" "+blue+")");

        //Now lets do the actual comparisons to tell which color is closest
        double shortestDistance = Double.MAX_VALUE;
        int indexOfBestMatch = -1;
        for(int i = 0; i < this.colorNames.size(); ++i){
            //Lets get this index's rgb
            int ired = 0;
            int igreen = 0;
            int iblue = 0;
            //TODO probably store directly rather than split each and every one.
            hex = this.colorNames.get(i)[HEX_INDEX];
            //Log.d("V2S1 colorname", "midtest i="+i+" hi="+HEX_INDEX+" hex="+hex);
            //TODO this crashes
            for(int x = 1; x < hex.length(); x+=2){
                //Substring excludes the end index itself, so +2 instead of +1.
                String hexPiece = hex.substring(x, x+2);
                int color = Integer.parseInt(hexPiece,16);
                if(x == 1){
                    ired = color;
                } else if (x == 3) {
                    igreen = color;
                } else if (x == 5) {
                    iblue = color;
                } else {
                    Log.d("V2S1 colorname", "Something weird happened when converting "+hex+"to rgb");
                    return errorColorName;
                }
            }

            double distance = getDistanceBetween(ired, igreen, iblue, red, green, blue);
            if(distance < shortestDistance){
                shortestDistance = distance;
                indexOfBestMatch = i;
                //Log.d("V2S1 colorname", "Found better distance to "+hex+" ("+ired+", "+igreen+", "+iblue+"), now is "+shortestDistance);
            }
        }
        if(indexOfBestMatch < 0 || indexOfBestMatch > this.colorNames.size()){
            Log.d("V2S1 colorname", "Something weird happened when finding distance");
            return errorColorName;
        }

        //Log.d("V2S1 colorname", "Found name. Distance is "+shortestDistance);

        return this.colorNames.get(indexOfBestMatch)[NAME_INDEX];
    }

    //For debug
    public void printArr(){
        //Note: Assumes each line has 2 String elements: Name, Hex
        for(int i = 0; i < this.colorNames.size(); ++i){
            String nameAndHex = this.colorNames.get(i)[NAME_INDEX] + ", "
                    + this.colorNames.get(i)[HEX_INDEX];
            Log.d("V2S1 colorname", "Color"+i+": "+nameAndHex);
        }
    }
}
