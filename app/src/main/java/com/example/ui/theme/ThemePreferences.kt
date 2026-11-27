package com.example.ui.theme

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class AppThemeMode(
    val key: String,
    val displayName: String,
    val tagline: String
) {
    NEON_GLOSSY(
        key = "neon_glossy",
        displayName = "Neon Glossy",
        tagline = "Glass, shine, neon glow"
    ),
    MINIMAL_DARK(
        key = "minimal_dark",
        displayName = "Minimal Dark",
        tagline = "Clean, single accent"
    ),
    AMOLED_BLACK(
        key = "amoled_black",
        displayName = "AMOLED Black",
        tagline = "True black, saves battery"
    );

    companion object {
        fun fromKey(raw: String?): AppThemeMode = when (raw?.lowercase()) {
            "neon", "neon_glossy" -> NEON_GLOSSY
            "minimal_dark", "android_ui" -> MINIMAL_DARK
            "amoled_black" -> AMOLED_BLACK
            else -> NEON_GLOSSY
        }
    }
}

enum class ZenixAccentColor(
    val key: String,
    val label: String,
    val color: Color
) {
    PURPLE("purple", "Purple", NeonPurple),
    CYAN("cyan", "Cyan", NeonCyan),
    PINK("pink", "Pink", NeonPink),
    GREEN("green", "Green", NeonGreen),
    AMBER("amber", "Amber", NeonAmber);

    companion object {
        fun fromKey(raw: String?): ZenixAccentColor =
            entries.firstOrNull { it.key.equals(raw, ignoreCase = true) } ?: PURPLE
    }
}

private val Context.themeDataStore by preferencesDataStore(name = "zenix_theme_prefs")

object ThemePreferences {
    private val KEY_THEME_MODE = stringPreferencesKey("app_theme_mode")
    private val KEY_ACCENT_COLOR = stringPreferencesKey("app_accent_color")
    private val KEY_NOTIFY_UPDATES = booleanPreferencesKey("notify_updates")
    private val KEY_LAST_SEEN_VERSION = stringPreferencesKey("last_seen_update_version")
    private val KEY_LAST_LAUNCHED_VERSION = stringPreferencesKey("last_launched_app_version")

    private fun safePrefsFlow(context: Context) = context.themeDataStore.data
        .catch { err ->
            if (err is IOException || err is SecurityException || err is IllegalStateException) {
                emit(emptyPreferences())
            } else {
                emit(emptyPreferences())
            }
        }

    fun observeTheme(context: Context): Flow<AppThemeMode> =
        safePrefsFlow(context).map { prefs ->
            AppThemeMode.fromKey(prefs[KEY_THEME_MODE])
        }

    fun observeAccentColor(context: Context): Flow<ZenixAccentColor> =
        safePrefsFlow(context).map { prefs ->
            ZenixAccentColor.fromKey(prefs[KEY_ACCENT_COLOR])
        }

    fun observeNotifyUpdates(context: Context): Flow<Boolean> =
        safePrefsFlow(context).map { prefs ->
            prefs[KEY_NOTIFY_UPDATES] ?: true
        }

    fun observeLastSeenVersion(context: Context): Flow<String> =
        safePrefsFlow(context).map { prefs ->
            prefs[KEY_LAST_SEEN_VERSION] ?: ""
        }

    fun observeLastLaunchedVersion(context: Context): Flow<String> =
        safePrefsFlow(context).map { prefs ->
            prefs[KEY_LAST_LAUNCHED_VERSION] ?: ""
        }

    suspend fun saveTheme(context: Context, mode: AppThemeMode) {
        runCatching {
            context.themeDataStore.edit { prefs ->
                prefs[KEY_THEME_MODE] = mode.key
            }
        }
    }

    suspend fun saveAppearance(context: Context, mode: AppThemeMode, accent: ZenixAccentColor) {
        runCatching {
            context.themeDataStore.edit { prefs ->
                prefs[KEY_THEME_MODE] = mode.key
                prefs[KEY_ACCENT_COLOR] = accent.key
            }
        }
    }

    suspend fun saveNotifyUpdates(context: Context, enabled: Boolean) {
        runCatching {
            context.themeDataStore.edit { prefs ->
                prefs[KEY_NOTIFY_UPDATES] = enabled
            }
        }
    }

    suspend fun saveLastSeenVersion(context: Context, version: String) {
        runCatching {
            context.themeDataStore.edit { prefs ->
                prefs[KEY_LAST_SEEN_VERSION] = version.trim()
            }
        }
    }

    suspend fun saveLastLaunchedVersion(context: Context, version: String) {
        runCatching {
            context.themeDataStore.edit { prefs ->
                prefs[KEY_LAST_LAUNCHED_VERSION] = version.trim()
            }
        }
    }
}
