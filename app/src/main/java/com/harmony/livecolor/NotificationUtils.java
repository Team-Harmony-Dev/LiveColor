package com.harmony.livecolor;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class NotificationUtils {


    public NotificationUtils() {
    }


    /**
     * ADD BASE NOTIFICATION
     * simple implementation for a basic notification
     *
     * @param context context of app
     * @param message message to be displayed
     * @param channelID what channel the notification should use
     *
     * @author Daniel
     * leaving this in mostly for debug stuff later
     */
    public void addNotification(Context context, String message, String channelID) {




        int icon = R.drawable.livecolor_logo_vectorized;
        String appname = context.getResources().getString(R.string.app_name);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
        notification = builder
                .setContentIntent(contentIntent)
                .setSmallIcon(icon)
                .setTicker(appname)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(appname)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        notificationManager.notify(0 , notification);

    }


    /**
     * ADD BASE COTD NOTIFICATION
     * for immediate cotd notification
     *
     * @param context context of app
     * @param message message to be displayed
     * @param channelID channel of notification
     * @param color of the day
     *
     * @author Daniel
     * again, leaving in for debug
     * also contains things for complete custom notifications
     * which are not currently being used
     */
    public void addColorNotification(Context context, String message, String channelID, int color) {


        RemoteViews contentView = new RemoteViews(context.getPackageName() , R.layout.notification_custom_cotd) ;
        contentView.setInt(R.id.layoutRelativeCustomNotification, "setBackgroundColor", color);
        contentView.setInt(R.id.imageCustomNotification, "setImageResource", R.drawable.livecolor_logo_vectorized);

        RemoteViews contentViewBig = new RemoteViews(context.getPackageName(), R.layout.notification_custom_cotd);
        contentViewBig.setInt(R.id.layoutRelativeCustomNotification, "setBackgroundColor", color);
        contentViewBig.setInt(R.id.imageCustomNotification, "setImageResource", R.drawable.livecolor_logo_vectorized);
        contentViewBig.setCharSequence(R.id.titleCustomNotification, "setText", "LiveColor");
        contentViewBig.setCharSequence(R.id.textCustomNotification, "setText", "message");
        contentViewBig.setInt(R.id.layoutRelativeCustomNotification, "setMinimumHeight", 128);
//        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_small);
//        RemoteViews notificationLayoutExpanded = new RemoteViews(context.getPackageName(), R.layout.notification_large);




        Bitmap image = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
        image.eraseColor(color);


        int icon = R.drawable.livecolor_logo_vectorized;
        Bitmap bitmapIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.livecolor_logo_vectorized);
//        long when = System.currentTimeMillis();
        String appname = context.getResources().getString(R.string.app_name);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
        notification = builder
//                .setContent(contentView)
                .setContentIntent(contentIntent)
                .setSmallIcon(icon)
                .setLargeIcon(image)
//                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setTicker(appname)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(appname)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(image)
                .bigLargeIcon(null))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(color)
                .setColorized(true)
//                .setCustomContentView(contentView)
//                .setCustomBigContentView(contentViewBig)
                .build();

        notificationManager.notify(0 , notification);

    }

    /**
     * ADD BASE COTD NOTIFICATION
     * for immediate cotd notification
     *
     * @param context context of app
     * @param message message to be displayed
     * @param channelID channel of notification
     * @param color of the day
     *
     * @author Daniel
     *
     */
    public Notification getCOTDNotification(Context context, String message, String channelID, int color) {

        Bitmap image = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888); // doesnt really need to be this big
        image.eraseColor(color);
        int icon = R.drawable.livecolor_logo_vectorized;
        String appname = context.getResources().getString(R.string.app_name);


        Notification notification;

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
        notification = builder
                .setContentIntent(contentIntent)
                .setSmallIcon(icon)
                .setLargeIcon(image)
                .setTicker(appname)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(appname)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .bigLargeIcon(null))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(color)
                .setColorized(true)
                .build();

        return notification;
    }

    /**
     * CREATE NOTIFICATION CHANNEL
     * as the title suggests, makes a notification channel to use later
     *
     * @param context context of app
     * @param channelID string of channel
     * @param name
     * @param description
     *
     * @author Daniel
     * this is needed for notifications past a Android api 25
     * lets user control notification in their own settings as well
     */
    public static void createNotificationChannel(Context context, String channelID, String name, String description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

    }
    /**
     * CHECK NOTIFICATION CHANNEL
     * as the title suggests, checks for a notification channel
     *
     * @param context context of app
     * @param channelID string of channel
     *
     * @author Daniel
     * more of a convienience then a necesity
     */
    public boolean isNotificationChannelEnabled(Context context, @Nullable String channelID){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!TextUtils.isEmpty(channelID)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = manager.getNotificationChannel(channelID);
                return channel.getImportance() != NotificationManager.IMPORTANCE_LOW;
            }
            return false;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
    }

    /**
     * DELETE NOTIFICATION CHANNEL
     * as the title suggests, deletes a notification channel
     *
     * @param context context of app
     * @param channelID string of channel
     *
     * @author Daniel
     *
     */
    public void deleteNotificationChannel(Context context, String channelID){
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.deleteNotificationChannel(channelID) ;

    }

