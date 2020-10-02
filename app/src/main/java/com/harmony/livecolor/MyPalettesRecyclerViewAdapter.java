package com.harmony.livecolor;

import androidx.cardview.widget.CardView;
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

import com.harmony.livecolor.PalettesFragment.OnListFragmentInteractionListener;
import com.harmony.livecolor.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPalettesRecyclerViewAdapter extends RecyclerView.Adapter<MyPalettesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<MyPalette> myPalettes;
    private OnListFragmentInteractionListener listener;
    private Context context;
    private boolean isHarmony;

    public MyPalettesRecyclerViewAdapter(Context context, ArrayList<MyPalette> myPalettes, OnListFragmentInteractionListener listener, boolean isHarmony) {
        Log.d("S3US2", "PaletteColorsRecyclerViewAdapter: Constructed");
        this.context = context;
        this.myPalettes = myPalettes;
        this.listener = listener;
        this.isHarmony = isHarmony;
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

        ArrayList<MyColor> paletteColors = myPalettes.get(position).getColors();
        //code for displaying maximum of 10 colors in the palette preview:
        //loop through first up-to 10 colors in palette
        for(int i = 0; i < 10; i++){
            //break if null aka end of palette
            if(i >= paletteColors.size()){
                //set image to weight 0 so that it doesn't appear
                holder.displayColors.get(i).setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.0f
                ));
            } else{
                MyColor thisColor = paletteColors.get(i);
                //set imageview of i to weight1
                holder.displayColors.get(i).setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                //set imageview to i's color
                holder.displayColors.get(i).setBackgroundColor(Color.parseColor(thisColor.getHex()));
            }
        }

        holder.paletteItem.setOnClickListener(getPaletteClickListener(position));
    }

    @Override
    public int getItemCount() {
        return myPalettes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView paletteName;
        ArrayList<ImageView> displayColors;
        ImageView color1, color2, color3, color4, color5, color6, color7, color8, color9, color10;
        CardView paletteItem;

        public ViewHolder(View view) {
            super(view);
            paletteName = view.findViewById(R.id.paletteName);
            paletteItem = view.findViewById(R.id.paletteItem);
            color1 = view.findViewById(R.id.color1);
            color2 = view.findViewById(R.id.color2);
            color3 = view.findViewById(R.id.color3);
            color4 = view.findViewById(R.id.color4);
            color5 = view.findViewById(R.id.color5);
            color6 = view.findViewById(R.id.color6);
            color7 = view.findViewById(R.id.color7);
            color8 = view.findViewById(R.id.color8);
            color9 = view.findViewById(R.id.color9);
            color10 = view.findViewById(R.id.color10);
            displayColors = new ArrayList<>();
            displayColors.add(color1);
            displayColors.add(color2);
            displayColors.add(color3);
            displayColors.add(color4);
            displayColors.add(color5);
            displayColors.add(color6);
            displayColors.add(color7);
            displayColors.add(color8);
            displayColors.add(color9);
            displayColors.add(color10);
        }
    }

    //TODO: UPDATE TO WORK WITH PALETTE FORMAT
    View.OnClickListener getPaletteClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create intent for PaletteInfo depending on whether palette is a harmony or not
                Intent intent;
                if(isHarmony){
                    intent = new Intent(context,HarmonyPaletteInfoActivity.class);
                } else {
                    intent = new Intent(context,PaletteInfoActivity.class);
                }
                //use putExtra Serializable to pass in desired color with intent
                intent.putExtra("PALETTE",myPalettes.get(position));
                //start new activity with this intent
                context.startActivity(intent);
            }
        };
    }
}
