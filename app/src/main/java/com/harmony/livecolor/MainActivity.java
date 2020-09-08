package com.harmony.livecolor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.harmony.livecolor.dummy.DummyContent;

import java.io.InputStream;

// MAIN ACTIVITY - COLOR PICKER
// [See the designs on our marvel for creating and implementing UI]
// -- Display camera and gallery view (requires permissions for both)
// -- Switch between the two with a radio button for each (styled like Instagram)
// -- Color picking feature (we can probably use a bitmap to obtain the pixel and then grab the data)

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        ColorPickerFragment.OnFragmentInteractionListener,
        SavedColorsFragment.OnListFragmentInteractionListener,
        PalettesFragment.OnListFragmentInteractionListener {

    //random number to help differentiate between permissions for different contexts
    private int REQUEST_CODE_PERMISSIONS = 101;
    //required permissions for this activity
    //If you change this, also change checkAndRequestPermissions()
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            "android.permission.CAMERA",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.INTERNET"};
    //colorNameGetter changes the text in these views
    //Name on the main picker page
    static TextView colorNameView;
    boolean isEnabledCotd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (NightModeUtils.isNightModeEnabled(MainActivity.this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Log.d("DARK", "dark mode should be on");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Log.d("DARK", "dark mode should be off");
        }

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Lifecycles", "onCreate: MainActivity created");

        ColorDatabase db = new ColorDatabase(this);

        BottomNavigationView navigation = findViewById(R.id.main_navi);
        navigation.setOnNavigationItemSelectedListener(this);


        // dark mode check
        int currentNightMode =  getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // dark mode changes
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme

                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme

                break;
        }

        // handles customized accent
        customAccent(findViewById(R.id.container));

        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        SharedPreferences myPrefs;
        myPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        // is cotd enabled?
        if(myPrefs.contains("dialogCotd")){
           isEnabledCotd = myPrefs.getBoolean("dialogCotd", true);
        }else{
            isEnabledCotd = true;
            myPrefs.edit().putBoolean("dialogCotd", true);
        }

        onLoadFragment();

        colorNameView = findViewById(R.id.colorName);

        checkAndRequestPermissions();

        //Load color names from CSV
        //TODO streamline
        InputStream inputStream = getResources().openRawResource(R.raw.colornames);
        ColorNameGetterCSV colors = new ColorNameGetterCSV(inputStream);
        colors.readColors();
        //String testInit = colors.searchForName("#100000");
        //Log.d("V2S1 colorname", "init: "+testInit);
    }

    //checks if given fragment exists, and loads it if possible
    private boolean loadFragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout,fragment).commit();
            return true;
        }
        return false;
    }


    /**
     * handles fragments when onCreate is called in a onrecreate context
     * stay on same frag when preforming actions that will recreate the app
     * like changes to the theme
     *
     * @author Daniel
     * efficacey of using prefs?
     */
    private void onLoadFragment(){
        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        Log.d("DARK", "Frag pref string: " +preferences.getString("frag", "none"));
        if (preferences.getString("fragStatic", "none") == "true"){
            Fragment fragment = null;
            String fragString = preferences.getString("frag", "none");
            switch(fragString) {
                case "ColorPickerFragment":
                    fragment = ColorPickerFragment.newInstance();
                    break;
                case "SavedColorsFragment":
                    fragment = SavedColorsFragment.newInstance();
                    break;
                case "PalettesFragment":
                    fragment = PalettesFragment.newInstance();
                    break;
                case "SettingsFragment":
                    fragment = SettingsStartFragment.newInstance();
                    break;
            }

            preferences.edit().putString("fragStatic", "false").commit();

            loadFragment(fragment);
        }else{
            loadFragment(new ColorPickerFragment());
        }
    }

    //switches between fragments for Main Activity
    //based on what the user has tapped on
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch(menuItem.getItemId()) {
            case R.id.navigation_color_picker:
                fragment = ColorPickerFragment.newInstance();
                break;
            case R.id.navigation_saved_colors:
                fragment = SavedColorsFragment.newInstance();
                break;
            case R.id.navigation_palettes:
                fragment = PalettesFragment.newInstance();
                break;
            case R.id.navigation_settings:
                fragment = SettingsStartFragment.newInstance();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    //Overwritten Lifecycle methods for debugging purposes
    @Override
    protected void onStart() {


        Log.d("Lifecycles", "onStart: MainActivity started");

        if(isEnabledCotd) {
            ColorOTDayDialog cotdDialog = new ColorOTDayDialog(MainActivity.this);
            cotdDialog.showColorOTD();
        }
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d("Lifecycles", "onPause: MainActivity paused");
        // clears shared prefs. on app exit only, not updating image :(
        super.onPause();
        String currFrag = String.valueOf(getSupportFragmentManager().findFragmentById(R.id.frameLayout)).substring(0,String.valueOf(getSupportFragmentManager().findFragmentById(R.id.frameLayout)).indexOf("{"));
        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        preferences.edit().putString("frag", currFrag).commit();
        Log.d("DEBUG", "Frag pref string: " +preferences.getString("frag", "none"));
    }

    @Override
    protected void onResume() {
        Log.d("Lifecycles", "onResume: MainActivity resumed");
        super.onResume();



    }

    @Override
    protected void onStop() {
        Log.d("Lifecycles", "onStop: MainActivity stopped");
        super.onStop();
        // clears shared prefs. on app exit only, not updating image :(
        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        preferences.edit().remove("color").commit();
        SharedPreferences prefs1 = getSharedPreferences("prefs", MODE_PRIVATE);
    }

    @Override
    protected void onRestart() {
        Log.d("Lifecycles", "onRestart: MainActivity restarted");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d("Lifecycles", "onDestroy: MainActivity destroyed");
        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        super.onDestroy();
    }

    void checkAndRequestPermissions(){
        //Not using a loop because Manifest.permission.MYVARNAME doesn't work.
        //Prompt the users for any permissions not given
        // https://developer.android.com/training/permissions/requesting
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("perms", "Missing permission, prompting");
            //Ask for permissions (no explanation given)
            //https://android--code.blogspot.com/2017/08/android-request-multiple-permissions.html
            ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
            );
        } else {
            Log.d("perms", "All necessary permissions granted");
        }
    }


    /**
     * CUSTOM ACCENT HANDLER
     * changes colors of specific activity/fragment
     *
     * @param view view of root container
     *
     * @author Daniel
     * takes a bit of elbow grease, and there maybe a better way to do this, but it works
     */
    public void customAccent(View view){
        BottomNavigationView nav = view.findViewById(R.id.main_navi);
        ColorStateList tintList = nav.getItemIconTintList();
        Log.d("DEBUG", "color tint list: " + tintList.toString());
        int[][] states = new int[][] {

                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_checked},  // checked
                new int[] { android.R.attr.state_enabled} // enabled
        };

        int[] colors = new int[] {
                ContextCompat.getColor(view.getContext(), R.color.colorIconPrimary),
                ContextCompat.getColor(view.getContext(), R.color.colorIconPrimary),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                ContextCompat.getColor(view.getContext(), R.color.colorIconPrimary)
        };

        ColorStateList myList = new ColorStateList(states, colors);
        nav.setItemIconTintList(myList);
    }

}