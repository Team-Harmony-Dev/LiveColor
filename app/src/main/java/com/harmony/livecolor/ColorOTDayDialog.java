package com.harmony.livecolor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ColorOTDayDialog {
    int colorOfTheDay;
    Date todayDate;
    Context context;
    Activity activity;
    AlertDialog alertDialogSave;

    public ColorOTDayDialog(Context context) {
        this.context = context;
        activity = (Activity) context;
    }

    public void showColorOTD() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        View colorOTDView = activity.getLayoutInflater().inflate(R.layout.dialog_color_day,null);

        //Get/Set the current date
        Date todayDate = Calendar.getInstance().getTime();
        TextView dateView = (TextView) colorOTDView.findViewById(R.id.dateView);
        SimpleDateFormat simpleDate =  new SimpleDateFormat("MM/dd/yyyy");
        String strDt = simpleDate.format(todayDate);
        dateView.setText(strDt);

        //Generate random color
        colorOfTheDay = generateRandomColor();
        ImageView colorView = colorOTDView.findViewById(R.id.colorOTDView);
        colorView.setBackgroundColor(colorOfTheDay);

        //Be able to fetch color name on first load? Works after first "start"
        TextView colorName = colorOTDView.findViewById(R.id.colorOTDNameView);
        ColorNameGetter.updateViewWithColorName(colorName, colorOfTheDay, 0.25, 30);

        builder.setView(colorOTDView);
        alertDialogSave = builder.create();
        alertDialogSave.show();
    }

    public int generateRandomColor(){
        Random rand = new Random();
        int randRed = rand.nextInt(256);
        int randGreen = rand.nextInt(256);
        int randBlue = rand.nextInt(256);
        //return getIntFromColor(randRed, randGreen, randBlue);
        return UsefulFunctions.getIntFromColor(randRed, randGreen, randBlue);
    }

}
