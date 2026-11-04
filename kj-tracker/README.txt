KJ STEADY — PERSONAL ANDROID TRACKER v1.0

INSTALL
1. Download KJ-Steady-v1.0.apk on your Android phone.
2. Open the file and, if prompted, allow installation from that browser/file manager.
3. Install KJ Steady. You can turn off that installation permission afterwards.
Requires Android 8.0 or later. This is a personally signed APK, not a Play Store release.

WHAT IT DOES
Manual daily entries: morning weight, steps, sleep, blood pressure, cigarettes,
alcohol, meals and notes. Editable history, latest weight, trailing seven-day
average, change from baseline, days until your chosen date and a weight chart.
Starting settings: approximate 104 kg, chosen 85 kg goal, 4 November 2026 to
1 February 2027. Change these under Setup. No invented sample entries are stored.
Your chosen deadline is not a safe weight-loss recommendation.

DATA
Works offline; the manifest requests no internet or health permissions.
Data is stored in private app preferences. It is not separately encrypted by the
app. No login, cloud sync, automatic backup, Fitbit/Google Health integration,
reminders or calorie calculations. Export JSON regularly under Setup. Uninstalling
or clearing app data deletes entries. Backup files contain health information.
Import validates a backup and asks before replacing all current data.

LIMITATIONS AND CHECKS
Compiled against Android API 35, minimum API 26. Signature verified using Android
apksigner. Java compilation, JavaScript syntax and test.cjs checks passed.
No Android emulator/physical-device installation or visual WebView test was
available. This is a first build for personal testing. Blood pressure is logged,
not interpreted. It is not a medical device or an emergency alert system.

SOURCE AND REBUILD
The source archive includes the personal development signing key so you can
install future rebuilds over this package without changing identity. Keep that
archive private. The local development keystore uses password 'android', alias
'kj-steady'; it is not a production distribution key.

Install JDK 21 (or a JDK supporting these build tools), Android SDK build tools
35.0.0 and platform android-35. Python 3, zip and keytool are also used.
Set ANDROID_BUILD_TOOLS and ANDROID_PLATFORM_JAR to those SDK paths, then run:
  ./build.sh
Run logic and UI-handler checks:
  node test.cjs
Output: build/KJ-Steady-v1.0.apk
Preserve package com.kj.steady and signing key; increment versionCode for updates.
Build uses only Android SDK tools and Java APIs; no third-party app dependencies.
