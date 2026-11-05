package com.kj.steady.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kj.steady.KJSteadyApp
import com.kj.steady.data.model.HydrationLog
import com.kj.steady.data.model.NotificationLog
import com.kj.steady.logic.DateUtils
import com.kj.steady.notifications.AlarmScheduler
import com.kj.steady.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TRIGGER_NOTIFICATION = "com.kj.steady.TRIGGER_NOTIFICATION"
        const val ACTION_SNOOZE = "com.kj.steady.ACTION_SNOOZE"
        const val ACTION_QUICK_DRINK_250 = "com.kj.steady.ACTION_QUICK_DRINK_250"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val app = context.applicationContext as? KJSteadyApp ?: return
        val repository = app.repository
        val scope = CoroutineScope(Dispatchers.IO)

        when (action) {
            ACTION_TRIGGER_NOTIFICATION -> {
                val alarmId = intent.getIntExtra("alarm_id", 0)
                val category = intent.getStringExtra("category") ?: "GENERAL"

                val (title, message, channel) = when (category) {
                    "WEIGH_IN" -> Triple("Morning Weigh-in", "Step on the scale before fluids to keep your 7-day trend accurate.", NotificationHelper.CHANNEL_WEIGH_IN)
                    "HYDRATION" -> Triple("Hydration & Movement", "Take a brief stretch and drink a glass of water (~250 mL).", NotificationHelper.CHANNEL_HYDRATION)
                    "DINNER_PREP" -> Triple("Dinner Strategy", "Cooking tonight? Egg Bhurji or Paneer Bhurji takes 15 mins and hits your ~40g protein target.", NotificationHelper.CHANNEL_ROUTINE)
                    "EVENING_LOG" -> Triple("Evening Reflection", "Log today's BP, dinner, and cigarettes. Small entries, clearer decisions.", NotificationHelper.CHANNEL_EVENING_LOG)
                    else -> Triple("Stratos", "Time for your scheduled routine check-in.", NotificationHelper.CHANNEL_ROUTINE)
                }

                NotificationHelper.postNotification(context, alarmId, channel, title, message, category)
                scope.launch {
                    repository.insertNotificationLog(
                        NotificationLog(
                            category = category,
                            title = title,
                            message = message,
                            actionTaken = "DELIVERED"
                        )
                    )
                }

                // Reschedule for next day
                val prefs = repository.getUserPreferences()
                AlarmScheduler.scheduleAllReminders(context, prefs)
            }

            ACTION_SNOOZE -> {
                val notifId = intent.getIntExtra("notification_id", 0)
                val category = intent.getStringExtra("category") ?: "GENERAL"
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.cancel(notifId)

                // Schedule alarm 30 minutes from now
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                    this.action = ACTION_TRIGGER_NOTIFICATION
                    putExtra("alarm_id", notifId)
                    putExtra("category", category)
                }
                val pending = PendingIntent.getBroadcast(
                    context,
                    notifId + 5000,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (30 * 60 * 1000), pending)

                scope.launch {
                    repository.insertNotificationLog(
                        NotificationLog(
                            category = category,
                            title = "Snoozed Reminder",
                            message = "Snoozed for 30 minutes",
                            actionTaken = "SNOOZED"
                        )
                    )
                }
            }

            ACTION_QUICK_DRINK_250 -> {
                val notifId = intent.getIntExtra("notification_id", 0)
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.cancel(notifId)

                scope.launch {
                    repository.insertHydrationLog(
                        HydrationLog(
                            date = DateUtils.today(),
                            amountMl = 250,
                            beverageType = "WATER",
                            vesselLabel = "Glass 250ml"
                        )
                    )
                    repository.insertNotificationLog(
                        NotificationLog(
                            category = "HYDRATION",
                            title = "Logged Water",
                            message = "Logged 250 mL via notification action",
                            actionTaken = "COMPLETED"
                        )
                    )
                }
            }
        }
    }
}
