package com.harmony.livecolor;

import android.util.Log;

import java.util.ArrayList;

import static android.graphics.Color.RGBToHSV;
import static java.lang.StrictMath.abs;

//Contains functions for generating colors for palettes based on a given color.
public class harmonyGenerator {

    //TODO Determine what input we want to support. Can overload I guess.
    //  Currently I'm using hue (0..359) saturation (0..100) value (0..100)
    //  It's be nice to be able to do something like analogous(complement())
    //TODO Best format for returning? I can use arrays I guess.
    // Helper functions if I need decide on a different format.

    //TODO tests

    //Each color[] should be the hsv like what is returned by the rest of the functions in this file.
    public static ArrayList<MyColor> colorsToMyColors(float[][] colors, int numberOfColors){
        ArrayList<MyColor> myColorArrayList = new ArrayList<>();
        for(int i = 0; i < numberOfColors; ++i){
            MyColor color = hsvToMyColor(colors[i], i);
            myColorArrayList.add(color);
        }
        return myColorArrayList;
    }

    public static MyColor hsvToMyColor(float[] color, int id){
        String hex = hsvToStringHex(color);
        String rgb = hsvToStringRgb(color);
        String hsv = hsvToStringHsv(color);
        MyColor colorObj = new MyColor(""+id, ("Test"+id), hex, rgb, hsv);
        return colorObj;
    }
    //For all these functions, currently I'm using hue (0..359) saturation (0..100) value (0..100)

    //Returns something like "FFFFFF"
    public static String hsvToStringHex(float[] color){
        //Easiest way to get hex is probably to convert to decimal rgb, and then to hex.
        float hue = color[0];
        float saturation = color[1];
        float value = color[2];
        int[] rgb = EditColorActivity.convertHSVtoRGB((int) hue, (int) (saturation * 100), (int) (value * 100));
        //https://stackoverflow.com/a/3607942
        String hex = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
        return hex;
    }

    //Returns something like "(0, 0 ,0)"
    public static String hsvToStringRgb(float[] color){
        float hue = color[0];
        float saturation = color[1];
        float value = color[2];
        int[] rgb = EditColorActivity.convertHSVtoRGB((int) hue, (int) (saturation * 100), (int) (value * 100));
        String strRgb = "("+rgb[0]+", "+rgb[1]+", "+rgb[2]+")";
        return strRgb;
    }
    //TODO need to decide on a format for this.
    public static String hsvToStringHsv(float[] color){
        //Easiest way to get proper hsv is to use the built in function like we did in ColorPickerFragment.
        float hue = color[0];
        float saturation = color[1];
        float value = color[2];
        int[] rgb = EditColorActivity.convertHSVtoRGB((int) hue, (int) (saturation * 100), (int) (value * 100));
        //Stores the result values
        float[] hsvArray = new float[3];
        RGBToHSV(rgb[0],rgb[1],rgb[2],hsvArray);
        //TODO decide on a format for the string.
        return "TODO harmonyGenerator.hsvToStringHsv()";
    }

    //Complement is just the opposite hue
    public static float[] complement(float hue, float saturation, float value){
        float complementHue = hue + 180;
        complementHue = checkHueForOverflow(complementHue);
        float[] color = new float[] {complementHue, saturation, value};
        return color;
    }
    public static float complementHue(float hue){
        float complementHue = hue + 180;
        complementHue = checkHueForOverflow(complementHue);
        return complementHue;
    }

    //numberOfColors should always be odd, the middle value is the color you passed in.
    public static float[][] analogousScheme(float hue, float saturation, float value, int degrees, int numberOfColors){
        //Hue, saturation, value. Three numbers to store in each array.
        final int numberOfComponents = 3;
        float[][] analogousColors = new float[numberOfColors][numberOfComponents];
        int middleIndex = numberOfColors / 2;
        for(int i = 0; i < numberOfColors; ++i){
            float analogHue;
            if(i < middleIndex){
                int numberOfColorsLeftFromMiddle = middleIndex - i;
                analogHue = hue + (degrees * numberOfColorsLeftFromMiddle);
                Log.d("S4US4", "Calculated analogous color -"+numberOfColorsLeftFromMiddle
                        +" :"+analogHue);
            } else {
                int numberOfColorsRightFromMiddle = i - middleIndex;
                analogHue = hue - (degrees * numberOfColorsRightFromMiddle);
                Log.d("S4US4", "Calculated analogous color +"+numberOfColorsRightFromMiddle
                        +" :"+analogHue);
            }

            analogHue = checkHueForOverflow(analogHue);

            float[] color = new float[] {analogHue, saturation, value};
            analogousColors[i] = color;
        }
        return analogousColors;
    }

    //This seems not great. Maybe https://www.w3schools.com/colors/colors_monochromatic.asp ?
    //Unneeded parameters but I sorta want to stay consistent.
    public static float[] monochrome(float hue, float saturation, float value){
        float[] color = new float[] {0, 0, value};
        return color;
    }

