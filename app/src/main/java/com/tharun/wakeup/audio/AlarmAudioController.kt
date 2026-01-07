package com.tharun.wakeup.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

class AlarmAudioController(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var originalVolume: Int = -1

    fun start(uri: Uri?) {
        stop()

        try {
            // Save original volume
            originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

            // Force maximum volume for ALARM stream
            ensureMaxVolume()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri ?: android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI)
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
            
            // Restore original volume
            if (originalVolume != -1) {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0)
            }
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
}