package com.tharun.wakeup.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import com.tharun.wakeup.Constants

class AlarmAudioController(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    private var audioFocusRequest: AudioFocusRequest? = null

    fun start(uri: Uri?) {
        stop()

        try {
            // Save original volume to preferences for robustness
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
            prefs.edit().putInt(Constants.PREF_ORIGINAL_VOLUME, currentVolume).apply()

            // Request Audio Focus
            requestAudioFocus()

            // Force maximum volume for ALARM stream
            ensureMaxVolume()

            mediaPlayer = MediaPlayer().apply {
                try {
                    val alarmUri = uri ?: android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
                    setDataSource(context, alarmUri)
                } catch (e: Exception) {
                    Log.e("AlarmAudioController", "Failed to set data source, using fallback", e)
                    // Fallback: try to use a raw resource or system default
                    setDataSource(context, android.provider.Settings.System.DEFAULT_RINGTONE_URI)
                }
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("AlarmAudioController", "Error playing alarm sound", e)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            
            // Restore original volume from preferences
            val originalVolume = prefs.getInt(Constants.PREF_ORIGINAL_VOLUME, -1)
            if (originalVolume != -1) {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0)
                prefs.edit().remove(Constants.PREF_ORIGINAL_VOLUME).apply()
            }

            // Abandon Audio Focus
            abandonAudioFocus()
        } catch (e: Exception) {
            Log.e("AlarmAudioController", "Error stopping alarm sound", e)
        }
    }

    /**
     * Forces the system alarm volume to maximum.
     */
    fun ensureMaxVolume() {
        try {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)
        } catch (e: Exception) {
            Log.e("AlarmAudioController", "Error forcing max volume", e)
        }
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener { /* Handle changes if needed */ }
                .build()
            
            audioFocusRequest?.let { audioManager.requestAudioFocus(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                { /* Handle changes if needed */ },
                AudioManager.STREAM_ALARM,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            )
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus { }
        }
    }
}