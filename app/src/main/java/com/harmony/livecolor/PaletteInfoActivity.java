package com.harmony.livecolor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.harmony.livecolor.dummy.DummyContent;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PaletteInfoActivity extends AppCompatActivity {

    private MyPalette palette;
    private ArrayList<MyColor> paletteColors;
    private SavedColorsFragment.OnListFragmentInteractionListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette_info);

        //removes action bar
        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();



        //get extra containing the palette object
        Intent intent = getIntent();
        palette = (MyPalette) intent.getSerializableExtra("PALETTE");

        //get palette name textview and set name of palette on activity
        TextView paletteName = findViewById(R.id.paletteName);
        paletteName.setText(palette.getName());

        //Color arraylist is initialized here. Gets arraylist of colors from palette object
        paletteColors = palette.getColors();

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
        paletteColors = palette.getColors();
    }

    //initializes the recycler view with the given color information
    public void initRecycler(){
        //get the RecyclerView from the view
        RecyclerView recyclerView = findViewById(R.id.paletteInfoRecycler);
        //then initialize the adapter, passing in the bookList
        MySavedColorsRecyclerViewAdapter adapter = new MySavedColorsRecyclerViewAdapter(this,paletteColors,listener,"list");
        //and set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
        //and set the layout manager as well
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyContent.DummyItem item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initColors();
        initRecycler();
    }
}
