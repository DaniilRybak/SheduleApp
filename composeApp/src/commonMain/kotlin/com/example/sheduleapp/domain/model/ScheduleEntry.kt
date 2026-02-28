package com.example.scheduleapp.domain.model

data class ScheduleEntry(
    val id: String,
    val name: String,
    val type: String = ""
)
