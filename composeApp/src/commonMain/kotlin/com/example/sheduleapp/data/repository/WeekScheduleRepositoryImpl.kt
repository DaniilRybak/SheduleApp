package com.example.scheduleapp.data.repository

import com.example.scheduleapp.data.model.EventDto
import com.example.scheduleapp.data.model.PersonDto
import com.example.scheduleapp.data.model.ScheduleRequest
import com.example.scheduleapp.data.model.ScheduleResponse
import com.example.scheduleapp.domain.model.DaySchedule
import com.example.scheduleapp.domain.model.Lesson
import com.example.scheduleapp.domain.model.WeekSchedule
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class WeekScheduleRepositoryImpl(
    private val scheduleRepository: ScheduleRepository
) : com.example.scheduleapp.domain.repository.WeekScheduleRepository {

    override suspend fun getWeekSchedule(date: LocalDate, entryId: String): WeekSchedule? {
        if (entryId.isBlank()) return null
        val daysToMonday = date.dayOfWeek.isoDayNumber - 1
        val weekStart = date.minus(daysToMonday.toLong(), DateTimeUnit.DAY)
        val weekEnd = weekStart.plus(6, DateTimeUnit.DAY)
        val timeMin = "${weekStart}T00:00:00"
        val timeMax = "${weekEnd}T23:59:59"
        return try {
            val response = scheduleRepository.getSchedule(
                ScheduleRequest(size = 200, timeMin = timeMin, timeMax = timeMax, attendeePersonId = listOf(entryId))
            )
            mapResponseToWeekSchedule(response, weekStart)
        } catch (e: Exception) {
            null
        }
    }

    private fun mapResponseToWeekSchedule(response: ScheduleResponse, weekStart: LocalDate): WeekSchedule {
        val events = response.embedded?.events ?: emptyList()
        val persons = response.embedded?.persons ?: emptyList()
        val dayMap = events.groupBy { event ->
            event.start?.substringBefore("T") ?: ""
        }
        val days = (0..6).map { offset ->
            val day = weekStart.plus(offset.toLong(), DateTimeUnit.DAY)
            val dateKey = day.toString()

            val dayEvents = dayMap[dateKey] ?: emptyList()
            val lessons = dayEvents.map { e -> mapEventToLesson(e, persons) }

            DaySchedule(dateKey = dateKey, lessons = lessons.sortedBy { it.startIso })
        }
        return WeekSchedule(
            weekStartIso = weekStart.toString() + "T00:00:00",
            days = days
        )
    }

    private fun mapEventToLesson(event: EventDto, persons: List<PersonDto>): Lesson {
        val start = event.start ?: ""
        val end = event.end ?: ""
        val name = event.name ?: ""
        return Lesson(
            id = event.id,
            title = name,
            courseName = name,
            teacherName = "",
            startIso = start,
            endIso = end,
            location = null
        )
    }
}