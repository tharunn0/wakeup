package com.tharun.wakeup

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tharun.wakeup.audio.AlarmAudioController

class AlarmForegroundService : Service() {

    private lateinit var audioController: AlarmAudioController

    override fun onCreate() {
        super.onCreate()
        audioController = AlarmAudioController(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if (action == Constants.ACTION_START_ALARM) {
            // 1. Show persistent high-priority notification
            startForeground(Constants.NOTIFICATION_ID_ALARM, createNotification())
            
            // 2. Start MediaPlayer loop
            audioController.start(null)
            
            // 3. Activity will be started via fullScreenIntent in the notification
        } else if (action == Constants.ACTION_STOP_ALARM) {
            // 4. Handle stop command from activity
            audioController.stop()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
            stopSelf()
        }

        return START_STICKY
    }

    private fun createNotification(): Notification {
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_FULLSCREEN
        }
        
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            Constants.REQUEST_CODE_FULL_SCREEN_INTENT,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, Constants.CHANNEL_ID_ALARM)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.alarm_active))
            .setContentText(getString(R.string.complete_challenge_stop))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setForegroundServiceType(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) 
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK 
                else 0
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID_ALARM,
                Constants.CHANNEL_NAME_ALARM,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical Alarm Notifications"
                setSound(null, null) // Sound is handled manually by MediaPlayer
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(true)
                enableLights(true)
                lightColor = android.graphics.Color.RED
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        audioController.stop()
        super.onDestroy()
    }
}