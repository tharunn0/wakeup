package com.tharun.wakeup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tharun.wakeup.alarm.AlarmScheduler

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
            val alarmTime = prefs.getLong(Constants.PREF_ALARM_TIME, 0)
            
            if (alarmTime > System.currentTimeMillis()) {
                val alarmScheduler = AlarmScheduler(context)
                alarmScheduler.scheduleAlarm(alarmTime)
            }
        }
    }
}
