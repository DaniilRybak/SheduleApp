package com.example.scheduleapp.domain.usecase

import com.example.scheduleapp.domain.model.SettingsModel
import com.example.scheduleapp.domain.repository.SettingsRepository

class GetSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): SettingsModel = settingsRepository.getSettings()
}
