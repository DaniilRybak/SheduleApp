package com.example.scheduleapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleRequest(
    val size: Int,
    val timeMin: String,
    val timeMax: String,
    val attendeePersonId: List<String>? = null,
    val roomId: List<String>? = null
)

@Serializable
data class ScheduleResponse(
    @SerialName("_embedded") val embedded: Embedded? = null
)

@Serializable
data class Embedded(
    val events: List<EventDto> = emptyList(),
    @SerialName("event-locations") val eventLocations: List<EventLocationDto> = emptyList(),
    @SerialName("event-rooms") val eventRooms: List<EventRoomDto> = emptyList(),
    val rooms: List<RoomDto> = emptyList(),
    @SerialName("course-unit-realizations") val courseUnitRealizations: List<CourseUnitRealizationDto> = emptyList(),
    @SerialName("event-attendees") val eventAttendees: List<EventAttendeeDto> = emptyList(),
    val persons: List<PersonDto> = emptyList()
)

@Serializable
data class EventDto(
    val id: String,
    val name: String? = null,
    val start: String? = null,
    val end: String? = null,
    @SerialName("_links") val links: EventLinks? = null
)

@Serializable
data class EventLinks(
    @SerialName("course-unit-realization") val courseUnitRealization: HrefWrapper? = null
)

@Serializable
data class CourseUnitRealizationDto(
    val id: String,
    val name: String? = null,
    @SerialName("nameShort") val nameShort: String? = null
)

@Serializable
data class EventAttendeeDto(
    val roleId: String? = null,
    @SerialName("_links") val links: EventAttendeeLinks? = null
)

@Serializable
data class EventAttendeeLinks(
    val event: HrefWrapper? = null,
    val person: HrefWrapper? = null
)

@Serializable
data class PersonDto(
    val id: String,
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("middleName") val middleName: String? = null
)

@Serializable
data class EventLocationDto(
    @SerialName("eventId") val eventId: String,
    @SerialName("customLocation") val customLocation: String? = null,
    @SerialName("_links") val links: LinksWrapper? = null
)

@Serializable
data class LinksWrapper(
    @SerialName("event-rooms") @Serializable(with = EventRoomsSerializer::class) val eventRooms: List<HrefWrapper>? = null
)

@Serializable
data class HrefWrapper(
    val href: String? = null
)

@Serializable
data class EventRoomDto(
    @SerialName("_links") val links: EventRoomLinks
)

@Serializable
data class EventRoomLinks(
    val event: HrefWrapper? = null,
    val room: HrefWrapper? = null
)

@Serializable
data class RoomDto(
    val id: String,
    val name: String? = null,
    @SerialName("nameShort") val nameShort: String? = null,
    @SerialName("_links") val links: RoomLinks? = null
)

@Serializable
data class RoomLinks(
    val self: HrefWrapper? = null
)
