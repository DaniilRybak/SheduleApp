package com.example.sheduleapp.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.sheduleapp.presentation.strings.ScheduleStrings
import com.example.sheduleapp.data.model.GroupDto
import com.example.sheduleapp.ui.theme.ScheduleAppTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

/** Основной экран расписания с фильтрацией по дням и режимами отображения. */
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
    val disciplineByEventId by viewModel.disciplineByEventId.collectAsState()
    val disciplineShortByEventId by viewModel.disciplineShortByEventId.collectAsState()

    val favoriteGroups = remember(groups, favoriteGroupIds) {
        groups
            .filter { it.personId in favoriteGroupIds }
            .sortedBy { it.name }
    }

    val compactDayItems = remember(dayItemsByDay) {
        dayItemsByDay.mapValues { (_, items) ->
            items.filterNot { it is DaySlotItem.WindowSlot }
        }
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
            _onOpenSettings = onOpenSettings
        )

        ScheduleContent(
            isLoading = isLoading,
            errorMessage = errorMessage,
            dayItemsByDay = dayItemsByDay,
            compactDayItems = compactDayItems,
            searchQuery = searchQuery,
            displayMode = displayMode,
            expandedDays = expandedDays,
            disciplineByEventId = disciplineByEventId,
            disciplineShortByEventId = disciplineShortByEventId,
            onRetry = { viewModel.fetchSchedule() },
            onToggleDay = { viewModel.toggleDayExpansion(it) }
        )
    }
}

@Composable
private fun ScheduleContent(
    isLoading: Boolean,
    errorMessage: String?,
    dayItemsByDay: Map<String, List<DaySlotItem>>,
    compactDayItems: Map<String, List<DaySlotItem>>,
    searchQuery: String,
    displayMode: DisplayMode,
    expandedDays: Set<String>,
    disciplineByEventId: Map<String, String>,
    disciplineShortByEventId: Map<String, String>,
    onRetry: () -> Unit,
    onToggleDay: (String) -> Unit
) {
    when {
        isLoading -> LoadingContent()
        errorMessage != null -> ErrorContent(errorMessage, onRetry)
        dayItemsByDay.isEmpty() && searchQuery.isNotEmpty() -> NoResultsContent(searchQuery)
        dayItemsByDay.isEmpty() -> EmptyContent()
        else -> when (displayMode) {
            DisplayMode.NORMAL -> EventsByDayList(
                dayItemsByDay = dayItemsByDay,
                expandedDays = expandedDays,
                disciplineByEventId = disciplineByEventId,
                disciplineShortByEventId = disciplineShortByEventId,
                onToggleDay = onToggleDay
            )

            DisplayMode.COMPACT -> EventsByDayList(
                dayItemsByDay = compactDayItems,
                expandedDays = expandedDays,
                disciplineByEventId = disciplineByEventId,
                disciplineShortByEventId = disciplineShortByEventId,
                onToggleDay = onToggleDay
            )

            DisplayMode.GRID -> ScheduleGrid(
                dayItemsByDay = compactDayItems,
                disciplineByEventId = disciplineByEventId,
                disciplineShortByEventId = disciplineShortByEventId
            )
        }
    }
}

/** Панель навигации по неделям с кнопками переключения и избранными группами. */
@Suppress("UNUSED_PARAMETER")
@Composable
private fun WeekNavigationBar(
    weekRangeText: String,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onCurrentWeek: () -> Unit,
    onOpenGroupSearch: () -> Unit = {},
    favoriteGroups: List<GroupDto>,
    onFavoriteClick: (GroupDto) -> Unit = {},
    _onOpenSettings: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
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
                                    text = "+ ${ScheduleStrings.addFavorite}",
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
                Button(
                    onClick = onPreviousWeek,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(ScheduleStrings.previousWeek, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
                }

                Text(
                    text = weekRangeText.ifEmpty { ScheduleStrings.loadingPlaceholder },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).clickable { onCurrentWeek() }.padding(horizontal = 8.dp)
                )

                Button(
                    onClick = onNextWeek,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(ScheduleStrings.nextWeek, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

/** Список событий, сгруппированных по дням недели в развернутом или компактном виде. */
@Composable
private fun EventsByDayList(
    dayItemsByDay: Map<String, List<DaySlotItem>>,
    expandedDays: Set<String>,
    disciplineByEventId: Map<String, String>,
    disciplineShortByEventId: Map<String, String>,
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
                    disciplineByEventId = disciplineByEventId,
                    disciplineShortByEventId = disciplineShortByEventId,
                    onToggle = { onToggleDay(day) }
                )
            }
        }
    }
}

