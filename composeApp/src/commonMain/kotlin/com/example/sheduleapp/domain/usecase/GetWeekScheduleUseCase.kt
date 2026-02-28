package com.example.scheduleapp.domain.usecase

import com.example.scheduleapp.domain.model.WeekSchedule
import kotlinx.datetime.LocalDate

class GetWeekScheduleUseCase(
    private val scheduleRepository: com.example.scheduleapp.domain.repository.WeekScheduleRepository
) {
    suspend operator fun invoke(date: LocalDate, entryId: String): WeekSchedule? =
        scheduleRepository.getWeekSchedule(date, entryId)
}