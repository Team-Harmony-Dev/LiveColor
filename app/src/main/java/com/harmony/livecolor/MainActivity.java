package com.harmony.livecolor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.view.TextureView;

// MAIN ACTIVITY - COLOR PICKER
// [See the designs on our marvel for creating and implementing UI]
// -- Display camera and gallery view (requires permissions for both)
// -- Switch between the two with a radio button for each (styled like Instagram)
// -- Color picking feature (we can probably use a bitmap to obtain the pixel and then grab the data)

public class MainActivity extends AppCompatActivity {

    //random number to help differentiate between permissions for different contexts
    private int REQUEST_CODE_PERMISSIONS = 101;
    //required permissions for this activity
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            "android.permission.CAMERA",
            "android.permission.READ_EXTERNAL_STORAGE" };
    TextureView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView = findViewById(R.id.textureView);
    }

    //starts the camera once all permissions are granted,
    //and displays the current capture on the textureView
    private void startCamera() {
    }
}
