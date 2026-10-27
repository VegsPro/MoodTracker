package com.vegspro.moodtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vegspro.moodtracker.data.Mood

private val rainbowBrush = Brush.horizontalGradient(
    listOf(
        Color(0xFFE91E63),
        Color(0xFFFF9800),
        Color(0xFFFFEB3B),
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFF9C27B0)
    )
)

@Composable
fun MoodSquare(
    mood: Mood,
    size: Dp = 18.dp,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(3.dp)
    val bgModifier = if (mood.isRainbow) {
        Modifier.background(rainbowBrush, shape)
    } else {
        Modifier.background(mood.color, shape)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .then(bgModifier)
            .then(if (selected) Modifier.border(2.dp, Color.White, shape) else Modifier)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    )
}
