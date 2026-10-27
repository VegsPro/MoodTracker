package com.vegspro.moodtracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MoodReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NotificationHelper.showReminderNotification(context)
        // Reschedule for next day
        NotificationHelper.scheduleDailyReminder(context)
    }
}
