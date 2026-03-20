package com.example.scheduleapp.domain.usecase

import com.example.scheduleapp.data.model.EventAttendeeDto
import com.example.scheduleapp.data.model.EventDto
import com.example.scheduleapp.data.model.EventLocationDto
import com.example.scheduleapp.data.model.EventRoomDto
import com.example.scheduleapp.data.model.PersonDto
import com.example.scheduleapp.data.model.RoomDto
import com.example.scheduleapp.domain.model.DaySlotItem
import com.example.scheduleapp.domain.model.TimeSlot
import com.example.sheduleapp.domain.usecase.EventLocationMapperUseCase

class BuildDaySlotsUseCase {

    operator fun invoke(
        events: List<EventDto>,
        slots: List<TimeSlot> = TimeSlot.defaultSlots(),
        eventLocations: List<EventLocationDto> = emptyList(),
        eventRooms: List<EventRoomDto> = emptyList(),
        rooms: List<RoomDto> = emptyList(),
        eventAttendees: List<EventAttendeeDto> = emptyList(),
        persons: List<PersonDto> = emptyList()
        ): List<DaySlotItem> {
        if (slots.isEmpty()) {
            return events.map {
                DaySlotItem.UnplacedLesson(
                    it,
                    "Сетка пар не задана",
                    teacherName = getTeacherName(it.id, eventAttendees, persons)
                )
            }
        }

        val sortedSlots = slots.sortedBy { it.index }
        val bucket = sortedSlots.associate { it.id to mutableListOf<EventDto>() }
        val unplaced = mutableListOf<DaySlotItem.UnplacedLesson>()
        val locationMapper = EventLocationMapperUseCase()

        events.sortedBy { eventStartMinutes(it = it) ?: Int.MAX_VALUE }.forEach { event ->
            val start = eventStartMinutes(event)
            val end = eventEndMinutes(event)

            if (start == null || end == null || end <= start) {
                val eventWithLocation = locationMapper(event, eventLocations, eventRooms, rooms)
                unplaced += DaySlotItem.UnplacedLesson(
                    event,
                    "Некорректное время пары",
                    roomName = eventWithLocation.roomName,
                    customLocation = eventWithLocation.customLocation,
                    teacherName = getTeacherName(event.id, eventAttendees, persons)
                )
                return@forEach
            }

            var bestSlot: TimeSlot? = null
            var bestOverlap = 0

            sortedSlots.forEach { slot ->
                val slotStart = parseHmToMinutes(slot.startHm) ?: return@forEach
                val slotEnd = parseHmToMinutes(slot.endHm) ?: return@forEach
                val overlap = overlapMinutes(start, end, slotStart, slotEnd)

                if (overlap > bestOverlap) {
                    bestOverlap = overlap
                    bestSlot = slot
                }
            }

            if (bestSlot == null || bestOverlap <= 0) {
                val eventWithLocation = locationMapper(event, eventLocations, eventRooms, rooms)
                unplaced += DaySlotItem.UnplacedLesson(
                    event,
                    "Пара вне сетки слотов",
                    roomName = eventWithLocation.roomName,
                    customLocation = eventWithLocation.customLocation,
                    teacherName = getTeacherName(event.id, eventAttendees, persons)
                )
            } else {
                bucket[bestSlot.id]?.add(event)
            }
        }

        val result = mutableListOf<DaySlotItem>()

        sortedSlots.forEach { slot ->
            val lessons = bucket[slot.id].orEmpty().sortedBy { eventStartMinutes(it) ?: Int.MAX_VALUE }

            when {
                lessons.isEmpty() -> result += DaySlotItem.WindowSlot(slot)
                lessons.size == 1 -> {
                    val lesson = lessons.first()
                    val eventWithLocation = locationMapper(lesson, eventLocations, eventRooms, rooms)
                    result += DaySlotItem.LessonSlot(
                        slot,
                        lesson,
                        roomName = eventWithLocation.roomName,
                        customLocation = eventWithLocation.customLocation,
                        teacherName = getTeacherName(lesson.id, eventAttendees, persons)
                    )
                }
                else -> {
                    val locations = lessons.associate { lesson ->
                        val eventWithLocation = locationMapper(lesson, eventLocations, eventRooms, rooms)
                        lesson.id to (eventWithLocation.roomName ?: eventWithLocation.customLocation)
                    }
                    val teachers = lessons.associate { lesson ->
                        lesson.id to getTeacherName(lesson.id, eventAttendees, persons)
                    }
                    result += DaySlotItem.ConflictSlot(slot, lessons, locations, teachers)
                }
            }
        }

        result += unplaced
        return result
    }

    private fun eventStartMinutes(it: EventDto): Int? = extractHm(it.start)?.let(::parseHmToMinutes)

    private fun eventEndMinutes(it: EventDto): Int? = extractHm(it.end)?.let(::parseHmToMinutes)

    private fun extractHm(isoDateTime: String?): String? {
        if (isoDateTime == null || isoDateTime.length < 16) return null
        return isoDateTime.substring(11, 16)
    }

    private fun parseHmToMinutes(hm: String): Int? {
        val parts = hm.split(":")
        if (parts.size != 2) return null
        val hh = parts[0].toIntOrNull() ?: return null
        val mm = parts[1].toIntOrNull() ?: return null
        if (hh !in 0..23 || mm !in 0..59) return null
        return hh * 60 + mm
    }

    private fun overlapMinutes(aStart: Int, aEnd: Int, bStart: Int, bEnd: Int): Int {
        val left = maxOf(aStart, bStart)
        val right = minOf(aEnd, bEnd)
        return (right - left).coerceAtLeast(0)
    }

    private fun getTeacherName(eventId: String, eventAttendees: List<EventAttendeeDto>, persons: List<PersonDto>): String? {
        val attendee = eventAttendees.find {
            it.roleId == "TEACH" && it.links?.event?.href?.substringAfterLast("/") == eventId
        }
        val personId = attendee?.links?.person?.href?.substringAfterLast("/")
        val person = persons.find { it.id == personId }
        return person?.fullName
    }
}
