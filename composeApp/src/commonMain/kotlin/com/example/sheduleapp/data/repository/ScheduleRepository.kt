// composeApp/src/commonMain/kotlin/com/example/scheduleapp/data/repository/ScheduleRepository.kt
package com.example.scheduleapp.data.repository

import com.example.scheduleapp.data.model.ScheduleRequest
import com.example.scheduleapp.data.model.ScheduleResponse
import com.example.scheduleapp.data.remote.ScheduleApi

interface ScheduleRepository {
    suspend fun getSchedule(request: ScheduleRequest): ScheduleResponse
    suspend fun getScheduleAsJsonString(request: ScheduleRequest): String
}

class ScheduleRepositoryImpl(
    private val api: ScheduleApi
) : ScheduleRepository {

    override suspend fun getSchedule(request: ScheduleRequest): ScheduleResponse {
        return api.getGroupSchedule(request)
    }

    override suspend fun getScheduleAsJsonString(request: ScheduleRequest): String {
        return api.getScheduleAsJsonString(request)
    }
}
