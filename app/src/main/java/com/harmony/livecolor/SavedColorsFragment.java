package com.harmony.livecolor;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.harmony.livecolor.dummy.DummyContent;
import com.harmony.livecolor.dummy.DummyContent.DummyItem;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SavedColorsFragment extends Fragment {

    private OnListFragmentInteractionListener listener;
    private Context context;
    private View view;
    private ArrayList<MyColor> colorList;
    ColorDatabase newColorDatabase;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SavedColorsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SavedColorsFragment newInstance() {
        SavedColorsFragment fragment = new SavedColorsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_saved_colors_list, container, false);

        Log.d("Lifecycles", "onCreateView: SavedColorsFragment created");

        context = view.getContext();

        newColorDatabase = new ColorDatabase(getActivity());

        initColors();

        String defaultView = "list";
        initRecycler(defaultView);

        /**
         * Button on click listeners for the grid/list buttons.
         * On click, the recycler is re-initialized.
         */
        final ImageButton listButton = view.findViewById(R.id.listViewButton);
        final ImageButton gridButton = view.findViewById(R.id.gridViewButton);

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                initRecycler("list");
                //Sets the image buttons to reflect current state
                listButton.setImageResource(R.drawable.list_view_selected);
                gridButton.setImageResource(R.drawable.grid_view);
            }
        });

        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                initRecycler("grid");
                listButton.setImageResource(R.drawable.list_view);
                gridButton.setImageResource(R.drawable.grid_view_selected);
            }
        });

        return view;
    }

    public void initColors(){
        //initialize ArrayList<MyColors> here
        String TAG = "COLORS";
        Cursor colorData = newColorDatabase.getColorInfoData();
        colorList = new ArrayList<>();

        if (colorData != null && colorData.getCount() > 0) {
            if (colorData.moveToFirst()) {
                do {
                    Log.d(TAG,  colorData.getString(2));
                    colorList.add(new MyColor(colorData.getString(0) + "",
                            colorData.getString(1) + "", colorData.getString(2) + "",
                            colorData.getString(3) + "", colorData.getString(4) + ""));
                }         while (colorData.moveToNext());

            }
        }
    }

    /**
     * Initialize the recycler.
     * @param selectedView - "list" or "grid" - Gabby
     */
    public void initRecycler(String selectedView){
        //get the RecyclerView from the view
        RecyclerView recyclerView = view.findViewById(R.id.savedColorsRecycler);
        //then initialize the adapter, passing in the bookList
        MySavedColorsRecyclerViewAdapter adapter = new MySavedColorsRecyclerViewAdapter(context, colorList, listener, selectedView);
        //and set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
        //and set the layout manager as well
        //Set the layout to be reverse and stacked from the end so that the newest colors appear at the top of the list

        /**
         * Set the appropriate layout manager for the recycler view depending on if list/grid is selected. - Gabby
         */
        if(selectedView == "list"){
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,true);
            recyclerView.setLayoutManager(layoutManager);
            layoutManager.setStackFromEnd(true);
        } else {
            int numberOfColumns = 3;
            GridLayoutManager layoutManager = new GridLayoutManager(context, numberOfColumns);
            recyclerView.setLayoutManager(layoutManager);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnListFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
