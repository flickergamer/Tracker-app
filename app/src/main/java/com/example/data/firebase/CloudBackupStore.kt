package com.example.data.firebase

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.export.ImportedBackup
import com.example.data.export.LibrarySerializer
import com.example.data.model.MediaItem
import com.example.data.model.QuoteItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

private val Context.cloudBackupDataStore by preferencesDataStore(name = "zenix_cloud_backup_prefs")

class CloudBackupStore(private val context: Context) {
    private val appContext = context.applicationContext

    private fun lastSyncKey(uid: String) = longPreferencesKey("last_sync_millis_$uid")
    private fun firstSyncConfirmedKey(uid: String) = booleanPreferencesKey("first_sync_confirmed_$uid")

    fun observeLastSyncedAt(uid: String): Flow<Long> =
        appContext.cloudBackupDataStore.data
            .catch { emit(emptyPreferences()) }
            .map { prefs -> prefs[lastSyncKey(uid)] ?: 0L }

    suspend fun hasConfirmedFirstSync(uid: String): Boolean = runCatching {
        appContext.cloudBackupDataStore.data
            .catch { emit(emptyPreferences()) }
            .map { prefs -> prefs[firstSyncConfirmedKey(uid)] ?: false }
            .first()
    }.getOrDefault(false)

    suspend fun markSynced(uid: String, timestampMillis: Long = System.currentTimeMillis()) {
        runCatching {
            appContext.cloudBackupDataStore.edit { prefs ->
                prefs[lastSyncKey(uid)] = timestampMillis
                prefs[firstSyncConfirmedKey(uid)] = true
            }
        }
    }

    private fun cacheFile(uid: String): File {
        val safeUid = uid.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        return File(appContext.filesDir, "zenix_local_cache_$safeUid.json")
    }

    suspend fun saveLocalSnapshot(
        uid: String,
        profile: UserProfile?,
        items: List<MediaItem>,
        quotes: List<QuoteItem>
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val json = LibrarySerializer.exportToJson(profile, items, quotes)
            cacheFile(uid).writeText(json, Charsets.UTF_8)
        }
    }

    suspend fun loadLocalSnapshot(uid: String): ImportedBackup? = withContext(Dispatchers.IO) {
        runCatching {
            val file = cacheFile(uid)
            if (!file.exists()) return@runCatching null
            LibrarySerializer.importFromJson(file.readText(Charsets.UTF_8)).getOrNull()
        }.getOrNull()
    }
}
