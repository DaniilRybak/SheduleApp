package com.example.scheduleapp.domain.model

data class Lesson(
    val id: String,
    val title: String,
    val courseName: String,
    val teacherName: String,
    val startIso: String,
    val endIso: String,
    val location: String? = null
)
