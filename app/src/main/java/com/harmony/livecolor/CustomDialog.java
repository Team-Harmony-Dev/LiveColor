package com.harmony.livecolor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;
import static com.harmony.livecolor.UsefulFunctions.makeToast;

/**
 * CustomDialog class
 *   Manages the custom dialog and its methods for easier creation.
 *   Takes the activity context to know where it is displaying the dialog
 *   As well as the color to be saved once the dialog is complete
 *
 * AlertDialog alertDialogSave, alertDialogName: respective dialogs for showSaveDialog() and showSetNameDialog()
 * AlertDialog.Builder builder: used to build both dialogs
 * View saveDialogView, setNameDialogView: set the views of the dialogs to their respective XML files
 *
 * EditText newPaletteName allows access to the user's input palette name for adding to the database
 */
public class CustomDialog implements SaveDialogRecyclerViewAdapter.OnListFragmentInteractionListener {

    Context context;
    Activity activity;

    ColorDatabase colorDB;

    AlertDialog alertDialogSave, alertDialogName;
    AlertDialog.Builder builder;
    View saveDialogView, setNameDialogView;
    EditText newPaletteName;

    String name, hex, rgb, hsv, id, newName;
    boolean newColor;

    ArrayList<MyPalette> paletteList;

    SaveListener listener;

    /**
     * Constructor for Custom Dialog
     * For when a new color needs to be saved to the database
     * @param context must be an Activity context
     */
    public CustomDialog(Context context, String name, String hex, String rgb, String hsv){
        this.context = context;
        activity = (Activity) context;

        colorDB = new ColorDatabase(activity);

        this.name = name;
        this.hex = hex;
        this.rgb = rgb;
        this.hsv = hsv;
        this.id = "";
        this.newName = "";

        newColor = true;
    }

    /**
     * Constructor for Custom Dialog
     * For when a palette only needs to be renamed
     * @param context must be an Activity context, is the activity that the dialog is displaying on
     */
    public CustomDialog(Context context, String id){
        this.context = context;
        activity = (Activity) context;

        colorDB = new ColorDatabase(activity);

        this.id = id;
        this.name = "";
        this.hex = "";
        this.rgb = "";
        this.hsv = "";
        this.newName = "";

        newColor = false;
    }

    /**
     * Adds a listener that will be notified when a save finishes (and not when the dialog is cancelled).
     * You need to use this before getting any callbacks.
     *
     * @param newListener Generally you just pass this "this". Where to callback to or whatever. TODO
     */
    public void addListener(SaveListener newListener) {
        listener = newListener;
    }

    /**
     * Calls the callback function, if it was given any. They can fill in the save button.
     */
    private void notifySaveCompleted(){
        if(listener != null) {
            listener.saveHappened();
        }
    }

