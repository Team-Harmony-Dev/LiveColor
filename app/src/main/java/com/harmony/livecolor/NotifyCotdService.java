package com.harmony.livecolor;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.io.InputStream;

import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationManagerCompat;

import static com.harmony.livecolor.UsefulFunctions.makeToast;

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
        Log.d("NOTIFY", "NotifyCotdService: Executing work: " + intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("NOTIFY", "NotifyCotdService: Work Completed" );
    }

    final Handler mHandler = new Handler();

    // Helper for showing tests
    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                makeToast(text.toString(),NotifyCotdService.this);
                //Toast.makeText(NotifyCotdService.this, text, Toast.LENGTH_SHORT).show();
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

            //Read names
            InputStream inputStream = getResources().openRawResource(R.raw.colornames);
            ColorNameGetterCSV colors = new ColorNameGetterCSV(inputStream);
            colors.readColors();
            //Get name for our hex
            String name = ColorNameGetterCSV.getName(""+UsefulFunctions.colorIntToHex(cotd));

            String msg = "The color of the day is " + name + "!!";
            Notification notification = notificationUtils.getCOTDNotification(this, msg, channelID, cotd);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.cancelAll(); // remove previous notifications

            notificationManager.notify( notificationID, notification);
        }
    }



}
