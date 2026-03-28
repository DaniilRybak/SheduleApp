package com.example.sheduleapp.data.model

import com.example.scheduleapp.data.model.EventDto

data class EventWithLocationDto(
    val event: EventDto,
    val roomName: String? = null,
    val customLocation: String? = null
)
