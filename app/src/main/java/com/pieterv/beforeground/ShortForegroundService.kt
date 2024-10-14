package com.pieterv.beforeground


import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build

class ShortForegroundService : Service() {

    private val channel = "Short"
    private val foregroundID = 2
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationBuilder = NotificationCompat.Builder(this, channel)
            .setContentTitle("Downloading")
            .setContentText("Progress")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setProgress(100, 0, false)

        startForeground(foregroundID, notificationBuilder.build())

        Thread {
            for (progress in 0..100 step 10) {
                Thread.sleep(1000)
                updateNotification(progress)
            }

            stopForeground(
                STOP_FOREGROUND_REMOVE //or detach
            )
            stopSelf()

        }.start()

        return START_NOT_STICKY
    }

    private fun updateNotification(progress: Int) {
        notificationBuilder.apply {
            setProgress(100, progress, false)
            setContentText("Progress: $progress%")
        }
        notificationManager.notify(foregroundID, notificationBuilder.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channel,
                "Short channel name",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }
}
