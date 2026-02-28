package com.example.sheduleapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GroupDto(
    val name: String,
    @SerialName("person_id") val personId: String
)