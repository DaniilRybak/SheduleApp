package com.example.scheduleapp.data.remote

import com.example.scheduleapp.data.model.ScheduleRequest
import com.example.scheduleapp.data.model.ScheduleResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class ScheduleApi(private val httpClient: HttpClient) {

    companion object {
        private const val BASE_URL = "https://schedule.rdcenter.ru"
        private const val GROUP_SCHEDULE_PATH = "$BASE_URL/api/proxy/events/search"
        private const val ROOM_SCHEDULE_PATH = "$BASE_URL/api/ictis"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val prettyJson = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
        ignoreUnknownKeys = true
        isLenient = true
    }

    private fun unwrapPossiblyQuotedJson(bodyText: String): String {
        val trimmed = bodyText.trimStart()
        return if (trimmed.startsWith("\"")) {
            json.decodeFromString<String>(bodyText)
        } else {
            bodyText
        }
    }

    private fun logProcessedJson(rawJson: String) {
        val compact = rawJson.trim()
        val pretty = runCatching {
            val element: JsonElement = prettyJson.parseToJsonElement(compact)
            prettyJson.encodeToString(element)
        }.getOrElse {
            compact
        }
        println(pretty)
    }

    private suspend fun fetchScheduleJson(url: String, request: ScheduleRequest): String {
        val response = httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            throw IllegalStateException("API Error ${response.status}: $errorBody")
        }

        return unwrapPossiblyQuotedJson(response.bodyAsText())
    }

    suspend fun getGroupSchedule(request: ScheduleRequest): ScheduleResponse {
        val rawJson = fetchScheduleJson(GROUP_SCHEDULE_PATH, request)
        logProcessedJson(rawJson)
        return json.decodeFromString(rawJson)
    }

    suspend fun getRoomSchedule(request: ScheduleRequest): ScheduleResponse {
        val rawJson = fetchScheduleJson(ROOM_SCHEDULE_PATH, request)
        logProcessedJson(rawJson)
        return json.decodeFromString(rawJson)
    }

    suspend fun getScheduleAsJsonString(request: ScheduleRequest): String {
        val url = if (!request.roomId.isNullOrEmpty()) {
            ROOM_SCHEDULE_PATH
        } else {
            GROUP_SCHEDULE_PATH
        }
        return fetchScheduleJson(url, request)
    }
}
