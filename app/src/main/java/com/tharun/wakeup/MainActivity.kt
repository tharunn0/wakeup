package com.tharun.wakeup

import android.os.Bundle
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

        setupListeners()
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
            
            Toast.makeText(this, "Alarm set for ${binding.timePicker.hour}:${String.format("%02d", binding.timePicker.minute)}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}