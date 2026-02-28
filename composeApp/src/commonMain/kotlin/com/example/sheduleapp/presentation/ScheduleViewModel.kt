package com.example.scheduleapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduleapp.data.model.ScheduleRequest
import com.example.scheduleapp.data.model.ScheduleResponse
import com.example.scheduleapp.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Clock

class ScheduleViewModel(private val scheduleRepository: ScheduleRepository) : ViewModel() {

    private val _scheduleState = MutableStateFlow<ScheduleResponse?>(null)
    val scheduleState = _scheduleState.asStateFlow()

    fun fetchSchedule() {
        viewModelScope.launch {
            val timeZone = TimeZone.currentSystemDefault()
            val today = Clock.System.now().toLocalDateTime(timeZone).date

            val timeMin = today.atStartOfDayIn(timeZone).toString()
            val timeMax = today.plus(7, DateTimeUnit.DAY).atStartOfDayIn(timeZone).toString()
            try {
                val request = ScheduleRequest(
                    size = 50,
                    timeMin = timeMin,
                    timeMax = timeMax,
                    attendeePersonId = listOf("d65a68a2-bfcf-4484-93f1-69deb3873e6a")
                )

                // Получаем сразу готовый объект, Ktor парсит JSON за нас
                val scheduleResponse = scheduleRepository.getSchedule(request)
                _scheduleState.value = scheduleResponse

            } catch (e: Exception) {
                println("Error fetching schedule object: ${e.message}")
                println("timeMin: $timeMin")
                e.printStackTrace()
                _scheduleState.value = null
            }
        }
    }
}
