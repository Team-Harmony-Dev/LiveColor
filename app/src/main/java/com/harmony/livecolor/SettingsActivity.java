package com.harmony.livecolor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onClickCredits(View view) {
        Intent intent = new Intent(view.getContext(), CreditsActivity.class);
        startActivity(intent);
    }
    /**
     * CALL FOR GETTING THE LINK TO THE LIVECOLOR GITHUB
     * gets the LiveColor GitHub link
     * @author Shealtiel
     * @return the uri of the LiveColor Github
     */
    public void onClickLiveColorGitHub(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TheBrows/LiveColor"));
        startActivity(intent);
    }
}