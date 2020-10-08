package com.harmony.livecolor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;

import com.google.android.material.snackbar.Snackbar;
import com.harmony.livecolor.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

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

        final View rootView = inflater.inflate(R.layout.fragment_saved_colors_list, container, false);
        // handles customized accent
        final ColorStateList[] myLists = customAccent(view.findViewById(R.id.savedColorsConstraint));

        //initColors();

        //String defaultView = "list";
        //initRecycler(defaultView);

        /**
         * Button on click listeners for the grid/list buttons.
         * On click, the recycler is re-initialized.
         */
        final ImageButton listButton = view.findViewById(R.id.listViewButton);
        final ImageButton gridButton = view.findViewById(R.id.gridViewButton);

        listButton.setImageResource(R.drawable.list_view_selected);
        gridButton.setImageResource(R.drawable.grid_view_selected);

        listButton.setImageTintList(myLists[0]);
        gridButton.setImageTintList(myLists[1]);


        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                initRecycler("list");
                SharedPreferences preferences = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
                preferences.edit().putString("savedLayout", "list").commit();
                //Sets the image buttons to reflect current state
                listButton.setImageTintList(myLists[0]);
                gridButton.setImageTintList(myLists[1]);
//                listButton.setImageResource(R.drawable.list_view_selected);
//                gridButton.setImageResource(R.drawable.grid_view);
            }
        });

        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                initRecycler("grid");
                SharedPreferences preferences = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
                preferences.edit().putString("savedLayout", "grid").commit();
                listButton.setImageTintList(myLists[1]);
                gridButton.setImageTintList(myLists[0]);
//                listButton.setImageResource(R.drawable.list_view);
//                gridButton.setImageResource(R.drawable.grid_view_selected);
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
         * Set the appropriate layout manager and animation for the recycler view depending on if list/grid is selected. - Gabby
         */
        if(selectedView == "list"){
            LinearLayoutManager layoutManager = new LinearLayoutManagerWrapper(context);
            recyclerView.setLayoutManager(layoutManager);
            //set animations
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_slide_from_right);
            recyclerView.setLayoutAnimation(animation);
        } else {
            int numberOfColumns = 3;
            GridLayoutManager layoutManager = new GridLayoutManager(context, numberOfColumns);
            recyclerView.setLayoutManager(layoutManager);
            //set animations
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_slide_from_bottom);
            recyclerView.setLayoutAnimation(animation);
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
            //save deleted color in case deletion is undone
            deletedColor = colorList.get(position);
            Log.d("SavedColorsFragment", "onSwiped: Color is " + deletedColor.getName());
            //remove from list and update palette database with new info
            colorList.remove(position);
            db.updateRefString("1", colorList, true);
            //check whether the color is still in use or not, and remove from the color database if no longer used
            if(!db.isColorInUse(deletedColor.getId())) {
                db.deleteColor(deletedColor.getId());
            }
            //notify the recycler
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position,colorList.size());
            Snackbar.make(recyclerView, deleteMsg + deletedColor.getName(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("SavedColorsFragment", "onClick: Adding deleted color " + deletedColor.getName() + " to position " + position);
                            //add the deleted color back to the list
                            colorList.add(position, deletedColor);
                            //update the palette database with the new info
                            db.updateRefString("1", colorList, true);
                            //check that the color needs to be re-added to the color database, and do so if needed
                            db.addPreExistingColor(deletedColor);
                            //notify the recycler
                            adapter.notifyItemInserted(position);
                            adapter.notifyItemRangeChanged(position,colorList.size());
                        }
                    }).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        initColors();
        ImageButton listButton = view.findViewById(R.id.listViewButton);
        ImageButton gridButton = view.findViewById(R.id.gridViewButton);
        SharedPreferences preferences = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        String savedLayout = preferences.getString("savedLayout", "list");
        ColorStateList[] myLists = customAccent(view.findViewById(R.id.savedColorsConstraint));
        if(savedLayout.equals("grid")) {
            initRecycler("grid");
            preferences.edit().putString("savedLayout", "grid").commit();
            listButton.setImageTintList(myLists[1]);
            gridButton.setImageTintList(myLists[0]);
        }else{
            initRecycler("list");
            preferences.edit().putString("savedLayout", "list").commit();
            listButton.setImageTintList(myLists[0]);
            gridButton.setImageTintList(myLists[1]);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


        /**
         * CUSTOM ACCENT HANDLER
         * changes colors of specific activity/fragment
         *
         * THIS ONE WORKS A LITTLE DIFFERENT
         * so, each time this is used, its a bespoke solution
         * this time needed a little something extra to change the tints on the fly
         * didnt want to crowd out onCreateView
         *
         * @param view view of root container
         *
         * @author Daniel
         * takes a bit of elbow grease, and there maybe a better way to do this, but it works
         */
    public ColorStateList[] customAccent(View view){
        ImageButton listView = view.findViewById(R.id.listViewButton);
        ImageButton gridView = view.findViewById(R.id.gridViewButton);

        int[][] states = new int[][] {

                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] {-android.R.attr.state_selected}, // unselected
                new int[] { android.R.attr.state_active}, // active
                new int[] { android.R.attr.state_pressed}, // pressed
                new int[] { android.R.attr.state_checked},  // checked
                new int[] { android.R.attr.state_selected}, // selected
                new int[] { android.R.attr.state_enabled} // enabled
//                new int[] { android.R.attr.}
        };

        int[] colors = new int[] {
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext())),
                Color.parseColor(AccentUtils.getAccent(view.getContext()))
        };
        int[] greys  = new int[] {
                ContextCompat.getColor(getContext(), R.color.colorIconPrimary),
                ContextCompat.getColor(getContext(), R.color.colorIconPrimary),
                ContextCompat.getColor(getContext(), R.color.colorIconPrimary),
                ContextCompat.getColor(getContext(), R.color.colorIconPrimary),
                ContextCompat.getColor(getContext(), R.color.colorIconPrimary),
                ContextCompat.getColor(getContext(), R.color.colorIconPrimary),
                ContextCompat.getColor(getContext(), R.color.colorIconPrimary),
                ContextCompat.getColor(getContext(), R.color.colorIconPrimary)
        };

        ColorStateList accentList = new ColorStateList(states, colors);
        ColorStateList primaryList = new ColorStateList(states, greys);

        ColorStateList[] selectedLists =  new ColorStateList[2];
        selectedLists[0] = accentList;
        selectedLists[1] = primaryList;

        return selectedLists;



    }
}
