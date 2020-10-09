package com.harmony.livecolor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class CreditsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
    }

    /**
     * CALL FOR GETTING THE LINK TO A TEAM MEMBER'S GITHUB
     * gets a specific team member's GitHub link
     * @author Shealtiel
     * @dependancy "android::tag" method depends on the button having a tag attribute that contains the
     *          specific team member's name. Without this the method will return a link to the
     *          LiveColor GitHub not the team member's specific page like intended.
     * @param view button passed in as a view
     * @return the uri of the specified team member's GitHub
     */
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
        } else if (name.equals("melanie")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mwong775"));
        } else if (name.equals("andrew")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/andrewtgg"));
        } else {
            // this else clause should never run. Without this there is an error in the startActivity
            // below. If there is an issue with tags this will run and link to the LiveColor GitHub
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TheBrows/LiveColor"));
        }
        startActivity(intent);
    }

    /**
     * CALL FOR GETTING THE LINK TO A TEAM MEMBER'S LINKEDIN
     * gets a specific team member's LinkedIn link
     * @author Shealtiel
     * @dependancy "android::tag" method depends on the button having a tag attribute that contains the
     *          specific team member's name. Without this the method will return a link to the
     *          LiveColor GitHub not the team member's specific LinkedIn page like intended.
     * @param view button passed in as a view
     * @return the uri of the specified team member's LinkedIn
     */
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
        } else if (name.equals("melanie")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/mwong775/"));
        } else if (name.equals("andrew")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/andrewtgg/"));
        } else {
            // this else clause should never run. Without this there is an error in the startActivity
            // below. If there is an issue with tags this will run and link to the LiveColor GitHub
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TheBrows/LiveColor"));
        }
        startActivity(intent);
    }

    public void onClickCardColor(View view) {
        String name = view.getTag().toString();
        String colorName;
        String colorHex;
        if (name.equals("daniel")) {
            colorName = "Jedi Knight";
            colorHex = "#041108";
        } else if (name.equals("paige")) {
            colorName = "Platonic Blue";
            colorHex = "#87C7FF";
        } else if (name.equals("gabby")) {
            colorName = "Ireland Green";
            colorHex = "#006C2E";
        } else if (name.equals("shealtiel")) {
            colorName = "Aggressive Baby Blue";
            colorHex = "#6FFFFF";
        } else if (name.equals("dustin")) {
            colorName = "Poppy Pompadour";
            colorHex = "#6B3FA0";
        } else if (name.equals("melanie")) {
            colorName = "Blue Nebula";
            colorHex = "#1199FF";
        } else if (name.equals("andrew")) {
            colorName = "Wasabi";
            colorHex = "#AFD77F";
        } else {
            // this else clause should never run. Without this there is an error in the startActivity
            // below. If there is an issue with tags this will
            colorName = "White";
            colorHex = "#FFFFFF";
        }
        Intent intent = new Intent(this, ColorInfoActivity.class);
        intent.putExtra("name", colorName);
        intent.putExtra("hex", colorHex);
        startActivity(intent);
    }
}