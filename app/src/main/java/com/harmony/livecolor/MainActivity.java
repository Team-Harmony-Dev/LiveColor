package com.harmony.livecolor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.harmony.livecolor.dummy.DummyContent;

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
    //colorNameGetter changes the text in this view
    static TextView colorNameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Lifecycles", "onCreate: MainActivity created");

        BottomNavigationView navigation = findViewById(R.id.main_navi);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new ColorPickerFragment());

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        SharedPreferences myPrefs;
        myPrefs = getSharedPreferences("pref", Context.MODE_PRIVATE);

        colorNameView = findViewById(R.id.colorName);

        checkAndRequestPermissions();
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
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d("Lifecycles", "onPause: MainActivity paused");
        super.onPause();
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
    }

    @Override
    protected void onRestart() {
        Log.d("Lifecycles", "onRestart: MainActivity restarted");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d("Lifecycles", "onDestroy: MainActivity destroyed");
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
}
