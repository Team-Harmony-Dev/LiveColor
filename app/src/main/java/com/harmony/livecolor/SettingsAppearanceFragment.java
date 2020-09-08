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

public class SettingsAppearanceFragment extends  Fragment{

    private SettingsAppearanceFragment.OnFragmentInteractionListener mListener;


    Switch switchDarkMode;
    EditText editTextAccent;
    ImageButton imageButtonReset;
    RotateAnimation rotate;
    private WeakReference<Activity> mActivity;

    public SettingsAppearanceFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsAppearanceFragment newInstance() {
        SettingsAppearanceFragment fragment = new SettingsAppearanceFragment();
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
        final View rootView = inflater.inflate(R.layout.fragment_settings_appearance, container, false);

        // handles customized accent
        customAccent(rootView.findViewById(R.id.constraintLayoutSettings));


        switchDarkMode = rootView.findViewById(R.id.switchDarkMode);
        editTextAccent = rootView.findViewById(R.id.editTextAccentHex);
        imageButtonReset = rootView.findViewById(R.id.imageButtonAccentReset);


        // show proper darkmode val
        switchDarkMode.setChecked(NightModeUtils.isDarkMode(getActivity()));


        // handle edit text interaction
        editTextAccent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                afterTextChangedAccentHex(s);
                editTextAccent.setHint(AccentUtils.getAccent(getContext()));
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {}
        });
        // filter for proper hex (with #)
        InputFilter hexFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                Pattern pattern = Pattern.compile("^(#)?\\p{XDigit}+$");
                StringBuilder sb = new StringBuilder();
                if(source.length() > 7) return source.subSequence(start, end-1); // max 7 characters
                for (int i = start; i < end; i++) {
                    //Only allow characters "0123456789ABCDEF";
                    Matcher matcher = pattern.matcher(String.valueOf(source.charAt(i)));
                    if (!matcher.matches()) {
                        return source.subSequence(start, end);
                    }
                    //Add character to Strinbuilder
                    sb.append(source.charAt(i));
                }
                //Return text in UpperCase. if all good
                if (sb.toString().length() == 7 ){
                    if(sb.toString().matches("^(#)\\p{XDigit}+$")){
                        return  sb.toString().toUpperCase();
                    }else{
                        return "";
                    }
                }else{
                if (sb.toString().length() == 6 ){
                    if(sb.toString().matches("^\\p{XDigit}+$")){
                        return  sb.toString().toUpperCase();
                    }else{
                        return "";
                    }
                }
                }
                return  sb.toString().toUpperCase();
            }
        };
        editTextAccent.setFilters(new InputFilter[] { hexFilter });
        editTextAccent.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        // set hint to match accent
        editTextAccent.setHint(AccentUtils.getAccent(rootView.getContext()));
        // reset animation stuff
        rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());


        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                onCheckedChangedDarkMode(buttonView, isChecked);
            }
        });


        imageButtonReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                    onClickReset(view);
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

    public void onClickCredits(View view) {
        Intent intent = new Intent(view.getContext(), CreditsActivity.class);
        startActivity(intent);
    }

    /**
     * DARK MODE SWITCH
     * switch toggle for dark mode
     *
     * @param buttonView view of switch
     * @param isChecked value
     *
     * @author Daniel
     */
    public void onCheckedChangedDarkMode(CompoundButton buttonView, boolean isChecked) {

        Log.d("DARK","night mode switch changed, current value: " + isChecked);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        preferences.edit().putString("fragStatic", "true").commit();

        NightModeUtils.setIsToogleEnabled(getContext(),isChecked);
        NightModeUtils.setIsNightModeEnabled(getContext(),isChecked);
        buttonView.setChecked(isChecked);
        mActivity = new WeakReference<Activity>(this.getActivity());
        mActivity.get().recreate();
    }


    /**
     * ACCENT RESET
     * reset button for custom accent pref
     *
     * @param view view of button
     *
     * @author Daniel
     */
    public void onClickReset(View view){
        // animation
        ImageButton reset =  (ImageButton) view;
        reset.startAnimation(rotate); // technically in there, but gets immediately cut off by the recreate
        // function
        AccentUtils.resetAccent(getContext());
        // this is such a hack to redraw
        SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        preferences.edit().putString("fragStatic", "true").commit();
        mActivity = new WeakReference<Activity>(this.getActivity());
        mActivity.get().recreate();
    }


    /**
     * AFTER TEXT CHANGED FOR CUSTOM ACCENT
     * decides what to do once the custom hex has been entered
     *
     * @param seq sequence entered into the editText view
     *
     * @author Daniel
     */
    public void afterTextChangedAccentHex(Editable seq){

        String accentColor = "";

        if (seq.toString().length() == 7 ){
            if(seq.toString().matches("^(#)\\p{XDigit}+$")){
                accentColor = seq.toString().toUpperCase();
            }else{
                accentColor = "";
            }
        }else{
            if (seq.toString().length() == 6 ){
                if(seq.toString().matches("^\\p{XDigit}+$")){
                    accentColor = "#"+seq.toString().toUpperCase();
                }else{
                    accentColor = "";
                }
            }else{
                accentColor = "";
            }
        }

        if (accentColor.length() > 0){

            AccentUtils.setAccent(getContext(), accentColor);
            seq.clear();
            // this is such a hack
            SharedPreferences preferences = this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
            preferences.edit().putString("fragStatic", "true").commit();
            mActivity = new WeakReference<Activity>(this.getActivity());
            mActivity.get().recreate();
        }else{

        }
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

      switchDM.setThumbTintList(accentList);
      switchDM.setBackgroundTintList(accentList);
      switchDM.setTrackTintList(accentList);
      editTextAccent.setTextColor(accentList);
      editTextAccent.setCompoundDrawableTintList(accentList);
      editTextAccent.setHintTextColor(accentList);
      editTextAccent.setForegroundTintList(accentList);
      editTextAccent.setBackgroundTintList(accentList);

    }

}

