package com.kj.steady.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.kj.steady.data.model.UserPreferences
import com.kj.steady.receiver.NotificationActionReceiver
import java.util.*

object AlarmScheduler {

    const val ID_WEIGH_IN = 101
    const val ID_BREAKFAST = 102
    const val ID_HYDRATION_1 = 103
    const val ID_HYDRATION_2 = 104
    const val ID_DINNER_PREP = 105
    const val ID_EVENING_LOG = 106

    fun scheduleAllReminders(context: Context, prefs: UserPreferences) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (prefs.notifyWeighIn) {
            scheduleDailyAlarm(context, alarmManager, ID_WEIGH_IN, prefs.weekdayWakeTime, "WEIGH_IN")
        } else {
            cancelAlarm(context, alarmManager, ID_WEIGH_IN)
        }

        if (prefs.notifyHydration) {
            scheduleDailyAlarm(context, alarmManager, ID_HYDRATION_1, "11:30", "HYDRATION")
            scheduleDailyAlarm(context, alarmManager, ID_HYDRATION_2, "15:30", "HYDRATION")
        } else {
            cancelAlarm(context, alarmManager, ID_HYDRATION_1)
            cancelAlarm(context, alarmManager, ID_HYDRATION_2)
        }

        if (prefs.notifyDinnerPrep) {
            scheduleDailyAlarm(context, alarmManager, ID_DINNER_PREP, prefs.dinnerPrepTime, "DINNER_PREP")
        } else {
            cancelAlarm(context, alarmManager, ID_DINNER_PREP)
        }

        if (prefs.notifyEveningLog) {
            scheduleDailyAlarm(context, alarmManager, ID_EVENING_LOG, prefs.eveningLogTime, "EVENING_LOG")
        } else {
            cancelAlarm(context, alarmManager, ID_EVENING_LOG)
        }
    }

    private fun scheduleDailyAlarm(
        context: Context,
        am: AlarmManager,
        alarmId: Int,
        timeHHmm: String,
        category: String
    ) {
        try {
            val parts = timeHHmm.split(":").map { it.toInt() }
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, parts[0])
                set(Calendar.MINUTE, parts[1])
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val intent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_TRIGGER_NOTIFICATION
                putExtra("alarm_id", alarmId)
                putExtra("category", category)
            }
            val pending = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Using setAndAllowWhileIdle for battery-friendly low-overhead scheduling
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pending)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelAlarm(context: Context, am: AlarmManager, alarmId: Int) {
        val intent = Intent(context, NotificationActionReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pending != null) {
            am.cancel(pending)
            pending.cancel()
        }
    }
}
