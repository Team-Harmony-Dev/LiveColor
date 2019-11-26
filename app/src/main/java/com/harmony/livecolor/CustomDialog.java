package com.harmony.livecolor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * CustomDialog class
 * Manages the custom dialog and its methods for easier creation.
 *
 * AlertDialog alertDialogSave, alertDialogName: respective dialogs for showSaveDialog() and showSetNameDialog()
 * AlertDialog.Builder builder: used to build both dialogs
 * View saveDialogView, setNameDialogView: set the views of the dialogs to their respective XML files
 *
 * EditText newPaletteName allows access to the user's input palette name for adding to the database
 */
public class CustomDialog {

    Context context;
    Activity activity;

    AlertDialog alertDialogSave, alertDialogName;
    AlertDialog.Builder builder;
    View saveDialogView, setNameDialogView;
    EditText newPaletteName;

    /**
     * Constructor for Custom Dialog
     * @param context must be an Activity context
     */
    public CustomDialog(Context context){
        this.context = context;
        activity = (Activity) context;
    }

    /**
     * showSaveDialog
     * Creates and displays the inital dialog for saving a color.
     */
    public void showSaveDialog(){
        builder = new AlertDialog.Builder(context);

        saveDialogView = activity.getLayoutInflater().inflate(R.layout.dialog_save_color,null);
        LinearLayout savedColorsItem = saveDialogView.findViewById(R.id.savedColorsItem);
        LinearLayout newPaletteItem = saveDialogView.findViewById(R.id.newPaletteItem);

        builder.setView(saveDialogView);

        newPaletteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogSave.dismiss();
                showSetNameDialog();
            }
        });

        savedColorsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogSave.dismiss();
                Toast.makeText(context,
                        "Color has been saved to Saved Colors",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //TODO: add recycler functionality and set up appropriate listener

        alertDialogSave = builder.create();
        alertDialogSave.show();
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

        builder.setView(setNameDialogView);

        //Positive Confirm Button Click Listener that adds the input palette name to the database
        //TODO: implement palette and color database part
        builder.setPositiveButton("Create New Palette", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String newName = newPaletteName.getText().toString();
                Toast.makeText(context,
                        "New palette \"" + newName + "\" created!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogName = builder.create();
        alertDialogName.show();
    }
}
