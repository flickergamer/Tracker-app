package com.example.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AppThemeMode(
    val key: String,
    val displayName: String,
    val tagline: String
) {
    NEON(
        key = "neon",
        displayName = "Neon",
        tagline = "Liquid glass, radial glows & Space Grotesk"
    ),
    ANDROID_UI(
        key = "android_ui",
        displayName = "Android UI",
        tagline = "Material 3 dark tonal surfaces & Roboto"
    ),
    MINIMAL_PASTEL(
        key = "minimal_pastel",
        displayName = "Minimal Pastel",
        tagline = "Warm cream light cards, pastel tints & DM Sans"
    );

    companion object {
        fun fromKey(raw: String?): AppThemeMode =
            entries.firstOrNull { it.key.equals(raw, ignoreCase = true) } ?: NEON
    }
}

private val Context.themeDataStore by preferencesDataStore(name = "zenix_theme_prefs")

object ThemePreferences {
    private val KEY_THEME_MODE = stringPreferencesKey("app_theme_mode")

    fun observeTheme(context: Context): Flow<AppThemeMode> =
        context.themeDataStore.data.map { prefs ->
            AppThemeMode.fromKey(prefs[KEY_THEME_MODE])
        }

    suspend fun saveTheme(context: Context, mode: AppThemeMode) {
        context.themeDataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode.key
        }
    }
}
