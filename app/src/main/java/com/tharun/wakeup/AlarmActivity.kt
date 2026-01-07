package com.tharun.wakeup

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.tharun.wakeup.audio.AlarmAudioController
import com.tharun.wakeup.databinding.ActivityAlarmBinding
import com.tharun.wakeup.util.TextChallengeGenerator

class AlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmBinding
    private val challengeGenerator = TextChallengeGenerator()
    private lateinit var currentChallenge: String
    private lateinit var audioController: AlarmAudioController

    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()
        super.onCreate(savedInstanceState)
        
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audioController = AlarmAudioController(this)
        
        setupChallenge()
        setupListeners()
        disableBackButton()
        
        // Ensure volume is max when activity starts
        audioController.ensureMaxVolume()
    }

    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
    }

    private fun setupChallenge() {
        currentChallenge = challengeGenerator.generateChallenge()
        binding.tvChallenge.text = currentChallenge
    }

    private fun setupListeners() {
        binding.etChallengeInput.addTextChangedListener { text ->
            val input = text.toString()
            val isMatch = challengeGenerator.isMatch(input, currentChallenge)
            
            binding.btnStopAlarm.isEnabled = isMatch
            
            if (isMatch) {
                binding.btnStopAlarm.setBackgroundColor(getColor(R.color.bright_red))
                binding.tvInstructions.text = "CHALLENGE COMPLETE!"
                binding.tvInstructions.setTextColor(getColor(R.color.white))
            } else {
                binding.btnStopAlarm.setBackgroundColor(getColor(R.color.black))
                binding.tvInstructions.text = "TYPE THE TEXT BELOW TO STOP"
                binding.tvInstructions.setTextColor(getColor(R.color.text_secondary))
            }
        }

        binding.btnStopAlarm.setOnClickListener {
            stopAlarmService()
            finish()
        }
    }

    private fun stopAlarmService() {
        val intent = Intent(this, AlarmForegroundService::class.java)
        intent.action = Constants.ACTION_STOP_ALARM
        startService(intent)
    }

    private fun disableBackButton() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to prevent dismissal
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Intercept volume keys to prevent silencing
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            audioController.ensureMaxVolume()
            return true // Consume the event
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onUserLeaveHint() {
        // Attempt to bring the activity back if the user tries to escape via Home/Recents
        super.onUserLeaveHint()
        val intent = Intent(this, AlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(intent)
    }
}