package com.example.data.firebase

import android.content.Context
import com.example.R
import com.example.data.export.ImportedBackup
import com.example.data.model.AccentColorPreset
import com.example.data.model.MediaItem
import com.example.data.model.QuoteItem
import com.example.data.model.SeasonProgress
import com.example.data.model.UserProfile
import com.example.data.model.WatchStatus
import com.example.data.model.normalizeGenres
import com.example.data.model.normalizePlatform
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CloudSyncResult(
    val uploadedCount: Int,
    val downloadedCount: Int,
    val totalCount: Int
)

class AfterglowRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(expectedUserId: String): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
        check(uid == expectedUserId) { "Signed-in user changed; refusing cross-user write." }
        return uid
    }

    private fun todayIso(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun observeProfile(userId: String): Flow<UserProfile?> = flow {
        val uid = requireUserId(userId)
        val docRef = db.collection("users").document(uid)
        docRef.snapshots()
            .map { snapshot ->
                if (!snapshot.exists()) null
                else snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                    handleFirestoreError(exceptionToLog, OperationType.GET, docRef.path)
                }
                throw error
            }
            .collect { emit(it) }
    }

    fun observeMediaItems(userId: String): Flow<List<MediaItem>> = flow {
        val uid = requireUserId(userId)
        val colRef = db.collection("users").document(uid).collection("media_items")
        colRef.snapshots()
            .map { snapshot ->
                snapshot.toObjects(MediaItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, colRef.path)
                }
                throw error
            }
            .collect { emit(it) }
    }

    fun observeQuotes(userId: String): Flow<List<QuoteItem>> = flow {
        val uid = requireUserId(userId)
        val colRef = db.collection("users").document(uid).collection("quotes")
        colRef.snapshots()
            .map { snapshot ->
                snapshot.toObjects(QuoteItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, colRef.path)
                }
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun ensureUserProfile(userId: String, defaultDisplayName: String?): Result<Unit> {
        var path = "users/$userId"
        return try {
            val uid = requireUserId(userId)
            val docRef = db.collection("users").document(uid)
            path = docRef.path
            val snap = docRef.get().await()
            if (!snap.exists()) {
                val safeName = defaultDisplayName?.trim()?.takeIf { it.isNotBlank() }?.take(60) ?: "Cinephile"
                val profile = UserProfile(
                    userId = uid,
                    displayName = safeName,
                    accentColorKey = AccentColorPreset.AFTERGLOW_PURPLE.name
                )
                docRef.set(profile.toCreateMap(uid)).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateProfile(userId: String, profile: UserProfile): Result<Unit> {
        var path = "users/$userId"
        return try {
            val uid = requireUserId(userId)
            require(profile.displayName.trim().isNotBlank()) { "Display name cannot be empty." }
            val docRef = db.collection("users").document(uid)
            path = docRef.path
            val snap = docRef.get().await()
            if (snap.exists()) {
                docRef.update(profile.toUpdateMap()).await()
            } else {
                docRef.set(profile.toCreateMap(uid)).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun createMediaItem(userId: String, item: MediaItem): Result<String> {
        var path = "users/$userId/media_items"
        return try {
            val uid = requireUserId(userId)
            require(item.title.trim().isNotBlank()) { "Title cannot be blank." }
            val docRef = db.collection("users").document(uid).collection("media_items").document()
            path = docRef.path
            val normalized = item.copy(
                userId = uid,
                genre = normalizeGenres(item.genre),
                streamingPlatform = normalizePlatform(item.streamingPlatform),
                dateAdded = item.dateAdded.ifBlank { todayIso() }
            )
            docRef.set(normalized.toCreateMap(uid)).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateMediaItem(userId: String, item: MediaItem): Result<Unit> {
        var path = "users/$userId/media_items/${item.id}"
        return try {
            val uid = requireUserId(userId)
            require(item.id.isNotBlank()) { "Item ID is required for update." }
            require(item.title.trim().isNotBlank()) { "Title cannot be blank." }
            val docRef = db.collection("users").document(uid).collection("media_items").document(item.id)
            path = docRef.path
            val normalized = item.copy(
                genre = normalizeGenres(item.genre),
                streamingPlatform = normalizePlatform(item.streamingPlatform)
            )
            docRef.update(normalized.toUpdateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun migrateLegacyNormalization(userId: String, items: List<MediaItem>): Result<Int> {
        val path = "users/$userId/media_items"
        return try {
            val uid = requireUserId(userId)
            val dirty = items.filter { item ->
                item.id.isNotBlank() && (
                    item.genre != normalizeGenres(item.genre) ||
                        item.streamingPlatform != normalizePlatform(item.streamingPlatform)
                    )
            }
            if (dirty.isEmpty()) return Result.success(0)
            val col = db.collection("users").document(uid).collection("media_items")
            dirty.chunked(450).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { item ->
                    batch.update(
                        col.document(item.id),
                        mapOf(
                            "genre" to normalizeGenres(item.genre).take(160),
                            "streamingPlatform" to normalizePlatform(item.streamingPlatform).take(80),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }
                batch.commit().await()
            }
            Result.success(dirty.size)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun incrementEpisode(userId: String, item: MediaItem, delta: Int): Result<Unit> {
        val activeSeason = item.effectiveSeasons
            .firstOrNull { it.totalEpisodes == 0L || it.episodesWatched < it.totalEpisodes }
            ?: item.effectiveSeasons.lastOrNull()
        return if (item.seasons.isNotEmpty() && activeSeason != null) {
            val targetSeason = if (delta < 0 && activeSeason.episodesWatched <= 0L) {
                item.effectiveSeasons.lastOrNull { it.episodesWatched > 0L }?.seasonNumber ?: activeSeason.seasonNumber
            } else {
                activeSeason.seasonNumber
            }
            stepSeasonEpisode(userId, item, targetSeason, delta)
        } else {
            stepLegacyEpisode(userId, item, delta)
        }
    }

    private suspend fun stepLegacyEpisode(userId: String, item: MediaItem, delta: Int): Result<Unit> {
        var path = "users/$userId/media_items/${item.id}"
        return try {
            val uid = requireUserId(userId)
            require(item.id.isNotBlank()) { "Item ID is required." }
            val maxEps = if (item.totalEpisodes > 0L) item.totalEpisodes else 10000L
            val nextWatched = (item.episodesWatched + delta).coerceIn(0L, maxEps)
            val reachedEnd = item.totalEpisodes > 0L && nextWatched >= item.totalEpisodes
            val nextStatus = when {
                reachedEnd -> WatchStatus.COMPLETED.name
                delta > 0 && item.statusEnum == WatchStatus.PLAN_TO_WATCH -> WatchStatus.WATCHING.name
                else -> item.watchStatus
            }
            val nextCompletionDate = if (reachedEnd && item.completionDate.isBlank()) todayIso() else item.completionDate
            val nextStartDate = if (delta > 0 && item.startDate.isBlank()) todayIso() else item.startDate
            val docRef = db.collection("users").document(uid).collection("media_items").document(item.id)
            path = docRef.path
            docRef.update(
                mapOf(
                    "episodesWatched" to nextWatched,
                    "currentEpisode" to nextWatched,
                    "watchStatus" to nextStatus,
                    "startDate" to nextStartDate,
                    "completionDate" to nextCompletionDate,
                    "watchlistItem" to (if (nextStatus == WatchStatus.PLAN_TO_WATCH.name) item.watchlistItem else false),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun stepSeasonEpisode(
        userId: String,
        item: MediaItem,
        seasonNumber: Long,
        delta: Int
    ): Result<Unit> {
        var path = "users/$userId/media_items/${item.id}"
        return try {
            val uid = requireUserId(userId)
            require(item.id.isNotBlank()) { "Item ID is required." }
            val currentSeasons = item.effectiveSeasons
            val updatedSeasons = currentSeasons.map { season ->
                if (season.seasonNumber == seasonNumber) {
                    val cap = if (season.totalEpisodes > 0L) season.totalEpisodes else 10000L
                    val nextWatched = (season.episodesWatched + delta).coerceIn(0L, cap)
                    season.copy(episodesWatched = nextWatched)
                } else {
                    season
                }
            }
            val synced = item.withSyncedSeasons(updatedSeasons)
            val reachedEnd = synced.totalEpisodes > 0L && synced.episodesWatched >= synced.totalEpisodes
            val nextStatus = when {
                reachedEnd -> WatchStatus.COMPLETED.name
                delta > 0 && item.statusEnum == WatchStatus.PLAN_TO_WATCH -> WatchStatus.WATCHING.name
                delta < 0 && item.statusEnum == WatchStatus.COMPLETED -> WatchStatus.WATCHING.name
                else -> item.watchStatus
            }
            val nextCompletionDate = if (reachedEnd && item.completionDate.isBlank()) todayIso() else item.completionDate
            val nextStartDate = if (delta > 0 && item.startDate.isBlank()) todayIso() else item.startDate
            val docRef = db.collection("users").document(uid).collection("media_items").document(item.id)
            path = docRef.path
            docRef.update(
                mapOf(
                    "seasons" to synced.seasons.take(100).map { it.toMap() },
                    "totalSeasons" to synced.totalSeasons.coerceIn(0L, 200L),
                    "currentSeason" to synced.currentSeason.coerceIn(0L, 200L),
                    "totalEpisodes" to synced.totalEpisodes.coerceIn(0L, 10000L),
                    "episodesWatched" to synced.episodesWatched.coerceIn(0L, 10000L),
                    "currentEpisode" to synced.currentEpisode.coerceIn(0L, 10000L),
                    "watchStatus" to nextStatus,
                    "startDate" to nextStartDate,
                    "completionDate" to nextCompletionDate,
                    "watchlistItem" to (if (nextStatus == WatchStatus.PLAN_TO_WATCH.name) item.watchlistItem else false),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun addSeasonToSeries(
        userId: String,
        item: MediaItem,
        episodeCount: Long
    ): Result<Unit> {
        var path = "users/$userId/media_items/${item.id}"
        return try {
            val uid = requireUserId(userId)
            require(item.id.isNotBlank()) { "Item ID is required." }
            val baseSeasons = item.effectiveSeasons
            val nextNumber = ((baseSeasons.maxOfOrNull { it.seasonNumber } ?: 0L) + 1L).coerceIn(1L, 200L)
            val updated = baseSeasons + SeasonProgress(
                seasonNumber = nextNumber,
                totalEpisodes = episodeCount.coerceIn(0L, 10000L),
                episodesWatched = 0L
            )
            val synced = item.withSyncedSeasons(updated)
            val nextStatus = if (item.statusEnum == WatchStatus.COMPLETED && episodeCount > 0L) {
                WatchStatus.WATCHING.name
            } else {
                item.watchStatus
            }
            val docRef = db.collection("users").document(uid).collection("media_items").document(item.id)
            path = docRef.path
            docRef.update(
                mapOf(
                    "seasons" to synced.seasons.take(100).map { it.toMap() },
                    "totalSeasons" to synced.totalSeasons.coerceIn(0L, 200L),
                    "currentSeason" to synced.currentSeason.coerceIn(0L, 200L),
                    "totalEpisodes" to synced.totalEpisodes.coerceIn(0L, 10000L),
                    "episodesWatched" to synced.episodesWatched.coerceIn(0L, 10000L),
                    "currentEpisode" to synced.currentEpisode.coerceIn(0L, 10000L),
                    "watchStatus" to nextStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun bulkUpdateStatus(
        userId: String,
        items: List<MediaItem>,
        targetStatus: WatchStatus
    ): Result<Int> {
        val path = "users/$userId/media_items"
        return try {
            val uid = requireUserId(userId)
            val valid = items.filter { it.id.isNotBlank() }
            if (valid.isEmpty()) return Result.success(0)
            val today = todayIso()
            val col = db.collection("users").document(uid).collection("media_items")
            valid.chunked(450).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { item ->
                    val updates = mutableMapOf<String, Any>(
                        "watchStatus" to targetStatus.name,
                        "watchlistItem" to (targetStatus == WatchStatus.PLAN_TO_WATCH),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    if (targetStatus == WatchStatus.WATCHING || targetStatus == WatchStatus.COMPLETED) {
                        updates["startDate"] = item.startDate.ifBlank { today }
                    }
                    if (targetStatus == WatchStatus.COMPLETED) {
                        updates["completionDate"] = item.completionDate.ifBlank { today }
                        if (item.categoryEnum.isEpisodic && item.totalEpisodes > 0L) {
                            updates["episodesWatched"] = item.totalEpisodes
                            updates["currentEpisode"] = item.totalEpisodes
                            if (item.seasons.isNotEmpty()) {
                                updates["seasons"] = item.seasons.map { s ->
                                    s.copy(episodesWatched = s.totalEpisodes).toMap()
                                }
                            }
                        }
                    }
                    batch.update(col.document(item.id), updates)
                }
                batch.commit().await()
            }
            Result.success(valid.size)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun bulkAddToWatchlist(
        userId: String,
        items: List<MediaItem>
    ): Result<Int> {
        val path = "users/$userId/media_items"
        return try {
            val uid = requireUserId(userId)
            val valid = items.filter { it.id.isNotBlank() }
            if (valid.isEmpty()) return Result.success(0)
            val col = db.collection("users").document(uid).collection("media_items")
            valid.chunked(450).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { item ->
                    batch.update(
                        col.document(item.id),
                        mapOf(
                            "watchlistItem" to true,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }
                batch.commit().await()
            }
            Result.success(valid.size)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun bulkDeleteMediaItems(
        userId: String,
        itemIds: List<String>
    ): Result<Int> {
        val path = "users/$userId/media_items"
        return try {
            val uid = requireUserId(userId)
            val valid = itemIds.filter { it.isNotBlank() }
            if (valid.isEmpty()) return Result.success(0)
            val col = db.collection("users").document(uid).collection("media_items")
            valid.chunked(450).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { id -> batch.delete(col.document(id)) }
                batch.commit().await()
            }
            Result.success(valid.size)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun fetchServerSnapshot(userId: String): Result<ImportedBackup> {
        val path = "users/$userId"
        return try {
            val uid = requireUserId(userId)
            val userDoc = db.collection("users").document(uid).get(Source.SERVER).await()
            val profile = if (userDoc.exists()) {
                userDoc.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            } else null
            val mediaDocs = db.collection("users").document(uid).collection("media_items")
                .get(Source.SERVER).await()
                .toObjects(MediaItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            val quoteDocs = db.collection("users").document(uid).collection("quotes")
                .get(Source.SERVER).await()
                .toObjects(QuoteItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            Result.success(ImportedBackup(profile, mediaDocs, quoteDocs))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    suspend fun syncLocalSnapshotLww(
        userId: String,
        localSnapshot: ImportedBackup?
    ): Result<CloudSyncResult> {
        val path = "users/$userId"
        return try {
            val uid = requireUserId(userId)
            val serverSnap = fetchServerSnapshot(uid).getOrThrow()
            val serverById = serverSnap.mediaItems.associateBy { it.id }
            val serverByKey = serverSnap.mediaItems.associateBy { "${it.title.trim().lowercase()}|${it.mediaCategory}" }

            val toCreate = mutableListOf<MediaItem>()
            val toUpdate = mutableListOf<MediaItem>()
            var downloadedCount = 0

            val localItems = localSnapshot?.mediaItems ?: emptyList()
            localItems.forEach { local ->
                val serverMatch = if (local.id.isNotBlank()) {
                    serverById[local.id]
                } else {
                    serverByKey["${local.title.trim().lowercase()}|${local.mediaCategory}"]
                }
                if (serverMatch == null) {
                    if (local.title.isNotBlank()) {
                        toCreate.add(local)
                    }
                } else {
                    val serverUpdated = serverMatch.updatedAt?.toDate()?.time ?: 0L
                    val localUpdated = local.updatedAt?.toDate()?.time ?: 0L
                    if (localUpdated > serverUpdated) {
                        toUpdate.add(local.copy(id = serverMatch.id))
                    } else if (serverUpdated > localUpdated) {
                        downloadedCount++
                    }
                }
            }

            serverSnap.mediaItems.forEach { srv ->
                val matchedLocal = localItems.any { loc ->
                    (loc.id.isNotBlank() && loc.id == srv.id) ||
                        (loc.title.trim().equals(srv.title.trim(), true) && loc.mediaCategory == srv.mediaCategory)
                }
                if (!matchedLocal) {
                    downloadedCount++
                }
            }

            val mediaCol = db.collection("users").document(uid).collection("media_items")
            toCreate.chunked(450).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { item ->
                    val ref = if (item.id.isNotBlank()) mediaCol.document(item.id) else mediaCol.document()
                    batch.set(
                        ref,
                        item.copy(
                            userId = uid,
                            genre = normalizeGenres(item.genre),
                            streamingPlatform = normalizePlatform(item.streamingPlatform),
                            dateAdded = item.dateAdded.ifBlank { todayIso() }
                        ).toCreateMap(uid)
                    )
                }
                batch.commit().await()
            }
            toUpdate.chunked(450).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { item ->
                    batch.update(
                        mediaCol.document(item.id),
                        item.copy(
                            genre = normalizeGenres(item.genre),
                            streamingPlatform = normalizePlatform(item.streamingPlatform)
                        ).toUpdateMap()
                    )
                }
                batch.commit().await()
            }

            val uploadedCount = toCreate.size + toUpdate.size
            val refreshed = if (uploadedCount > 0) fetchServerSnapshot(uid).getOrNull()?.mediaItems?.size ?: serverSnap.mediaItems.size else serverSnap.mediaItems.size
            Result.success(CloudSyncResult(uploadedCount = uploadedCount, downloadedCount = downloadedCount, totalCount = refreshed))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun moveWatchlistToLibrary(
        userId: String,
        item: MediaItem,
        targetStatus: WatchStatus
    ): Result<Unit> {
        var path = "users/$userId/media_items/${item.id}"
        return try {
            val uid = requireUserId(userId)
            require(item.id.isNotBlank()) { "Item ID is required." }
            val today = todayIso()
            val docRef = db.collection("users").document(uid).collection("media_items").document(item.id)
            path = docRef.path
            val updates = mutableMapOf<String, Any>(
                "watchlistItem" to false,
                "watchStatus" to targetStatus.name,
                "startDate" to item.startDate.ifBlank { today },
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (targetStatus == WatchStatus.COMPLETED) {
                updates["completionDate"] = item.completionDate.ifBlank { today }
                if (item.categoryEnum.isEpisodic && item.totalEpisodes > 0L) {
                    updates["episodesWatched"] = item.totalEpisodes
                    updates["currentEpisode"] = item.totalEpisodes
                    if (item.seasons.isNotEmpty()) {
                        updates["seasons"] = item.seasons.map { it.copy(episodesWatched = it.totalEpisodes).toMap() }
                    }
                }
            }
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateQuickFlags(
        userId: String,
        itemId: String,
        favourite: Boolean? = null,
        allTimeFavourite: Boolean? = null,
        hiddenGem: Boolean? = null,
        rewatchFlag: Boolean? = null,
        rewatchCount: Long? = null,
        manualRank: Long? = null,
        watchStatus: WatchStatus? = null
    ): Result<Unit> {
        var path = "users/$userId/media_items/$itemId"
        return try {
            val uid = requireUserId(userId)
            require(itemId.isNotBlank()) { "Item ID is required." }
            val docRef = db.collection("users").document(uid).collection("media_items").document(itemId)
            path = docRef.path
            val updates = mutableMapOf<String, Any>("updatedAt" to FieldValue.serverTimestamp())
            favourite?.let { updates["favourite"] = it }
            allTimeFavourite?.let { updates["allTimeFavourite"] = it }
            hiddenGem?.let { updates["hiddenGem"] = it }
            rewatchFlag?.let { updates["rewatchFlag"] = it }
            rewatchCount?.let { updates["rewatchCount"] = it.coerceIn(0L, 500L) }
            manualRank?.let { updates["manualRank"] = it.coerceIn(0L, 500L) }
            watchStatus?.let {
                updates["watchStatus"] = it.name
                if (it == WatchStatus.WATCHING) {
                    updates["watchlistItem"] = false
                }
            }
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteMediaItem(userId: String, itemId: String): Result<Unit> {
        var path = "users/$userId/media_items/$itemId"
        return try {
            val uid = requireUserId(userId)
            require(itemId.isNotBlank()) { "Item ID is required." }
            val docRef = db.collection("users").document(uid).collection("media_items").document(itemId)
            path = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun addQuote(userId: String, quote: QuoteItem): Result<Unit> {
        var path = "users/$userId/quotes"
        return try {
            val uid = requireUserId(userId)
            require(quote.quoteText.trim().isNotBlank()) { "Quote text cannot be blank." }
            require(quote.sourceTitle.trim().isNotBlank()) { "Source title cannot be blank." }
            val docRef = db.collection("users").document(uid).collection("quotes").document()
            path = docRef.path
            docRef.set(quote.toCreateMap(uid)).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateQuote(userId: String, quote: QuoteItem): Result<Unit> {
        var path = "users/$userId/quotes/${quote.id}"
        return try {
            val uid = requireUserId(userId)
            require(quote.id.isNotBlank()) { "Quote ID is required." }
            require(quote.quoteText.trim().isNotBlank()) { "Quote text cannot be blank." }
            require(quote.sourceTitle.trim().isNotBlank()) { "Source title cannot be blank." }
            val docRef = db.collection("users").document(uid).collection("quotes").document(quote.id)
            path = docRef.path
            docRef.update(quote.toUpdateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteQuote(userId: String, quoteId: String): Result<Unit> {
        var path = "users/$userId/quotes/$quoteId"
        return try {
            val uid = requireUserId(userId)
            require(quoteId.isNotBlank()) { "Quote ID is required." }
            val docRef = db.collection("users").document(uid).collection("quotes").document(quoteId)
            path = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun importData(
        userId: String,
        items: List<MediaItem>,
        quotes: List<QuoteItem>,
        profile: UserProfile?
    ): Result<Int> {
        val path = "users/$userId"
        return try {
            val uid = requireUserId(userId)
            var count = 0
            if (profile != null) {
                val userDoc = db.collection("users").document(uid)
                val snap = userDoc.get().await()
                if (snap.exists()) {
                    userDoc.update(profile.toUpdateMap()).await()
                } else {
                    userDoc.set(profile.toCreateMap(uid)).await()
                }
            }
            val mediaCol = db.collection("users").document(uid).collection("media_items")
            items.chunked(450).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { item ->
                    if (item.title.isNotBlank()) {
                        val ref = mediaCol.document()
                        batch.set(
                            ref,
                            item.copy(
                                userId = uid,
                                genre = normalizeGenres(item.genre),
                                streamingPlatform = normalizePlatform(item.streamingPlatform),
                                dateAdded = item.dateAdded.ifBlank { todayIso() }
                            ).toCreateMap(uid)
                        )
                        count++
                    }
                }
                batch.commit().await()
            }
            val quoteCol = db.collection("users").document(uid).collection("quotes")
            quotes.chunked(450).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { q ->
                    if (q.quoteText.isNotBlank() && q.sourceTitle.isNotBlank()) {
                        val ref = quoteCol.document()
                        batch.set(ref, q.toCreateMap(uid))
                        count++
                    }
                }
                batch.commit().await()
            }
            Result.success(count)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun clearAllUserData(userId: String): Result<Unit> {
        val path = "users/$userId"
        return try {
            val uid = requireUserId(userId)
            val mediaDocs = db.collection("users").document(uid).collection("media_items").get().await().documents
            mediaDocs.chunked(450).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { batch.delete(it.reference) }
                batch.commit().await()
            }
            val quoteDocs = db.collection("users").document(uid).collection("quotes").get().await().documents
            quoteDocs.chunked(450).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { batch.delete(it.reference) }
                batch.commit().await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }
}
