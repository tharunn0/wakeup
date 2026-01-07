package com.tharun.wakeup

object Constants {
    // Intent Actions
    const val ACTION_START_ALARM = "com.tharun.wakeup.ACTION_START_ALARM"
    const val ACTION_STOP_ALARM = "com.tharun.wakeup.ACTION_STOP_ALARM"

    // Intent Extras
    const val EXTRA_ALARM_TIME = "extra_alarm_time"
    const val EXTRA_RINGTONE_URI = "extra_ringtone_uri"
    const val EXTRA_VOLUME = "extra_volume"

    // Request Codes
    const val REQUEST_CODE_ALARM = 1001
    const val REQUEST_CODE_FULL_SCREEN_INTENT = 1002

    // Notification Channel
    const val CHANNEL_ID_ALARM = "alarm_channel"
    const val CHANNEL_NAME_ALARM = "Active Alarms"
    const val NOTIFICATION_ID_ALARM = 1

    // SharedPreferences Keys (for basic persistence if needed)
    const val PREFS_NAME = "wakeup_prefs"
    const val PREF_ALARM_TIME = "pref_alarm_time"
    const val PREF_RINGTONE_URI = "pref_ringtone_uri"
    const val PREF_VOLUME = "pref_volume"
}

data class AlarmSettings(
    val timeInMillis: Long,
    val ringtoneUri: String,
    val volume: Int
)