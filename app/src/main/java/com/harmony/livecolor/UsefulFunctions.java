package com.harmony.livecolor;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import static android.graphics.Color.RGBToHSV;

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
     * Converts given RGB ints to HSV values and returns them in an array
     * @param red
     * @param green
     * @param blue
     *
     * @return an array of length 3 containing the RGB values, respectively
     *
     * @author Gabby
     */
    public static int[] convertRGBtoHSV(int red, int green, int blue){
        float[] hsvArray = new float[3];
        RGBToHSV(red,green,blue,hsvArray);
        int[] convertedHSVForSeekbars = new int[3];
        convertedHSVForSeekbars[0] = Math.round(hsvArray[0]);
        convertedHSVForSeekbars[1] = Math.round((hsvArray[1])*100);
        convertedHSVForSeekbars[2] = Math.round((hsvArray[2])*100);
        return convertedHSVForSeekbars;
    }

    /**
     * Converts given HSV ints to RGB values and returns them in an array
     * @param hue
     * @param saturation
     * @param value
     *
     * @return an array of length 3 containing the HSV values, respectively
     *
     * @author Gabby
     */
    public static int[] convertHSVtoRGB(int hue, int saturation, int value){
        float[] hsv = new float[3];
        hsv[0] = hue;
        hsv[1] = ((float) saturation) / 100;
        hsv[2] = ((float) value) / 100;
        int outputColor = Color.HSVToColor(hsv);
        int[] newRGBValues = new int[3];
        newRGBValues[0] = Color.red(outputColor);
        newRGBValues[1] = Color.green(outputColor);
        newRGBValues[2] = Color.blue(outputColor);
        return newRGBValues;
    }

    /**
     *
     * @param color
     * @return
     */
    public static String colorIntToHex(int color){
        return String.format("#%06X", (0xFFFFFF & color));
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
        int padding = 40;
        view.setPadding(padding,padding,padding,padding);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        text.setGravity(Gravity.CENTER);

        toast.show();
    }
}
