package com.vegspro.moodtracker

import android.app.Application
import com.vegspro.moodtracker.notification.NotificationHelper

class MoodTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        NotificationHelper.scheduleDailyReminder(this)
    }
}
