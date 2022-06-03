package com.yujun.trucksharing.notificationutil

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.yujun.trucksharing.R
import com.yujun.trucksharing.UsersActivity
import com.yujun.trucksharing.notificationreceiver.SnoozeReceiver

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

// Step 1.1 extension function to send messages (GIVEN)
/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
@SuppressLint("WrongConstant")
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    // Create the content intent for the notification, which launches
    // this activity
    // Step 1.11 create intent
    val contentIntent = Intent(applicationContext, UsersActivity::class.java)
    // Step 1.12 create PendingIntent
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Step 2.0 add style
    val eggImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.baseline_av_timer_green_700_36dp
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(eggImage)
        .bigLargeIcon(null)

    // Step 2.2 add snooze action
    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
    val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        REQUEST_CODE,
        snoozeIntent,
        FLAGS)

    // Step 1.2 get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.item_notification_channel_id)
    )

        // Step 1.8 use the new 'breakfast' notification channel

        // Step 1.3 set title, text and icon to builder
        .setSmallIcon(R.drawable.baseline_av_timer_green_700_36dp)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(messageBody)

        // Step 1.13 set content intent
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

        // Step 2.1 add style to builder
        .setStyle(bigPicStyle)
        .setLargeIcon(eggImage)

        // Step 2.3 add snooze action
        .addAction(
            R.drawable.baseline_av_timer_green_700_36dp,
            applicationContext.getString(R.string.snooze),
            snoozePendingIntent
        )

        // Step 2.5 set priority
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    // Step 1.4 call notify
    notify(NOTIFICATION_ID, builder.build())
}

/**
 * Step 1.14 Cancel all notifications
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}