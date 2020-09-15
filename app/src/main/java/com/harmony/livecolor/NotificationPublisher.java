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


    public void onReceive (Context context , Intent intent) {

        intent = new Intent(context, NotifyCotdService.class);
        NotifyCotdService.enqueueWork(context,intent);

    }


}