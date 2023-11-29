package com.kgxl.base.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kgxl.base.R
import com.kgxl.base.Utils

/**
 * Created by kgxl on 2022/11/10
 */
object NotificationUtil {
    const val DEFAULT_CHANNEL_ID = 111
    fun startNotify(ctx: Context, icon: Int, defaultChannelId: Int, title: String, content: String) {
        val builder =
            NotificationCompat.Builder(ctx, createChannel(ctx))
        builder.setSound(null)
            .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(icon)
        NotificationManagerCompat.from(ctx).notify(defaultChannelId, builder.build())
    }

    fun startDownloadNotify(ctx: Context, icon: Int, defaultChannelId: Int, title: String, progress: Int) {
        val remoteViews = createRemoteViews()
        remoteViews.setCharSequence(R.id.title, "setText", title)
        if (progress >= 100) {
            remoteViews.setViewVisibility(R.id.pb, View.GONE)
            remoteViews.setViewVisibility(R.id.content, View.VISIBLE)
        } else {
            remoteViews.setViewVisibility(R.id.pb, View.VISIBLE)
            remoteViews.setViewVisibility(R.id.content, View.GONE)
            remoteViews.setInt(R.id.pb, "setProgress", progress)
        }
        val builder =
            NotificationCompat.Builder(ctx, createChannel(ctx))
        builder.setSound(null)
            .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCustomContentView(remoteViews)
            .setSmallIcon(icon)
        NotificationManagerCompat.from(ctx).notify(defaultChannelId, builder.build())
    }

    fun clearNotify(ctx: Context, defaultChannelId: Int) {
        NotificationManagerCompat.from(ctx).cancel(defaultChannelId)
    }

    private fun createRemoteViews(): RemoteViews {
        return RemoteViews(Utils.app.packageName, R.layout.layout_remote_download)
    }

    private fun createChannel(ctx: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelImportance = NotificationManager.IMPORTANCE_LOW
            val appName = ctx.getString(R.string.app_name)
            val notificationChannel = NotificationChannel(appName, appName, channelImportance)
            // 设置描述 最长30字符
            notificationChannel.description = "${appName}的重要通知"
            // 该渠道的通知是否使用震动
            notificationChannel.enableVibration(false)
            // 设置显示模式
            notificationChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            notificationChannel.setSound(null, null)
            notificationChannel.enableLights(false)
            (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(notificationChannel)
            return appName
        } else {
            return ""
        }
    }
}