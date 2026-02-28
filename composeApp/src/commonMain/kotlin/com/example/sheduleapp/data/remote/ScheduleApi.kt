package com.example.scheduleapp.data.remote

import com.example.scheduleapp.data.model.ScheduleRequest
import com.example.scheduleapp.data.model.ScheduleResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.statement.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class ScheduleApi(private val httpClient: HttpClient) {

    companion object {
        private const val BASE_URL = "https://schedule.rdcenter.ru"
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
            // Сервер иногда возвращает JSON как строку (с экранированием).
            json.decodeFromString<String>(bodyText)
        } else {
            bodyText
        }
    }

    private fun logProcessedJson(rawJson: String) {
        // В лог выводим только обработанный JSON без "REQUEST/HEADERS" и т.п.
        val compact = rawJson.trim()
        val pretty = runCatching {
            val element: JsonElement = prettyJson.parseToJsonElement(compact)
            prettyJson.encodeToString(element)
        }.getOrElse {
            // Если вдруг пришёл невалидный JSON — всё равно покажем как есть
            compact
        }
        println(pretty)
    }

    suspend fun getGroupSchedule(request: ScheduleRequest): ScheduleResponse {
        val response = httpClient.post("$BASE_URL/api/proxy/events/search") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            throw IllegalStateException("API Error ${response.status}: $errorBody")
        }

        val bodyText = response.bodyAsText()
        val rawJson = unwrapPossiblyQuotedJson(bodyText)
        logProcessedJson(rawJson)
        return json.decodeFromString(rawJson)
    }

    suspend fun getRoomSchedule(request: ScheduleRequest): ScheduleResponse {
        val response = httpClient.post("$BASE_URL/api/ictis") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val bodyText = response.bodyAsText()
        val rawJson = unwrapPossiblyQuotedJson(bodyText)
        logProcessedJson(rawJson)
        return json.decodeFromString(rawJson)
    }

    suspend fun getScheduleAsJsonString(request: ScheduleRequest): String {
        val response = httpClient.post("$BASE_URL/api/proxy/events/search") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return unwrapPossiblyQuotedJson(response.bodyAsText())
    }

}
