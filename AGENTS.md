APP OVERVIEW
------------
A personal, offline, forced-attention alarm app designed to reliably wake the user by
preventing dismissal, silencing, or volume reduction until a cognitive task is completed.
Built for personal use on a single Android device.

PURPOSE
-------
- Ensure alarms are impossible to ignore
- Force full wakefulness before dismissal
- Act as a discipline / habit-enforcement tool
- Prioritize reliability over polish or extensibility

CORE FEATURES
-------------
1. Alarm Scheduling
   - User sets alarm time
   - Uses exact system alarms (time-critical)
   - Alarm survives app kill, idle mode, and screen lock

2. Forced Alarm Trigger
   - Alarm plays at a preselected, enforced volume
   - System volume changes are overridden
   - Alarm continues indefinitely until dismissed correctly

3. Lock-Screen Fullscreen Alarm
   - Fullscreen UI shown over lock screen
   - Cannot be dismissed by back button or gestures
   - App takes full user attention

4. Mandatory Dismissal Challenge
   - User must type a randomly generated text (10–20 words)
   - Exact match required (no partials)
   - Only successful completion stops the alarm

5. Local-Only Operation
   - No internet
   - No accounts
   - No analytics
   - No data sync
   - All logic runs on-device

NON-FEATURES (INTENTIONALLY OMITTED)
-----------------------------------
- No snooze
- No smart detection
- No sleep tracking
- No history or statistics
- No cloud backup
- No customization beyond essentials

HIGH-LEVEL TECHNICAL DESIGN
---------------------------
- Language: Kotlin (native Android)
- Alarm Trigger: AlarmManager (exact alarms)
- Reliability: Foreground Service
- Audio Control: System AudioManager
- UI: One fullscreen Activity
- State: In-memory only (no database)

SCREEN FLOW (MAX 2 SCREENS)
--------------------------
1. Alarm Setup Screen
   - Time picker
   - Ringtone selection
   - Fixed volume selection
   - Set alarm button

2. Alarm Ringing Screen
   - Fullscreen, lock-screen visible
   - Loud alarm sound
   - Text challenge input
   - Validation feedback
   - Alarm stops only on success

DESIGN PHILOSOPHY
-----------------
- Reliability > elegance
- System-level control > cross-platform abstraction
- Minimal UI, maximum enforcement
- No failure states tolerated (alarm must ring)

TARGET USER
-----------
- Single user (the developer)
- Personal device only
- Comfortable with aggressive behavior
- Accepts inconvenience for effectiveness



==============================================================================================

Activity
- What it is: A single screen of the app (UI)
- Why you need it here: The alarm screen that shows full-screen over the lock screen
  and forces the user to type the text lives in an Activity.

Service
- What it is: Background code that can run even when no screen is visible
- Why you need it here: The alarm sound must keep playing even if the app is killed,
  the screen is locked, or the system is idle. This is handled by a foreground Service.

AndroidManifest.xml
- What it is: The app’s wiring and permission declaration file
- Why you need it here: Alarms, foreground services, boot receivers, lock-screen
  behavior, and special permissions only work if declared correctly in the Manifest.

Running on a real device
- What it is: Installing and testing the app on your physical phone
- Why you need it here: Emulators do not behave like real phones for alarms,
  volume control, Doze mode, or OEM battery restrictions.

Logcat
- What it is: System-level logs from your app and Android OS
- Why you need it here: When alarms don’t fire or services get killed silently,
  Logcat is the only way to see what actually happened.


==========================================

app/
└── src/
    └── main/
        ├── AndroidManifest.xml
        │
        ├── java/                (or kotlin/)
        │   └── com/yourname/alarmapp/
        │       │
        │       ├── ui/
        │       │   ├── SetupActivity.kt
        │       │   └── AlarmActivity.kt
        │       │
        │       ├── service/
        │       │   └── AlarmForegroundService.kt
        │       │
        │       ├── receiver/
        │       │   └── AlarmReceiver.kt
        │       │
        │       ├── audio/
        │       │   └── AlarmAudioController.kt
        │       │
        │       ├── alarm/
        │       │   └── AlarmScheduler.kt
        │       │
        │       └── util/
        │           ├── TextChallengeGenerator.kt
        │           └── Constants.kt
        │
        └── res/
            ├── layout/
            │   ├── activity_setup.xml
            │   └── activity_alarm.xml
            │
            ├── values/
            │   ├── strings.xml
            │   ├── themes.xml
            │   └── colors.xml
            │
            └── raw/
                └── (optional default alarm sound)
