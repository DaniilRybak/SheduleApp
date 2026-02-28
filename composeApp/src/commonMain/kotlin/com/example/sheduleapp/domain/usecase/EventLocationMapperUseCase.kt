package com.example.sheduleapp.domain.usecase

import com.example.scheduleapp.data.model.EventDto
import com.example.scheduleapp.data.model.EventLocationDto
import com.example.scheduleapp.data.model.EventRoomDto
import com.example.scheduleapp.data.model.RoomDto
import com.example.sheduleapp.data.model.EventWithLocationDto

class EventLocationMapperUseCase {
    operator fun invoke(
        eventDto: EventDto,
        eventLocations: List<EventLocationDto>,
        eventRooms: List<EventRoomDto>,
        rooms: List<RoomDto>
    ): EventWithLocationDto {

        val eventLocation = eventLocations.find { it.eventId == eventDto.id }
        val customLocation = eventLocation?.customLocation

        val roomName = eventRooms
            .find { eventRoom ->

                eventRoom.links.event?.href?.contains(eventDto.id) == true
            }
            ?.links?.room?.href?.let { roomHref ->
                val roomId = extractIdFromHref(roomHref)
                rooms.find { it.id == roomId }?.name
            }

        return EventWithLocationDto(
            event = eventDto,
            roomName = roomName,
            customLocation = customLocation
        )
    }

    private fun extractIdFromHref(href: String): String {
        return href.trim('/').split("/").lastOrNull() ?: ""
    }
}