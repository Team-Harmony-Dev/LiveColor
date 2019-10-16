package com.harmony.livecolor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

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
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            "android.permission.CAMERA",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.main_navi);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new ColorPickerFragment());
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
}
