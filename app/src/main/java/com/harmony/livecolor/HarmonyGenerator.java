package com.harmony.livecolor;

import android.util.Log;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.graphics.Color.RGBToHSV;
import static com.harmony.livecolor.UsefulFunctions.convertHSVtoRGB;

/**
 * Contains functions for generating colors for palettes based on a given color for HarmonyInfoActivity.java.
 * @author Dustin
 */
public class HarmonyGenerator {

    private static DecimalFormat df = new DecimalFormat("0.000");

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
        String colorName = "";
        MyColor colorObj = new MyColor(""+id, colorName, hex, rgb, hsv);
        return colorObj;
    }
    //For all these functions, currently I'm using hue (0..359) saturation (0..100) value (0..100)

    //Returns something like "FFFFFF"
    public static String hsvToStringHex(float[] color){
        //Easiest way to get hex is probably to convert to decimal rgb, and then to hex.
        float hue = color[0];
        float saturation = color[1];
        float value = color[2];
        int[] rgb = convertHSVtoRGB((int) hue, (int) (saturation * 100), (int) (value * 100));
        //https://stackoverflow.com/a/3607942
        String hex = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]).toUpperCase();
        return hex;
    }

    //Returns something like "(0, 0 ,0)"
    public static String hsvToStringRgb(float[] color){
        float hue = color[0];
        float saturation = color[1];
        float value = color[2];
        int[] rgb = convertHSVtoRGB((int) hue, (int) (saturation * 100), (int) (value * 100));
        String strRgb = "("+rgb[0]+", "+rgb[1]+", "+rgb[2]+")";
        return strRgb;
    }

    public static String hsvToStringHsv(float[] color){
        //Easiest way to get proper hsv is to use the built in function like we did in ColorPickerFragment.
        float hue = color[0];
        float saturation = color[1];
        float value = color[2];
        int[] rgb = convertHSVtoRGB((int) hue, (int) (saturation * 100), (int) (value * 100));
        //Stores the result values
        float[] hsvArray = new float[3];
        RGBToHSV(rgb[0],rgb[1],rgb[2],hsvArray);
        //TODO decide on a format for the string.
        // We aren't actually using this anyway currently since palettes aren't finished.
        Log.d("HarmonyGenerator", "hsvToStringHsv unformatted: " + hue + " " + saturation + " " + value);
        Log.d("HarmonyGenerator", "hsvToStringHsv formatted: " + Math.round(hue) + " " + df.format(saturation) + " " + df.format(value));
        return "(" + Math.round(hue) + ", " + df.format(saturation) + ", " + df.format(value) + ")";
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

    //
    public static float[][] complementScheme(float hue, float saturation, float value, int numberOfColors){
        //Hue, saturation, value. Three numbers to store in each array.
        final int numberOfComponents = 3;
        float[][] colors = new float[numberOfColors][numberOfComponents];
        colors[0] = new float[] {hue, saturation, value};
        colors[1] = complement(hue, saturation, value);
        return colors;
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
    //TODO how should we handle percent? Currently it does a percent of available space in each direction,
    //  meaning left and right colors are equally spaced with respect to their sides but not the opposite side.
    public static float[][] monochromaticScheme(float hue, float saturation, float value, float percent, int numberOfColors, boolean skipRedundantColors){
        //Hue, saturation, value. Three numbers to store in each array.
        final int numberOfComponents = 3;
        float distanceFromRight = 1-value;
        float distanceFromLeft = value;
        final int numberOfColorsLeft = (int) (numberOfColors / 2);
        final int numberOfColorsRight = numberOfColorsLeft;
        //How much spacing between each color.
        float differenceLeft = distanceFromLeft * percent / numberOfColorsLeft;
        boolean leftRedundant = false;
        if(differenceLeft <= 0.01){
            leftRedundant = true;
        }
        boolean shouldNotSkipLeft = !(skipRedundantColors && leftRedundant);
        float differenceRight = distanceFromRight * percent / numberOfColorsRight;
        boolean rightRedundant = false;
        if(differenceRight <= 0.01){
            rightRedundant = true;
        }
        boolean shouldNotSkipRight = !(skipRedundantColors && rightRedundant);

        int resultNumberOfColors = 1;
        if(!skipRedundantColors){
            resultNumberOfColors = numberOfColors;
        }
        if(skipRedundantColors && shouldNotSkipRight){
            resultNumberOfColors += (int) numberOfColors/2;
        }
        if(skipRedundantColors && shouldNotSkipLeft){
            resultNumberOfColors += (int) numberOfColors/2;
        }
        float[][] monochromaticColors = new float[resultNumberOfColors][numberOfComponents];
        Log.d("S4US4", "v="+value+" dfl="+distanceFromLeft+" dl="+differenceLeft
                + " dfr="+distanceFromRight+" dr="+differenceRight +" lR="+leftRedundant +" rR="+rightRedundant
                + " snsl=" + shouldNotSkipLeft + " snsr=" + shouldNotSkipRight + " rnc=" + resultNumberOfColors
        );
        int middleIndex = numberOfColors / 2;
        for(int i = 0; i < numberOfColors; ++i){
            float monoValue;
            //I actually messed up left and right, meant left to mean lighter, right to mean darker.
            //So this just does the reverse of what the names imply.
            //TODO cleanup
            if(i > middleIndex && shouldNotSkipLeft){
                int numberOfColorsLeftFromMiddle = middleIndex - i;
                monoValue = value + (differenceLeft * numberOfColorsLeftFromMiddle);
                Log.d("S4US4", "Calculated mono color  left "+numberOfColorsLeftFromMiddle
                        +" : "+monoValue);
            } else if (i == middleIndex || (i < middleIndex && shouldNotSkipRight)) {
                int numberOfColorsRightFromMiddle = i - middleIndex;
                monoValue = value - (differenceRight * numberOfColorsRightFromMiddle);
                Log.d("S4US4", "Calculated mono color right "+numberOfColorsRightFromMiddle
                        +" : "+monoValue);
            } else {
                continue;
            }

            //An overflow would just be an error, right?
            //monoValue = checkValueForOverflow(monoValue);

            float[] color = new float[] {hue, saturation, monoValue};
            int index = i;
            //If we skipped the real left side ("right" side according mislabeled junk) then we need to adjust the index
            if(i >= middleIndex && !shouldNotSkipRight){
                int numberOfColorsFromMiddle = i - middleIndex;
                index = numberOfColorsFromMiddle;
            }
            monochromaticColors[index] = color;
        }
        return monochromaticColors;
    }

    //TODO refactor, this is actually split-complementary.
    public static float[][] triadicScheme(float hue, float saturation, float value, int degrees, int numberOfColors){
        //Hue, saturation, value. Three numbers to store in each array.
        final int numberOfComponents = 3;
        Log.d("S4US4", "Getting analogous colors for triadic...");
        float[][] triadicColors = analogousScheme(complementHue(hue), saturation, value, degrees, numberOfColors);//new float[numberOfColors][numberOfComponents];
        int middleIndex = numberOfColors / 2;

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

    //Note that 0 and 360 are the same. If they go to 361, they loop around to 1, not 0.
    final static int MAX_HUE_DEGREES = 360;
    final static int MIN_HUE_DEGREES = 0;
    private static float checkHueForOverflow(float hue){
        if(hue > MAX_HUE_DEGREES){
            return hue - MAX_HUE_DEGREES;
        } else if(hue < MIN_HUE_DEGREES){
            return hue + MAX_HUE_DEGREES;
        } else {
            return hue;
        }
    }
}
