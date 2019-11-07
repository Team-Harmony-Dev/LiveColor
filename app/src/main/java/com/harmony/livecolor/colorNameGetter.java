package com.harmony.livecolor;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


// Lets get some color names!
// Takes the color int, returns a string of the color name
// Retrieves names from https://github.com/meodai/color-names
// Some code based on a CSE 118 example. (nanorouz, Lecture 11)
// https://stackoverflow.com/a/31775646
//TODO clean this up and either make it a combined get and set thing, or return the proper value.
//TODO Is there any limit on how many calls we can send them? Click-dragging will spam.
//  Maybe just call this on release.
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
            Log.d("colorname", "json: "+json);
            //TODO is this middle step actually necessary?
            JSONArray jsonArray = new JSONArray(json.getString("colors"));
            Log.d("colorname", "jsonarray("+jsonArray.length()+"): "+jsonArray);
            json = jsonArray.getJSONObject(0);
            Log.d("colorname", "json now: "+json);
            colorName = json.getString("name");
            //return sb.toString();
            //return colorName;
        } catch (Exception e) {
            Log.w("DEBUG colorname", "Problem fetching color name.");
            e.printStackTrace();
            //return defaultColorName;
            colorName = defaultColorName;
        }
        //android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
        //TextView colorNameDisplay = getActivity().findViewById(R.id.colorName);
        //colorNameDisplay.setText(colorName);
        Log.d("colorname", "Found name: "+colorName);
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //MainActivity.textView.setText(s);
    }
}
