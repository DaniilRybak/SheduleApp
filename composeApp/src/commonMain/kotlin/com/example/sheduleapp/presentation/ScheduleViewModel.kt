package com.example.scheduleapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduleapp.data.model.EventDto
import com.example.scheduleapp.data.model.ScheduleRequest
import com.example.scheduleapp.data.model.ScheduleResponse
import com.example.scheduleapp.data.repository.ScheduleRepository
import com.example.scheduleapp.domain.usecase.SearchScheduleEntriesUseCase
import com.example.sheduleapp.util.getStartOfWeek
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Clock


class ScheduleViewModel(private val scheduleRepository: ScheduleRepository,
                        private val searchUseCase: SearchScheduleEntriesUseCase = SearchScheduleEntriesUseCase()
) : ViewModel() {

    private val _scheduleState = MutableStateFlow<ScheduleResponse?>(null)
    val scheduleState = _scheduleState.asStateFlow()

    private val _filteredEvents = MutableStateFlow<List<EventDto>>(emptyList())
    val filteredEvents = _filteredEvents.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun fetchSchedule() {
        viewModelScope.launch {
            val timeZone = TimeZone.currentSystemDefault()
            val today = Clock.System.now().toLocalDateTime(timeZone).date

            val weekStart = getStartOfWeek(today)
            val weekEnd = weekStart.plus(6, DateTimeUnit.DAY)

            val timeMin = weekStart.atStartOfDayIn(timeZone).toString()
            val timeMax = weekEnd.atTime(23, 59, 59).toInstant(timeZone).toString()

            try {
                val request = ScheduleRequest(
                    size = 50,
                    timeMin = timeMin,
                    timeMax = timeMax,
                    attendeePersonId = listOf("d65a68a2-bfcf-4484-93f1-69deb3873e6a")
                )

                val scheduleResponse = scheduleRepository.getSchedule(request)
                _scheduleState.value = scheduleResponse

                filterEvents()

            } catch (e: Exception) {
                println("Error fetching schedule object: ${e.message}")
                println("timeMin: $timeMin")
                val f = getStartOfWeek(today);
                println("Today: $f")
                e.printStackTrace()
                _scheduleState.value = null
            }
        }
    }


fun updateSearchQuery(query: String) {
    _searchQuery.value = query
    filterEvents()
}

private fun filterEvents() {
    val allEvents = _scheduleState.value?.embedded?.events ?: emptyList()
    _filteredEvents.value = searchUseCase(searchQuery.value, allEvents)
}
}