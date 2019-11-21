package com.harmony.livecolor;

import android.util.Log;

import java.util.ArrayList;

import static android.graphics.Color.RGBToHSV;

//Contains functions for generating colors for palettes based on a given color.
public class harmonyGenerator {

    //TODO Determine what input we want to support. Can overload I guess.
    //  Currently I'm using hue (0..359) saturation (0..100) value (0..100)
    //  It's be nice to be able to do something like analogous(complement())
    //TODO Best format for returning? I can use arrays I guess.
    // Helper functions if I need decide on a different format.

    //TODO tests

    //Each color[] should be the hsv like what is returned by the rest of the functions in this file.
    public static ArrayList<MyColor> colorsToMyColors(int[][] colors, int numberOfColors){
        ArrayList<MyColor> myColorArrayList = new ArrayList<>();
        for(int i = 0; i < numberOfColors; ++i){
            MyColor color = hsvToMyColor(colors[i], i);
            myColorArrayList.add(color);
        }
        return myColorArrayList;
    }

    public static MyColor hsvToMyColor(int[] color, int id){
        String hex = hsvToStringHex(color);
        String rgb = hsvToStringRgb(color);
        String hsv = hsvToStringHsv(color);
        MyColor colorObj = new MyColor(""+id, ("Test"+id), hex, rgb, hsv);
        return colorObj;
    }
    //For all these functions, currently I'm using hue (0..359) saturation (0..100) value (0..100)

    //Returns something like "FFFFFF"
    public static String hsvToStringHex(int[] color){
        //Easiest way to get hex is probably to convert to decimal rgb, and then to hex.
        int hue = color[0];
        int saturation = color[1];
        int value = color[2];
        int[] rgb = EditColorActivity.convertHSVtoRGB(hue, saturation, value);
        //https://stackoverflow.com/a/3607942
        String hex = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
        return hex;
    }

    //Returns something like "(0, 0 ,0)"
    public static String hsvToStringRgb(int[] color){
        int hue = color[0];
        int saturation = color[1];
        int value = color[2];
        int[] rgb = EditColorActivity.convertHSVtoRGB(hue, saturation, value);
        String strRgb = "("+rgb[0]+", "+rgb[1]+", "+rgb[2]+")";
        return strRgb;
    }
    //TODO need to decide on a format for this.
    public static String hsvToStringHsv(int[] color){
        //Easiest way to get proper hsv is to use the built in function like we did in ColorPickerFragment.
        int hue = color[0];
        int saturation = color[1];
        int value = color[2];
        int[] rgb = EditColorActivity.convertHSVtoRGB(hue, saturation, value);
        //Stores the result values
        float[] hsvArray = new float[3];
        RGBToHSV(rgb[0],rgb[1],rgb[2],hsvArray);
        //TODO decide on a format for the string.
        return "TODO harmonyGenerator.hsvToStringHsv()";
    }

    //Complement is just the opposite hue
    public static int[] complement(int hue, int saturation, int value){
        int complementHue = hue + 180;
        complementHue = checkHueForOverflow(complementHue);
        int[] color = new int[] {complementHue, saturation, value};
        return color;
    }

    //numberOfColors should always be odd, the middle value is the color you passed in.
    public static int[][] analogousScheme(int hue, int saturation, int value, int degrees, int numberOfColors){
        //Hue, saturation, value. Three numbers to store in each array.
        final int numberOfComponents = 3;
        int[][] analogousColors = new int[numberOfColors][numberOfComponents];
        for(int i = 0; i < numberOfColors; ++i){
            int analogHue;
            int middleIndex = numberOfColors / 2;
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

            int[] color = new int[] {analogHue, saturation, value};
            analogousColors[i] = color;
        }
        return analogousColors;
    }

    //This seems not great. Maybe https://www.w3schools.com/colors/colors_monochromatic.asp ?
    //Unneeded parameters but I sorta want to stay consistent.
    public static int[] monochrome(int hue, int saturation, int value){
        int[] color = new int[] {0, 0, value};
        return color;
    }

    //numberOfColors should always be odd, the middle value is the color you passed in.
    public static int[][] monochromaticScheme(int hue, int saturation, int value, int percent, int numberOfColors){
        //Hue, saturation, value. Three numbers to store in each array.
        final int numberOfComponents = 3;
        int[][] monochromaticColors = new int[numberOfColors][numberOfComponents];
        for(int i = 0; i < numberOfColors; ++i){
            int monoValue;
            int middleIndex = numberOfColors / 2;
            if(i < middleIndex){
                int numberOfColorsLeftFromMiddle = middleIndex - i;
                monoValue = hue + (percent * numberOfColorsLeftFromMiddle);
                Log.d("S4US4", "Calculated mono color "+numberOfColorsLeftFromMiddle
                        +" :"+monoValue);
            } else {
                int numberOfColorsRightFromMiddle = middleIndex - i;
                monoValue = hue - (percent * numberOfColorsRightFromMiddle);
                Log.d("S4US4", "Calculated mono color "+numberOfColorsRightFromMiddle
                        +" :"+monoValue);
            }

            monoValue = checkValueForOverflow(monoValue);

            int[] color = new int[] {hue, saturation, monoValue};
            monochromaticColors[i] = color;
        }
        return monochromaticColors;
    }

    //TODO triadic scheme, just do analog of complement, with the original color as the middle value.

    //https://www.tigercolor.com/color-lab/color-theory/color-harmonies.htm
    //Complement could actually just call this.
    public static int[][] evenlySpacedScheme(int hue, int saturation, int value, int numberOfColors){
        //Hue, saturation, value. Three numbers to store in each array.
        final int numberOfComponents = 3;
        int[][] resultColors = new int[numberOfColors][numberOfComponents];
        int degrees = 360 / numberOfColors;
        for(int i = 0; i < numberOfColors; ++i){
            int resultHue = hue + (i * degrees);
            resultHue = checkHueForOverflow(resultHue);

            int[] color = new int[] {resultHue, saturation, value};
            resultColors[i] = color;
        }
        return resultColors;
    }

    private static int checkHueForOverflow(int hue){
        if(hue >= 360){
            return hue - 360;
        } else {
            return hue;
        }
    }
    private static int checkValueForOverflow(int value){
        if(value >= 100){
            return value - 100;
        } else {
            return value;
        }
    }
}
