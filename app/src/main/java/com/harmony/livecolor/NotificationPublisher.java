package com.harmony.livecolor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationPublisher extends BroadcastReceiver {


    public void onReceive (Context context , Intent intent) {

        intent = new Intent(context, NotifyCotdService.class);
        NotifyCotdService.enqueueWork(context,intent);
        Log.d("NOTIFY", "NotificationPublisher: Broadcast Received" );

    }


}