package com.harmony.livecolor;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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

        context = view.getContext();

        colorDB = new ColorDatabase(getActivity());

        SearchView searchView = view.findViewById(R.id.searchBarPalette);

        searchView.setOnQueryTextListener(this);

        initPalettes(colorDB.getPaletteDatabaseCursor());

        initRecycler();

        return view;
    }

    public void initPalettes(Cursor cursor){
        //initialize ArrayList<MyPalette> here
        paletteList = colorDB.getPaletteList(cursor);

    }

    public void initRecycler(){
        //get the RecyclerView from the view
        RecyclerView recyclerView = view.findViewById(R.id.palettesRecycler);
        //then initialize the adapter, passing in the bookList
        adapter = new MyPalettesRecyclerViewAdapter(context,paletteList,listener);
        //and set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
        //and set the layout manager as well
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

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

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Lifecycles", "onResume: PalettesFragment resumed");
        //TODO: There *must* be a better way to refresh the lists than this (notifyDataSetChanged() isn't working)
        initPalettes(colorDB.getPaletteDatabaseCursor());
        initRecycler();
    }
}