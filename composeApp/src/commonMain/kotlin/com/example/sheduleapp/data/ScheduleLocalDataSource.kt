package com.example.sheduleapp.data

interface ScheduleLocalDataSource {
    suspend fun saveSchedule(cacheKey: String, json: String)
    suspend fun getScheduleJson(cacheKey: String): String?
    suspend fun getLastUpdateTime(cacheKey: String): Long?
}