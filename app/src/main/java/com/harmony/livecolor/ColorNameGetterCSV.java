package com.harmony.livecolor;

import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//Credit for color names:
//https://github.com/meodai/color-names
//https://github.com/meodai/ClosestVector
//https://github.com/dtao/nearest-color

//Assumption: Names never contain commas, hopefully, because it's a CSV.

//A lot of our bugs with color names are from using the async API call, so lets try reading from a local CSV.
//Load from res/raw/colornames.csv
//https://stackoverflow.com/questions/38415680/how-to-parse-csv-file-into-an-array-in-android-studio#38415815
public class ColorNameGetterCSV extends android.app.Application {

    private InputStream inputStream;
    private static ArrayList<String[]> colorNames;
    //This might be redundant? Whatever.
    private static boolean haveAlreadyReadNames;
    //If for some reason the csv changes format, you can change these and it all should still work.
    private static final int NAME_INDEX = 0;
    private static final int HEX_INDEX = 1;

    /**
     * Example of initializing this class:
     *    InputStream inputStream = getResources().openRawResource(R.raw.colornames);
     *    ColorNameGetterCSV colors = new ColorNameGetterCSV(inputStream);
     *    colors.readColors();
     * @param inputStream File to read from, if you're going to read color names. Can be null if it's already been read.
     * @author https://stackoverflow.com/questions/38415680/how-to-parse-csv-file-into-an-array-in-android-studio#38415815
     */
    public ColorNameGetterCSV(InputStream inputStream){
        this.inputStream = inputStream;
    }

    /**
     * Reads data from the file that was passed when creating an instance of the class.
     * @return ArrayList of [name, hex] string pairs.
     * @author https://stackoverflow.com/questions/38415680/how-to-parse-csv-file-into-an-array-in-android-studio#38415815
     */
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

    /**
     * Reads from res/raw/colornames.csv
     * Call this once, before using getName() or getAndFitName()
     * This is currently being called in MainActivity.java
     * @author Dustin
     */
    public void readColors(){
        this.colorNames = this.read();
        this.haveAlreadyReadNames = true;
        //printArr();
    }

    /**
     * Computes the squared distance between two 3D points (RGB colors).
     * Uses squared distance because the actual distance doesn't matter,
     *     we just need to know which is smallest to find the closest name.
     *
     * @param r1
     * @param g1
     * @param b1
     * @param r2
     * @param g2
     * @param b2
     * @return Squared distance between the points.
     * @author Dustin
     */
    private double getDistanceBetween(int r1, int g1, int b1, int r2, int g2, int b2){
        return /*Math.sqrt*/(Math.pow(r1-r2, 2)+Math.pow(g1-g2, 2)+Math.pow(b1-b2, 2));
    }

