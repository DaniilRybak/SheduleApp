package com.example.scheduleapp.domain.model

data class TimeSlot(
    val id: String,
    val index: Int,
    val startHm: String,
    val endHm: String
) {
    companion object {
        fun defaultSlots(): List<TimeSlot> = listOf(
            TimeSlot(id = "1", index = 1, startHm = "08:00", endHm = "09:30"),
            TimeSlot(id = "2", index = 2, startHm = "09:50", endHm = "11:25"),
            TimeSlot(id = "3", index = 3, startHm = "11:55", endHm = "13:30"),
            TimeSlot(id = "4", index = 4, startHm = "13:45", endHm = "15:20"),
            TimeSlot(id = "5", index = 5, startHm = "15:50", endHm = "17:25"),
            TimeSlot(id = "6", index = 6, startHm = "17:35", endHm = "19:10"),
            TimeSlot(id = "7", index = 7, startHm = "19:15", endHm = "20:50")
        )
    }
}