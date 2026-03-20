package com.example.sheduleapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduleapp.data.model.EventDto
import com.example.scheduleapp.di.commonModule
import com.example.scheduleapp.domain.model.DaySlotItem
import com.example.scheduleapp.domain.model.DisplayMode
import com.example.scheduleapp.domain.model.TimeSlot
import com.example.scheduleapp.presentation.ScheduleViewModel
import com.example.sheduleapp.data.model.GroupDto
import com.example.sheduleapp.ui.theme.ScheduleAppTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = koinInject(),
    onOpenGroupSearch: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val weekRangeText by viewModel.weekRangeText.collectAsState()
    val dayItemsByDay by viewModel.dayItemsByDay.collectAsState()
    val expandedDays by viewModel.expandedDays.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val favoriteGroupIds by viewModel.favoriteGroupIds.collectAsState()
    val displayMode by viewModel.displayMode.collectAsState()

    val favoriteGroups = groups
        .filter { it.personId in favoriteGroupIds }
        .sortedBy { it.name }

    val compactDayItems = dayItemsByDay.mapValues { (_, items) ->
        items.filterNot { it is DaySlotItem.WindowSlot }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchSchedule()
        viewModel.loadGroups()
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
            onCurrentWeek = { viewModel.goToCurrentWeek() },
            onOpenGroupSearch = onOpenGroupSearch,
            favoriteGroups = favoriteGroups,
            onFavoriteClick = { group ->
                viewModel.selectGroup(group.personId, group.name)
            },
            onOpenSettings = onOpenSettings
        )

//        SearchBar(
//            query = searchQuery,
//            onQueryChange = { viewModel.updateSearchQuery(it) }
//        )

        when {
            isLoading -> LoadingContent()
            errorMessage != null -> ErrorContent(errorMessage!!) { viewModel.fetchSchedule() }
            dayItemsByDay.isEmpty() && searchQuery.isNotEmpty() -> NoResultsContent(searchQuery)
            dayItemsByDay.isEmpty() -> EmptyContent()
            else -> when (displayMode) {
                DisplayMode.NORMAL -> EventsByDayList(
                    dayItemsByDay = dayItemsByDay,
                    expandedDays = expandedDays,
                    onToggleDay = { viewModel.toggleDayExpansion(it) }
                )
                DisplayMode.COMPACT -> EventsByDayList(
                    dayItemsByDay = compactDayItems,
                    expandedDays = expandedDays,
                    onToggleDay = { viewModel.toggleDayExpansion(it) }
                )
                DisplayMode.GRID -> ScheduleGrid(
                    dayItemsByDay = compactDayItems
                )
            }
        }
    }
}

