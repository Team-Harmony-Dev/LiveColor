package com.harmony.livecolor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CreditsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Whatever the heck";
    TextView mtv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        mtv = findViewById(R.id.helloWorldText);
        Button myB = findViewById(R.id.my_button);
        myB.setOnClickListener(this);
    }

    public void buttonOnClick(View view){
        mtv.setText(":)");
        Log.d(TAG, "This thing dont work");
    }

    @Override
    public void onClick(View v) {

    }
}