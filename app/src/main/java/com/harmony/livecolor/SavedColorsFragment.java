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

        initColors();

        initRecycler();

        return view;
    }

    public void initColors(){
        //initialize ArrayList<MyColors> here
        colorList = new ArrayList<>();
        //will access colors from database and put into MyColor objects
        //TODO: Andrew's database code/method call will go here
        //Temporary Colors atm:
        colorList.add(new MyColor("1","Hot Pink", "#FF00FF", "(255, 0, 255)","(5:001, 255, 255)"));
        colorList.add(new MyColor("2","Highlighter", "#FFFF00", "(255, 255, 0)","(1:001, 255, 255)"));
        colorList.add(new MyColor("3","Hot Cyan", "#00FFFF", "(0, 255, 255)","(3:001, 255, 255)"));
        colorList.add(new MyColor("4","Hot Pink", "#FF00FF", "(255, 0, 255)","(5:001, 255, 255)"));
        colorList.add(new MyColor("5","Highlighter", "#FFFF00", "(255, 255, 0)","(1:001, 255, 255)"));
        colorList.add(new MyColor("6","Hot Cyan", "#00FFFF", "(0, 255, 255)","(3:001, 255, 255)"));
    }

    public void initRecycler(){
        //get the RecyclerView from the view
        RecyclerView recyclerView = view.findViewById(R.id.savedColorsRecycler);
        //add divider decoration to make it match the assignment example?
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        //then initialize the adapter, passing in the bookList
        MySavedColorsRecyclerViewAdapter adapter = new MySavedColorsRecyclerViewAdapter(context,colorList,listener);
        //and set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
        //and set the layout manager as well
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
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
