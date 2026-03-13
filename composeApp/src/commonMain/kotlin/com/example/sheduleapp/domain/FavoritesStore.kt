package com.example.sheduleapp.domain

import androidx.compose.runtime.mutableStateListOf

private val favoriteIds = mutableStateListOf<String>()

fun isFavorite(groupId: String): Boolean = favoriteIds.contains(groupId)

fun addToFavorites(groupId: String) {
    if (!favoriteIds.contains(groupId)) {
        favoriteIds.add(groupId)
    }
}

fun removeFromFavorites(groupId: String) {
    favoriteIds.remove(groupId)
}


fun all(): List<String> = favoriteIds.toList()