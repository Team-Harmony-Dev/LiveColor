package com.harmony.livecolor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import static android.content.Context.MODE_PRIVATE;


/**
 * Common Utils for accent changes
 *
 * @author Daniel
 *
 */
public class AccentUtils {



    /**
     * SET ACCENT
     * sets accent pref to custom for current mode
     *
     * @param context context of app
     * @param hex string of desired color
     *
     * @author Daniel
     *
     */
    public static void setAccent(Context context, String hex) {
        SharedPreferences mPrefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        if(NightModeUtils.isNightModeEnabled(context)){
            editor.putString("accentDark", hex);
        }else{
            editor.putString("accentLight", hex);
        }
        editor.apply();
    }

    /**
     * RESET TO DEFAULT
     * resets accent pref to default for current mode
     *
     * @param context context of app
     *
     * @author Daniel
     *
     */
    public static void resetAccent(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        if(NightModeUtils.isNightModeEnabled(context)){
            editor.putString("accentDark", "#FB6FEA");
        }else{
            editor.putString("accentLight", "#86357C");
        }
        editor.apply();
    }

    /**
     * RETURN ACCENT
     * returns accent pref for current mode
     *
     * @param context context of app
     *
     * @return hexString color of hex for current mode
     *
     * @author Daniel
     *
     */
    public static String getAccent(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
        if(NightModeUtils.isNightModeEnabled(context)){
            return mPrefs.getString("accentDark", "#aaffff");
        }else{
            return mPrefs.getString("accentLight", "#10246a");
        }
    }

    /**
     * RETURN OPPOSITE ACCENT
     * returns accent pref for opposite mode
     *
     * @param context context of app
     *
     * @return hexString color of hex for opposite mode
     *
     * @author Daniel
     *
     */
    public static String getOtherAccent(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
        if(!NightModeUtils.isNightModeEnabled(context)){
            return mPrefs.getString("accentDark", "#aaffff");
        }else{
            return mPrefs.getString("accentLight", "#10246a");
        }
    }




}
