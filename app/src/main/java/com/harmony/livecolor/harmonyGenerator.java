package com.harmony.livecolor;

import android.util.Log;

public class harmonyGenerator {

    //TODO Determine what input we want to support. Can overload I guess.
    //  Currently I'm using hue (0..359) saturation (0..100) value (0..100)
    //  It's be nice to be able to do something like analogous(complement())
    //TODO Best format for returning? I can use arrays I guess.
    // Helper functions if I need decide on a different format.

    //TODO tests

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
                Log.d("S3US4", "Calculated analogous color "+numberOfColorsLeftFromMiddle
                        +" :"+analogHue);
            } else {
                int numberOfColorsRightFromMiddle = middleIndex - i;
                analogHue = hue - (degrees * numberOfColorsRightFromMiddle);
                Log.d("S3US4", "Calculated analogous color "+numberOfColorsRightFromMiddle
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
                Log.d("S3US4", "Calculated mono color "+numberOfColorsLeftFromMiddle
                        +" :"+monoValue);
            } else {
                int numberOfColorsRightFromMiddle = middleIndex - i;
                monoValue = hue - (percent * numberOfColorsRightFromMiddle);
                Log.d("S3US4", "Calculated mono color "+numberOfColorsRightFromMiddle
                        +" :"+monoValue);
            }

            monoValue = checkValueForOverflow(monoValue);

            int[] color = new int[] {hue, saturation, monoValue};
            monochromaticColors[i] = color;
        }
        return monochromaticColors;
    }

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
