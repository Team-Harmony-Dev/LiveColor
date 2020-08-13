package com.harmony.livecolor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import static android.graphics.Color.RGBToHSV;
import static com.harmony.livecolor.UsefulFunctions.getIntFromColor;

public class ColorOTDayDialog {
    int colorOfTheDay;
    Date todayDate;
    Context context;
    Activity activity;
    AlertDialog alertDialogSave;
    View colorOTDView;
    String strDt;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    HashMap<String, Integer> specialDates;


    public ColorOTDayDialog(Context context) {
        this.context = context;
        activity = (Activity) context;
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        specialDates = new HashMap<String, Integer>();
        specialDates.put("07/05", Color.parseColor("#006c2e"));
    }

    public void showColorOTD() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        colorOTDView = activity.getLayoutInflater().inflate(R.layout.dialog_color_day,null);

        //Get/Set the current date
        todayDate = Calendar.getInstance().getTime();
        TextView dateView = (TextView) colorOTDView.findViewById(R.id.textView);
        SimpleDateFormat simpleDate =  new SimpleDateFormat("MMMM dd yyyy");
        SimpleDateFormat monthDay =  new SimpleDateFormat("MM/dd");
        strDt = simpleDate.format(todayDate);
        dateView.setText(strDt);

        // If it is a new day - create and show the dialog!
        if(newDay()){
            //Store the date in shared preferences
            editor.putString("Date", strDt);
            editor.commit();

            String shortHand = monthDay.format(todayDate);

            //Generate random color
            if(specialDates.containsKey(shortHand)){
                colorOfTheDay = specialDates.get(shortHand);
            } else {
                colorOfTheDay = generateRandomColor();
            }
            ImageView colorView = colorOTDView.findViewById(R.id.colorOTDView);
            colorView.setBackgroundColor(colorOfTheDay);

            //Be able to fetch color name on first load? Works after first "start"
            final TextView colorName = colorOTDView.findViewById(R.id.colorOTDNameView);
            Log.d("V2S2 bugfix cotd", "------------------------------------------------------size before="+colorName.getTextSize());
            //70% is arbitraryish.
            ColorNameGetterCSV.getAndFitName(colorName, "#"+ColorPickerFragment.colorToHex(colorOfTheDay), 0.70, 30);
            Log.d("V2S2 bugfix cotd", "size after set="+colorName.getTextSize());

            //Set onClick for back button
            final ImageButton backButton = colorOTDView.findViewById(R.id.backCOTD);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialogSave.cancel();
                }
            });

            //Set onClick for save color of the day button
            final ImageButton saveCOTD = colorOTDView.findViewById(R.id.saveCOTD);
            saveCOTD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("V2S2 bugfix cotd", "size on click="+colorName.getTextSize());
                    final String colorNameStr = (String) colorName.getText();

                    int RV = Color.red(colorOfTheDay);
                    int GV = Color.green(colorOfTheDay);
                    int BV = Color.blue(colorOfTheDay);

                    //update the RGB value displayed
                    String RGB = String.format("(%1$d, %2$d, %3$d)",RV,GV,BV);

                    float[] hsvArray = new float[3];
                    RGBToHSV(RV,GV,BV,hsvArray);
                    int hue = Math.round(hsvArray[0]);
                    String HSV = String.format("(%1$d, %2$.3f, %3$.3f)",hue,hsvArray[1],hsvArray[2]);

                    //Color name won't save properly until updated color name fetching is added
                    CustomDialog pickerDialog = new CustomDialog(activity,colorNameStr,UsefulFunctions.colorIntToHex(colorOfTheDay),RGB,HSV);
                    pickerDialog.showSaveDialog();

                    alertDialogSave.cancel();
                }
            });

            builder.setView(colorOTDView);
            alertDialogSave = builder.create();

            alertDialogSave.show();
            //makeShine();
            Log.d("V2S2 bugfix cotd", "size after show="+colorName.getTextSize());
        }
    }

    /**
     * GENERATE A RANDOM COTD
     * generates a random RGB color, based on the date
     * each color channel is seeded and generated individually as follows
     * R: MMDDYY
     * G: YYMMDD
     * B: DDYYMM
     * a simple rotation on the american dat format in one number
     *
     * shame and guilt
     *
     * @return getIntFromColor( R, G, B)
     *
     * @author Daniel, Gabby
     */
    public int generateRandomColor(){
        DateFormat dfRed = new SimpleDateFormat("MMddyy");
        DateFormat dfGreen = new SimpleDateFormat("yyMMdd");
        DateFormat dfBlue = new SimpleDateFormat("ddyyMM");


        int nowRed = Integer.parseInt(dfRed.format(new Date()));
        int nowGreen = Integer.parseInt(dfGreen.format(new Date()));
        int nowBlue = Integer.parseInt(dfBlue.format(new Date()));

        Log.d("DEBUG", "generateRandomColor: seeds" + " R: " + nowRed + " G: " + nowGreen + " B: " + nowBlue);

        Random randRedGen = new Random(nowRed);
        Random randGreenGen = new Random(nowGreen);
        Random randBlueGen = new Random(nowBlue);

        int randRed = randRedGen.nextInt(256);
        int randGreen = randGreenGen.nextInt(256);
        int randBlue = randBlueGen.nextInt(256);

        Log.d("DEBUG", "generateRandomColor: values" + " R: " + randRed + " G: " + randGreen + " B: " + randBlue);

        return getIntFromColor(randRed, randGreen, randBlue);
    }
    

    public boolean newDay(){
        String storedDate = sharedPref.getString("Date", "No date");
        Log.d("storedDate", storedDate);
        Log.d("strDt", strDt);
        if(storedDate.equals("No date")){
            //First launch of the app
            return true;
        }
        if(storedDate.equals(strDt)){
            return false;
        } else {
            return true;
        }
    }

    public void makeShine(){
        ImageView colorView = colorOTDView.findViewById(R.id.colorOTDView);
        //ImageView shineView = colorOTDView.findViewById(R.id.shine);

        //Attempted rotated shine
        /*RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(10000);
        rotate.setInterpolator(new LinearInterpolator());
        shineView.startAnimation(rotate);*/

        //Attempted shine on color (vertical)
        /*String toX = Integer.toString(colorView.getWidth()+shineView.getWidth());
        Log.d("toXDelta is", toX);
        Animation animation = new TranslateAnimation(-120, 220,0, 0);
        animation.setDuration(1550);
        animation.setFillAfter(true);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        shineView.startAnimation(animation);*/
    }
}
