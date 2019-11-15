package com.harmony.livecolor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
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
// Takes the color int, returns a string of the color name
// Retrieves names from https://github.com/meodai/color-names
// Some code based on a CSE 118 example. (nanorouz, Lecture 11)
// Relies on ColorPickerFragment.colorToHex()
//TODO simplify using this. Currently it needs you to make sure the
//  textView colorNameView is set properly because apparently onCreate can't.
//Example of use:
//colorNameGetter.updateViewWithColorName(getActivity(), viewToUpdateColorName, pixel);
//TODO maybe return the value in some way? Save it somewhere other than the textView?
//TODO doing some weird stuff with static? Currently assumes only one call at a time?
public class colorNameGetter extends AsyncTask<Integer, Void, String> {

    //TODO If this works, remove some commented code
    public static void updateViewWithColorName(Activity activityThatYourViewIsIn, TextView view, int pixelColor){
        MainActivity.colorNameView = view;

        //Get the font size
        //https://stackoverflow.com/a/14078085
        //https://stackoverflow.com/a/10641257
        DisplayMetrics metrics;
        metrics = MainActivity.colorNameView.getContext().getResources().getDisplayMetrics();
        if(originalTextSize == -1) {
            originalTextSize = MainActivity.colorNameView.getTextSize() / metrics.density;
        }
        activityViewIsIn = activityThatYourViewIsIn;
        //textViewsToEditToColorNameShouldUpdate[0] = true;
        colorNameGetter tmp = new colorNameGetter();
        tmp.execute(pixelColor);
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
            MainActivity.colorNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, originalTextSize);
            setAppropriatelySizedText(colorName);
        } catch (Exception e) {
            Log.e("S3US5", "Something wrong in updating color name textview: "+e);
        }
    }

    //TODO currently it assumes all textViews have the same starting size? Or just doesn't work
    //  at all since it doesn't reset afterwards.
    //Starting size, in sp
    private static float originalTextSize = -1;
    private static Activity activityViewIsIn;

    //TODO Actually it looks like it's already been done.
    //https://stackoverflow.com/questions/2617266/how-to-adjust-text-font-size-to-fit-textview

    //TODO store the original text size and somehow link it to the view? User shouldn't have to manage it?
    //  I suppose I could make the text bigger to fit (and forget original size). Maybe with some threshold.
    protected void setAppropriatelySizedText(String colorName){
        //The view we're sticking the color name in
        TextView view = MainActivity.colorNameView;
        view.setText(colorName);
        float fontSize = originalTextSize;
        if(view.getLineCount() > 1){
            Log.d("S3US5", "Ran over a line, changing fontsize");
            Log.d("S3US5", "# lines is currently: "+view.getLineCount());

            //First lets get the width of the text
            // https://stackoverflow.com/a/37930140
            MainActivity.colorNameView.measure(0, 0);
            int textWidth = MainActivity.colorNameView.getMeasuredWidth();
            //And now the width of the screen
            //https://stackoverflow.com/a/31377616
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

            Log.d("S3US5", "w="+textWidth+" sw="+screenWidth);

            //TODO don't use hardcoded percent, take as a parameter or pull weights or something
            double PERCENT = 0.6;
            double maximumTextWidth = PERCENT * screenWidth;
            double reduceToThisPercent = maximumTextWidth / textWidth;
            Log.d("S3US5", "w="+textWidth+" sw="+screenWidth+" mtw="+maximumTextWidth
                    +"rp="+reduceToThisPercent);
            //Update font size to be smaller
            //TODO font size is not linear?
            fontSize = (int) (fontSize*reduceToThisPercent);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            view.setText(colorName);
        }
        /*
        //The idea is to decrease the font size by 1 until it fits on one line.
        //The problem is the textView doesn't refresh instantly so the loop ends.
        while(view.getLineCount() > 1){
            Log.d("S3US5", "Ran over a line, changing fontsize");
            Log.d("S3US5", "# lines is currently: "+view.getLineCount());

            //Update font size to be smaller
            fontSize = fontSize - 1;
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            view.setText(colorName);

            //TODO maybe don't try all this junk, just check if 2 lines and reduce font size based on ???
            //  Maybe based on proportions vs line #?
            //Needs to do a new layout pass?
            // https://stackoverflow.com/questions/12037377/how-to-get-number-of-lines-of-textview
            //https://developer.android.com/guide/topics/ui/how-android-draws

            //https://stackoverflow.com/questions/5991968/how-to-force-an-entire-layout-view-refresh
            //error: non-static method getWindow() cannot be referenced from a static context
            //MainActivity.getWindow().getDecorView().findViewById(android.R.id.content).invalidate();

            //Doesn't work.
            //activityViewIsIn.getWindow().getDecorView().findViewById(android.R.id.content).invalidate();

            //
            //need to use activity and view, static issues?
            //ViewGroup vg = findViewById (R.id.mainLayout);
            //vg.invalidate();

            //TODO if this works remove the activity stuff
            //Seems to be async so we exit the loop
            //MainActivity.view.invalidate();

            //This is async or something? We're not waiting, we're exiting the loop
            //Also it wipes other stuff.
            //activityViewIsIn.recreate();
        }
        */
        Log.d("S3US5", "# lines is now: "+view.getLineCount());
        //Looks like there's a library function that does roughly what I want.
        //If I remove all this should also remove imports
        /*
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
        */
    }
    /*
    //https://stackoverflow.com/a/42851039
    private Activity mContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = (Activity) context;
    }
    */
}
