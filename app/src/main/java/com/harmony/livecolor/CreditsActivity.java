package com.harmony.livecolor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CreditsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }


    public void onClickGitHubLink(View view) {
        String name = view.getTag().toString();
        Intent intent;
        if (name.equals("daniel")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DanielLuft-Martinez"));
        } else if (name.equals("paige")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TheBrows"));
        } else if (name.equals("gabby")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/glindsey22"));
        } else if (name.equals("shealtiel")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tielm1997"));
        } else if (name.equals("dustin")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DustinSeltz"));
        } else {
            // this else clause should never run. Without this there is an error in the startActivity
            // below. If there is an issue with tags this will run and link to the LiveColor GitHub
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TheBrows/LiveColor"));
        }
        startActivity(intent);
    }

    public void onClickLinkedInLink(View view) {
        String name = view.getTag().toString();
        Intent intent;
        if (name.equals("daniel")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/daniel-luft-martinez/"));
        } else if (name.equals("paige")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/paige-riola/"));
        } else if (name.equals("gabby")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/gabriella-lindsey-8493951b0/"));
        } else if (name.equals("shealtiel")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/shealtiel-mulder-6329641b0/"));
        } else if (name.equals("dustin")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/dustin-s-7938a394/"));
        } else {
            // this else clause should never run. Without this there is an error in the startActivity
            // below. If there is an issue with tags this will run and link to the LiveColor GitHub
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TheBrows/LiveColor"));
        }
        startActivity(intent);
    }
}