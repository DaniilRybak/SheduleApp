package com.example.scheduleapp.data.repository

import com.example.scheduleapp.domain.model.ScheduleEntry
import com.example.scheduleapp.domain.repository.ScheduleEntryRepository

class ScheduleEntryRepositoryImpl : ScheduleEntryRepository {
    private val entries = mutableListOf<ScheduleEntry>()
    override suspend fun getEntries(): List<ScheduleEntry> = entries.toList()
    fun setEntries(list: List<ScheduleEntry>) {
        entries.clear()
        entries.addAll(list)
    }
}
