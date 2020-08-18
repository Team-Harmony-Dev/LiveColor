package com.harmony.livecolor;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.snackbar.Snackbar;
import com.harmony.livecolor.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

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

    private RecyclerView recyclerView;
    private MySavedColorsRecyclerViewAdapter adapter;

    ColorDatabase db;

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

        db = new ColorDatabase(getActivity());

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

    public void initColors() {
        //initialize ArrayList<MyColors> here, gets the colors in reverse order to show most recently picked colors at the top
        colorList = db.getColorList("1",true);
    }

    /**
     * Initialize the recycler.
     * @param selectedView - "list" or "grid" - Gabby
     */
    public void initRecycler(String selectedView){
        //get the RecyclerView from the view
        recyclerView = view.findViewById(R.id.savedColorsRecycler);
        //then initialize the adapter, passing in the bookList
        adapter = new MySavedColorsRecyclerViewAdapter(context,colorList,listener, selectedView);
        //and set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
        //and set the layout manager as well
        //Set the layout to be reverse and stacked from the end so that the newest colors appear at the top of the list

        /**
         * Set the appropriate layout manager for the recycler view depending on if list/grid is selected. - Gabby
         */
        if(selectedView == "list"){
            LinearLayoutManager layoutManager = new LinearLayoutManagerWrapper(context);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            int numberOfColumns = 3;
            GridLayoutManager layoutManager = new GridLayoutManager(context, numberOfColumns);
            recyclerView.setLayoutManager(layoutManager);
        }

        //set ItemTouchHelper for item deletion
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
                listener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnListFragmentInteractionListener");
        }
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

    MyColor deletedColor = null;
    String deleteMsg = "Deleted ";

    /**
     * Handles swipe listening for individual list items. Used for list item deletion. Can be used for list rearranging as well in the future.
     */
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //we can use a switch to handle different cases for different swipe directions if desired
            final int position = viewHolder.getAdapterPosition();
            Log.d("SavedColorsFragment", "onSwiped: deleting " + position);
            deletedColor = colorList.get(position);
            Log.d("SavedColorsFragment", "onSwiped: Color is " + deletedColor.getName());
            colorList.remove(position);
            db.updateRefString("1", colorList, true);
            adapter.notifyItemRemoved(position);
            Snackbar.make(recyclerView, deleteMsg + deletedColor.getName(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("SavedColorsFragment", "onClick: Adding deleted color " + deletedColor.getName() + " to position " + position);
                            colorList.add(position, deletedColor);
                            db.updateRefString("1", colorList, true);
                            adapter.notifyItemInserted(position);
                        }
                    }).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        initColors();
        initRecycler("list");
    }
}
