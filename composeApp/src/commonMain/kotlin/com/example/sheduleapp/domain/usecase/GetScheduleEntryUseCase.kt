package com.example.scheduleapp.domain.usecase

import com.example.scheduleapp.domain.model.ScheduleEntry

class GetScheduleEntryUseCase(
    private val entryRepository: com.example.scheduleapp.domain.repository.ScheduleEntryRepository
) {
    suspend operator fun invoke(): List<ScheduleEntry> = entryRepository.getEntries()
}