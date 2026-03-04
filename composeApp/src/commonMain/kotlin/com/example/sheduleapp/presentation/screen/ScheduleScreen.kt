package com.example.scheduleapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.data.model.EventDto
import com.example.scheduleapp.di.commonModule
import com.example.scheduleapp.presentation.ScheduleViewModel
import com.example.sheduleapp.ui.theme.ScheduleAppTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = koinInject()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val weekRangeText by viewModel.weekRangeText.collectAsState()
    val eventsByDay by viewModel.eventsByDay.collectAsState()
    val expandedDays by viewModel.expandedDays.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchSchedule()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        WeekNavigationBar(
            weekRangeText = weekRangeText,
            onPreviousWeek = { viewModel.goToPreviousWeek() },
            onNextWeek = { viewModel.goToNextWeek() },
            onCurrentWeek = { viewModel.goToCurrentWeek() }
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) }
        )

        when {
            isLoading -> LoadingContent()
            errorMessage != null -> ErrorContent(errorMessage!!) { viewModel.fetchSchedule() }
            eventsByDay.isEmpty() && searchQuery.isNotEmpty() -> NoResultsContent(searchQuery)
            eventsByDay.isEmpty() -> EmptyContent()
            else -> EventsByDayList(
                eventsByDay = eventsByDay,
                expandedDays = expandedDays,
                onToggleDay = { viewModel.toggleDayExpansion(it) }
            )
        }
    }
}

@Composable
private fun WeekNavigationBar(
    weekRangeText: String,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onCurrentWeek: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
//            Text(
//                text = "Расписание",
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.onPrimaryContainer
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка "Предыдущая неделя"
                Button(
                    onClick = onPreviousWeek,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("◀", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
                }

                Text(
                    text = weekRangeText.ifEmpty { "Загрузка..." },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).clickable { onCurrentWeek() }.padding(horizontal = 8.dp)
                )

                // Кнопка "Следующая неделя"
                Button(
                    onClick = onNextWeek,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("▶", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        placeholder = { Text("Поиск предметов...") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                TextButton(onClick = { onQueryChange("") }) {
                    Text("✕", style = MaterialTheme.typography.titleMedium)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}
// Список пар, сгруппированных по дням
@Composable
private fun EventsByDayList(
    eventsByDay: Map<String, List<EventDto>>,
    expandedDays: Set<String>,
    onToggleDay: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        eventsByDay.forEach { (day, events) ->
            item(key = day) {
                DaySection(
                    day = day,
                    events = events,
                    isExpanded = day in expandedDays,
                    onToggle = { onToggleDay(day) }
                )
            }
        }
    }
}

// Секция для одного дня
@Composable
private fun DaySection(
    day: String,
    events: List<EventDto>,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Заголовок дня
        DayHeader(
            day = day,
            eventCount = events.size,
            isExpanded = isExpanded,
            onToggle = onToggle
        )

        // Список событий (показываем только если развернут)
        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                events.forEach { event ->
                    EventCard(event)
                }
            }
        }
    }
}

// Заголовок дня
@Composable
private fun DayHeader(
    day: String,
    eventCount: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$eventCount ${getPluralForm(eventCount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Text(
                text = if (isExpanded) "▼" else "▶",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

private fun getPluralForm(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "пара"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "пары"
        else -> "пар"
    }
}


@Composable
private fun EventCard(event: EventDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = event.name ?: "Без названия",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${formatEventTime(event.start, event.end)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (event.start != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatEventDate(event.start),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

private fun formatEventTime(start: String?, end: String?): String {
    if (start == null) return "Время не указано"
    val startTime = start.substring(11, 16)
    val endTime = end?.substring(11, 16) ?: ""
    return if (endTime.isNotEmpty()) "$startTime - $endTime" else startTime
}

private fun formatEventDate(dateTime: String): String {
    val date = dateTime.substring(0, 10)
    val parts = date.split("-")
    if (parts.size != 3) return date

    val months = listOf("янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек")
    val day = parts[2].toIntOrNull() ?: return date
    val month = parts[1].toIntOrNull()?.let { if (it in 1..12) months[it - 1] else null } ?: return date
    return "$day $month"
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Загрузка расписания...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ErrorContent(errorMessage: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text("Ошибка", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Повторить") }
        }
    }
}

@Composable
private fun NoResultsContent(query: String) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍 Ничего не найдено", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("По запросу \"$query\" ничего не найдено", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Нет событий", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("На этой неделе расписание отсутствует", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}

@Preview
@Composable
private fun ScheduleScreenPreview() {
    KoinApplication(application = { modules(commonModule) }) {
        ScheduleAppTheme {
            ScheduleScreen()
        }
    }
}