/** Сетка расписания (таблица «время × дни» с событиями в ячейках). */
@Composable
private fun ScheduleGrid(
    dayItemsByDay: Map<String, List<DaySlotItem>>,
    disciplineByEventId: Map<String, String>,
    disciplineShortByEventId: Map<String, String>
) {
    val dayKeys = remember(dayItemsByDay) { dayItemsByDay.keys.toList() }
    val slots = remember { TimeSlot.defaultSlots() }
    val horizontalScroll = rememberScrollState()
    val verticalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 8.dp)
            .horizontalScroll(horizontalScroll)
            .verticalScroll(verticalScroll)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = ScheduleStrings.gridTime,
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
                    text = ScheduleStrings.slotLabel(slot.index, slot.startHm, slot.endHm),
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
                        disciplineByEventId = disciplineByEventId,
                        disciplineShortByEventId = disciplineShortByEventId,
                        modifier = Modifier
                            .width(180.dp)
                            .fillMaxHeight()
                            .padding(horizontal = 6.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        val unplacedByDay = remember(dayItemsByDay) {
            dayItemsByDay.mapValues { (_, items) ->
                items.filterIsInstance<DaySlotItem.UnplacedLesson>()
            }.filterValues { it.isNotEmpty() }
        }

        if (unplacedByDay.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = ScheduleStrings.outOfGrid,
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
                        customLocation = item.customLocation,
                        teacherName = item.teacherName,
                        disciplineName = disciplineByEventId[item.lesson.id],
                        disciplineShortName = disciplineShortByEventId[item.lesson.id]
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

/** Ячейка сетки расписания, содержащая события за определенный день и временной слот. */
@Composable
private fun GridCell(
    items: List<DaySlotItem>,
    disciplineByEventId: Map<String, String>,
    disciplineShortByEventId: Map<String, String>,
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (items.isEmpty()) {
                Text(
                    text = ScheduleStrings.emptyCell,
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
                        val disciplineName = disciplineByEventId[item.lesson.id]
                        val disciplineShortName = disciplineShortByEventId[item.lesson.id]
                        val containerColor = lessonTypeContainerColor(disciplineShortName)
                        val location = item.roomName ?: item.customLocation

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(containerColor)
                                .padding(8.dp)
                                .heightIn(min = 86.dp)
                        ) {
                            Text(
                                text = buildLessonTitle(
                                    lesson = item.lesson,
                                    disciplineName = disciplineName
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            TeacherAndLocationBlock(
                                teacherName = item.teacherName,
                                location = location,
                                topPadding = 16.dp,
                                teacherStyle = MaterialTheme.typography.labelSmall,
                                locationStyle = MaterialTheme.typography.bodySmall
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

/** Секция дня с заголовком, счетчиком событий и развертываемым списком занятий. */
@Composable
private fun DaySection(
    day: String,
    dayItems: List<DaySlotItem>,
    isExpanded: Boolean,
    disciplineByEventId: Map<String, String>,
    disciplineShortByEventId: Map<String, String>,
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
                            teacherName = item.teacherName,
                            disciplineName = disciplineByEventId[item.lesson.id],
                            disciplineShortName = disciplineShortByEventId[item.lesson.id]
                        )
                        is DaySlotItem.WindowSlot -> WindowCard(item.slot)
                        is DaySlotItem.ConflictSlot -> ConflictCardWithLocation(
                            item.slot,
                            item.lessons,
                            item.locations,
                            item.teachers,
                            disciplineByEventId,
                            disciplineShortByEventId
                        )
                        is DaySlotItem.UnplacedLesson -> UnplacedLessonCardWithLocation(
                            item.lesson,
                            item.reason,
                            customLocation = item.customLocation,
                            teacherName = item.teacherName,
                            disciplineName = disciplineByEventId[item.lesson.id],
                            disciplineShortName = disciplineShortByEventId[item.lesson.id]
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

/** Заголовок дня с названием, количеством пар и иконкой разворота. */
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
                    text = ScheduleStrings.lessonsCountLabel(eventCount),
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

/** Карточка события с временем, преподавателем и локацией. */
@Composable
private fun EventCardWithLocation(
    event: EventDto,
    roomName: String? = null,
    customLocation: String? = null,
    teacherName: String? = null,
    disciplineName: String? = null,
    disciplineShortName: String? = null
) {
    val containerColor = lessonTypeContainerColor(disciplineShortName)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = buildLessonTitle(event, disciplineName),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatEventTime(event.start, event.end),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TeacherAndLocationBlock(
                teacherName = teacherName,
                location = roomName ?: customLocation,
                topPadding = 8.dp,
                teacherStyle = MaterialTheme.typography.bodySmall,
                locationStyle = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/** Карточка для отображения окна (свободного времени) между занятиями. */
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
                text = ScheduleStrings.window,
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

/** Карточка конфликта расписания с перечислением всех пересекающихся занятий. */
@Suppress("UNUSED_PARAMETER")
@Composable
private fun ConflictCardWithLocation(
    slot: TimeSlot,
    lessons: List<EventDto>,
    locations: Map<String, String?> = emptyMap(),
    teachers: Map<String, String?> = emptyMap(),
    disciplineByEventId: Map<String, String> = emptyMap(),
    _disciplineShortByEventId: Map<String, String> = emptyMap()
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = ScheduleStrings.conflictTitle(slot.startHm, slot.endHm),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            lessons.forEach { event ->
                val locationStr = locations[event.id]?.let { " 📍 $it" } ?: ""
                val teacherStr = teachers[event.id]?.let { " 👨‍🏫 $it" } ?: ""
                Text(
                    text = "• ${buildLessonTitle(event, disciplineByEventId[event.id])} (${formatEventTime(event.start, event.end)})$teacherStr$locationStr",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

/** Карточка занятия вне основной сетки расписания. */
@Suppress("UNUSED_PARAMETER")
@Composable
private fun UnplacedLessonCardWithLocation(
    event: EventDto,
    _roomName: String? = null,
    customLocation: String? = null,
    teacherName: String? = null,
    disciplineName: String? = null,
    disciplineShortName: String? = null
) {
    val containerColor = lessonTypeContainerColor(disciplineShortName)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(
                text = ScheduleStrings.outOfGridTitle(buildLessonTitle(event, disciplineName)),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (teacherName != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = ScheduleStrings.teacher(teacherName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (customLocation != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = ScheduleStrings.location(customLocation),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun TeacherAndLocationBlock(
    teacherName: String?,
    location: String?,
    topPadding: androidx.compose.ui.unit.Dp,
    teacherStyle: androidx.compose.ui.text.TextStyle,
    locationStyle: androidx.compose.ui.text.TextStyle
) {
    if (teacherName == null && location == null) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
    ) {
        if (teacherName != null) {
            Text(
                text = teacherName,
                style = teacherStyle,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (teacherName != null && location != null) {
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (location != null) {
            Text(
                text = location,
                style = locationStyle,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/** Определяет тип занятия (лекция, практика, лаба или другое) по short-названию из JSON. */
private fun resolveLessonType(disciplineShortName: String?): LessonType {
    val short = disciplineShortName.orEmpty().lowercase()

    return when {
        short.contains("лаб") || short.contains("лаборатор") -> LessonType.LAB
        short.contains("лекц") || short.contains("лекцион") || short.contains("лк") -> LessonType.LECTURE
        short.contains("практ") || short.contains("практичес") || short.contains("семинар") -> LessonType.PRACTICE
        else -> LessonType.OTHER
    }
}

/** Возвращает цвет фона ячейки в зависимости от типа занятия. */
@Composable
private fun lessonTypeContainerColor(disciplineShortName: String?): Color {
    return when (resolveLessonType(disciplineShortName)) {
        LessonType.PRACTICE -> Color(0xFFE3F2FD)
        LessonType.LECTURE -> Color(0xFFE8F5E9)
        LessonType.LAB -> Color(0xFFFFF3E0)
        LessonType.OTHER -> MaterialTheme.colorScheme.surfaceVariant
    }
}

/** Формирует заголовок занятия из названия дисциплины и темы. */
private fun buildLessonTitle(
    lesson: EventDto,
    disciplineName: String?
): String {
    val discipline = disciplineName?.trim().orEmpty()
    val topic = lesson.name?.trim().orEmpty()

    return when {
        discipline.isBlank() && topic.isBlank() -> ScheduleStrings.unknownTitle
        discipline.isBlank() -> topic
        topic.isBlank() -> discipline
        discipline.equals(topic, ignoreCase = true) -> discipline
        else -> "$discipline / $topic"
    }
}

/** Форматирует время события в удобный для отображения вид. */
private fun formatEventTime(start: String?, end: String?): String {
    val startTime = extractTime(start)
    val endTime = extractTime(end)

    return when {
        startTime == null -> ScheduleStrings.unknownTime
        endTime == null -> startTime
        else -> "$startTime - $endTime"
    }
}

/** Извлекает время (HH:mm) из строки формата ISO 8601. */
private fun extractTime(dateTime: String?): String? {
    if (dateTime == null || dateTime.length < 16) return null
    return dateTime.substring(11, 16)
}

private enum class LessonType {
    PRACTICE,
    LECTURE,
    LAB,
    OTHER
}

/** Экран загрузки расписания со спиннером и текстом. */
@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(ScheduleStrings.loadingSchedule, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/** Экран ошибки с сообщением и кнопкой повтора. */
@Composable
private fun ErrorContent(errorMessage: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text(ScheduleStrings.error, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) { Text(ScheduleStrings.retry) }
        }
    }
}

/** Экран отсутствия результатов поиска. */
@Composable
private fun NoResultsContent(query: String) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔍 ${ScheduleStrings.noResultsTitle}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(ScheduleStrings.noResultsMessage(query), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}

/** Экран пустого расписания (нет событий на выбранную неделю). */
@Composable
private fun EmptyContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(ScheduleStrings.emptyEventsTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(ScheduleStrings.emptyWeekMessage, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}

/** Preview экрана расписания с KoinApplication для DI. */
@Preview
@Composable
private fun ScheduleScreenPreview() {
    KoinApplication(application = { modules(commonModule) }) {
        ScheduleAppTheme {
            ScheduleScreen()
        }
    }
}
