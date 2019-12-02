package com.harmony.livecolor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
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
//TODO currently haven't tested doing multiple calls at the same time. Some static stuff should but might not support that.
public class ColorNameGetter extends AsyncTask<Integer, Void, String> {

    //TODO If this works, remove some commented code
    //Font size is in sp.
    public static void updateViewWithColorName(TextView view, int pixelColor, double maximumViewWidthPercentOfScreen, float maximumFontSize){
        MainActivity.colorNameView = view;

        originalTextSize = maximumFontSize;

        viewWidthPercentOfScreen = maximumViewWidthPercentOfScreen;
        //activityViewIsIn = activityThatYourViewIsIn;
        ColorNameGetter tmp = new ColorNameGetter();
        tmp.execute(pixelColor);
    }


    private final static String loadingText = ". . .";

    @Override
    protected void onPreExecute(){
        //TODO We do thise here because it's async and might not happen in time for setAppropriatelySizedText()
        //  There must be a better way, properly wait for it.
        MainActivity.colorNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, originalTextSize);
        //TODO this is always slightly ugly, but prevents the big ugly of squishing then unsquishing.
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

    //TODO Actually it looks like it's already been done.
    //https://stackoverflow.com/a/31399534
    //And maybe https://stackoverflow.com/questions/2617266/how-to-adjust-text-font-size-to-fit-textview

    //TODO handle weight better, probably don't need it as a parameter?
    //TODO make this function work without calling the whole class? Because sometimes we may just store the name, no need for a full api call.
    public static void setAppropriatelySizedText(TextView view, String colorName, double maximumViewWidthPercentOfScreen, float maximumFontSize) {
        //Do this first so we don't trigger two changes.
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, maximumFontSize);
        //TODO The following statements are async, so there's no guarantee we're over two lines when we check
        //TextWatcher's onTextChanged() may fix the problem?
        //addWatcher(view, colorName, maximumViewWidthPercentOfScreen, maximumFontSize);
        //TODO I don't actually need to pass the colorName to helper, right?
        view.setText(colorName);
        //TODO remove watcher when done. From inside watcher call?

        //If the watcher isn't working, I suppose we can just use a constant delay?
        //Looks like the answer is no, that must be sleeping the textview as well
        //https://stackoverflow.com/a/24104332
        /*
        try
        {
            Thread.sleep(500);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        */

        //Watcher isn't working any better than this.
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

            //TODO don't use hardcoded percent, take as a parameter or pull weights or something
            //  Using .6 for 60% doesn't quite work? Error in my math or some sort of padding?
            maximumViewWidthPercentOfScreen = maximumViewWidthPercentOfScreen - 0.05;
            double maximumTextWidth = maximumViewWidthPercentOfScreen * screenWidth;
            double reduceToThisPercent = maximumTextWidth / textWidth;
            Log.d("S3US5", "w="+textWidth+" sw="+screenWidth+
                    " maxPercent="+maximumViewWidthPercentOfScreen+" mtw="+maximumTextWidth
                    +" rp="+reduceToThisPercent);
            //Update font size to be smaller
            fontSize = (int) (fontSize*(reduceToThisPercent));
            //There's a bug where fitting text gets bigger to fully fit for some reason.
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
    /*
    //TODO we actually don't need screen size as a parameter
    protected static void recursiveTextFix(TextView view, String colorName, double maximumViewWidthPercentOfScreen, float maximumFontSize){
        if(view.getLineCount() > 1){
            //TODO look at old code for grabbing font size from a view
            //Get current font size
            //https://stackoverflow.com/a/14078085
            //https://stackoverflow.com/a/10641257
            DisplayMetrics metrics;
            metrics = MainActivity.colorNameView.getContext().getResources().getDisplayMetrics();
            float fontSize = MainActivity.colorNameView.getTextSize()/metrics.density;
            fontSize = fontSize - 1;
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        } else if (view.getLineCount() == 0){
            //We could maybe recurse here until it changes ? Ugly.
            //Looks like this crashes the program. Needs an actual way to wait.
            //recursiveTextFix(view, colorName, maximumViewWidthPercentOfScreen, maximumFontSize);
        } else {
            //I suppose we could remove the watcher but I think it just overrides anyway?
            //Maybe removing it would be good to prevent weird changes if anyone else uses it.
            //TODO
        }
    }


    //https://stackoverflow.com/a/8543479
    protected static void addWatcher(final TextView view, final String colorName, final double maximumViewWidthPercentOfScreen, final float maximumFontSize){
        view.addTextChangedListener(new TextWatcher() {
            //This is the one we want I believe
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d("S3US5", "Calling helper from onTextChanged");
                //setAppropriatelySizedTextHelper(view, colorName, maximumViewWidthPercentOfScreen, maximumFontSize);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("S3US5", "Calling helper from afterTextChanged");
                //TODO so basically this still isn't waiting for the text to finish changing, it says line # is 0.
                recursiveTextFix(view, colorName, maximumViewWidthPercentOfScreen, maximumFontSize);

                // TODO Auto-generated method stub
            }
        });
    }
    */

}
