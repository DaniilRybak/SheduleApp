package com.example.scheduleapp.domain.model

import com.example.scheduleapp.data.model.EventDto

sealed interface DaySlotItem {
    data class LessonSlot(
        val slot: TimeSlot,
        val lesson: EventDto,
        val roomName: String? = null,
        val customLocation: String? = null
    ) : DaySlotItem

    data class WindowSlot(
        val slot: TimeSlot
    ) : DaySlotItem

    data class ConflictSlot(
        val slot: TimeSlot,
        val lessons: List<EventDto>,
        val locations: Map<String, String?> = emptyMap()
    ) : DaySlotItem

    data class UnplacedLesson(
        val lesson: EventDto,
        val reason: String,
        val roomName: String? = null,
        val customLocation: String? = null
    ) : DaySlotItem
}

