package com.harmony.livecolor;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static androidx.core.content.ContextCompat.getColorStateList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ColorPickerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ColorPickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorPickerFragment extends Fragment {

    private boolean isButtonClicked = false;
    int colorT;
    String colorNamePass;

    TextView editName, editHex, editRgb, editHsv;
    ColorDatabase colorDB;

    private OnFragmentInteractionListener mListener;
    private static final int CAMERA_OR_GALLERY = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private String imagePath = null;
    private ImageView pickingImage;
    private Uri imageUri;

    public ColorPickerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ColorPickerFragment newInstance() {
        ColorPickerFragment fragment = new ColorPickerFragment();
        //Bundle args = new Bundle();
        //args can be bundled and sent through here if needed
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //if arguments are needed ever, use this to set them to static values in the class
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Lifecycles", "onCreateView: ColorPickerFragment created");

        //Set title on action bar to match current fragment
        getActivity().setTitle(
                getResources().getText(R.string.app_name) +
                        " - " + getResources().getText(R.string.title_color_picker)
        );

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_color_picker, container, false);

        ImageButton button = (ImageButton) rootView.findViewById(R.id.openCameraButton);

        colorDB = new ColorDatabase(getActivity());

        editName = rootView.findViewById(R.id.colorName);
        editHex = rootView.findViewById(R.id.HEXText);
        editRgb = rootView.findViewById(R.id.RGBText);
        editHsv = rootView.findViewById(R.id.HSVText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_OR_GALLERY);
                }
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
            }
        });

        ImageButton button2 = rootView.findViewById(R.id.viewGalleryButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_OR_GALLERY);
                }
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        final ImageButton saveColorB = rootView.findViewById(R.id.saveButton);
        saveColorB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                String hex = editHex.getText().toString();
                String rgb = editRgb.getText().toString();
                String hsv = editHsv.getText().toString();
                colorDB.addColorInfoData(name, hex, rgb, hsv);
                isButtonClicked = !isButtonClicked;
                saveColorB.setImageResource(isButtonClicked ? R.drawable.bookmark_selected : R.drawable.ic_action_name);
                ImageButton saveButton = (ImageButton) rootView.findViewById(R.id.saveButton);
                if(isButtonClicked){
                    saveButton.setColorFilter(colorT);
                }else{
                    saveButton.setColorFilter(null);
                }
            }
        });

        final ImageButton infoColorB = (ImageButton) rootView.findViewById(R.id.infoButton);
        infoColorB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                updateColorName(getView());
                Intent startCIA = new Intent(getActivity(), ColorInfoActivity.class);
                startCIA.putExtra("get_hex", Integer.toString(colorT));
                startActivity(startCIA);
            }
        });

        final ImageButton editColorB = (ImageButton) rootView.findViewById(R.id.editButton);
        editColorB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                updateColorName(getView());
                Intent startCIA = new Intent(getActivity(), EditColorActivity.class);
                startCIA.putExtra("get_hex", Integer.toString(colorT));
                startActivity(startCIA);
            }
        });

        pickingImage = rootView.findViewById(R.id.pickingImage);
        if (pickingImage != null) { // loads saved image to fragment using path
            SharedPreferences prefs = getContext().getSharedPreferences("prefs", MODE_PRIVATE);
            imagePath = prefs.getString("image", null);
            pickingImage.setImageURI(imageUri);
            if(imagePath != null) {
                Drawable drawable = Drawable.createFromPath(imagePath);
                pickingImage.setImageDrawable(drawable);
            }
        }
        //Adds a listener to get the x and y coordinates of taps and update the display
        pickingImage.setOnTouchListener(handleTouch);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && data != null) {
            if(requestCode == RESULT_LOAD_IMAGE) {
                imageUri = data.getData(); // only needed for gallery images
            }
            pickingImage.setImageURI(imageUri); // updates the view
            imagePath = getPath(data, this.getActivity());
            if (imagePath != null) {
                // save image path to saved prefs after updating imageview
                SharedPreferences.Editor editor = getContext().getSharedPreferences("prefs", MODE_PRIVATE).edit();
                editor.putString("image", imagePath);
                editor.apply();
            }
        }
    }
    // gets path of image to save to fragment
    public String getPath(Intent data, Context context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(imageUri, projection, null, null, null);
        if (cursor == null) return null;
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        imagePath = cursor.getString(column_index);
        cursor.close();
        return imagePath;
    }

    // https://stackoverflow.com/a/39588899
    // For Sprint 2 User Story 2.
    //TODO there's clearly some sort of error in either getting the coordinates or turning them into a color. Not quite sure where.
    //TODO maybe the image view's size is changing, maybe the image dimensions do not match the imageview size (stretched/compressed to fit)
    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            //Retrieve image from view
            ImageView pickedImage = view.findViewById(R.id.pickingImage);

            //get image as bitmap to get color data
            Bitmap bitmap;
            try {
                bitmap = ((BitmapDrawable) pickedImage.getDrawable()).getBitmap();
            } catch (Exception e){
                Log.w("onTouch() possible error", "(Probably unable to retrieve image and/or turn it into a bitmap): "+e);
                return true;
            }

            //The horizontal space we have to display it in, in pixels.
            //Image doesn't necessarily take the entire ImageView!
            double newImageMaxWidth = pickedImage.getWidth();
            double newImageMaxHeight = pickedImage.getHeight();
            //The original image size, before it was scaled to our screen.
            double originalImageWidth = bitmap.getWidth();
            double originalImageHeight = bitmap.getHeight();
            //The space that it actually uses, in pixels.
            double newImageWidth = 0.0;
            double newImageHeight = 0.0;

            // https://stackoverflow.com/a/13318469
            // Gets the actual size of the image inside the imageview, since it might not take
            //   up the entire space.
            if (newImageMaxHeight * originalImageWidth <= newImageMaxWidth * originalImageHeight) {
                newImageWidth = originalImageWidth * newImageMaxHeight / originalImageHeight;
                newImageHeight = newImageMaxHeight;
            } else {
                newImageWidth = newImageMaxWidth;
                newImageHeight = originalImageHeight * newImageMaxWidth / originalImageWidth;
            }

            //TODO delete this frame resize if the other fix (translating x y at line 185) worked.

            //TODO For some reason, the image gets smaller when the ImageView resizes.
            //  The ImageView seems to perfectly contain the image though.
            //TODO Actually we'd want to do this when loading the image or the first click would be off
            //TODO We want to be able to undo this as well for when we select a new image. Save the coordinates somewhere?
            //TODO This, and many other things, should be in their own functions
            //If you delete this, also delete the FrameLayout import.
            // https://stackoverflow.com/a/8233084
            // Now change ImageView's dimensions to match the scaled image
            //FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) pickedImage.getLayoutParams();
            //params.width = (int) newImageWidth;
            //params.height = (int) newImageHeight;
            //pickedImage.setLayoutParams(params);

            Log.d("DEBUG S2US2","Found ImageView dimensions: "+newImageMaxWidth+" "
                    +newImageMaxHeight +" image takes up "+newImageWidth +" "+newImageHeight);


            Log.d("DEBUG S2US2","Source image has dimensions "+originalImageWidth+" "+originalImageHeight);
            //This should get us x and y with respect to the ImageView we click on,
            //  not the whole screen, and not the image itself.
            double x = event.getX();
            double y = event.getY();
            Log.d("DEBUG S2US2", "ImageView click x="+x+" y="+y);

            //The image might not take the whole imageview. We could try to resize the imageview, or
            //  we could translate the x y coordinates like this:
            x = x - (newImageMaxWidth/2 - newImageWidth/2);
            //For some reason this doesn't work? Maybe newImageHeight contains the wrong values?
            y = y - (newImageMaxHeight/2 - newImageHeight/2);
            //Now we need to change the coordinates because when we get stuff from the bitmap it's
            //  using pixels based on the original image size.
            double rescaleX = originalImageWidth / newImageWidth;
            double rescaleY = originalImageHeight / newImageHeight;
            x = x * rescaleX;
            y = y * rescaleY;

            Log.d("DEBUG S2US2", "Modified coordinates are now x="+x+" y="+y+" using rescales "+rescaleX+" "+rescaleY);

            //If you click in the image and then drag outside the image this function still fires,
            //  but with invalid x & y, causing a crash on bitmap.getPixel()
            if(x < 0 || y < 0 || x > originalImageWidth || y > originalImageHeight) {
                Log.d("DEBUG S2US2", "Ignoring invalid click coordinates");
                return true; //I'm not sure if our return value really matters.
            }
            //get color int from said pixel coordinates using the source image
            int pixel = bitmap.getPixel((int) x, (int) y);
            //send to Gabby's script to updated the displayed values on screen
            //if android doesn't like us sending the whole color object we can send the color string
            //and use Color.valueOf() on Gabby's end
            updateColorValues(view, pixel);
            isButtonClicked = false;
            ImageButton saveColorB = (ImageButton) getView().findViewById(R.id.saveButton);
            saveColorB.setImageResource(R.drawable.ic_action_name);
            saveColorB.setColorFilter(null);
            //Get the color name from an API call
            //TODO if the name is very long it may go to a new line and shrink the buttons.
            //It takes a second to load and I don't want to spam the API so lets only call it when we release
            if(event.getActionMasked() == MotionEvent.ACTION_UP) {
                Log.d("S3US5", "Release detected");
                /*
                MainActivity.colorNameView = getActivity().findViewById(R.id.colorName);
                colorNameGetter tmp = new colorNameGetter();
                tmp.execute(pixel);
                 */
                TextView viewToUpdateColorName = getActivity().findViewById(R.id.colorName);
                colorNameGetter.updateViewWithColorName(viewToUpdateColorName, pixel);
            } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                //Wipe the color name until we get a new one during drags.
                ((TextView) getActivity().findViewById(R.id.colorName)).setText("");
            }
            return true;
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        updateColorName(getView()); // saves color name to sharedprefs upon leaving fragment
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("Lifecycles", "onViewCreated: View Created for Color Picker Fragment");
        loadColorView();
    }

    public void loadColorView() {
        // To load saved color onto fragment, default/initial load is white?
        SharedPreferences prefs = getContext().getSharedPreferences("prefs", MODE_PRIVATE);
        int savedColorInt = prefs.getInt("colorValue", Color.WHITE);
        String savedColorName = prefs.getString("colorName", null);
        if(savedColorName != null) { // loads saved name, if it exists
            ((TextView) getActivity().findViewById(R.id.colorName)).setText(savedColorName);
        }
        updateColorValues(getView(), savedColorInt);
//        updateColorValues(getView(), getResources().getColor(R.color.colorPicked));
    }

    public void updateColorName(View view){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        TextView colorNameView = getActivity().findViewById(R.id.colorName);
        String input = colorNameView.getText().toString();
        editor.putString("colorName", input);
        editor.apply();
    }

    //TODO a fully transparent color displays as black (0,0,0), even though our background is white.
    public void updateColorValues(View view, int colorNew){
        Log.d("DEBUG", "updateColorValues: called");
        Log.d("DEBUG", "updateColorValues: color int = " + colorNew);

        colorT = colorNew;

        //fetch the color components from colorNew
        int RV = Color.red(colorNew);
        int GV = Color.green(colorNew);
        int BV = Color.blue(colorNew);

        //update the HEX value displayed
        String hexValue = colorToHex(colorNew);
        String fullHEX = String.format("HEX: #%1$s",hexValue);
        Log.d("DEBUG", "updateColorValues: fullHEX = " + fullHEX);
        TextView hexDisplay = getActivity().findViewById(R.id.HEXText);
        hexDisplay.setText(fullHEX);

        //Update the color display with the color they've chosen, ignoring transparency.
        ImageView colorDisplay = getActivity().findViewById(R.id.pickedColorDisplayView);
        // https://stackoverflow.com/a/7741300
        final int TRANSPARENT = 0xFF000000;
        colorNew = colorNew | TRANSPARENT;
        colorDisplay.setBackgroundColor(colorNew);

        // save color value (int) to Shared Prefs.
        SharedPreferences.Editor editor = getContext().getSharedPreferences("prefs", MODE_PRIVATE).edit();
        editor.putInt("colorValue", colorNew);
        editor.apply();

        //Put the color in SharedPreferences as a String with key nameKey
        editor.putString("colorString", Integer.toString(colorNew));
        editor.apply();
    }

    //Takes a pixel color, returns the hex
    //Ignores transparency.
    public static String colorToHex(int color){
        return String.format("%06X", (0xFFFFFF & color));
    }
}