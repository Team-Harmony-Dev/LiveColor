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

import java.util.ArrayList;

public class SaveDialogRecyclerViewAdapter extends RecyclerView.Adapter<SaveDialogRecyclerViewAdapter.ViewHolder> {

    private ArrayList<MyPalette> myPalettes;
    private OnListFragmentInteractionListener listener;
    private Context context;

    public SaveDialogRecyclerViewAdapter(Context context, ArrayList<MyPalette> myPalettes) {
        Log.d("Lifecycles", "SaveDialogRecyclerViewAdapter: Constructed");
        this.context = context;
        this.myPalettes = myPalettes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_save_color_palette_item,parent,false);
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

    // separate method for the onClickListener in order to pass the position from onBVH in
    //sends the palette in that position to CustomDialog with the info on the color to be saved
    View.OnClickListener getSaveDialogClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onListFragmentInteraction(myPalettes.get(position));
            }
        };
    }

    public void setOnListFragmentInteractionListener(OnListFragmentInteractionListener listener) {
        this.listener = listener;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(MyPalette palette);
    }
}
