package com.harmony.livecolor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class NotificationPublisher extends BroadcastReceiver {

    private final String channelID = "COTD";


    public void onReceive (Context context , Intent intent) {

        SharedPreferences preferences =
                context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

        if(preferences.getBoolean("notificationCOTDEnabled", true)){

            NotificationUtils notificationUtils = new NotificationUtils();

            int notificationID = notificationUtils.getUniqueNotificationID(context);

            ColorOTDayDialog colorOTDayDialog = new ColorOTDayDialog("id",context);
            Integer cotd = colorOTDayDialog.getColorOTD();
            Log.d("DEBUG", "cotd: " + cotd.toString());
            String name = ColorNameGetterCSV.getName(""+UsefulFunctions.colorIntToHex(cotd));
            Log.d("DEBUG", "cotd: " + name);
            String msg = "The color of the day is " + name + "!!";
            Notification notification = notificationUtils.getCOTDNotification(context, msg, channelID, cotd);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            notificationManager.notify( notificationID, notification);

        }


    }
}