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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.graphics.Color.toArgb;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ColorPickerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ColorPickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorPickerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    public ColorPickerFragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ColorPickerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ColorPickerFragment newInstance(/*String param1, String param2*/) {
        ColorPickerFragment fragment = new ColorPickerFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        pickingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //retrieve image from view
                ImageView pickedImage = view.findViewById(R.id.pickingImage);
                //get image as bitmap to get color data
                Bitmap bitmap = ((BitmapDrawable)pickedImage.getDrawable()).getBitmap();
                //retrieve pixel coordinates for selected pixel, using dustin's script
                //TODO: get x and y values from dustin's script
                int x = 0;
                int y = 0;
                //get color int from said pixel coordinates
                int pixel = bitmap.getPixel(x,y);
                Log.d("DEBUG", "onClick: color int = " + pixel);
                //send to Gabby's script to updated the displayed values on screen
                updateColorValues(view, pickedColor);
            }
        });

        return rootView;
    }




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
        updateColorValues(getView(),Color.valueOf(Color.WHITE));
    }

    public void updateColorValues(View view, Color pickedColor){
        //fetch the color from colorPicked
        int colorNew = getResources().getColor(R.color.colorPicked);
        int RV = red(colorNew);
        int GV = green(colorNew);
        int BV = blue(colorNew);

        //update the RGB value displayed
        String rgb = String.format("(%1$d, %2$d, %3$d)",RV,GV,BV);
        String fullRGB = String.format("RGB: %1$s",rgb);  //add "RGB: " and rgb together
        TextView temp = (TextView)view.findViewById(R.id.RGBText);//get the textview that displays the RGB value
        temp.setText(fullRGB); //set the textview to the new RGB: rgbvalue

        //update the HEX value displayed
        String hexValue = String.format("#%06X", (0xFFFFFF & colorNew)); //get the hex representation minus the first ff
        String fullHEX = String.format("HEX: %1$s",hexValue);
        TextView temp2 = (TextView)view.findViewById(R.id.HEXText);
        temp2.setText(fullHEX);

        //update the HSV value displayed
        float[] hsvArray = new float[3];
        RGBToHSV(RV,GV,BV,hsvArray);
        int hue = Math.round(hsvArray[0]);
        String fullHSV = String.format("HSV: (%1$d, %2$.3f, %3$.3f)",hue,hsvArray[1],hsvArray[2]);
        TextView temp3 = (TextView)view.findViewById(R.id.HSVText);
        temp3.setText(fullHSV);
    }
}
