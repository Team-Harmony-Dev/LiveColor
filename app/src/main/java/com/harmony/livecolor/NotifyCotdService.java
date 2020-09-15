package com.harmony.livecolor;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifyCotdService extends JobIntentService {
    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1010;
    private final String channelID = "COTD";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotifyCotdService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(Intent intent) {
        // We have received work to do.  The system or framework is already
        // holding a wake lock for us at this point, so we can just go.
        cotdNotifyTask();
        Log.d("DEBUG", "NotifyCotdService: Executing work: " + intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DEBUG", "NotifyCotdService: Work Completed" );
    }

    final Handler mHandler = new Handler();

    // Helper for showing tests
    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(NotifyCotdService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * HANDLES ALL THINGS REGARDING THE *TASK* OF NOTIFYING
     * builds and fires the notification from the service/jobintentservice level
     *
     * @author Daniel
     */
    private void cotdNotifyTask(){

        SharedPreferences preferences =
                getSharedPreferences("prefs", Context.MODE_PRIVATE);

        if(preferences.getBoolean("notificationCOTDEnabled", true)){

            NotificationUtils notificationUtils = new NotificationUtils();

            int notificationID = notificationUtils.getUniqueNotificationID(this);

            ColorOTDayDialog colorOTDayDialog = new ColorOTDayDialog("id",this);
            Integer cotd = colorOTDayDialog.getColorOTD();
            Log.d("DEBUG", "cotd: " + cotd.toString());
            String name = ColorNameGetterCSV.getName(""+UsefulFunctions.colorIntToHex(cotd));
            Log.d("DEBUG", "cotd: " + name);
            String msg = "The color of the day is " + name + "!!";
            Notification notification = notificationUtils.getCOTDNotification(this, msg, channelID, cotd);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify( notificationID, notification);
        }
    }



}
