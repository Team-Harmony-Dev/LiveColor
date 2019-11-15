package com.harmony.livecolor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
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
// Takes the color int, returns a string of the color name
// Retrieves names from https://github.com/meodai/color-names
// Some code based on a CSE 118 example. (nanorouz, Lecture 11)
// Relies on ColorPickerFragment.colorToHex()
//TODO simplify using this. Currently it needs you to make sure the
//  textView colorNameView is set properly because apparently onCreate can't.
//Example of use:
//MainActivity.colorNameView = getActivity().findViewById(R.id.colorName);
//colorNameGetter tmp = new colorNameGetter();
//tmp.execute(pixel);
//TODO maybe return the value in some way? Save it somewhere?
public class colorNameGetter extends AsyncTask<Integer, Void, String> {
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
            //TODO is this middle step actually necessary?
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

        MainActivity.colorNameView.setText(colorName);
        /*
        try {
            MainActivity.colorNameView.setText(colorName);
        } catch(Exception e) {

        }
        try {
            MainActivity.editedColorNameView.setText(colorName);
        } catch(Exception e) {

        }
        */
        /*
        for(int i = 0; i < numberOfViews; ++i){
            if(textViewsToEditToColorNameShouldUpdate[i]) {
                try {
                    textViewsToEditToColorName[i].setText(colorName);
                } catch (Exception e) {
                    Log.w("S3US5", "Something wrong in updating textview "+i+": "+e);
                }
            }
        }
        */
        //TODO
        //Prevent the text from taking up multiple lines by reducing font size as required
        //MainActivity.colorNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, originalTextSize);
        //setAppropriatelySizedText(colorName);
    }

    //TODO
    //The color name being gotten is placed in each one of these textViews, and their font is resized to fit on a single line.
    /*
    private final TextView[] textViewsToEditToColorName = new TextView[]{
            MainActivity.colorNameView,
            MainActivity.editedColorNameView
    };
    private final int numberOfViews = 2;
    private boolean[] textViewsToEditToColorNameShouldUpdate = new boolean[]{
            true,
            true
    };
    */
    //TODO If this works, remove those comments above and remove that var from MainActivity.java
    public static void updateViewWithColorName(TextView view, int pixelColor){
        MainActivity.colorNameView = view;
        //textViewsToEditToColorNameShouldUpdate[0] = true;
        colorNameGetter tmp = new colorNameGetter();
        tmp.execute(pixelColor);
    }
    //TODO maybe grab the size instead of hardcoding this
    final float originalTextSize = 30;
    //TODO maybe grab the weight instead of hardcoding this
    final double nameDisplaySpacePercent = 0.60;
    protected void setAppropriatelySizedText(String colorName){

        MainActivity.colorNameView.setText(colorName);
        //If the text takes more than one line, lets shrink the text size.
        //First lets get the width of the text
        // https://stackoverflow.com/a/37930140
        MainActivity.colorNameView.measure(0, 0);
        int textWidth = MainActivity.colorNameView.getMeasuredWidth();
        //And now the width of the screen
        //https://stackoverflow.com/a/31377616
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        //And the font size
        //https://stackoverflow.com/a/14078085
        //https://stackoverflow.com/a/10641257
        DisplayMetrics metrics;
        metrics = MainActivity.colorNameView.getContext().getResources().getDisplayMetrics();
        float previousSize = MainActivity.colorNameView.getTextSize()/metrics.density;
        Log.d("S3US5", "Found text width of "+textWidth
                +" with font size of " + previousSize
                +" and height width of "+screenWidth
                +" and we're given "+nameDisplaySpacePercent+" of the screen space"
                +"("+screenWidth*nameDisplaySpacePercent+")");
        //TODO This doesn't seem to work, width takes the new line into account. Check height?
        //  Could try adding a new line, testing height difference, using that to estimate the
        //  height of a single line
        if(textWidth > screenWidth*nameDisplaySpacePercent){
            Log.d("S3US5", "Ran over a line, changing fontsize");
            MainActivity.colorNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP,previousSize - 1);
            setAppropriatelySizedText(colorName);
        }

    }
}
