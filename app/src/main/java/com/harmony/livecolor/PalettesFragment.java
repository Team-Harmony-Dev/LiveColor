package com.harmony.livecolor;

import android.content.Context;
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

import com.harmony.livecolor.dummy.DummyContent;
import com.harmony.livecolor.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PalettesFragment extends Fragment {

    private OnListFragmentInteractionListener listener;
    private Context context;
    private View view;
    private ArrayList<MyPalette> paletteList;
    private ArrayList<MyColor> colorList;
    ColorDatabase newColorDatabase;

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

        newColorDatabase = new ColorDatabase(getActivity());

        initPalettes();

        initRecycler();

        return view;
    }

    public void initPalettes(){
        //initialize ArrayList<MyPalette> here
        //will access palettes from database and put into MyPalette objects
        //TODO: Andrew's database code/method call will go here
        //initialize ArrayList<MyColors> here
        String TAG = "PALETTES";
        Cursor colorData = newColorDatabase.getColorInfoData();
        Cursor paletteData = newColorDatabase.getPaletteInfoData();
        paletteList = new ArrayList<>();
        colorList = new ArrayList<>();

        if (paletteData != null && paletteData.getCount() > 0) {
            paletteData.moveToFirst();
            colorData.move(1);
            do {
                //Log.d(TAG,  paletteDataData.getString(2));
                colorList.add(new MyColor(colorData.getString(0) + "",
                        colorData.getString(1) + "", colorData.getString(2) + "",
                        colorData.getString(3) + "", colorData.getString(4) + ""));
                paletteList.add(new MyPalette (paletteData.getString(0), paletteData.getString(1), colorList));
                }         while (colorData.moveToNext());
        }
    }

    public void initRecycler(){
        //get the RecyclerView from the view
        RecyclerView recyclerView = view.findViewById(R.id.palettesRecycler);
        //then initialize the adapter, passing in the bookList
        MyPalettesRecyclerViewAdapter adapter = new MyPalettesRecyclerViewAdapter(context,paletteList,listener);
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
