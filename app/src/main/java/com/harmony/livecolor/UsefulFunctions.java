package com.harmony.livecolor;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

/*
A class that contains some useful functions. Currently has:
    - getIntFromColor
    - makeToast
 */
public class UsefulFunctions {
    /**
     *  Takes in RGB values and returns the associated color int
     * @param Red red value (R)
     * @param Green green value (G)
     * @param Blue blue value (B)
     * @return the color int of the passed RGB value
     */
    public static int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    /**
     * Creates custom toast and displays it with the passed message
     * @param toasty (passed message - string)
     * @author Gabby
     */
    public static void makeToast(String toasty, Context context){
        Toast toast = Toast.makeText(context,
                toasty,
                Toast.LENGTH_SHORT);
        View view = toast.getView();

        view.setBackgroundResource(R.color.colorDark);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);

        toast.show();
    }
}
