package com.example.scheduleapp.data.local

interface PrefsRepository {
    suspend fun getEntryId(): String
    suspend fun setEntryId(id: String)
}

class PrefsRepositoryImpl : PrefsRepository {
    private var entryId: String = ""
    override suspend fun getEntryId(): String = entryId
    override suspend fun setEntryId(id: String) {
        entryId = id
    }
}
