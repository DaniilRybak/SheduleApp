package com.example.scheduleapp.domain.model

data class DaySchedule(
    val dateKey: String,
    val lessons: List<Lesson>
)
