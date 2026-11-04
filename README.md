# KJ Steady (v2.0.5)

**KJ Steady** is an offline-first, native Android personal health, routine, and weight-management companion built specifically for KJ (Samsung Galaxy S24 Ultra, Asia/Kolkata timezone).

It replaces the prototype Java WebView app with modern native Android architecture: **100% offline, privacy-focused, zero subscriptions, zero ads, and zero AI credits.**

---

## 🚀 Download & Installation

* **Latest Release APK**: [Download KJ-Steady-v2.0.5.apk](https://github.com/lycanthropekj/kj-steady/releases/download/v2.0.5/KJ-Steady-v2.0.5.apk)
* **Package ID**: `com.kj.steady`
* **Version**: `2.0.5` (Build `7`)
* **Target Device**: Samsung Galaxy S24 Ultra (Android 14 / Android 15 / SDK 35)
* **Signing**: Signed with personal development keystore for in-place updates.

### Direct In-Place Installation:
1. Download `KJ-Steady-v2.0.5.apk` on your phone.
2. Tap the file in **Samsung My Files** and select **Update**.
3. All existing prototype logs (weights, steps, notes) migrate automatically on first launch via Room SQLite.

---

## ⚡ Core Features

1. **Weight & Body Composition**:
   * Latest weight, measurement timestamp, 7-day trailing moving average with measurement counts.
   * Baseline delta and editable personal ambition target (85 kg).
   * Interactive Canvas trend chart with 7D, 30D, 90D, and All filters.
2. **Clinical Blood Pressure Logging**:
   * Multiple daily readings with timestamps, arm selection (Left/Right), and posture (Seated/Supine/Standing).
   * Evaluated per 2017 ACC/AHA clinical guidelines (Normal, Elevated, Stage 1, Stage 2, Hypertensive Crisis).
3. **Hydration & Nephrolithiasis Management**:
   * Tailored for kidney stone recurrence prevention: 2,800 mL daytime intake evenly paced across waking hours.
   * Beverage classification: Distinguishes plain water from other fluids.
   * Dedicated **Americano & Coffee presets calibrated to 80 mL** (80 mL Standard Cup, 120 mL Medium, 160 mL Double Cup, 200 mL Mug).
   * 1-tap quick log assist chip in the daily log.
   * Strict night-time taper (stops reminders after 8:30 PM to preserve sleep architecture).
4. **Eggitarian Nutrition & High-Protein Meal Planner**:
   * Muscle retention targets (~140g protein/day) on a safe ~600 kcal deficit (~2,050 kcal/day).
   * Pre-populated vegetarian/egg recipes: Carbamide Forte plant protein, egg bhurji, paneer stir fry, Max Protein bars.
   * Weekly rotating dinner planner and auto-generated grocery shopping lists.
5. **Home Strength & Muscle Retention Routine**:
   * Custom circuits designed for KJ's exact equipment: **Yoga mat, 10 kg resistance band, and 2 × 3 kg dumbbells**.
   * Progressive bodyweight and resistance exercises: Chair box squats, band rows, incline push-ups, band RDLs, DB lateral/overhead raises, and glute bridges.
6. **Health Connect Sync**:
   * Granular, read-only on-device synchronization with Samsung Health and Google Health / Fitbit Air.
   * Imports steps, sleep stages, and weights with timezone-aware deduplication.
7. **Schedule-Based Notifications & Samsung Battery Guide**:
   * Aligned with real daily schedule: Wake (5:45 AM), Office (9:30 AM–6:30 PM), Dinner (8:30 PM), Bedtime (11:30 PM).
   * Quiet hours enforcement (10:30 PM–5:45 AM) with snooze and quick-action notification buttons.
   * Samsung One UI background optimization guidance.
8. **Over-The-Air In-App Updates**:
   * 1-tap in-app self-updater checks `version.json` on GitHub and updates the app in-place without Google Play store delays.

---

## 🛠️ Tech Stack & Architecture

* **Language**: Kotlin 2.0.21
* **UI**: Jetpack Compose, Material 3
* **Database**: Room SQLite (with automated v1 `SharedPreferences` migration helper)
* **Preferences**: Android DataStore / Encrypted SharedPreferences
* **Health**: Android Health Connect API (`androidx.health.connect:connect-client:1.1.0-alpha11`)
* **Background Scheduling**: Android AlarmManager & WorkManager (`androidx.work:work-runtime-ktx:2.10.0`)
* **Build System**: Gradle 8.10.2, Android SDK 35 (Android 15)

---

## 📦 Building from Source

```bash
git clone https://github.com/lycanthropekj/kj-steady.git
cd kj-steady/kj-steady-app

# Ensure JAVA_HOME points to JDK 17+ and ANDROID_HOME points to Android SDK
./gradlew testReleaseUnitTest assembleRelease
```

The signed release APK will be generated at:
`kj-steady-app/app/build/outputs/apk/release/app-release.apk`
