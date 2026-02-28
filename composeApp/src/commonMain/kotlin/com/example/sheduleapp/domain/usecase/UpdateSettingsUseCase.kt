package com.example.scheduleapp.domain.usecase

import com.example.scheduleapp.domain.model.SettingsModel
import com.example.scheduleapp.domain.repository.SettingsRepository

class UpdateSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(settings: SettingsModel) {
        settingsRepository.updateSettings(settings)
    }
}