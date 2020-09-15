package com.harmony.livecolor;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifyCotdService extends android.app.Service {

    private final String channelID = "COTD";
    private final String channelName = "Color of The Day";
    private final String channelDecription = "The color of the day";
    private final int notificationID = 0;




    public void onCreate(){
        Log.d("DEBUG", "onCreate: start");

        NotificationUtils notificationUtils = new NotificationUtils();


//        if(NotificationUtils.isNotificationEnabled(this)){
            Log.d("DEBUG", "onCreate: enabled");
            NotificationUtils.createNotificationChannel(this, channelID, channelName, channelDecription);
            ColorOTDayDialog colorOTDayDialog = new ColorOTDayDialog(this);
            Integer cotd = colorOTDayDialog.getColorOTD();
            Log.d("DEBUG", "cotd: " + cotd.toString());
            String name = ColorNameGetterCSV.getName(""+UsefulFunctions.colorIntToHex(cotd));
            Log.d("DEBUG", "cotd: " + name);
            String message = "The color of the day is " + name + "!!";

            Notification notification = notificationUtils.getCOTDNotification(this, message, channelID, cotd);


            NotificationManager mNM = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);

            mNM.notify( notificationID, notification);


//        }
        Log.d("DEBUG", "onCreate: end");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
