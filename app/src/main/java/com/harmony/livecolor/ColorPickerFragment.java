package com.harmony.livecolor;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.app.Activity.RESULT_OK;
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

    private OnFragmentInteractionListener mListener;
    public static final int RESULT_LOAD_IMAGE = 1;
    ImageView pickingImage;

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
    private static final int IMAGE_CAPTURE_CODE = 1001;
    ImageView mImageView;
    Uri image_uri;
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
        View rootView = inflater.inflate(R.layout.fragment_color_picker, container, false);

        Button button = rootView.findViewById(R.id.openCameraButton);
        mImageView = rootView.findViewById(R.id.pickingImage);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
            }
        });

        Button button2 = rootView.findViewById(R.id.viewGalleryButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        /*final Button saveColorB = rootView.findViewById(R.id.saveButton);
        saveColorB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveColorB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_text)));
            }
        });*/



        //onClickListener for
        pickingImage = rootView.findViewById(R.id.pickingImage);
        //Adds a listener to get the x and y coordinates of taps and update the display
        pickingImage.setOnTouchListener(handleTouch);


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            mImageView.setImageURI(image_uri);
        }
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            pickingImage.setImageURI(selectedImage);
        }
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
            Bitmap bitmap = ((BitmapDrawable)pickedImage.getDrawable()).getBitmap();

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
            //colorNameGetter(pixel);
            return true;
        }
    };

    /*
    public String getColorName(final int color) {
        Thread background = new Thread(new Runnable() {
            public void run() {
                final String baseColorNameUrl = "https://api.color.pizza/v1/";
                //TODO maybe stick this in a function since it's shared with the hex text display.
                String hex = String.format("%06X", (0xFFFFFF & color)); //get the hex representation minus the first ff
                String colorNameUrl = baseColorNameUrl + hex;
                final String defaultColorName = "Your Color";
                String colorName = "Error";
                try {
                    URL url = new URL(colorNameUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(500);//500 is arbitary, should name
                    httpURLConnection.setReadTimeout(500);
                    Log.d("colorname", "Attempting to connect with url=" + url);
                    httpURLConnection.connect();
                    Log.d("colorname", "afterConnect");
                    InputStream is = httpURLConnection.getInputStream();
                    BufferedReader bf = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while((line = bf.readLine()) != null){
                        sb.append(line);
                    }
                    bf.close();
                    is.close();
                    // https://stackoverflow.com/a/26358942
                    JSONObject json = new JSONObject(sb.toString());
                    Log.d("colorname", "json: "+json);
                    //TODO is this middle step actually necessary?
                    JSONArray jsonArray = new JSONArray(json.getString("colors"));
                    Log.d("colorname", "jsonarray("+jsonArray.length()+"): "+jsonArray);
                    json = jsonArray.getJSONObject(0);
                    Log.d("colorname", "json now: "+json);
                    colorName = json.getString("name");
                    //return sb.toString();
                    //return colorName;
                } catch (Exception e) {
                    Log.w("DEBUG colorname", "Problem fetching color name.");
                    e.printStackTrace();
                    //return defaultColorName;
                    colorName = defaultColorName;
                }
                //android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
                TextView colorNameDisplay = getActivity().findViewById(R.id.colorName);
                colorNameDisplay.setText(colorName);
            }
        });

     background.start();
     //Doing it like this with a global variable causes it to lag a color or more behind since the call takes time.
     //TextView colorNameDisplay = getActivity().findViewById(R.id.colorName);
     //colorNameDisplay.setText(colorName);
     return "";
    }
    */

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
        updateColorValues(getView(),Color.WHITE);
    }

    //TODO a fully transparent color displays as black (0,0,0), even though our background is white.
    public void updateColorValues(View view, int colorNew){
        Log.d("DEBUG", "updateColorValues: called");
        Log.d("DEBUG", "updateColorValues: color int = " + colorNew);

        //fetch the color from pickedColor
        int RV = Color.red(colorNew);
        int GV = Color.green(colorNew);
        int BV = Color.blue(colorNew);

        Log.d("DEBUG", "updateColorValues: red = " + RV + ", blue = " + GV + ", blue = " + BV);

        //update the RGB value displayed
        String rgb = String.format("(%1$d, %2$d, %3$d)",RV,GV,BV);
        String fullRGB = String.format("RGB: %1$s",rgb);  //add "RGB: " and rgb together
        Log.d("DEBUG", "updateColorValues: fullRGB = " + fullRGB);
        //I (Dustin) changed all the calls to view.findViewById to getActivity().findViewById.
        //Is the view argument to updateColorValues needed?
        //TextView rgbDisplay = getActivity().findViewById(R.id.RGBText);//get the textview that displays the RGB value
        //rgbDisplay.setText(fullRGB); //set the textview to the new RGB: rgbvalue

        //update the HEX value displayed
        String hexValue = String.format("#%06X", (0xFFFFFF & colorNew)); //get the hex representation minus the first ff
        String fullHEX = String.format("HEX: %1$s",hexValue);
        Log.d("DEBUG", "updateColorValues: fullHEX = " + fullHEX);
        TextView hexDisplay = getActivity().findViewById(R.id.HEXText);
        hexDisplay.setText(fullHEX);

        //update the HSV value displayed
        float[] hsvArray = new float[3];
        RGBToHSV(RV,GV,BV,hsvArray);
        int hue = Math.round(hsvArray[0]);
        String fullHSV = String.format("HSV: (%1$d, %2$.3f, %3$.3f)",hue,hsvArray[1],hsvArray[2]);
        //TextView hsvDisplay = getActivity().findViewById(R.id.HSVText);
        //hsvDisplay.setText(fullHSV);

        //Set the color display
        ImageView colorDisplay = getActivity().findViewById(R.id.pickedColorDisplayView);
        colorDisplay.setBackgroundColor(colorNew);
    }
}