    /**
     * showSaveDialog
     * Creates and displays the initial dialog for saving a color.
     */
    public void showSaveDialog(){
        builder = new AlertDialog.Builder(context);

        saveDialogView = activity.getLayoutInflater().inflate(R.layout.dialog_save_color,null);
        LinearLayout savedColorsItem = saveDialogView.findViewById(R.id.savedColorsItem);
        LinearLayout newPaletteItem = saveDialogView.findViewById(R.id.newPaletteItem);

        initPalettes();

        initRecycler();

        builder.setView(saveDialogView);

        newPaletteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogSave.dismiss();
                showSetNameDialog();

                //Tell the listener that it's saved, so it can fill in the save button.
                //notifySaveCompleted();
            }
        });



        savedColorsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetch new or existing color id for given color
                long colorId = colorDB.addColorInfoData(name, hex, rgb, hsv);
                Log.d("Saved Colors", "onClick: color returned as " + colorId);
                //save color to Saved Colors palette (id = 1)
                if(colorDB.addColorToPalette("1",Long.toString(colorId))) {
                    alertDialogSave.dismiss();
                    makeToast("Color has been saved to Saved Colors", context);
                } else {
                    alertDialogSave.dismiss();
                    makeToast("This color already exists in Saved Colors", context);
                }

                //Tell the listener that it's saved, so it can fill in the save button.
                notifySaveCompleted();
            }
        });

        alertDialogSave = builder.create();
        alertDialogSave.show();
    }

    /**
     * initialize palette list from database for recycler
     * for displaying existing palettes that can be saved to
     */
    private void initPalettes() {
        //initialize ArrayList<MyPalette> here
        //cursor from getPaletteDatabaseCursor is closed in getPaletteList
        paletteList = colorDB.getPaletteList(colorDB.getPaletteDatabaseCursor());
    }

    /**
     * initialize recycler with the palette info
     * for displaying existing palettes that can be saved to
     */
    private void initRecycler() {
        //get the RecyclerView from the view
        RecyclerView recyclerView = saveDialogView.findViewById(R.id.dialogRecycler);
        //then initialize the adapter, passing in the list
        SaveDialogRecyclerViewAdapter adapter = new SaveDialogRecyclerViewAdapter(context, paletteList);
        //set item listener that gets position
        adapter.setOnListFragmentInteractionListener(this);
        //add dividers
        DividerItemDecoration divider = new DividerItemDecoration(context, VERTICAL);
        recyclerView.addItemDecoration(divider);
        //and set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
        //and set the layout manager as well
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     * showNewNameDialog()
     * Creates and displays the dialog for creating a new palette to add a color to.
     */
    public void showSetNameDialog(){
        //set up builder
        builder = new AlertDialog.Builder(context);

        //add appropriate view to builder
        setNameDialogView = activity.getLayoutInflater().inflate(R.layout.dialog_rename_palette,null);
        newPaletteName = setNameDialogView.findViewById(R.id.newPaletteName);

        int[][] states = new int[][] {

                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] {-android.R.attr.state_selected}, // unselected
                new int[] { android.R.attr.state_active}, // active
                new int[] { android.R.attr.state_pressed}, // pressed
                new int[] { android.R.attr.state_checked},  // checked
                new int[] { android.R.attr.state_selected}, // selected
                new int[] { android.R.attr.state_enabled} // enabled
        };

        int[] accent = new int[] {
                Color.parseColor(AccentUtils.getAccent(context)),
                Color.parseColor(AccentUtils.getAccent(context)),
                Color.parseColor(AccentUtils.getAccent(context)),
                Color.parseColor(AccentUtils.getAccent(context)),
                Color.parseColor(AccentUtils.getAccent(context)),
                Color.parseColor(AccentUtils.getAccent(context)),
                Color.parseColor(AccentUtils.getAccent(context)),
                Color.parseColor(AccentUtils.getAccent(context))
        };

        ColorStateList accentList = new ColorStateList(states, accent);


        newPaletteName.setTextColor(accentList);
        newPaletteName.setCompoundDrawableTintList(accentList);
        newPaletteName.setHintTextColor(accentList);
        newPaletteName.setForegroundTintList(accentList);
        newPaletteName.setBackgroundTintList(accentList);
//        newPaletteName.setTextCursorDrawable(R.id.color_cursor);

        builder.setView(setNameDialogView);

        //Positive Confirm Button Click Listener that adds the input palette name to the database
        //TODO: UPDATE TO USE THE APPROPRIATE HELPER METHODS INSTEAD
        builder.setPositiveButton("Save Palette", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //gets the input name from edittext field
                newName = newPaletteName.getText().toString();

                //if you are creating a new name for a palette
                if(newColor) {
                    Log.d("CustomDialog", "setName is for new palette");
                    //adds the color to the database
                    long colorId = colorDB.addColorInfoData(name, hex, rgb, hsv);
                    //gets newest added color and adds it to the palette
                    colorDB.addNewPalette(newName, Long.toString(colorId));

                    dialog.dismiss();
                    makeToast("New palette \"" + newName + "\" created!", context);

                    //Tell the listener that it's saved, so it can fill in the save button.
                    notifySaveCompleted();
                } //if you are renaming an existing palette
                else {
                    Log.d("CustomDialog", "setName is for existing palette");
                    boolean nameChanged = colorDB.changePaletteName(id,newName);
                    dialog.dismiss();
                    if(nameChanged) {
                        //obtain the new palette name to update the current activity with said name
                        TextView tvPaletteName = activity.findViewById(R.id.paletteName);
                        tvPaletteName.setText(newName);
                        //send confirmation message
                        makeToast("Set palette name to \"" + newName + "\"", context);
                    }
                    else {
                        //send error message
                        makeToast("Changing palette name failed", context);
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialogName = builder.create();
        alertDialogName.show();
        alertDialogName.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(AccentUtils.getAccent(context)));
        alertDialogName.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor(AccentUtils.getAccent(context)));
    }

    /**
     * listener from the recycler, saves a color to the selected palette
     * @param palette contains the data for the selected palette
     */
    @Override
    public void onListFragmentInteraction(MyPalette palette) {
        //fetch new or existing color id for given color
        long colorId = colorDB.addColorInfoData(name, hex, rgb, hsv);
        Log.d("CustomDialog", "onListFragmentInteraction: color returned as " + colorId);
        //save color to existing palette by id
        Log.d("CustomDialog", "onListFragmentInteraction: saving " + colorId + " to palette " + palette.getId());
        if(colorDB.addColorToPalette(palette.getId(),Long.toString(colorId))) {
            alertDialogSave.dismiss();
            makeToast("Color has been saved to \"" + palette.getName() + "\"", context);
        } else {
            alertDialogSave.dismiss();
            makeToast("This color already exists in \"" + palette.getName() + "\"", context);
        }

        //Tell the listener that it's saved, so it can fill in the save button.
        notifySaveCompleted();
    }
}