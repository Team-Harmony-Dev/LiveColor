package com.harmony.livecolor;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import static android.provider.Settings.System.getString;


public class NotificationUtils {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    public NotificationUtils() {
    }

    public void addNotification(Context context, String message, String channelID) {

        int icon = R.drawable.livecolor_logo_vectorized;
        long when = System.currentTimeMillis();
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
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        notificationManager.notify(0 , notification);

    }

    public void addColorNotification(Context context, String message, String channelID, int color) {


        RemoteViews contentView = new RemoteViews(context.getPackageName() , R.xml.custom_notification_layout ) ;
//        contentView.setInt(R.id.layoutRelativeCustomNotification, "setBackgroundResource", color);
        contentView.setInt(R.id.imageCustomNotification, "setImageResource", R.drawable.livecolor_logo_vectorized);


        int icon = R.drawable.livecolor_logo_vectorized;
        Bitmap bitmapIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.livecolor_logo_vectorized);
        long when = System.currentTimeMillis();
        String appname = context.getResources().getString(R.string.app_name);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
        notification = builder
                .setContent(contentView)
                .setContentIntent(contentIntent)
                .setSmallIcon(icon)
                .setLargeIcon(bitmapIcon)
//                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setTicker(appname)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(appname)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(color)
                .setColorized(true)
                .build();

        notificationManager.notify(0 , notification);

    }

    public void createNotificationChannel(Context context, String channelID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Color of The Day";
            String description = "The color of the day";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public void deleteNotificationChannel(Context context, String channelID){
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.deleteNotificationChannel(channelID) ;

    }




}
