package com.vegspro.moodtracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "moods")

class MoodRepository(private val context: Context) {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getMoodFlow(date: LocalDate): Flow<Mood> {
        val key = intPreferencesKey(date.format(dateFormatter))
        return context.dataStore.data.map { prefs ->
            Mood.fromId(prefs[key] ?: 0)
        }
    }

    fun getAllMoodsFlow(): Flow<Map<LocalDate, Mood>> {
        return context.dataStore.data.map { prefs ->
            prefs.asMap().mapNotNull { (key, value) ->
                try {
                    val date = LocalDate.parse(key.name, dateFormatter)
                    val mood = Mood.fromId(value as? Int ?: 0)
                    if (mood != Mood.NONE) date to mood else null
                } catch (e: Exception) {
                    null
                }
            }.toMap()
        }
    }

    suspend fun setMood(date: LocalDate, mood: Mood) {
        val key = intPreferencesKey(date.format(dateFormatter))
        context.dataStore.edit { prefs ->
            if (mood == Mood.NONE) {
                prefs.remove(key)
            } else {
                prefs[key] = mood.id
            }
        }
    }
}
