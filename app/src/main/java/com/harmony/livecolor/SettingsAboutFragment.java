package com.harmony.livecolor;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsAboutFragment extends Fragment {

    ImageButton imageButtonBack;
    TextView textLibraries;

    public SettingsAboutFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsAboutFragment newInstance() {
        SettingsAboutFragment fragment = new SettingsAboutFragment();
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
        final View rootView = inflater.inflate(R.layout.fragment_settings_about, container, false);

        // handles customized accent
        customAccent(rootView.findViewById(R.id.constraintLayoutSettings));

        imageButtonBack = rootView.findViewById(R.id.backButton);
        textLibraries = rootView.findViewById(R.id.librariesLink);

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        textLibraries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TheBrows/LiveColor/blob/master/LIBRARIES.md"));
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("Lifecycles", "onViewCreated: View Created for Settings Fragment");
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

        TextView textLibraries = view.findViewById(R.id.librariesLink);

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

        textLibraries.setTextColor(accentList);
    }

}

