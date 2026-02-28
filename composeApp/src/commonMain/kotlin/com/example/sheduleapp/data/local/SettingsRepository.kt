package com.example.scheduleapp.data.local

import com.example.scheduleapp.domain.model.SettingsModel
import com.example.scheduleapp.domain.repository.SettingsRepository as DomainSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl : DomainSettingsRepository {
    private val _settings = MutableStateFlow(SettingsModel())
    override fun getSettings(): SettingsModel = _settings.value
    override suspend fun updateSettings(settings: SettingsModel) { _settings.value = settings }
    override fun subscribeSettings(): Flow<SettingsModel> = _settings.asStateFlow()
}
