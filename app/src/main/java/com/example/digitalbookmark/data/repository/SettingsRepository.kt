package com.example.digitalbookmark.data.repository

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.digitalbookmark.ui.theme.ColorSlot
import com.example.digitalbookmark.ui.theme.ThemeMode
import com.example.digitalbookmark.ui.theme.ThemeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val modeKey = stringPreferencesKey("theme_mode")
    private fun colorKey(slot: ColorSlot) = intPreferencesKey("color_${slot.name}")

    val settings: Flow<ThemeSettings> = context.settingsStore.data.map { p ->
        ThemeSettings(
            mode = p[modeKey]
                ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            overrides = ColorSlot.entries.mapNotNull { slot ->
                p[colorKey(slot)]?.let { slot to Color(it) }
            }.toMap()
        )
    }

    suspend fun setMode(mode: ThemeMode) {
        context.settingsStore.edit { it[modeKey] = mode.name }
    }

    suspend fun setColor(slot: ColorSlot, color: Color) {
        context.settingsStore.edit { it[colorKey(slot)] = color.toArgb() }
    }

    suspend fun resetColor(slot: ColorSlot) {
        context.settingsStore.edit { it.remove(colorKey(slot)) }
    }

    suspend fun resetAll() {
        context.settingsStore.edit { prefs -> ColorSlot.entries.forEach { prefs.remove(colorKey(it)) } }
    }
}