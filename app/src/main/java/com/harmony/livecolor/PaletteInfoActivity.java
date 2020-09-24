package com.harmony.livecolor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.harmony.livecolor.dummy.DummyContent;

import java.util.ArrayList;

public class PaletteInfoActivity extends AppCompatActivity {

    private MyPalette palette;
    private ArrayList<MyColor> colorList;
    private SavedColorsFragment.OnListFragmentInteractionListener listener;

    private RecyclerView recyclerView;
    private MySavedColorsRecyclerViewAdapter adapter;

    ColorDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette_info);

        //removes action bar
        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        db = new ColorDatabase(this);

        //get extra containing the palette object
        Intent intent = getIntent();
        palette = (MyPalette) intent.getSerializableExtra("PALETTE");

        //get palette name textview and set name of palette on activity
        TextView paletteName = findViewById(R.id.paletteName);
        paletteName.setText(palette.getName());

        //Color arraylist is initialized here. Gets arraylist of colors from palette object
        colorList = palette.getColors();

        //initialize the recycler
        initRecycler();
    }

    /**
     *  BACK BUTTON
     *  simple back button
     * @param view view of button
     *
     *
     * part of the refactor
     * set back button to leave activity
     */
    public void onClickBackButton(View view){
        finish();
    }

    /**
     *  EDIT BUTTON
     *  placeholder edit button
     * @param view view of button
     *
     *
     * @author Daniel
     * part of the refactor
     *
     */
    public void onClickEditButton(View view){
        Log.d("DEBUG","edit button pressed in palette info activity");
        CustomDialog setNameDialog = new CustomDialog(PaletteInfoActivity.this,palette.getId());
        setNameDialog.showSetNameDialog();
    }

    public void initColors(){
        //Color arraylist is initialized here. Gets arraylist of colors from palette object
        colorList = db.getColorList(palette.getId(),false);
    }

    //initializes the recycler view with the given color information
    public void initRecycler(){
        //get the RecyclerView from the view
        recyclerView = findViewById(R.id.paletteInfoRecycler);
        //then initialize the adapter, passing in the bookList
        adapter = new MySavedColorsRecyclerViewAdapter(this, colorList,listener,"list");
        //and set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
        //and set the layout manager as well
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(this));
        //set ItemTouchHelper for item deletion
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        //set animation
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this,R.anim.layout_slide_from_right);
        recyclerView.setLayoutAnimation(animation);

    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyContent.DummyItem item);
    }

    MyColor deletedColor = null;
    String deleteMsg = "Deleted ";

    /**
     * Handles swipe listening for individual list items. Used for list item deletion. Can be used for list rearranging as well in the future.
     */
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //we can use a switch to handle different cases for different swipe directions if desired
            final int position = viewHolder.getAdapterPosition();
            deletedColor = colorList.get(position);
            colorList.remove(position);
            db.updateRefString(palette.getId(), colorList, false);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position,colorList.size());
            Snackbar.make(recyclerView, deleteMsg + deletedColor.getName(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            colorList.add(position, deletedColor);
                            db.updateRefString(palette.getId(), colorList, false);
                            adapter.notifyItemInserted(position);
                        }
                    }).show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        initColors();
        initRecycler();
    }
}
