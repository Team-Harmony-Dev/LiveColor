package com.harmony.livecolor;

import android.content.Context;
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

        initPalettes();

        initRecycler();

        return view;
    }

    public void initPalettes(){
        //initialize ArrayList<MyPalette> here
        paletteList = new ArrayList<>();
        //will access palettes from database and put into MyPalette objects
        //TODO: Andrew's database code/method call will go here
        //Temporary Palettes atm:
        MyColor magenta = new MyColor("1","Hot Pink", "#FF00FF", "(255, 0, 255)","(5:001, 255, 255)");
        MyColor yellow = new MyColor("2","Highlighter", "#FFFF00", "(255, 255, 0)","(1:001, 255, 255)");
        MyColor cyan = new MyColor("3","Hot Cyan", "#00FFFF", "(0, 255, 255)","(3:001, 255, 255)");
        //test 3 colors
        ArrayList<MyColor> colorList1 = new ArrayList<>();
        colorList1.add(magenta);
        colorList1.add(yellow);
        colorList1.add(cyan);
        paletteList.add(new MyPalette("2","Three Colors",colorList1));
        //test 6 colors
        ArrayList<MyColor> colorList2 = new ArrayList<>();
        colorList2.add(magenta);
        colorList2.add(yellow);
        colorList2.add(cyan);
        colorList2.add(magenta);
        colorList2.add(yellow);
        colorList2.add(cyan);
        paletteList.add(new MyPalette("3","Six Colors",colorList2));
        //test 10+ colors
        /*
        ArrayList<MyColor> colorList3 = new ArrayList<>();
        colorList3.add(magenta);
        colorList3.add(yellow);
        colorList3.add(cyan);
        colorList3.add(magenta);
        colorList3.add(yellow);
        colorList3.add(cyan);
        colorList3.add(magenta);
        colorList3.add(yellow);
        colorList3.add(cyan);
        colorList3.add(magenta);
        colorList3.add(yellow);
        colorList3.add(cyan);
        paletteList.add(new MyPalette("3","Ten+ Colors",colorList3));
        */
        //Testing getting the analogous colors 15 degrees to each side of the given color ().
        int[][] testAnalogous = harmonyGenerator.analogousScheme(180, 100, 100, 15, 3);
        ArrayList<MyColor> testAnalogousMyColors = harmonyGenerator.colorsToMyColors(testAnalogous, 3);
        MyPalette testAnalogousPalette = new MyPalette("4", "Analogous", testAnalogousMyColors);
        paletteList.add(testAnalogousPalette);
    }

    public void initRecycler(){
        //get the RecyclerView from the view
        RecyclerView recyclerView = view.findViewById(R.id.palettesRecycler);
        //add divider decoration to make it match the assignment example?
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
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
