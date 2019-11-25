package com.harmony.livecolor;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harmony.livecolor.PalettesFragment.OnListFragmentInteractionListener;
import com.harmony.livecolor.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SaveDialogRecyclerViewAdapter extends RecyclerView.Adapter<SaveDialogRecyclerViewAdapter.ViewHolder> {

    private ArrayList<MyPalette> myPalettes;
    private OnListFragmentInteractionListener listener;
    private Context context;

    public SaveDialogRecyclerViewAdapter(Context context, ArrayList<MyPalette> myPalettes, OnListFragmentInteractionListener listener) {
        Log.d("S4US1", "SaveDialogRecyclerViewAdapter: Constructed");
        this.context = context;
        this.myPalettes = myPalettes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_palettes,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.paletteName.setText(myPalettes.get(position).getName());
        holder.paletteItem.setOnClickListener(getSaveDialogClickListener(position));
    }

    @Override
    public int getItemCount() {
        return myPalettes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView paletteName;
        LinearLayout paletteItem;

        public ViewHolder(View view) {
            super(view);
            paletteName = view.findViewById(R.id.paletteName);
            paletteItem = view.findViewById(R.id.paletteItem);
        }
    }

    //TODO: uncomment method when PaletteInfoActivity is complete
    // separate method for the onClickListener in order to pass the position from onBVH in
    View.OnClickListener getSaveDialogClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save to selected palette and close dialog
            }
        };
    }
}
