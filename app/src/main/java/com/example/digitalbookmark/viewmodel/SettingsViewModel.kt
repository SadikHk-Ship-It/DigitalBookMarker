package com.example.digitalbookmark.ui.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalbookmark.data.repository.SettingsRepository
import com.example.digitalbookmark.ui.theme.ColorSlot
import com.example.digitalbookmark.ui.theme.ThemeMode
import com.example.digitalbookmark.ui.theme.ThemeSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SettingsRepository(app)

    val settings: StateFlow<ThemeSettings> = repo.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeSettings())

    fun setMode(mode: ThemeMode) = viewModelScope.launch { repo.setMode(mode) }
    fun setColor(slot: ColorSlot, color: Color) = viewModelScope.launch { repo.setColor(slot, color) }
    fun resetColor(slot: ColorSlot) = viewModelScope.launch { repo.resetColor(slot) }
    fun resetAll() = viewModelScope.launch { repo.resetAll() }
}