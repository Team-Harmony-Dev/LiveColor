package com.harmony.livecolor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

// MAIN ACTIVITY - COLOR PICKER
// [See the designs on our marvel for creating and implementing UI]
// -- Display camera and gallery view (requires permissions for both)
// -- Switch between the two with a radio button for each (styled like Instagram)
// -- Color picking feature (we can probably use a bitmap to obtain the pixel and then grab the data)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //TODO how do we treat different actions?
    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();
        //TODO use that to get color of an image.
        //TODO Maybe zoom in so that they can select a single color, or suggest several nearby colors.
        ((TextView) findViewById(R.id.helloWorldBox)).setText("Detected press at x="+x+" y="+y);
        return true;
    }
}
