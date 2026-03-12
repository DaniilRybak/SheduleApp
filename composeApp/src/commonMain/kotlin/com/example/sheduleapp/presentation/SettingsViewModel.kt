package com.example.sheduleapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduleapp.domain.model.AppTheme
import com.example.scheduleapp.domain.model.DisplayMode
import com.example.scheduleapp.domain.model.SettingsModel
import com.example.scheduleapp.domain.usecase.UpdateSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    private val _settings = MutableStateFlow(SettingsModel())
    val settings = _settings.asStateFlow()


    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            val updated = _settings.value.copy(theme = theme)
            _settings.value = updated
            updateSettingsUseCase(updated)
        }
    }

    fun updateShowWindows(showWindows: Boolean) {
        viewModelScope.launch {
            val updated = _settings.value.copy(showWindows = showWindows)
            _settings.value = updated
            updateSettingsUseCase(updated)
        }
    }

    fun updateDisplayMode(displayMode: DisplayMode) {
        viewModelScope.launch {
            val updated = _settings.value.copy(displayMode = displayMode)
            _settings.value = updated
            updateSettingsUseCase(updated)
        }
    }

    fun updateWeeklyMode(isWeeklyMode: Boolean) {
        viewModelScope.launch {
            val updated = _settings.value.copy(isWeeklyMode = isWeeklyMode)
            _settings.value = updated
            updateSettingsUseCase(updated)
        }
    }
}

