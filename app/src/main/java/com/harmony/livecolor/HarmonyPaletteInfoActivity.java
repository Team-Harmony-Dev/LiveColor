package com.harmony.livecolor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.harmony.livecolor.dummy.DummyContent;

import java.util.ArrayList;

public class HarmonyPaletteInfoActivity extends AppCompatActivity {

    private MyPalette palette;
    private ArrayList<MyColor> colorList;
    private SavedColorsFragment.OnListFragmentInteractionListener listener;

    private RecyclerView recyclerView;
    private MySavedColorsRecyclerViewAdapter adapter;

    ColorDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harmony_palette_info);

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
     *  SAVE BUTTON
     *  placeholder save button
     * @param view view of button
     *
     *
     * @author Daniel
     * part of the refactor
     *
     * once edit button, changed to save button by Paige
     */
    public void onClickSaveButton(View view){
        Log.d("DEBUG","save button pressed in palette info activity");
        CustomDialog setNameDialog = new CustomDialog(HarmonyPaletteInfoActivity.this,palette);
        setNameDialog.showSetNameDialog();
    }

    public void initColors(){
        //Color arraylist is initialized here. Gets arraylist of colors from palette object
        colorList = palette.getColors();
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

    @Override
    protected void onResume() {
        super.onResume();
        initColors();
        initRecycler();
    }
}
