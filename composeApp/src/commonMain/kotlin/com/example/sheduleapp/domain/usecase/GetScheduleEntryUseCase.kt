package com.example.scheduleapp.domain.usecase

import com.example.scheduleapp.domain.model.ScheduleEntry
import com.example.scheduleapp.domain.repository.ScheduleEntryRepository

class GetScheduleEntryUseCase(
    private val entryRepository: ScheduleEntryRepository
) {
    suspend operator fun invoke(): List<ScheduleEntry> = entryRepository.getEntries()
}