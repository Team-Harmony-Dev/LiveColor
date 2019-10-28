package com.harmony.livecolor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;


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

        View rootView = inflater.inflate(R.layout.fragment_color_picker, container, false);

        Button button = rootView.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CameraColorPicker.class);
                startActivity(intent);
            }
        });

        //onClickListener for
        ImageView pickingImage = rootView.findViewById(R.id.pickingImage);
        //Adds a listener to get the x and y coordinates of taps
        /*pickingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //retrieve image from view
                ImageView pickedImage = view.findViewById(R.id.pickingImage);
                //get image as bitmap to get color data
                Bitmap bitmap = ((BitmapDrawable)pickedImage.getDrawable()).getBitmap();
                //retrieve pixel coordinates for selected pixel, using dustin's script
                //TODO: get x and y values from dustin's script
                int x = lastTapX;
                int y = lastTapY;
                //get color int from said pixel coordinates
                int pixel = bitmap.getPixel(x,y);
                Log.d("DEBUG S2US2", "onClick: color int = " + pixel);
                //send to Gabby's script to updated the displayed values on screen
                //if android doesn't like us sending the whole color object we can send the color string
                //and use Color.valueOf() on Gabby's end
                //TODO: delete current update call and uncomment other once Dustin's script is incorporated
                updateColorValues(view,Color.BLUE);
                //updateColorValues(view, pixel);
            }
        });
        */
        //TODO I think this is taking priority over the other click listener.
        pickingImage.setOnTouchListener(handleTouch);

        return rootView;
    }
    // TODO What's the best way to pass this information?
    //TODO redundant, remove
    //int lastTapX = 0;
    //int lastTapY = 0;
    // https://stackoverflow.com/a/39588899
    // For Sprint 2 User Story 2.
    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            //TODO redundant, remove
            int lastTapX = (int) event.getX();
            int lastTapY = (int) event.getY();
            Log.d("S2US2", "lastTaps are x="+lastTapX+" y="+lastTapY);
            /*
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("TAG", "touched down");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("TAG", "moving: (" + x + ", " + y + ")");
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("TAG", "touched up");
                    break;
            }
            */
            //retrieve image from view
            ImageView pickedImage = view.findViewById(R.id.pickingImage);
            //get image as bitmap to get color data
            Bitmap bitmap = ((BitmapDrawable)pickedImage.getDrawable()).getBitmap();
            //retrieve pixel coordinates for selected pixel, using dustin's script
            //TODO: get x and y values from dustin's script
            int x = lastTapX;
            int y = lastTapY;
            //get color int from said pixel coordinates
            int pixel = bitmap.getPixel(x,y);
            Log.d("DEBUG S2US2", "onClick: color int = " + pixel);
            //send to Gabby's script to updated the displayed values on screen
            //if android doesn't like us sending the whole color object we can send the color string
            //and use Color.valueOf() on Gabby's end
            //TODO: delete current update call and uncomment other once Dustin's script is incorporated //Done
            //updateColorValues(view,Color.BLUE);
            updateColorValues(view, pixel);
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
        TextView rgbDisplay = getActivity().findViewById(R.id.RGBText);//get the textview that displays the RGB value
        rgbDisplay.setText(fullRGB); //set the textview to the new RGB: rgbvalue

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
        TextView hsvDisplay = getActivity().findViewById(R.id.HSVText);
        hsvDisplay.setText(fullHSV);

        //TODO: programmatically change the displayed square's color to the new color
        //this can be done in a very similar way to how the text is changed

        ImageView colorDisplay = getActivity().findViewById(R.id.pickedColorDisplayView);
        colorDisplay.setBackgroundColor(colorNew);
    }
}