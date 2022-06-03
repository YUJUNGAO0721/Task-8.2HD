package com.yujun.trucksharing.notificationreceiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.yujun.trucksharing.R
import com.yujun.trucksharing.notificationutil.sendNotification

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Step 1.10 [Optional] remove toast
//        Toast.makeText(context, context.getText(R.string.pickup_ready), Toast.LENGTH_SHORT).show()

        // Step 1.9 add call to sendNotification
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            context.getText(R.string.pickup_ready).toString(),
            context
        )
    }
}