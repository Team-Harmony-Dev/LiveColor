package com.harmony.livecolor;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//So many probs with API, try CSV instead.
//TODO load from res/raw/colornames.csv
//https://stackoverflow.com/questions/38415680/how-to-parse-csv-file-into-an-array-in-android-studio#38415815
public class ColorNameGetterCSV extends android.app.Application { //TODO extends gives context for getresources? Is that right?

    private InputStream inputStream;
    //Might be redundant
    private boolean haveAlreadyReadNames = false;
    private List colorNames;

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

    private List read(){
        List resultList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
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

    private void readColors(){
        InputStream inputStream = getResources().openRawResource(R.raw.colornames);
        ColorNameGetterCSV colors = new ColorNameGetterCSV(inputStream);
        //TODO is this the best way to do this?
        this.colorNames = colors.read();
    }

    public String getName(){
        if(!haveAlreadyReadNames) {
            readColors();
            haveAlreadyReadNames = true;
        }
        //Use this list of names, do some rounding to find nearest
        //TODO

        return "Black?";//I bet it's Black
    }
}
