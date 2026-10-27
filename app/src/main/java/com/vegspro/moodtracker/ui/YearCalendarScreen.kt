package com.vegspro.moodtracker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewMonth
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vegspro.moodtracker.data.Mood
import com.vegspro.moodtracker.notification.NotificationHelper
import com.vegspro.moodtracker.ui.components.MoodPickerDialog
import com.vegspro.moodtracker.ui.components.MoodSquare
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

enum class ViewMode { YEAR, MONTH }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun YearCalendarScreen(viewModel: MoodViewModel) {
    val moods by viewModel.moods.collectAsState()
    val context = LocalContext.current
    var currentYear by remember { mutableIntStateOf(Year.now().value) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showLegend by remember { mutableStateOf(false) }
    var remindersEnabled by remember { mutableStateOf(true) }
    var viewMode by remember { mutableStateOf(ViewMode.YEAR) }

    val today = remember { LocalDate.now() }
    val scope = rememberCoroutineScope()

    // Precompute year moods only when needed
    val yearMoods by remember(moods, currentYear) {
        derivedStateOf { moods.filter { it.key.year == currentYear } }
    }

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
                        viewMode = if (viewMode == ViewMode.YEAR) ViewMode.MONTH else ViewMode.YEAR
                    }) {
                        Icon(
                            imageVector = if (viewMode == ViewMode.YEAR)
                                Icons.Default.CalendarMonth
                            else
                                Icons.Default.CalendarViewMonth,
                            contentDescription = "Режим просмотра"
                        )
                    }
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
        ) {
            // Year selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
                        .padding(horizontal = 12.dp, vertical = 4.dp),
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

            when (viewMode) {
                ViewMode.YEAR -> YearOverview(
                    currentYear = currentYear,
                    moods = moods,
                    today = today,
                    yearMoods = yearMoods,
                    onDayClick = { selectedDate = it }
                )
                ViewMode.MONTH -> MonthPager(
                    currentYear = currentYear,
                    moods = moods,
                    today = today,
                    onDayClick = { selectedDate = it }
                )
            }
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
private fun YearOverview(
    currentYear: Int,
    moods: Map<LocalDate, Mood>,
    today: LocalDate,
    yearMoods: Map<LocalDate, Mood>,
    onDayClick: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        val months = remember { Month.entries }
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
                        onDayClick = onDayClick,
                        modifier = Modifier.weight(1f),
                        compact = true
                    )
                }
                repeat(3 - rowMonths.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (yearMoods.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            StatsCard(currentYear, yearMoods)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MonthPager(
    currentYear: Int,
    moods: Map<LocalDate, Mood>,
    today: LocalDate,
    onDayClick: (LocalDate) -> Unit
) {
    val initialPage = remember(currentYear) {
        val now = LocalDate.now()
        if (now.year == currentYear) now.monthValue - 1 else 0
    }
    val pagerState = rememberPagerState(initialPage = initialPage) { 12 }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        // Month navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                scope.launch {
                    val prev = (pagerState.currentPage - 1).coerceAtLeast(0)
                    pagerState.animateScrollToPage(prev)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Предыдущий месяц")
            }
            Text(
                text = Month.of(pagerState.currentPage + 1)
                    .getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru"))
                    .replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            IconButton(onClick = {
                scope.launch {
                    val next = (pagerState.currentPage + 1).coerceAtMost(11)
                    pagerState.animateScrollToPage(next)
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Следующий месяц")
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            val month = Month.of(page + 1)
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    MonthGrid(
                        year = currentYear,
                        month = month,
                        moods = moods,
                        today = today,
                        onDayClick = onDayClick,
                        compact = false
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthGrid(
    year: Int,
    month: Month,
    moods: Map<LocalDate, Mood>,
    today: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = true
) {
    val daysInMonth = remember(year, month) { month.length(Year.isLeap(year.toLong())) }
    val firstDay = remember(year, month) { LocalDate.of(year, month, 1) }
    val offset = remember(firstDay) { firstDay.dayOfWeek.value - 1 } // Monday = 0

    val cellSize = if (compact) 15.dp else 36.dp
    val fontSize = if (compact) 9.sp else 12.sp

    Column(modifier = modifier) {
        if (compact) {
            Text(
                text = month.getDisplayName(TextStyle.SHORT, Locale("ru")).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }

        // Weekday headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("П", "В", "С", "Ч", "П", "С", "В").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = fontSize,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(if (compact) 2.dp else 6.dp))

        var day = 1
        for (week in 0 until 6) {
            if (day > daysInMonth) break
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dow in 0 until 7) {
                    if ((week == 0 && dow < offset) || day > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).size(cellSize))
                    } else {
                        val date = LocalDate.of(year, month, day)
                        val mood = moods[date] ?: Mood.NONE
                        val isToday = date == today
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                        ) {
                            if (compact) {
                                MoodSquare(
                                    mood = mood,
                                    size = cellSize,
                                    selected = isToday,
                                    onClick = { onDayClick(date) }
                                )
                            } else {
                                // Larger mode: square + day number
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    MoodSquare(
                                        mood = mood,
                                        size = 28.dp,
                                        selected = isToday,
                                        onClick = { onDayClick(date) }
                                    )
                                    Text(
                                        text = day.toString(),
                                        fontSize = 11.sp,
                                        color = if (isToday)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        day++
                    }
                }
            }
            Spacer(modifier = Modifier.height(if (compact) 2.dp else 4.dp))
        }
    }
}

@Composable
private fun StatsCard(year: Int, yearMoods: Map<LocalDate, Mood>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Статистика $year",
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
