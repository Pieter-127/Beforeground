package com.pieterv.beforeground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Build
import androidx.core.app.NotificationCompat

class LongRunningForegroundService : Service() {

    private val channel = "Long"
    private val foregroundID = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification: Notification = NotificationCompat.Builder(this, channel)
            .setContentTitle("Foreground Service")
            .setContentText("This is a basic foreground service that takes a long time")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setProgress(0, 0, true)  // Indeterminate progress (third parameter is `true`)
            .build()

        startForeground(foregroundID, notification)

        //put work here
        Thread {
            Thread.sleep(5000)
            stopForeground(
                STOP_FOREGROUND_REMOVE //or remove
            )
            stopSelf()
        }.start()

        return START_NOT_STICKY //consider also not sticky
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up if needed
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Return null because this is not a bound service
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channel,
                "Long channel name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
