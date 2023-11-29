package com.kgxl.toalive.open;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.kgxl.toalive.R;


/**
 * Created by kgxl on 2023/8/17
 */
public class NotificationUtils {
    private String ID = "channel_1";
    private String NAME = "notification";

    private NotificationManager manager = null;

    private NotificationUtils() {
    }

    public static NotificationUtils getInstance() {
        return NotificationUtilsHolder.sNotificationUtils;
    }

    static class NotificationUtilsHolder {
        static NotificationUtils sNotificationUtils = new NotificationUtils();
    }

    private NotificationManager getNotificationManagerManager(Context context) {
        if (manager == null) {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public void sendNotificationFullScreen(Context context, Intent pendingIntent, String title, String content) {
        if (Build.VERSION.SDK_INT >= 26) {
            clearAllNotification(context);
            NotificationChannel channel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_MIN);
            channel.setSound(null, null);
            getNotificationManagerManager(context).createNotificationChannel(channel);
            Notification notification = getChannelNotificationQ(context, pendingIntent, title, content);
            getNotificationManagerManager(context).notify(1, notification);
        }
    }

    public void clearAllNotification(Context context) {
        getNotificationManagerManager(context).cancelAll();
    }

    private Notification getChannelNotificationQ(Context context, Intent pendingIntent, String title, String content) {
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                0,
                pendingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, ID)
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(null)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_EVENT)
                .setOngoing(false)
                .setFullScreenIntent(fullScreenPendingIntent, true);
        return notificationBuilder.build();
    }
}