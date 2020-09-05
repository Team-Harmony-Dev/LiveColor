package com.harmony.livecolor;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.Switch;

import android.widget.ToggleButton;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import static android.content.Context.MODE_PRIVATE;


public class SettingsCOTDFragment extends  Fragment{

    private SettingsCOTDFragment.OnFragmentInteractionListener mListener;


    ToggleButton toggleButtonCotd;
    Button buttonNotify;

    private WeakReference<Activity> mActivity;

    public SettingsCOTDFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsCOTDFragment newInstance() {
        SettingsCOTDFragment fragment = new SettingsCOTDFragment();
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
        final View rootView = inflater.inflate(R.layout.fragment_settings_cotd, container, false);

        // handles customized accent
        customAccent(rootView.findViewById(R.id.constraintLayoutSettings));

        toggleButtonCotd = rootView.findViewById(R.id.toggleButtonCotd);

        buttonNotify = rootView.findViewById(R.id.buttonNotify);


        // show proper Cotd val
        SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isCotdEnabled = preferences.getBoolean("dialogCotd",true);
        toggleButtonCotd.setChecked(isCotdEnabled);


        toggleButtonCotd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                onCheckChangedCotd(buttonView, isChecked);
            }
        });

        buttonNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                createNotification(view);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {

        }
    }


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
     * COTD TOGGLE
     * toggle button for Cotd
     *
     * @param buttonView view of switch
     * @param isChecked value
     *
     * @author Daniel
     */
    public void onCheckChangedCotd(CompoundButton buttonView, boolean isChecked){


        Log.d("COTD","COTD current value: " + isChecked);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        preferences.edit().putBoolean("dialogCotd", isChecked).commit();
//
//        NightModeUtils.setIsToogleEnabled(getContext(),isChecked);
//        NightModeUtils.setIsNightModeEnabled(getContext(),isChecked);

        buttonView.setChecked(isChecked);

    }
    /**
     * NOTIFICATION SETTER
     * sets notification for COTD
     * handles the when mostly,
     * what handeled in NotificationUtils
     *
     * @param view view of button
     *
     * @author Daniel
     * testing fuinctionality
     */
    public void createNotification (View view) {
        Intent myIntent = new Intent(view.getContext() , NotificationUtils.class );
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService( this.getContext(), 0 , myIntent , 0 );
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 13);
        calendar.set(Calendar.HOUR, 6);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP , calendar.getTimeInMillis() , 1000*60*60*24 , pendingIntent);

        Log.d("DEBUG", "createNotification: ");
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
        Switch switchDM = view.findViewById(R.id.switchDarkMode);
        ToggleButton toggleButtonCotd = view.findViewById(R.id.toggleButtonCotd);
        EditText editTextAccent = view.findViewById(R.id.editTextAccentHex);

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

