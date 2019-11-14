package com.harmony.livecolor;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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
public class MyPalettesRecyclerViewAdapter extends RecyclerView.Adapter<MyPalettesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<MyPalette> myPalettes;
    private OnListFragmentInteractionListener listener;
    private Context context;

    public MyPalettesRecyclerViewAdapter(Context context, ArrayList<MyPalette> myPalettes, OnListFragmentInteractionListener listener) {
        Log.d("S3US2", "PaletteColorsRecyclerViewAdapter: Constructed");
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

        ArrayList<MyColor> paletteColors = myPalettes.get(position).getColors();
        //loop through first up-to 10 colors in palette
        for(int i = 0; i < 10; i++){
            MyColor thisColor = paletteColors.get(i);
            //break if null aka end of palette
            if(thisColor == null){
                break;
            }
            //set imageview of i to weight1

            //set imageview to i's color
            //set linear layout's weight sum to i
        }
    }

    @Override
    public int getItemCount() {
        return myPalettes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView paletteName;
        LinearLayout listItem;

        public ViewHolder(View view) {
            super(view);
            paletteName = view.findViewById(R.id.paletteName);
            listItem = view.findViewById(R.id.paletteItem);
        }
    }

    //TODO: uncomment method when PaletteInfoActivity is complete
    // separate method for the onClickListener in order to pass the position from onBVH in
    /*View.OnClickListener getPaletteClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create intent for PaletteInfo
                Intent intent = new Intent(context,PaletteInfoActivity.class);
                //use putExtra Serializable to pass in desired color with intent
                intent.putExtra("palette",myPalettes.get(position));
                //start new activity with this intent
                context.startActivity(intent);
            }
        };
    }*/
}
