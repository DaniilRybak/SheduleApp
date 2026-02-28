package com.example.sheduleapp.util

import kotlinx.datetime.*
import kotlin.time.Clock

/**
 * Получить начало недели (понедельник) для заданной даты
 * @param date дата, для которой нужно найти начало недели
 * @return дата понедельника текущей недели
 */
fun getStartOfWeek(date: LocalDate): LocalDate {
    val daysToSubtract = date.dayOfWeek.isoDayNumber - DayOfWeek.MONDAY.isoDayNumber
    return date.minus(daysToSubtract, DateTimeUnit.DAY)
}

/**
 * Получить конец недели (воскресенье) для заданной даты
 */
fun getEndOfWeek(date: LocalDate): LocalDate {
    return getStartOfWeek(date).plus(6, DateTimeUnit.DAY)
}

/**
 * Получить текущую дату
 */
fun getCurrentDate(): LocalDate {
    val timeZone = TimeZone.currentSystemDefault()
    return Clock.System.now().toLocalDateTime(timeZone).date
}