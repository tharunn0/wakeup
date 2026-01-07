package com.tharun.wakeup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm received!")

        val serviceIntent = Intent(context, AlarmForegroundService::class.java).apply {
            action = Constants.ACTION_START_ALARM
        }

        // We use goAsync() to ensure the broadcast doesn't timeout while we start the service,
        // although starting a service is generally fast.
        val pendingResult = goAsync()
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Failed to start AlarmForegroundService", e)
        } finally {
            pendingResult.finish()
        }
    }
}