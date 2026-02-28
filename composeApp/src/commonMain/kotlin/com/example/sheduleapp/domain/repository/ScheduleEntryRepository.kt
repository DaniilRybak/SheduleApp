package com.example.scheduleapp.domain.repository

import com.example.scheduleapp.domain.model.ScheduleEntry

interface ScheduleEntryRepository {
    suspend fun getEntries(): List<ScheduleEntry>
}