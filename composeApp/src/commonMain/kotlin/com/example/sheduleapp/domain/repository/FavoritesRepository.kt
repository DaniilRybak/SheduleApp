package com.example.scheduleapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    val favoriteGroupIds: Flow<Set<String>>

    suspend fun addFavoriteGroup(groupId: String)
    suspend fun removeFavoriteGroup(groupId: String)
}