    //numberOfColors should always be odd, the middle value is the color you passed in.
    //TODO how to handle parcent? Currently it does a percent of available space in each direction,
    //  meaning left and right colors are equally spaced with respect to their sides but not the opposite side.
    public static float[][] monochromaticScheme(float hue, float saturation, float value, float percent, int numberOfColors){
        //Hue, saturation, value. Three numbers to store in each array.
        final int numberOfComponents = 3;
        float[][] monochromaticColors = new float[numberOfColors][numberOfComponents];
        float distanceFromRight = 1-value;
        float distanceFromLeft = value;
        final int numberOfColorsLeft = (int) (numberOfColors / 2);
        final int numberOfColorsRight = numberOfColorsLeft;
        //How much spacing between each color.
        float differenceLeft = distanceFromLeft * percent / numberOfColorsLeft;
        float differenceRight = distanceFromRight * percent / numberOfColorsRight;
        Log.d("S4US4", "v="+value+" dfl="+distanceFromLeft+" dl="+differenceLeft + " dfr="+distanceFromRight+" dr="+differenceRight);
        int middleIndex = numberOfColors / 2;
        for(int i = 0; i < numberOfColors; ++i){
            float monoValue;
            //I actually messed up left and right, meant left to mean lighter, right to mean darker.
            //So this just does the reverse of what the names imply.
            //TODO cleanup
            if(i > middleIndex){
                int numberOfColorsLeftFromMiddle = middleIndex - i;
                monoValue = value + (differenceLeft * numberOfColorsLeftFromMiddle);
                Log.d("S4US4", "Calculated mono color  left "+numberOfColorsLeftFromMiddle
                        +" : "+monoValue);
            } else {
                int numberOfColorsRightFromMiddle = i - middleIndex;
                monoValue = value - (differenceRight * numberOfColorsRightFromMiddle);
                Log.d("S4US4", "Calculated mono color right "+numberOfColorsRightFromMiddle
                        +" : "+monoValue);
            }

            //An overflow would just be an error, right?
            //monoValue = checkValueForOverflow(monoValue);

            float[] color = new float[] {hue, saturation, monoValue};
            monochromaticColors[i] = color;
        }
        return monochromaticColors;
    }

    public static float[][] triadicScheme(float hue, float saturation, float value, int degrees, int numberOfColors){
        //Hue, saturation, value. Three numbers to store in each array.
        final int numberOfComponents = 3;
        Log.d("S4US4", "Getting analogous colors for triadic...");
        float[][] triadicColors = analogousScheme(complementHue(hue), saturation, value, degrees, numberOfColors);//new float[numberOfColors][numberOfComponents];
        int middleIndex = numberOfColors / 2;
        /*
        for(int i = 0; i < numberOfColors; ++i){
            float triadicHue;
            if(i < middleIndex){
                int numberOfColorsLeftFromMiddle = middleIndex - i;
                triadicHue = 180 + hue + (degrees * numberOfColorsLeftFromMiddle);
                Log.d("S4US4", "Calculated triadic color -"+numberOfColorsLeftFromMiddle
                        +" :"+triadicHue);
            } else {
                int numberOfColorsRightFromMiddle = i - middleIndex;
                triadicHue = 180 + hue - (degrees * numberOfColorsRightFromMiddle);
                Log.d("S4US4", "Calculated triadic color +"+numberOfColorsRightFromMiddle
                        +" :"+triadicHue);
            }

            triadicHue = checkHueForOverflow(triadicHue);

            float[] color = new float[] {triadicHue, saturation, value};
            triadicColors[i] = color;
        }
        */
        float[] originalColor = new float[] {hue, saturation, value};
        triadicColors[middleIndex] = originalColor;

        return triadicColors;
    }

    //https://www.tigercolor.com/color-lab/color-theory/color-harmonies.htm
    //Complement could actually just call this.
    public static float[][] evenlySpacedScheme(float hue, float saturation, float value, int numberOfColors){
        //Hue, saturation, value. Three numbers to store in each array.
        final int numberOfComponents = 3;
        float[][] resultColors = new float[numberOfColors][numberOfComponents];
        int degrees = 360 / numberOfColors;
        for(int i = 0; i < numberOfColors; ++i){
            float resultHue = hue + (i * degrees);
            resultHue = checkHueForOverflow(resultHue);

            float[] color = new float[] {resultHue, saturation, value};
            resultColors[i] = color;
        }
        return resultColors;
    }

    private static float checkHueForOverflow(float hue){
        if(hue >= 360){
            return hue - 360;
        } else {
            return hue;
        }
    }
    /*I'm not sure this is actually meaningful
    private static float checkValueForOverflow(float value){
        if(value >= 100){
            return value - 100;
        } else {
            return value;
        }
    }
    */
}
