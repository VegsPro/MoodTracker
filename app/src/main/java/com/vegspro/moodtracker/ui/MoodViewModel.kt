package com.vegspro.moodtracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vegspro.moodtracker.data.Mood
import com.vegspro.moodtracker.data.MoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class MoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MoodRepository(application)

    val moods: StateFlow<Map<LocalDate, Mood>> = repository.getAllMoodsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun setMood(date: LocalDate, mood: Mood) {
        viewModelScope.launch {
            repository.setMood(date, mood)
        }
    }

    fun getMoodFor(date: LocalDate): Mood {
        return moods.value[date] ?: Mood.NONE
    }
}
