package com.harmony.livecolor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;


/**
 * SETTINGS ACTIVITY
 * loads up the settings activity, intents etc
 *
 * @author Daniel
 */
public class SettingsActivity extends AppCompatActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();


        //TODO: Move SharedPrefs to outside of the method
        // pass colors through MyColor and intent (talk with Gabby)


    }


    /**
     * ALL CAPS METHOD DESCRIPTION
     * lowercase description of the means
     * @param param breif description of param
     * @return description as necessary of the return value
     *
     * @author {someone}, Daniel
     * changed as part of the onCreate inner to outer method refactor
     */


}
