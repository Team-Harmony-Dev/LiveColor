package com.harmony.livecolor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

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
//TODO simplify using this. Currently it needs you to make sure the
//  textView colorNameView is set properly because apparently onCreate can't.
//Example of use:
//MainActivity.colorNameView = getActivity().findViewById(R.id.colorName);
//colorNameGetter tmp = new colorNameGetter();
//tmp.execute(pixel);
//TODO maybe return the value
public class colorNameGetter extends AsyncTask<Integer, Void, String> {
    @Override
    protected String doInBackground(Integer... colorButArray){
        final String baseColorNameUrl = "https://api.color.pizza/v1/";
        final String defaultColorName = "Your Color";
        String colorName = "Error";
        try {
            int color = colorButArray[0];
            //TODO maybe stick this in a function since it's shared with the hex text display.
            String hex = String.format("%06X", (0xFFFFFF & color)); //get the hex representation minus the first ff
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
    }
}
