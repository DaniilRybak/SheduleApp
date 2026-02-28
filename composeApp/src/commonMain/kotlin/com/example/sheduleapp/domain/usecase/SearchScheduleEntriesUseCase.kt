package com.example.scheduleapp.domain.usecase

import com.example.scheduleapp.data.model.EventDto

class SearchScheduleEntriesUseCase {
    /**
     * Выполнить поиск
     * @param query строка для поиска (игнорирует регистр)
     * @param events список всех событий
     * @return отфильтрованный список событий
     */
    operator fun invoke(query: String, events: List<EventDto>): List<EventDto> {

        if (query.isBlank()) return events

        return events.filter { event ->
            event.name?.contains(query, ignoreCase = true) == true ||
            event.id.contains(query, ignoreCase = true) ||
            event.start?.contains(query, ignoreCase = true) == true ||
            event.end?.contains(query, ignoreCase = true) == true
        }
    }
}