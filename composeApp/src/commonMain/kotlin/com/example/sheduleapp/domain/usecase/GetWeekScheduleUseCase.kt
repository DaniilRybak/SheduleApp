package com.example.scheduleapp.domain.usecase

import com.example.scheduleapp.domain.model.WeekSchedule
import com.example.scheduleapp.domain.repository.WeekScheduleRepository
import kotlinx.datetime.LocalDate

class GetWeekScheduleUseCase(
    private val scheduleRepository: WeekScheduleRepository
) {
    suspend operator fun invoke(date: LocalDate, entryId: String): WeekSchedule? =
        scheduleRepository.getWeekSchedule(date, entryId)
}