package com.harmony.livecolor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id" ;
    public static String NOTIFICATION = "notification" ;

    private final String channelID = "COTD";
    private final String channelName = "Color of The Day";
    private final String channelDecription = "The color of the day";
    private final int notificationID = 121;

    public void onReceive (Context context , Intent intent) {

        NotificationUtils notificationUtils = new NotificationUtils();

        ColorOTDayDialog colorOTDayDialog = new ColorOTDayDialog("id",context);
        Integer cotd = colorOTDayDialog.getColorOTD();
        Log.d("DEBUG", "cotd: " + cotd.toString());
        String name = ColorNameGetterCSV.getName(""+UsefulFunctions.colorIntToHex(cotd));
        Log.d("DEBUG", "cotd: " + name);
        String msg = "The color of the day is " + name + "!!";
        Notification notification = notificationUtils.getCOTDNotification(context, msg, channelID, cotd);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify( notificationID, notification) ;
    }
}