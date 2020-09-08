package com.harmony.livecolor;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import static android.content.Context.MODE_PRIVATE;


/**
 * Common Utils for dark mode
 *
 * @author Daniel
 * found in one of the better night mode guides
 */
public class NightModeUtils {

    private static final String NIGHT_MODE = "NIGHT_MODE";
    private static final String TOOGLE = "TOOGLE";


    /**
     * NIGHT MODE CHECK
     *
     * @param context context of app
     *
     * @return nigtModeEnabled boolean
     * @author Daniel
     * found in one of the better night mode guides
     */
    public static boolean isNightModeEnabled(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("MY_PREF", MODE_PRIVATE);
        return mPrefs.getBoolean(NIGHT_MODE, false);
    }

    /**
     * NIGHT MODE SET
     *
     * @param context context of app
     * @param isNightModeEnabled boolean of night mode check
     *
     * @author Daniel
     * found in one of the better night mode guides
     */
    public static void setIsNightModeEnabled(Context context, boolean isNightModeEnabled) {
        SharedPreferences mPrefs = context.getSharedPreferences("MY_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(NIGHT_MODE, isNightModeEnabled);
        editor.apply();
    }


    /**
     * TOGGLE SET
     *
     * @param context context of app
     * @param isToogleEnabled boolean of toggle
     *
     * @author Daniel
     * found in one of the better night mode guides
     */
    public static void setIsToogleEnabled(Context context, boolean isToogleEnabled) {
        SharedPreferences mPrefs = context.getSharedPreferences("MY_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(TOOGLE, isToogleEnabled);
        editor.apply();
    }

    /**
     * TOGGLE CHECK
     *
     * @param context context of app
     *
     * @return isToggleEnabled boolean
     * @author Daniel
     * found in one of the better night mode guides
     */
    public static boolean isToogleEnabled(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("MY_PREF", MODE_PRIVATE);
        return mPrefs.getBoolean(TOOGLE, false);
    }


    /**
     *  DARK MODE CHECK ACTUAL
     *  checks the multiple places necessary for dark mode
     *
     * @param activity
     *
     * @return isDarkMode boolean
     *
     * @author Daniel
     * found in one of the better night mode guides
     */
    public static boolean isDarkMode(Activity activity) {
        return (activity.getResources().getConfiguration()
                .uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
