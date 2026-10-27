package com.vegspro.moodtracker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vegspro.moodtracker.data.Mood
import com.vegspro.moodtracker.notification.NotificationHelper
import com.vegspro.moodtracker.ui.components.MoodPickerDialog
import com.vegspro.moodtracker.ui.components.MoodSquare
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearCalendarScreen(viewModel: MoodViewModel) {
    val moods by viewModel.moods.collectAsState()
    val context = LocalContext.current
    var currentYear by remember { mutableIntStateOf(Year.now().value) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showLegend by remember { mutableStateOf(false) }
    var remindersEnabled by remember { mutableStateOf(true) }

    val today = LocalDate.now()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Цвета настроения",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        remindersEnabled = !remindersEnabled
                        if (remindersEnabled) {
                            NotificationHelper.scheduleDailyReminder(context)
                        } else {
                            NotificationHelper.cancelReminder(context)
                        }
                    }) {
                        Icon(
                            imageVector = if (remindersEnabled)
                                Icons.Default.Notifications
                            else
                                Icons.Default.NotificationsOff,
                            contentDescription = "Напоминания"
                        )
                    }
                    IconButton(onClick = { showLegend = !showLegend }) {
                        Icon(Icons.Default.Info, contentDescription = "Легенда")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            // Year selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentYear-- }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Предыдущий год")
                }
                Text(
                    text = currentYear.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                IconButton(onClick = { currentYear++ }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Следующий год")
                }
            }

            // Legend (collapsible)
            AnimatedVisibility(visible = showLegend) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Легенда",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Mood.selectable.chunked(2).forEach { rowMoods ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowMoods.forEach { mood ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        MoodSquare(mood = mood, size = 16.dp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = mood.title,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1
                                        )
                                    }
                                }
                                if (rowMoods.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = "Напоминание каждый день в 22:00",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Months grid - 3 columns
            val months = Month.entries
            months.chunked(3).forEach { rowMonths ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowMonths.forEach { month ->
                        MonthGrid(
                            year = currentYear,
                            month = month,
                            moods = moods,
                            today = today,
                            onDayClick = { date -> selectedDate = date },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill empty slots if less than 3
                    repeat(3 - rowMonths.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats
            val yearMoods = moods.filter { it.key.year == currentYear }
            if (yearMoods.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Статистика $currentYear",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Заполнено дней: ${yearMoods.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Mood.selectable.forEach { mood ->
                            val count = yearMoods.count { it.value == mood }
                            if (count > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    MoodSquare(mood = mood, size = 14.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${mood.title}: $count",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    selectedDate?.let { date ->
        MoodPickerDialog(
            date = date,
            currentMood = moods[date] ?: Mood.NONE,
            onMoodSelected = { mood ->
                viewModel.setMood(date, mood)
                selectedDate = null
            },
            onDismiss = { selectedDate = null }
        )
    }
}

@Composable
private fun MonthGrid(
    year: Int,
    month: Month,
    moods: Map<LocalDate, Mood>,
    today: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysInMonth = month.length(Year.isLeap(year.toLong()))
    val firstDay = LocalDate.of(year, month, 1)
    val startOffset = (firstDay.dayOfWeek.value % 7) // Monday=1 -> 1, Sunday=7 -> 0? Adjust for Mon start

    // Russian week starts on Monday
    val offset = firstDay.dayOfWeek.value - 1 // 0 for Monday

    Column(modifier = modifier) {
        Text(
            text = month.getDisplayName(TextStyle.SHORT, Locale("ru")).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        // Weekday headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("П", "В", "С", "Ч", "П", "С", "В").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        var day = 1
        for (week in 0 until 6) {
            if (day > daysInMonth) break
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dow in 0 until 7) {
                    if ((week == 0 && dow < offset) || day > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).size(18.dp))
                    } else {
                        val date = LocalDate.of(year, month, day)
                        val mood = moods[date] ?: Mood.NONE
                        val isToday = date == today
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            MoodSquare(
                                mood = mood,
                                size = 16.dp,
                                selected = isToday,
                                onClick = { onDayClick(date) }
                            )
                        }
                        day++
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}
