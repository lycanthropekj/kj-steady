package com.kj.steady.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kj.steady.KJSteadyApp
import com.kj.steady.notifications.AlarmScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as? KJSteadyApp ?: return
        val prefs = app.repository.getUserPreferences()
        AlarmScheduler.scheduleAllReminders(context, prefs)
    }
}
