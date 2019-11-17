package com.harmony.livecolor;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harmony.livecolor.SavedColorsFragment.OnListFragmentInteractionListener;
import com.harmony.livecolor.dummy.DummyContent.DummyItem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySavedColorsRecyclerViewAdapter extends RecyclerView.Adapter<MySavedColorsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<MyColor> myColors;
    private OnListFragmentInteractionListener listener;
    private Context context;

    public MySavedColorsRecyclerViewAdapter(Context context, ArrayList<MyColor> myColors, OnListFragmentInteractionListener listener) {
        Log.d("S3US1", "SavedColorsRecyclerViewAdapter: Constructed");
        this.context = context;
        this.myColors = myColors;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_saved_colors,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.color.setBackgroundColor(Color.parseColor(myColors.get(position).getHex()));
        holder.colorName.setText(myColors.get(position).getName());
        holder.colorHex.setText(myColors.get(position).getHex());

        holder.colorItem.setOnClickListener(getColorClickListener(position));
    }

    @Override
    public int getItemCount() {
        return myColors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView colorName;
        ImageView color;
        TextView colorHex;
        LinearLayout colorItem;

        public ViewHolder(View view) {
            super(view);
            colorName = view.findViewById(R.id.colorName);
            color = view.findViewById(R.id.color);
            colorHex = view.findViewById(R.id.colorHex);
            colorItem = view.findViewById(R.id.colorItem);
        }
    }

    //separate method for the onClickListener in order to pass the position from onBVH in
    View.OnClickListener getColorClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create intent for ColorInfo
                Intent intent = new Intent(context,ColorInfoActivity.class);
                //use putExtra Serializable to pass in desired color with intent
                intent.putExtra("color",myColors.get(position));
                //start new activity with this intent
                context.startActivity(intent);
            }
        };
    }
}
