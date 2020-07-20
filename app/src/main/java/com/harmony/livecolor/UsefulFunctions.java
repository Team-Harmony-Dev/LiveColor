package com.harmony.livecolor;

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
}
