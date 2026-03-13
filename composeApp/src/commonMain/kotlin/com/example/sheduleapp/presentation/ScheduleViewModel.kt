package com.example.scheduleapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduleapp.data.model.EventDto
import com.example.scheduleapp.data.model.ScheduleRequest
import com.example.scheduleapp.data.model.ScheduleResponse
import com.example.scheduleapp.data.repository.ScheduleRepository
import com.example.scheduleapp.domain.model.DaySlotItem
import com.example.scheduleapp.domain.usecase.BuildDaySlotsUseCase
import com.example.scheduleapp.domain.usecase.SearchScheduleEntriesUseCase
import com.example.sheduleapp.data.model.GroupDto
import com.example.sheduleapp.data.repository.RemoteConfigRepository
import com.example.sheduleapp.util.getStartOfWeek
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlin.time.Clock


class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val searchUseCase: SearchScheduleEntriesUseCase = SearchScheduleEntriesUseCase(),
    private val buildDaySlotsUseCase: BuildDaySlotsUseCase = BuildDaySlotsUseCase()
) : ViewModel() {

    private enum class ScheduleTargetType {
        PERSON,
        ROOM
    }

    private val _groups = MutableStateFlow<List<GroupDto>>(emptyList())
    val groups = _groups.asStateFlow()

    private val _groupQuery = MutableStateFlow("")
    val groupQuery = _groupQuery.asStateFlow()

    private val _selectedPersonId = MutableStateFlow("d65a68a2-bfcf-4484-93f1-69deb3873e6a")
    private val _selectedTargetType = MutableStateFlow(ScheduleTargetType.PERSON)

    // Состояние расписания
    private val _scheduleState = MutableStateFlow<ScheduleResponse?>(null)
    val scheduleState = _scheduleState.asStateFlow()

    // Состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Состояние ошибки
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Отфильтрованные события
    private val _filteredEvents = MutableStateFlow<List<EventDto>>(emptyList())
    val filteredEvents = _filteredEvents.asStateFlow()

    // Поисковый запрос
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate = _selectedDate.asStateFlow()

    private val _weekRangeText = MutableStateFlow("")
    val weekRangeText = _weekRangeText.asStateFlow()

    private val _eventsByDay = MutableStateFlow<Map<String, List<EventDto>>>(emptyMap())
    val eventsByDay = _eventsByDay.asStateFlow()

    private val _dayItemsByDay = MutableStateFlow<Map<String, List<DaySlotItem>>>(emptyMap())
    val dayItemsByDay = _dayItemsByDay.asStateFlow()

    private val _expandedDays = MutableStateFlow<Set<String>>(emptySet())
    val expandedDays = _expandedDays.asStateFlow()

    fun fetchSchedule(date: LocalDate? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val timeZone = TimeZone.currentSystemDefault()
                val targetDate = date ?: _selectedDate.value ?: Clock.System.now().toLocalDateTime(timeZone).date

                _selectedDate.value = targetDate

                val weekStart = getStartOfWeek(targetDate)
                val weekEnd = weekStart.plus(6, DateTimeUnit.DAY)

                updateWeekRangeText(weekStart, weekEnd)

                val timeMin = weekStart.atStartOfDayIn(timeZone).toString()
                val timeMax = weekEnd.atTime(23, 59, 59).toInstant(timeZone).toString()

                println("Fetching schedule: $timeMin to $timeMax")

                val selectedId = _selectedPersonId.value
                val request = when (_selectedTargetType.value) {
                    ScheduleTargetType.PERSON -> ScheduleRequest(
                        size = 50,
                        timeMin = timeMin,
                        timeMax = timeMax,
                        attendeePersonId = listOf(selectedId)
                    )

                    ScheduleTargetType.ROOM -> ScheduleRequest(
                        size = 50,
                        timeMin = timeMin,
                        timeMax = timeMax,
                        roomId = listOf(selectedId)
                    )
                }

                val scheduleResponse = scheduleRepository.getSchedule(request)
                _scheduleState.value = scheduleResponse
                filterEvents()

                println(
                    "Schedule loaded successfully: ${scheduleResponse.embedded?.events?.size ?: 0} events, " +
                        "targetType=${_selectedTargetType.value}"
                )

            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("Field") == true -> "Ошибка формата данных. Попробуйте еще раз."
                    e.message?.contains("Unable to resolve") == true -> "Нет подключения к интернету"
                    e.message?.contains("timeout") == true -> "Превышено время ожидания"
                    else -> e.message ?: "Неизвестная ошибка"
                }
                _errorMessage.value = errorMsg
                _scheduleState.value = null
                println("Error fetching schedule: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Перейти к следующей неделе
    fun goToNextWeek() {
        val currentDate = _selectedDate.value ?: return
        val nextWeekDate = currentDate.plus(7, DateTimeUnit.DAY)
        fetchSchedule(nextWeekDate)
    }

    // Перейти к предыдущей неделе
    fun goToPreviousWeek() {
        val currentDate = _selectedDate.value ?: return
        val previousWeekDate = currentDate.minus(7, DateTimeUnit.DAY)
        fetchSchedule(previousWeekDate)
    }

    // Вернуться к текущей неделе
    fun goToCurrentWeek() {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        fetchSchedule(today)
    }

    // Загрузить список групп
    fun loadGroups() {
        viewModelScope.launch {
            try {
                val groupsList = remoteConfigRepository.loadGroups()
                _groups.value = groupsList
            } catch (e: Exception) {
                println("Error loading groups: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // Обновить строку поиска групп
    fun onGroupQueryChanged(query: String) {
        _groupQuery.value = query
    }

    fun selectGroup(personId: String, displayName: String? = null) {
        _selectedPersonId.value = personId
        _selectedTargetType.value = if (isLikelyRoomName(displayName)) {
            ScheduleTargetType.ROOM
        } else {
            ScheduleTargetType.PERSON
        }
        _groupQuery.value = ""
        fetchSchedule()
    }

    fun selectRoom(roomId: String) {
        _selectedPersonId.value = roomId
        _selectedTargetType.value = ScheduleTargetType.ROOM
        _groupQuery.value = ""
        fetchSchedule()
    }

    private fun isLikelyRoomName(name: String?): Boolean {
        if (name.isNullOrBlank()) return false

        val normalized = name.trim()
        val roomRegex = Regex("""^(?:[А-ЯA-Z]\s*-\s*\d{2,4}|\d{1,2}\s*-\s*\d{2,4}|ауд\.?\s*\d{2,4})$""", RegexOption.IGNORE_CASE)
        return roomRegex.matches(normalized)
    }

    private fun updateWeekRangeText(weekStart: LocalDate, weekEnd: LocalDate) {
        val months = listOf(
            "янв", "фев", "мар", "апр", "май", "июн",
            "июл", "авг", "сен", "окт", "ноя", "дек"
        )

        val startMonth = months[weekStart.month.number - 1]
        val endMonth = months[weekEnd.month.number - 1]

        _weekRangeText.value = if (weekStart.month == weekEnd.month) {
            "${weekStart.day} - ${weekEnd.day} $startMonth ${weekStart.year}"
        } else {
            "${weekStart.day} $startMonth - ${weekEnd.day} $endMonth ${weekStart.year}"
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterEvents()
    }

    // Переключить раскрытие дня
    fun toggleDayExpansion(dayKey: String) {
        _expandedDays.value = if (dayKey in _expandedDays.value) {
            _expandedDays.value - dayKey
        } else {
            _expandedDays.value + dayKey
        }
    }

    private fun filterEvents() {
        val allEvents = _scheduleState.value?.embedded?.events ?: emptyList()
        _filteredEvents.value = searchUseCase(searchQuery.value, allEvents)
        groupEventsByDay()
    }

    private fun groupEventsByDay() {
        val response = _scheduleState.value ?: return
        val events = _filteredEvents.value

        // Группируем по дням и сортируем
        val grouped = events
            .groupBy { event -> formatDayOfWeek(event.start) }
            .toList()
            .sortedBy { (day, _) -> getDayOrder(day) }
            .toMap()

        _eventsByDay.value = grouped

        _dayItemsByDay.value = grouped.mapValues { (_, dayEvents) ->
            val eventLocations = response.embedded?.eventLocations ?: emptyList()
            val eventRooms = response.embedded?.eventRooms ?: emptyList()
            val rooms = response.embedded?.rooms ?: emptyList()

            buildDaySlotsUseCase(
                dayEvents,
                eventLocations = eventLocations,
                eventRooms = eventRooms,
                rooms = rooms
            )
        }

        if (_expandedDays.value.isEmpty() && grouped.isNotEmpty()) {
            val timeZone = TimeZone.currentSystemDefault()
            val today = Clock.System.now().toLocalDateTime(timeZone).date
            val todayKey = formatDayOfWeek(today.toString())
            if (todayKey in grouped.keys) {
                _expandedDays.value = setOf(todayKey)
            } else {
                _expandedDays.value = setOf(grouped.keys.first())
            }
        }
    }

    private fun formatDayOfWeek(dateTime: String?): String {
        if (dateTime == null) return "Неизвестная дата"

        val date = dateTime.substring(0, 10)
        val parts = date.split("-")
        if (parts.size != 3) return date

        val year = parts[0].toIntOrNull() ?: return date
        val month = parts[1].toIntOrNull() ?: return date
        val day = parts[2].toIntOrNull() ?: return date

        val localDate = LocalDate(year, month, day)

        val daysShort = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        val months = listOf("янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек")

        val dayOfWeek = localDate.dayOfWeek.isoDayNumber - 1
        val dayShort = daysShort[dayOfWeek]
        val monthName = months[localDate.month.number - 1]

        return "$dayShort, $day $monthName"
    }

    private fun getDayOrder(dayStr: String): Int {
        val daysShort = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        val dayPrefix = dayStr.take(2)
        return daysShort.indexOf(dayPrefix).takeIf { it >= 0 } ?: 7
    }
}