@Composable
private fun WeekNavigationBar(
    weekRangeText: String,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onCurrentWeek: () -> Unit,
    onOpenGroupSearch: () -> Unit = {},
    favoriteGroups: List<GroupDto>,
    onFavoriteClick: (GroupDto) -> Unit = {},
    onOpenSettings: () -> Unit = {}
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

            if (favoriteGroups.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(favoriteGroups, key = { it.personId }) { group ->
                        AssistChip(
                            onClick = { onFavoriteClick(group) },
                            label = {
                                Text(
                                    text = group.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            border = null
                        )
                    }

                    item {
                        AssistChip(
                            onClick = onOpenGroupSearch,
                            label = {
                                Text(
                                    text = "+ Добавить",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            border = null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

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

//@Composable
//private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
//    OutlinedTextField(
//        value = query,
//        onValueChange = onQueryChange,
//        modifier = Modifier.fillMaxWidth().padding(16.dp),
//        placeholder = { Text("Поиск предметов...") },
//        trailingIcon = {
//            if (query.isNotEmpty()) {
//                TextButton(onClick = { onQueryChange("") }) {
//                    Text("✕", style = MaterialTheme.typography.titleMedium)
//                }
//            }
//        },
//        singleLine = true,
//        shape = RoundedCornerShape(12.dp),
//        colors = OutlinedTextFieldDefaults.colors(
//            focusedContainerColor = MaterialTheme.colorScheme.surface,
//            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
//            focusedBorderColor = MaterialTheme.colorScheme.primary,
//            unfocusedBorderColor = MaterialTheme.colorScheme.outline
//        )
//    )
//}

@Composable
private fun EventsByDayList(
    dayItemsByDay: Map<String, List<DaySlotItem>>,
    expandedDays: Set<String>,
    onToggleDay: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        dayItemsByDay.forEach { (day, items) ->
            item(key = day) {
                DaySection(
                    day = day,
                    dayItems = items,
                    isExpanded = day in expandedDays,
                    onToggle = { onToggleDay(day) }
                )
            }
        }
    }
}

@Composable
private fun ScheduleGrid(dayItemsByDay: Map<String, List<DaySlotItem>>) {
    val dayKeys = dayItemsByDay.keys.toList()
    val slots = TimeSlot.defaultSlots()
    val horizontalScroll = rememberScrollState()
    val verticalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp)
            .horizontalScroll(horizontalScroll)
            .verticalScroll(verticalScroll)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Время",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.width(92.dp)
            )
            dayKeys.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(180.dp).padding(horizontal = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        slots.forEach { slot ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "${slot.id} Пара\n${slot.startHm}\n${slot.endHm}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(92.dp).padding(top = 10.dp)
                )

                dayKeys.forEach { day ->
                    val cellItems = dayItemsByDay[day].orEmpty().filter { item ->
                        when (item) {
                            is DaySlotItem.LessonSlot -> item.slot.id == slot.id
                            is DaySlotItem.ConflictSlot -> item.slot.id == slot.id
                            else -> false
                        }
                    }

                    GridCell(
                        items = cellItems,
                        modifier = Modifier
                            .width(180.dp)
                            .fillMaxHeight()
                            .padding(horizontal = 6.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        val unplacedByDay = dayItemsByDay.mapValues { (_, items) ->
            items.filterIsInstance<DaySlotItem.UnplacedLesson>()
        }.filterValues { it.isNotEmpty() }

        if (unplacedByDay.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Вне сетки",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            unplacedByDay.forEach { (day, items) ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                items.forEach { item ->
                    UnplacedLessonCardWithLocation(
                        event = item.lesson,
                        roomName = item.roomName,
                        customLocation = item.customLocation,
                        teacherName = item.teacherName
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun GridCell(
    items: List<DaySlotItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 88.dp)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (items.isEmpty()) {
                Text(
                    text = "—",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                return@Column
            }

            items.forEach { item ->
                when (item) {
                    is DaySlotItem.LessonSlot -> {
                        Text(
                            text = item.lesson.name ?: "Без названия",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (item.teacherName != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.teacherName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (item.roomName != null || item.customLocation != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${item.roomName ?: item.customLocation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    is DaySlotItem.ConflictSlot -> {
                        Text(
                            text = "Конфликт: ${item.lessons.size}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}

// Секция для одного дня
@Composable
private fun DaySection(
    day: String,
    dayItems: List<DaySlotItem>,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DayHeader(
            day = day,
            eventCount = countLessons(dayItems),
            isExpanded = isExpanded,
            onToggle = onToggle
        )

        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dayItems.forEach { item ->
                    when (item) {
                        is DaySlotItem.LessonSlot -> EventCardWithLocation(
                            item.lesson,
                            roomName = item.roomName,
                            customLocation = item.customLocation,
                            teacherName = item.teacherName
                        )
                        is DaySlotItem.WindowSlot -> WindowCard(item.slot)
                        is DaySlotItem.ConflictSlot -> ConflictCardWithLocation(
                            item.slot,
                            item.lessons,
                            item.locations,
                            item.teachers
                        )
                        is DaySlotItem.UnplacedLesson -> UnplacedLessonCardWithLocation(
                            item.lesson,
                            item.reason,
                            customLocation = item.customLocation,
                            teacherName = item.teacherName
                        )
                    }
                }
            }
        }
    }
}

private fun countLessons(items: List<DaySlotItem>): Int {
    return items.sumOf { item ->
        when (item) {
            is DaySlotItem.LessonSlot -> 1
            is DaySlotItem.ConflictSlot -> item.lessons.size
            else -> 0
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
private fun EventCardWithLocation(event: EventDto, roomName: String? = null, customLocation: String? = null, teacherName: String? = null) {
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
                text = formatEventTime(event.start, event.end),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (teacherName != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "👨‍🏫 $teacherName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (roomName != null || customLocation != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${roomName ?: customLocation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun WindowCard(slot: TimeSlot) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(
                text = "Окно",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${slot.startHm} - ${slot.endHm}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun ConflictCardWithLocation(slot: TimeSlot, lessons: List<EventDto>, locations: Map<String, String?> = emptyMap(), teachers: Map<String, String?> = emptyMap()) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Конфликт пар (${slot.startHm} - ${slot.endHm})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            lessons.forEach { event ->
                val locationStr = locations[event.id]?.let { " 📍 $it" } ?: ""
                val teacherStr = teachers[event.id]?.let { " 👨‍🏫 $it" } ?: ""
                Text(
                    text = "• ${event.name ?: "Без названия"} (${formatEventTime(event.start, event.end)})$teacherStr$locationStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun UnplacedLessonCardWithLocation(event: EventDto, roomName: String? = null, customLocation: String? = null, teacherName: String? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(
                text = "Вне сетки: ${event.name ?: "Без названия"}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (teacherName != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "👨‍🏫 $teacherName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (roomName != null || customLocation != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "📍 ${roomName ?: customLocation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

private fun formatEventTime(start: String?, end: String?): String {
    val startTime = extractTime(start)
    val endTime = extractTime(end)

    return when {
        startTime == null -> "Время не указано"
        endTime == null -> startTime
        else -> "$startTime - $endTime"
    }
}

private fun extractTime(dateTime: String?): String? {
    if (dateTime == null || dateTime.length < 16) return null
    return dateTime.substring(11, 16)
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