    /**
     * Takes the hex of a color and gets the closest color name from
     *     https://github.com/meodai/color-names/blob/master/scripts/server.js
     *     (Read from the version of that file in res/raw/colornames.csv).
     * Important note: Relies on colors already having been read (happens in MainActivity.java)
     *
     * I had some problem making it static, so just use this through getName()
     *
     * @param hex A color like #FFFFFF. # is expected. Transparency is not expected.
     * @return A human readable color name.
     * @author Dustin
     */
    protected String searchForName(String hex){
        //This is returned if something goes wrong
        final String errorColorName = "Error";

        if(!haveAlreadyReadNames) {
            if(this.inputStream == null){
                Log.w("V2S1 colorname", "Attempted to get a name before reading");
                return errorColorName;
            } else {
                this.readColors();
            }
        }

        //Use the list of names, finds the nearest
        //Naive approach with no caching to begin with, see how fast that is.

        //Expects #RRGGBB (includes the #, and has  no alpha)
        if(hex.length() != 7) {
            Log.w("V2S1 colorname", "Hex " + hex + " not valid");
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
            //TODO could store rgb directly rather than doing this on each color each time.
            hex = this.colorNames.get(i)[HEX_INDEX];
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

        //Log.d("V2S1 colorname", "Found name. Distance squared is "+shortestDistance);

        return this.colorNames.get(indexOfBestMatch)[NAME_INDEX];
    }

    /**
     * Takes the hex of a color and gets the closest color name from
     *     https://github.com/meodai/color-names/blob/master/scripts/server.js
     *     (Read from the version of that file in res/raw/colornames.csv).
     * Important note: Relies on colors already having been read (happens in MainActivity.java)
     * @param hex A color like #FFFFFF. # is expected. Transparency is not expected.
     * @return A human readable color name.
     * @author Dustin
     */
    public static String getName(String hex){
        InputStream inputStream = null;
        ColorNameGetterCSV colors = new ColorNameGetterCSV(inputStream);
        return colors.searchForName(hex);
    }

    //TODO mess around more with library functions? https://developer.android.com/reference/android/widget/TextView#setAutoSizeTextTypeUniformWithConfiguration(int,%20int,%20int,%20int)
    /**
     * A function that places the name corresponding to a hex color into a specified TextView,
     *     and sets the font size to either maximumFontSize or smaller to ensure that the
     *     TextView with the unpredictable-length name stays on a single line.
     *
     * Important note: Relies on colors already having been read (happens in MainActivity.java)
     *
     * Known bugs: Rounding can result in the same name being given slightly different font size depending on what the TextView's font size was beforehand.
     *
     * Note: If you want multiple lines instead of one, multiply maximumViewWidthPercentOfScreen by number of lines.
     *
     * @param view The TextView to put the name in
     * @param hex A string of hex, including the #, excluding any transparency. Ex: #FFFFFF
     * @param maximumViewWidthPercentOfScreen The horizontal percentage of the screen that the view takes up.
     * @param maximumFontSize The font size that will be used if no reduction is needed.
     */
    public static void getAndFitName(TextView view, String hex, double maximumViewWidthPercentOfScreen, float maximumFontSize){
        if(view == null){
            Log.w("V2S1 colorname", "getAndFitName Was passed a null view with hex "+hex);
            return;
        }

        String colorName = getName(hex);
        //colorName = "Really long color name fits how";//For debugging

        //Now, we need to stick it in the view and make sure it stays on a single line.
        setAppropriatelySizedText(view, colorName, maximumViewWidthPercentOfScreen, maximumFontSize);
    }

    /**
     * Heavily based on ColorNameGetter's method.
     * Displays the color name in a TextView, reducing font size to ensure that it fits in the space.
     * Known bugs: Rounding can result in the same name being given slightly different font size depending on what the TextView's font size was beforehand.
     * Note: If you want multiple lines instead of one, multiply maximumViewWidthPercentOfScreen by number of lines.
     * @param view The TextView to put the name into.
     * @param colorName The name to display in the view.
     * @param maximumViewWidthPercentOfScreen The horizontal percentage of the screen that the view takes up.
     * @param maximumFontSize The font size that will be used if no reduction is needed.
     * @author Dustin
     */
    public static void setAppropriatelySizedText(TextView view, String colorName, double maximumViewWidthPercentOfScreen, float maximumFontSize) {
        view.setText(colorName);

        // The idea is to detect how much we need to reduce the font size by, and then
        //    do that in one go because font size updates don't work instantly and
        //    I can't find a good way to force waiting for it (to do a binary search or whatever).
        //    See old commits for ColorNameGetter to see various other stuff I tried.
        //Get TextView's current font size.
        //We need the metrics density because getTextSize() returns actual pixel size, not sp
        float fontSize = view.getTextSize()/Resources.getSystem().getDisplayMetrics().density;
        Log.d("V2S1 colorname", "---Preparing to fit "+colorName+"-------------------------------------------");
        Log.d("V2S1 colorname", "Read fontsize as "+fontSize);
        //Looks like height is not what we want. Line number sort of is, but can be 0 if the textview isn't done updating.
        Log.d("V2S1 colorname", "# lines is currently: "+view.getLineCount()+" and height is "+view.getLineHeight());

        //First lets get the width of the text
        // https://stackoverflow.com/a/37930140
        view.measure(0, 0);
        //If line count is 0, then it's pretty straightforward: The width is obtained correctly.
        //  If we end up with actual multiple lines though, then the width (of the widest line) is <=1 line.
        //  So we can change the font size to force it to update, and that makes line count return 0, which will work. Probably. It seems to.
        //  This is terrible. Might break in some situations?
        //Guarantee a size change. Arbitrary numbers.
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        int textWidth = view.getMeasuredWidth();

        //And now the width of the screen
        //https://stackoverflow.com/a/31377616
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        Log.d("V2S1 colorname", "w="+textWidth+" sw="+screenWidth);

        //There's some sort of minor padding so I need to reduce it slightly
        //TODO this really shouldn't be hardcoded, especially as a raw value instead of %, but I'm not exactly sure where it's coming from.
        //  Note: *0.95 instead of raw value does not quite work in all cases on Pixel 2 (see Ornery Tangerine).
        maximumViewWidthPercentOfScreen = maximumViewWidthPercentOfScreen - 0.05;
        double maximumTextWidth = maximumViewWidthPercentOfScreen * screenWidth;
        double reduceToThisPercent = maximumTextWidth / textWidth;
        Log.d("V2S1 colorname", "sw="+screenWidth
                +" maxPercent="+maximumViewWidthPercentOfScreen
                +" mtw="+maximumTextWidth
                +" tw="+textWidth
                +" rp="+reduceToThisPercent);
        //Update font size to be smaller
        fontSize = (int) (fontSize*(reduceToThisPercent)); //TODO There might be some rounding issue. Just changing this to float doesn't fix. See #F57C21 "Ornery Tangerine" on a Pixel 2.

        Log.d("V2S1 colorname", "fontSize that can fit: "+fontSize);
        //There was a bug where fitting text gets bigger to fully fit.
        //  We could easily make it a feature and just ignore the max size and always use fontSize.
        if(fontSize < maximumFontSize) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        } else {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, maximumFontSize);
            //Log.d("V2S1 colorname resizeFont", "Was attempting resize on already fitting text?");
        }
    }

    /**
     * For debug. Prints all name and hex pairs to log.
     */
    public void printArr(){
        //Note: Assumes each line has 2 String elements: Name, Hex
        for(int i = 0; i < this.colorNames.size(); ++i){
            String nameAndHex = this.colorNames.get(i)[NAME_INDEX] + ", "
                    + this.colorNames.get(i)[HEX_INDEX];
            Log.d("V2S1 colorname", "Color"+i+": "+nameAndHex);
        }
    }
}
