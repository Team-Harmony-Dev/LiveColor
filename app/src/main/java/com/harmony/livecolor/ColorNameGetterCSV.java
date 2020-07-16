package com.harmony.livecolor;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

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

//TODO test how slow this is on main activity
//TODO proper function commenting
//TODO static everything might work best since we should only need to read from the file once.

//A lot of our bugs with color names are from using the async API call, so lets try reading from a local CSV.
//Load from res/raw/colornames.csv
//https://stackoverflow.com/questions/38415680/how-to-parse-csv-file-into-an-array-in-android-studio#38415815
public class ColorNameGetterCSV extends android.app.Application {

    private InputStream inputStream;
    //Might be redundant
    private static boolean haveAlreadyReadNames = false;
    private static ArrayList<String[]> colorNames;
    private static final int NAME_INDEX = 0;
    private static final int HEX_INDEX = 1;

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
                /*
                //API actually seems to give both. https://api.color.pizza/v1/100000 gives "Dark Matter" which is marked
                //TODO should probably not store the "x" if we aren't using it for anything

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
//        Log.d("V2S1 colorname", "Read: "+this.colorNames);
//        Log.d("V2S1 colorname", "type: "+this.colorNames.getClass().getName());
//        Log.d("V2S1 colorname", "EachLine: "+this.colorNames.get(0));
//        Log.d("V2S1 colorname", "type: "+this.colorNames.get(0).getClass().getName());
//        Log.d("V2S1 colorname", "InnerElem: "+this.colorNames.get(0)[0]);
//        Log.d("V2S1 colorname", "type: "+this.colorNames.get(0)[0].getClass().getName());

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

    //TODO proper comments
    //TODO version that takes int pixel
    //Trying to copy the behavior of the API ColorNameGetter without any async junk.
    final static String loadingText = ". . .";
    public static void getAndFitName(TextView view, String hex, double maximumViewWidthPercentOfScreen, float maximumFontSize){
        if(view == null){
            Log.w("V2S1 colorname", "getAndFitName Was passed a null view with hex "+hex);
            return;
        }
        //Be removing this and getting the font size live we can fix a bug caused by this font size change not happening synchronously.
        //Set up starting font size.
        ////view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50/*maximumFontSize*/);
        ////view.setText(loadingText);//TODO remove ? Need to figure out a better way to split or combine these functions
        //Get an instance, because this is how it's set up atm.
        //(Note: requires you've already read it. Still really should update this syntax for calling)
        InputStream inputStream = null;
        ColorNameGetterCSV colors = new ColorNameGetterCSV(inputStream);
        //Get the name that corresponds to the given hex
        String colorName = colors.getName(hex);
        //colorName = "Really long color name fits how";//For debugging
        //Display the name
        //view.setText(colorName);
        //Now, we need to see if it's on multiple lines.
        setAppropriatelySizedText(view, colorName, maximumViewWidthPercentOfScreen, maximumFontSize);
    }

    //Heavily based on ColorNameGetter.java
    protected static void setAppropriatelySizedText(TextView view, String colorName, double maximumViewWidthPercentOfScreen, float maximumFontSize) {
        ////view.setTextSize(TypedValue.COMPLEX_UNIT_SP, maximumFontSize);
        //Note: I don't actually need to pass the colorName to helper anymore
        view.setText(colorName);

        //Attempt at using a text Watcher didn't work any better than this.
        setAppropriatelySizedTextHelper(view, colorName, maximumViewWidthPercentOfScreen, maximumFontSize);
    }
    protected static void setAppropriatelySizedTextHelper(TextView view, String colorName, double maximumViewWidthPercentOfScreen, float maximumFontSize){
        // The idea is to detect how much we need to reduce the font size by,
        //   and then do that in one go
        //float fontSize = maximumFontSize;
        //Don't assume, get font size live.
        //We need the metrics density because getTextSize() returns actual pixel size, not sp
        float fontSize = view.getTextSize()/Resources.getSystem().getDisplayMetrics().density;
        Log.d("S3US5 updated", "---Preparing to fit "+colorName+"-------------------------------------------");
        Log.d("S3US5 updated", "Read fontsize as "+fontSize);
        //Looks like height is not what we want. Line number sort of is, but can be 0 if the textview isn't done updating.
        Log.d("S3US5 updated", "# lines is currently: "+view.getLineCount()+" and height is "+view.getLineHeight());
        //if( view.getLineCount() > 1){ //Sometimes it's detecting 0. TextView should have plenty of time to update between clicks though ?
        //if( true /*view.getLineCount() != 1*/ ){

        //First lets get the width of the text
        // https://stackoverflow.com/a/37930140
        view.measure(0, 0);
        int textWidth = 0;

        //This is terrible. Might break in some situations?
        //  If line count is 0, then it's pretty straightforward: The width is obtained correctly.
        //  If we end up with actual multiple lines though, then the width (of the widest line) is <=1 line.
        //  So we can change the font size to force it to update, and that makes line count return 0, which will work. Probably. It seems to.

        //Guarantee a size change. Arbitrary numbers.
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textWidth = view.getMeasuredWidth();

        //And now the width of the screen
        //https://stackoverflow.com/a/31377616
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        Log.d("S3US5 updated", "w="+textWidth+" sw="+screenWidth);

        //There's some sort of minor padding so I need to reduce it slightly
        maximumViewWidthPercentOfScreen = maximumViewWidthPercentOfScreen - 0.05;
        double maximumTextWidth = maximumViewWidthPercentOfScreen * screenWidth;
        double reduceToThisPercent = maximumTextWidth / textWidth;
        Log.d("S3US5", "sw="+screenWidth
                +" maxPercent="+maximumViewWidthPercentOfScreen
                +" mtw="+maximumTextWidth
                +" tw="+textWidth
                +" rp="+reduceToThisPercent);
        //Update font size to be smaller
        fontSize = (int) (fontSize*(reduceToThisPercent));

        Log.d("S3US5 updated", "fontSize that can fit: "+fontSize);
        //There was a bug where fitting text gets bigger to fully fit.
        //  We could easily make it a feature and just ignore the max size and always use fontSize.
        if(fontSize < maximumFontSize) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        } else {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, maximumFontSize);
            //Log.d("S3US5 updated resizeFont", "Was attempting resize on already fitting text?");
        }
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
