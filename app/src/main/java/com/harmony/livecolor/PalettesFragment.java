package com.harmony.livecolor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.google.android.material.snackbar.Snackbar;
import com.harmony.livecolor.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

import static com.harmony.livecolor.UsefulFunctions.makeToast;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PalettesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private OnListFragmentInteractionListener listener;
    private Context context;
    private View view;
    private ArrayList<MyPalette> paletteList;

    private RecyclerView recyclerView;
    private MyPalettesRecyclerViewAdapter adapter;

    ColorDatabase colorDB;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PalettesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PalettesFragment newInstance() {
        PalettesFragment fragment = new PalettesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_palettes_list, container, false);

        Log.d("Lifecycles", "onCreateView: PaletteFragment created");

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_palettes_list, container, false);

        // handles customized accent
        customAccent(rootView.findViewById(R.id.constraintLayoutPalettes));

        context = view.getContext();

        colorDB = new ColorDatabase(getActivity());

        SearchView searchView = view.findViewById(R.id.searchBarPalette);

        searchView.setOnQueryTextListener(this);

        initPalettes(colorDB.getPaletteDatabaseCursor());

        initRecycler();

        return view;
    }

    /**
     * Initializes an Arraylist of MyPalette with the appropriate MyColor arraylist for each
     * Arraylist is made using the provided cursor from the palette table from colorDB
     * @param cursor pointing to palettes that the arraylist should be created from
     */
    public void initPalettes(Cursor cursor){
        //initialize ArrayList<MyPalette> here
        paletteList = colorDB.getPaletteList(cursor);

    }

    /**
     * Initializes the MyPaletteRecyclerView with the appropriate adapter, listener, and layout.
     */
    public void initRecycler(){
        //get the RecyclerView from the view
        recyclerView = view.findViewById(R.id.palettesRecycler);
        //then initialize the adapter, passing in the bookList
        adapter = new MyPalettesRecyclerViewAdapter(context,paletteList,listener,false);
        //and set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
        //and set the layout manager as well
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(context));
        //set ItemTouchHelper for item deletion
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        //set animation
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(animation);
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
     * Required Override, unused. See onQueryTextChange for search bar listener.
     * @param query the submitted text in the search bar
     * @return does not apply here for now, relates to suggestions. Simply return as false.
     */
    @Override
    public boolean onQueryTextSubmit(String query) { return false; }

    /**
     * Method called when user changes the text within the search bar
     * @param query the new text in the search bar
     * @return does not apply here for now, relates to suggestions. Simply return as false.
     */
    @Override
    public boolean onQueryTextChange(String query) {
        //trim any whitespace
        query = query.trim();
        //default cursor to make list from, for incorrect HEX input
        Cursor cursor = colorDB.getPaletteDatabaseCursor();
        //check whether starting with a # or not to determine whether to conduct a HEX or palette name query
        if(query.startsWith("#")) {
            //check that HEX size is correct, if not, notify the user
            if(query.length() > 7) {
                makeToast("Invalid HEX entered. Should have no more than 6 digits.", context);
            } else if(query.length() > 1) {
                //perform and retrieve a cursor for our query
                cursor = colorDB.searchPalettesByHex(query);
            }
        } else {
            //perform and retrieve a cursor for our palette name query
            cursor = colorDB.searchPalettesByName(query);
        }

        //use the returned cursor from the above to reload the recycler with the results of the query
        initPalettes(cursor);
        initRecycler();
        return false;
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

    MyPalette deletedPalette = null;
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
            deletedPalette = paletteList.get(position);
            paletteList.remove(position);
            colorDB.deletePalette(deletedPalette.getId());
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position,paletteList.size());
            Snackbar.make(recyclerView, deleteMsg + deletedPalette.getName(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            paletteList.add(position, deletedPalette);
                            colorDB.addPreExistingPalette(deletedPalette);
                            adapter.notifyItemInserted(position);
                            adapter.notifyItemRangeChanged(position,paletteList.size());
                        }
                    }).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Lifecycles", "onResume: PalettesFragment resumed");
        //TODO: There *must* be a better way to refresh the lists than this (notifyDataSetChanged() isn't working)
        initPalettes(colorDB.getPaletteDatabaseCursor());
        initRecycler();
    }

    /**
     * CUSTOM ACCENT HANDLER
     * changes colors of specific activity/fragment
     *
     * @param view view of root container
     *
     * @author Daniel
     * takes a bit of elbow grease, and there maybe a better way to do this, but it works
     */
    public void customAccent(View view){
        SearchView searchViewBar = view.findViewById(R.id.searchBarPalette);

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

        ColorStateList accentList = new ColorStateList(states, accent);

        searchViewBar.setBackgroundTintList(accentList);
        searchViewBar.setForegroundTintList(accentList);
        searchViewBar.setBackgroundColor(Color.parseColor(AccentUtils.getAccent(view.getContext())));

    }
}