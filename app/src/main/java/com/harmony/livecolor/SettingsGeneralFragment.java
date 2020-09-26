package com.harmony.livecolor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingsGeneralFragment extends  Fragment{

    private SettingsGeneralFragment.OnFragmentInteractionListener mListener;

    ImageButton imageButtonResetImage;
    RotateAnimation rotate;
    ImageButton imageButtonBack;
    EditText editZoom;

    public SettingsGeneralFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsGeneralFragment newInstance() {
        SettingsGeneralFragment fragment = new SettingsGeneralFragment();
        //Bundle args = new Bundle();
        //args can be bundled and sent through here if needed
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (NightModeUtils.isNightModeEnabled(getContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //if arguments are needed ever, use this to set them to static values in the class
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Lifecycles", "onCreateView: SettingsFragment created");

        //Set title on action bar to match current fragment
        getActivity().setTitle(
                getResources().getText(R.string.app_name) +
                        " - " + getResources().getText(R.string.title_settings)
        );

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_settings_general, container, false);

        rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());

        imageButtonResetImage = rootView.findViewById(R.id.imageButtonImageReset);
        imageButtonBack = rootView.findViewById(R.id.backButton);
        imageButtonBack = rootView.findViewById(R.id.backButton);
        
        
        imageButtonResetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickResetImage(v);
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        editZoom = rootView.findViewById(R.id.editZoom);
        setupMaxZoom();

        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {

        }
    }

    // https://stackoverflow.com/a/39588899
    // For Sprint 2 User Story 2.


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
             mListener = (OnFragmentInteractionListener) context;
        } else {
         //   throw new RuntimeException(context.toString()
         //           + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        Log.d("Lifecycles", "onViewCreated: View Created for Settings Fragment");
    }

    /**
     * COLOR PICKER IMAGE RESET
     * resets the image shown on the color picker page to the original image (the livecolor logo)
     *
     * @param view is only needed to animate the button when clicked
     *
     * @author Paige
     */
    public void onClickResetImage(View view) {
        ImageButton reset = (ImageButton) view;
        reset.startAnimation(rotate);

        //clear shared pref of currently saved path
        SharedPreferences.Editor editor = getContext().getSharedPreferences("prefs", MODE_PRIVATE).edit();
        editor.putString("image", null);
        editor.apply();
    }

    //https://stackoverflow.com/a/6832095
    private void setupMaxZoom(){
        if(editZoom == null){
            Log.w("I100", "EditZoom was null");
            return;
        }
        editZoom.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press:

                    //Uses shared preferences to store this number, and it will be loaded in ColorPickerFragment
                    //TODO if you go back without pressing enter should it save?
                    //TODO load and prefill with previous zoom level when bringing up settings
                    SharedPreferences preferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    int input = 1;
                    try {
                        input = Integer.parseInt(editZoom.getText().toString());
                    } catch (NumberFormatException e) {
                        Log.d("I100", "Input was empty");
                    }
                    editor.putInt("maxZoom", input);
                    editor.apply();
                    Log.d("I100", "Enter key pressed while entering zoom number. "+input);
                    //TODO pressing enter should close the input
                    //https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-using-java
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * CUSTOM ACCENT HANDLER
     * changes colors of specific activity/fragment
     *
     *
     * @param view view of root container
     *
     * @author Daniel
     * takes a bit of elbow grease, and there maybe a better way to do this, but it works
     */
    public void customAccent(View view){


        int[][] states = new int[][] {

                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] {-android.R.attr.state_selected}, // unselected
                new int[] { android.R.attr.state_active}, // active
                new int[] { android.R.attr.state_pressed}, // pressed
                new int[] { android.R.attr.state_checked},  // checked
                new int[] { android.R.attr.state_selected}, // selected
                new int[] { android.R.attr.state_enabled} // enabled
        };

        int[] accent = new int[] {
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext()))
        };

        ColorStateList  accentList = new ColorStateList(states, accent);

//      switchDM.setThumbTintList(accentList);
//      switchDM.setBackgroundTintList(accentList);
//      switchDM.setTrackTintList(accentList);
//      editTextAccent.setTextColor(accentList);
//      editTextAccent.setCompoundDrawableTintList(accentList);
//      editTextAccent.setHintTextColor(accentList);
//      editTextAccent.setForegroundTintList(accentList);
//      editTextAccent.setBackgroundTintList(accentList);

    }

}

