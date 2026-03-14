package com.example.scheduleapp.data.repository

import com.example.scheduleapp.data.model.ScheduleRequest
import com.example.scheduleapp.data.model.ScheduleResponse
import com.example.scheduleapp.data.remote.ScheduleApi
import com.example.sheduleapp.data.ScheduleLocalDataSource
import kotlinx.serialization.json.Json

interface ScheduleRepository {
    suspend fun getSchedule(request: ScheduleRequest): ScheduleResponse
    suspend fun getScheduleAsJsonString(request: ScheduleRequest): String
}

class ScheduleRepositoryImpl(
    private val api: ScheduleApi,
    private val localDataSource: ScheduleLocalDataSource
) : ScheduleRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun getSchedule(request: ScheduleRequest): ScheduleResponse {
        val rawJson = getScheduleAsJsonString(request)
        return json.decodeFromString(rawJson)
    }

    override suspend fun getScheduleAsJsonString(request: ScheduleRequest): String {
        val cacheKey = buildCacheKey(request)

        return try {
            val rawJson = api.getScheduleAsJsonString(request)
            localDataSource.saveSchedule(cacheKey, rawJson)
            rawJson
        } catch (error: Exception) {
            val cachedJson = localDataSource.getScheduleJson(cacheKey)
            if (cachedJson != null) {
                println("Using cached schedule for key=$cacheKey because of network error: ${error.message}")
                cachedJson
            } else {
                throw error
            }
        }
    }

    private fun buildCacheKey(request: ScheduleRequest): String {
        val isRoomRequest = !request.roomId.isNullOrEmpty()
        val targetType = if (isRoomRequest) "room" else "person"
        val targetId = request.roomId?.firstOrNull()
            ?: request.attendeePersonId?.firstOrNull()
            ?: "unknown"

        return listOf(
            targetType,
            normalize(targetId),
            normalize(request.timeMin),
            normalize(request.timeMax)
        ).joinToString("__")
    }

    private fun normalize(value: String): String =
        value.replace(Regex("[^A-Za-z0-9_.-]"), "_")
}
