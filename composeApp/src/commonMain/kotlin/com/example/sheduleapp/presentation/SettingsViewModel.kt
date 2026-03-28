package com.example.sheduleapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduleapp.domain.model.AppTheme
import com.example.scheduleapp.domain.model.DisplayMode
import com.example.scheduleapp.domain.model.SettingsModel
import com.example.scheduleapp.domain.repository.FavoritesRepository
import com.example.scheduleapp.domain.usecase.UpdateSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(SettingsModel())
    val settings = _settings.asStateFlow()

    init {
        observeTheme()
        observeDisplayMode()
    }

    private fun observeTheme() {
        viewModelScope.launch {
            favoritesRepository.observeAppTheme().collect { theme ->
                _settings.value = _settings.value.copy(theme = theme)
            }
        }
    }

    private fun observeDisplayMode() {
        viewModelScope.launch {
            favoritesRepository.observeDisplayMode().collect { mode ->
                _settings.value = _settings.value.copy(displayMode = mode)
            }
        }
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            val updated = _settings.value.copy(theme = theme)
            _settings.value = updated
            favoritesRepository.setAppTheme(theme)
            updateSettingsUseCase(updated)
        }
    }

    fun updateDisplayMode(displayMode: DisplayMode) {
        viewModelScope.launch {

            _settings.value = _settings.value.copy(displayMode = displayMode)

            favoritesRepository.setDisplayMode(displayMode)

            updateSettingsUseCase(_settings.value)
        }
    }
}
