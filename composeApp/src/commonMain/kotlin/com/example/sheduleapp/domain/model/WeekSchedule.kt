package com.example.scheduleapp.domain.model

data class WeekSchedule(
    val weekStartIso: String,
    val days: List<DaySchedule>
)
