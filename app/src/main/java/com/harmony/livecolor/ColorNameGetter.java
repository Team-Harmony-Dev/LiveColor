package com.harmony.livecolor;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



// Lets get some color names!
// Takes the color int, "returns" a string of the color name by placing it in the TextView
// Automatically reduces the font size of the TextView to ensure that the name fits on one line.
//Example of use:
//ColorNameGetter.updateViewWithColorName(viewToUpdateColorName, pixel, viewWidthPercentOfScreen);
//
// Retrieves names from https://github.com/meodai/color-names
// Some code based on a CSE 118 example. (nanorouz, Lecture 11)
// Relies on ColorPickerFragment.colorToHex()
//Note: currently haven't tested doing multiple calls at the same time. Some static stuff should but might not support that.
public class ColorNameGetter extends AsyncTask<Integer, Void, String> {

    //Font size is in sp.
    public static void updateViewWithColorName(TextView view, int pixelColor, double maximumViewWidthPercentOfScreen, float maximumFontSize){
        MainActivity.colorNameView = view;

        originalTextSize = maximumFontSize;

        viewWidthPercentOfScreen = maximumViewWidthPercentOfScreen;
        ColorNameGetter tmp = new ColorNameGetter();
        tmp.execute(pixelColor);
    }


    private final static String loadingText = ". . .";

    @Override
    protected void onPreExecute(){
        //We do this here because it's async and might not happen in time for setAppropriatelySizedText()
        //  There must be a better way to properly wait for it.
        MainActivity.colorNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, originalTextSize);
        //This is always slightly ugly, but prevents the big ugly of squishing then unsquishing.
        MainActivity.colorNameView.setText(loadingText);
    }

    @Override
    protected String doInBackground(Integer... colorButArray){
        final String baseColorNameUrl = "https://api.color.pizza/v1/";
        final String defaultColorName = "Your Color";
        String colorName = "Error";
        try {
            int color = colorButArray[0];
            String hex = ColorPickerFragment.colorToHex(color);
            String colorNameUrl = baseColorNameUrl + hex;


            URL url = new URL(colorNameUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(500);//500 is arbitary, should name
            httpURLConnection.setReadTimeout(500);
            httpURLConnection.connect();
            InputStream is = httpURLConnection.getInputStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while((line = bf.readLine()) != null){
                sb.append(line);
            }
            bf.close();
            is.close();
            // https://stackoverflow.com/a/26358942
            JSONObject json = new JSONObject(sb.toString());
            Log.d("S3US5 colorname", "json: "+json);
            JSONArray jsonArray = new JSONArray(json.getString("colors"));
            Log.d("S3US5 colorname", "jsonarray("+jsonArray.length()+"): "+jsonArray);
            json = jsonArray.getJSONObject(0);
            Log.d("S3US5 colorname", "json now: "+json);
            colorName = json.getString("name");
        } catch (Exception e) {
            Log.w("DEBUG S3US5 colorname", "Problem fetching color name.");
            e.printStackTrace();
            colorName = defaultColorName;
        }
        Log.d("S3US5 colorname", "Found name: "+colorName);
        return colorName;
    }

    @Override
    protected void onPostExecute(String colorName) {
        super.onPostExecute(colorName);
        try {
            setAppropriatelySizedText(MainActivity.colorNameView, colorName, viewWidthPercentOfScreen, originalTextSize);
        } catch (Exception e) {
            Log.e("S3US5", "Something wrong in updating color name textview: "+e);
        }
    }

    //Starting size, in sp. This is essentially the maximum size, aka what it'll be set to if it
    //  can already fit on one line. It'll be reduced as needed.
    private static float originalTextSize;
    private static double viewWidthPercentOfScreen;

    //TODO Actually it looks like it's already been done. Maybe this'll suit our needs?
    //https://stackoverflow.com/a/31399534
    //And maybe https://stackoverflow.com/questions/2617266/how-to-adjust-text-font-size-to-fit-textview

    protected static void setAppropriatelySizedText(TextView view, String colorName, double maximumViewWidthPercentOfScreen, float maximumFontSize) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, maximumFontSize);
        //Note: I don't actually need to pass the colorName to helper anymore
        view.setText(colorName);

        //Attempt at using a text Watcher didn't work any better than this.
        setAppropriatelySizedTextHelper(view, colorName, maximumViewWidthPercentOfScreen, maximumFontSize);
    }
    protected static void setAppropriatelySizedTextHelper(TextView view, String colorName, double maximumViewWidthPercentOfScreen, float maximumFontSize){
        // The idea is to detect how much we need to reduce the font size by,
        //   and then do that in one go
        float fontSize = maximumFontSize;
        if( view.getLineCount() > 1){
            Log.d("S3US5", "Ran over a line, changing fontsize");
            Log.d("S3US5", "# lines is currently: "+view.getLineCount());

            //First lets get the width of the text
            // https://stackoverflow.com/a/37930140
            view.measure(0, 0);
            int textWidth = view.getMeasuredWidth();
            //And now the width of the screen
            //https://stackoverflow.com/a/31377616
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

            Log.d("S3US5", "w="+textWidth+" sw="+screenWidth);

            //There's some sort of minor padding so I need to reduce it slightly
            maximumViewWidthPercentOfScreen = maximumViewWidthPercentOfScreen - 0.05;
            double maximumTextWidth = maximumViewWidthPercentOfScreen * screenWidth;
            double reduceToThisPercent = maximumTextWidth / textWidth;
            Log.d("S3US5", "w="+textWidth+" sw="+screenWidth+
                    " maxPercent="+maximumViewWidthPercentOfScreen+" mtw="+maximumTextWidth
                    +" rp="+reduceToThisPercent);
            //Update font size to be smaller
            fontSize = (int) (fontSize*(reduceToThisPercent));
            //There was a bug where fitting text gets bigger to fully fit for some reason.
            //  We could easily make it a feature and just ignore the max size and always resize to fit.
            //  Just remove this if and the line count if.
            if(fontSize < maximumFontSize) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            } else {
                Log.d("S3US5 resizeFont", "Was attempting resize on already fitting text?");
            }
        } else {
            Log.d("S3US5", "# lines is currently: "+view.getLineCount());
        }
    }
}
