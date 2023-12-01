package com.example.markscalculator.datastores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Settings (private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")
        val THEME_KEY = stringPreferencesKey("app_theme")
        val COLOR_THEME_KEY = stringPreferencesKey("color_theme")
        val LANGUAGE_KEY = stringPreferencesKey("app_language")
        val DYNAMIC_KEY = booleanPreferencesKey("dynamic_theme")
        val EXTRA_DARK_KEY = booleanPreferencesKey("extra_dark")
    }

    val theme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: "system"
    }

    val colorTheme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[COLOR_THEME_KEY] ?: "blue"
    }

    val dynamicColors: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DYNAMIC_KEY] ?: true
    }

    val extraDark: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[EXTRA_DARK_KEY] ?: false
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "system"
    }

    suspend fun saveSettings(theme: String? = null, colorTheme: String? = null, dynamic: Boolean? = null, extraDark: Boolean? = null, language: String? = null) {
        context.dataStore.edit { preferences ->
            if (theme != null) { preferences[THEME_KEY] = theme }
            if (colorTheme != null) { preferences[COLOR_THEME_KEY] = colorTheme }
            if (dynamic != null) { preferences[DYNAMIC_KEY] = dynamic }
            if (extraDark != null) { preferences[EXTRA_DARK_KEY] = extraDark }
            if (language != null) { preferences[LANGUAGE_KEY] = language }
        }
    }
}