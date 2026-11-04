package com.kj.steady.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kj.steady.MainActivity
import com.kj.steady.R
import com.kj.steady.receiver.NotificationActionReceiver
import java.util.*

object NotificationHelper {
    const val CHANNEL_WEIGH_IN = "steady_weigh_in"
    const val CHANNEL_HYDRATION = "steady_hydration"
    const val CHANNEL_EVENING_LOG = "steady_evening_log"
    const val CHANNEL_ROUTINE = "steady_routine"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel(CHANNEL_WEIGH_IN, "Morning Weigh-in", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Consistent morning scale weigh-in prompts"
                },
                NotificationChannel(CHANNEL_HYDRATION, "Hydration & Movement", NotificationManager.IMPORTANCE_LOW).apply {
                    description = "Daytime fluid pacing reminders"
                },
                NotificationChannel(CHANNEL_EVENING_LOG, "Evening Reflection", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Daily reflection, dinner, BP, and sleep check"
                },
                NotificationChannel(CHANNEL_ROUTINE, "Routine Milestones", NotificationManager.IMPORTANCE_LOW).apply {
                    description = "Breakfast protein & dinner prep reminders"
                }
            )
            channels.forEach { nm.createNotificationChannel(it) }
        }
    }

    fun isQuietHours(startHourMin: String = "22:30", endHourMin: String = "05:45"): Boolean {
        try {
            val now = Calendar.getInstance()
            val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

            val sParts = startHourMin.split(":").map { it.toInt() }
            val startMinutes = sParts[0] * 60 + sParts[1]

            val eParts = endHourMin.split(":").map { it.toInt() }
            val endMinutes = eParts[0] * 60 + eParts[1]

            return if (startMinutes > endMinutes) {
                // Spans midnight (e.g. 22:30 to 05:45)
                currentMinutes >= startMinutes || currentMinutes < endMinutes
            } else {
                currentMinutes in startMinutes until endMinutes
            }
        } catch (e: Exception) {
            return false
        }
    }

    fun postNotification(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        message: String,
        category: String
    ) {
        if (isQuietHours()) {
            // Drop notifications during quiet hours to protect sleep
            return
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Snooze 30 mins
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SNOOZE
            putExtra("notification_id", notificationId)
            putExtra("category", category)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 1000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)
            .addAction(0, "Snooze 30m", snoozePendingIntent)

        // Optional quick action for hydration
        if (channelId == CHANNEL_HYDRATION) {
            val drinkIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_QUICK_DRINK_250
                putExtra("notification_id", notificationId)
            }
            val drinkPending = PendingIntent.getBroadcast(
                context,
                notificationId + 2000,
                drinkIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(0, "Drank 250ml", drinkPending)
        }

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notificationId, builder.build())
    }
}