//    /**
//     * NOTIFICATION SETTER
//     * sets notification for COTD
//     * handles the when mostly,
//     * what handeled in NotificationUtils
//     *
//     * @param view view of button
//     *
//     * @author Daniel
//     * testing fuinctionality
//     */
//    public void createNotification (View view) {
//        Intent myIntent = new Intent(view.getContext() , NotificationUtils.class );
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getService( this.getContext(), 0 , myIntent , 0 );
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MINUTE, 13);
//        calendar.set(Calendar.HOUR, 6);
//        calendar.set(Calendar.AM_PM, Calendar.PM);
//        calendar.add(Calendar.DAY_OF_MONTH, 1);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP , calendar.getTimeInMillis() , 1000*60*60*24 , pendingIntent);
//
//        Log.d("DEBUG", "createNotification: ");
//    }

    /**
     * NOTIFICATION CHECK
     *
     * @param context context of app
     *
     * @return isNotificationEnabled boolean
     * @author Daniel
     *
     */
    public static boolean isNotificationEnabled(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("pref", MODE_PRIVATE);
        return mPrefs.getBoolean( "notificationCOTD", true);
    }

    /**
     * NOTIFICATION SET
     *
     * @param context context of app
     * @param isEnabled boolean of notification check
     *
     * @author Daniel
     *
     */
    public static void setNotificationEnabled(Context context, boolean isEnabled) {
        SharedPreferences mPrefs = context.getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean("notificationCOTD", isEnabled);
        editor.apply();

        //                NotificationUtils notificationUtils = new NotificationUtils();
//                notificationUtils.createNotificationChannel(getContext(), "COTD");
//                ColorOTDayDialog colorOTDayDialog = new ColorOTDayDialog(getContext());
//                Integer cotd = colorOTDayDialog.getColorOTD();
//                Log.d("DEBUG", "cotd: " + cotd.toString());
//                String name = ColorNameGetterCSV.getName(""+UsefulFunctions.colorIntToHex(cotd));
//                Log.d("DEBUG", "cotd: " + name);
//                String msg = "The color of the day is " + name + "!!";
//                notificationUtils.addColorNotification(getContext(),msg, "COTD", cotd);
//            }

    }

    public static void setRepeating(Context context){

        String channelID = "COTD";
        String channelName = "Color of The Day";
        String channelDecription = "The color of the day";
        int notificationID = 0;

        NotificationUtils notificationUtils = new NotificationUtils();
        notificationUtils.createNotificationChannel(context,channelID,channelName,channelDecription);
        ColorOTDayDialog colorOTDayDialog = new ColorOTDayDialog(context);
        Integer cotd = colorOTDayDialog.getColorOTD();
        Log.d("DEBUG", "cotd: " + cotd.toString());
        String name = ColorNameGetterCSV.getName(""+UsefulFunctions.colorIntToHex(cotd));
        Log.d("DEBUG", "cotd: " + name);
        String msg = "The color of the day is " + name + "!!";
        Notification notification = notificationUtils.getCOTDNotification(context, msg, channelID, cotd);


        Intent notificationIntent = new Intent( context, NotificationPublisher.class ) ;
        //notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID , 1 ) ;
        //notificationIntent.putExtra(NotificationPublisher.NOTIFICATION , notification );
        PendingIntent pendingIntent = PendingIntent.getBroadcast( context, 0 , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT ) ;

        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.SECOND, 10);
//        calendar.set(Calendar.MINUTE, 34);
//        calendar.set(Calendar.HOUR, 12);
//        calendar.set(Calendar.AM_PM, Calendar.PM);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context. ALARM_SERVICE );
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24 , pendingIntent);
        Log.d("DEBUG", "setRepeating: ");

    }




    private void scheduleNotification (Context context, Notification notification , long delay) {
        Intent notificationIntent = new Intent( context, NotificationPublisher.class ) ;
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( context, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , delay , pendingIntent) ;
    }





}
