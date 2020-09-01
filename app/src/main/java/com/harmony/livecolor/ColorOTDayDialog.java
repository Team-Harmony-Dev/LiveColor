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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
    HashMap<String, Integer[]> specialDates;


    public ColorOTDayDialog(Context context) {
        this.context = context;
        activity = (Activity) context;
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        specialDates = loadSpecialDays(this.context);


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
                // random special day color
                int rnd =  new Random().nextInt(specialDates.get(shortHand).length);
                colorOfTheDay = (specialDates.get(shortHand))[rnd];
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



        Random randRedGen;
        Random randGreenGen;
        Random randBlueGen;

        // rotation to keep things fresh each day
        if (nowGreen % 3 == 0){
            randRedGen = new Random(nowRed);
            randGreenGen = new Random(nowGreen);
            randBlueGen = new Random(nowBlue);
        } else if (nowGreen % 3 == 1){
            randRedGen = new Random(nowBlue);
            randGreenGen = new Random(nowRed);
            randBlueGen = new Random(nowGreen);

        } else {
            randRedGen = new Random(nowGreen);
            randGreenGen = new Random(nowBlue);
            randBlueGen = new Random(nowRed);
        }

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



    /**
     * LOADS THE SPECIAL DAY CSV INTO A HASHMAP
     * loads the special days, skips the empty ones
     * somewhat inefficient
     *
     * @param context context of app
     *
     * @return HashMap<String, Integer[]>
     *     string being the month/day
     *     int array being the special colors,
     *          parsed form hex to int form
     *
     * @author Daniel
     */
    public HashMap<String, Integer[]> loadSpecialDays(Context context){

        HashMap<String, Integer[]> specialDayColors = new HashMap<String, Integer[]>();

        InputStream intputStream =  context.getResources().openRawResource(R.raw.specialdays);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(intputStream, Charset.forName("UTF-8"))
        );

        String line = "";
        try{
            while ( (line = bufferedReader.readLine()) != null){

                String[] tokens = line.split(",");

                if (tokens[0].equals("date")){
                    continue;
                }
                if (tokens.length == 1){
                    continue;
                }
                String date = tokens[0];
                int len = tokens.length - 1;
                while (tokens[len].length() == 0){
                    len--;
                }


                Integer[] colors = new Integer[len];


                for(int i = 1; i <= len; i++){
                    colors[i-1] =
                            Color.parseColor(tokens[i]);
                }

                specialDayColors.put(date,colors);
            }
        }catch(IOException e ){
            e.printStackTrace();
            Log.d("DEBUG", "loadSpecialDays: IOException\nError reading ata file on line " + line, e);

        }

        return  specialDayColors;
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
