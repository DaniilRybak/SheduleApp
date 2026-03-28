package com.example.sheduleapp.presentation.strings

/** Локализованные строки экрана расписания. */
object ScheduleStrings {
    const val loadingPlaceholder = "Загрузка..."
    const val addFavorite = "Добавить"
    const val previousWeek = "◀"
    const val nextWeek = "▶"

    const val gridTime = "Время"
    const val outOfGrid = "Вне сетки"
    const val emptyCell = "—"
    const val window = "Окно"

    const val unknownTitle = "Без названия"
    const val unknownTime = "Время не указано"

    const val lessonOne = "пара"
    const val lessonFew = "пары"
    const val lessonMany = "пар"

    const val conflictPrefix = "Конфликт"
    const val conflictLessons = "Конфликт пар"

    const val loadingSchedule = "Загрузка расписания..."
    const val error = "Ошибка"
    const val retry = "Повторить"

    const val noResultsTitle = "Ничего не найдено"
    const val emptyEventsTitle = "Нет событий"
    const val emptyWeekMessage = "На этой неделе расписание отсутствует"

    /** Возвращает подпись количества занятий по правилам русского языка. */
    fun lessonsCountLabel(count: Int): String {
        val form = when {
            count % 10 == 1 && count % 100 != 11 -> lessonOne
            count % 10 in 2..4 && count % 100 !in 12..14 -> lessonFew
            else -> lessonMany
        }
        return "$count $form"
    }

    /** Формирует подпись конфликта для ячейки сетки. */
    fun conflictShort(count: Int): String = "$conflictPrefix: $count"

    /** Формирует заголовок карточки конфликта по тайм-слоту. */
    fun conflictTitle(startHm: String, endHm: String): String = "$conflictLessons ($startHm - $endHm)"

    /** Формирует строку времени в левой колонке сетки. */
    fun slotLabel(slotId: Int, startHm: String, endHm: String): String =
        "$slotId Пара\n$startHm\n$endHm"

    /** Формирует заголовок блока вне сетки для занятия. */
    fun outOfGridTitle(title: String): String = "$outOfGrid: $title"

    /** Формирует строку преподавателя. */
    fun teacher(name: String): String = "👨‍🏫 $name"

    /** Формирует строку локации. */
    fun location(value: String): String = "📍 $value"

    /** Формирует сообщение об отсутствии результатов поиска. */
    fun noResultsMessage(query: String): String = "По запросу \"$query\" ничего не найдено"
}

