package com.example.scheduleapp.domain.repository

import com.example.scheduleapp.domain.model.WeekSchedule
import kotlinx.datetime.LocalDate
interface WeekScheduleRepository {
    suspend fun getWeekSchedule(date: LocalDate, entryId: String): WeekSchedule?
}