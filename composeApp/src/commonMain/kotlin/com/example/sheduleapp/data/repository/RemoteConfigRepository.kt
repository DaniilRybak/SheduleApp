package com.example.sheduleapp.data.repository

import com.example.sheduleapp.data.model.GroupDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

class RemoteConfigRepository(
    private val httpClient: HttpClient
) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadGroups(): List<GroupDto> {
        val url = "https://raw.githubusercontent.com/bratusev07/app-config/main/groups.json"
        val responseText: String = httpClient.get(url).body()
        return json.decodeFromString(responseText)
    }
}