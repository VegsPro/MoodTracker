package com.vegspro.moodtracker.data

import androidx.compose.ui.graphics.Color

enum class Mood(
    val id: Int,
    val title: String,
    val color: Color,
    val isRainbow: Boolean = false
) {
    NONE(0, "Не заполнено", Color(0xFFE0E0E0)),
    WOW(1, "Вау", Color(0xFFE91E63), isRainbow = true),
    BEST(2, "Лучший день", Color(0xFF2196F3)),
    COOL(3, "Классный день", Color(0xFF1565C0)),
    GOOD(4, "Хороший день", Color(0xFF26A69A)),
    NORMAL(5, "Обычный день", Color(0xFFFFEB3B)),
    BAD(6, "Плохой день", Color(0xFFFF9800)),
    TERRIBLE(7, "Ужасный день", Color(0xFFE53935)),
    DISGUSTING(8, "Отвратительный день", Color(0xFF5D4037));

    companion object {
        fun fromId(id: Int): Mood = entries.find { it.id == id } ?: NONE
        val selectable = entries.filter { it != NONE }
    }
}
