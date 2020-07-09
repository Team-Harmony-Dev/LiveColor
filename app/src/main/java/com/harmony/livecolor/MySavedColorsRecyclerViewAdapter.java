package com.harmony.livecolor;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import java.util.Collection;
import java.util.Collections;
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
    private String selectedView;
    ColorDatabase newColorDatabase;

    /**
     * MySavedColorsRecyclerViewAdapter
     * @param context
     * @param myColors
     * @param listener
     * @param selectedV - The selected view: "list" or "grid" - Gabby
     */
    public MySavedColorsRecyclerViewAdapter(Context context, ArrayList<MyColor> myColors, OnListFragmentInteractionListener listener, String selectedV) {
        Log.d("S3US1", "SavedColorsRecyclerViewAdapter: Constructed");
        this.context = context;
        this.myColors = myColors;

        /* Makes a shallow copy of the saved color array, reverses it, and sets it as the arrayList used by the recycler adapter
           This is so the grid layout will have the newest colors at the top - Gabby
         */
        if(selectedV != "list"){
            ArrayList<MyColor> copyColors = new ArrayList<>(myColors);
            Collections.reverse(copyColors);
            this.myColors = copyColors;
        }

        this.listener = listener;
        this.selectedView = selectedV;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_saved_colors,parent,false);
        ViewHolder holder = new ViewHolder(view);

        /*
         * This changes the weights on the saved color cardView so that the text is "invisible" (weight 0) if the selected view is not list - Gabby
         */
        if(this.selectedView != "list") {
            LinearLayout cardText = view.findViewById(R.id.listText);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    cardText.getLayoutParams();
            params.weight = 0f;

            ImageView colorImage = view.findViewById(R.id.color);
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) colorImage.getLayoutParams();
            params2.setMargins(3, 3, 3, 3);
            params2.weight = 1.0f;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.color.setBackgroundColor(Color.parseColor(myColors.get(position).getHex()));
        holder.colorName.setText(myColors.get(position).getName());

        String hex = myColors.get(position).getHex();
        /*
        //Get the last 6 digits, the actual hex characters
        String hexValue = hex.substring(hex.length()-6, hex.length());
        Log.d("Bugfix saved colorname", "HexValue = "+hexValue);
        int pixel = 0;
        final double viewWidthPercentOfScreen = 0.80;
        final float maxFontSize = 26;
        colorNameGetter.updateViewWithColorName(holder.colorName, pixel, );
        */
        holder.colorHex.setText(hex);

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
                newColorDatabase = new ColorDatabase(context);
                final Cursor colorData = newColorDatabase.getColorInfoData();
                int size = myColors.size();
                if(selectedView == "list"){
                    colorData.moveToPosition(position);
                } else {
                    colorData.moveToPosition(size - position - 1);
                }

                Intent intent=new Intent(context, ColorInfoActivity.class);
                intent.putExtra("id", colorData.getString(0));
                intent.putExtra("name", colorData.getString(1));
                intent.putExtra("hex", colorData.getString(2));
                intent.putExtra("rgb", colorData.getString(3));
                intent.putExtra("hsv", colorData.getString(4));
                //start new activity with this intent
                context.startActivity(intent);
            }
        };
    }
}
