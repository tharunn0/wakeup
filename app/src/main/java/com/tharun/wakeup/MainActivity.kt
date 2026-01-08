package com.tharun.wakeup

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tharun.wakeup.alarm.AlarmScheduler
import com.tharun.wakeup.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var alarmScheduler: AlarmScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmScheduler = AlarmScheduler(this)
        
        checkFirstRun()
        checkBatteryOptimization()
        setupListeners()
        updateAlarmDisplay()
    }

    private fun checkFirstRun() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean(Constants.PREF_IS_FIRST_RUN, true)
        if (isFirstRun) {
            prefs.edit().putBoolean(Constants.PREF_IS_FIRST_RUN, false).apply()
            try {
                startActivity(Intent(this, InstructionsActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Unable to open instructions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateAlarmDisplay() {
        val prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val alarmTime = prefs.getLong(Constants.PREF_ALARM_TIME, 0)

        if (alarmTime > System.currentTimeMillis()) {
            binding.cvActiveAlarm.visibility = android.view.View.VISIBLE
            val calendar = Calendar.getInstance().apply { timeInMillis = alarmTime }
            val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            binding.tvAlarmTime.text = timeFormat.format(calendar.time)
        } else {
            binding.cvActiveAlarm.visibility = android.view.View.GONE
        }
    }

    private fun checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
            val hasAskedBefore = prefs.getBoolean("battery_opt_asked", false)
            
            if (!hasAskedBefore) {
                val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    showBatteryOptimizationDialog()
                }
            }
        }
    }

    private fun showBatteryOptimizationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.battery_opt_title))
            .setMessage(getString(R.string.battery_opt_message))
            .setPositiveButton(getString(R.string.btn_settings)) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Unable to open battery settings", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.btn_not_now)) { _, _ ->
                // Mark as asked so we don't show again
                getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("battery_opt_asked", true)
                    .apply()
            }
            .show()
    }

    private fun setupListeners() {
        binding.btnSetAlarm.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            calendar.set(Calendar.MINUTE, binding.timePicker.minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // If time is in the past, schedule for tomorrow
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            alarmScheduler.scheduleAlarm(calendar.timeInMillis)
            
            val timeString = "${binding.timePicker.hour}:${String.format("%02d", binding.timePicker.minute)}"
            Toast.makeText(this, getString(R.string.toast_alarm_set, timeString), Toast.LENGTH_SHORT).show()
            updateAlarmDisplay()
        }

        binding.btnCancelAlarm.setOnClickListener {
            alarmScheduler.cancelAlarm()
            updateAlarmDisplay()
            Toast.makeText(this, getString(R.string.toast_alarm_cancelled), Toast.LENGTH_SHORT).show()
        }

        binding.btnHelp.setOnClickListener {
            try {
                startActivity(Intent(this, InstructionsActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Unable to open instructions", Toast.LENGTH_SHORT).show()
            }
        }
    }
}