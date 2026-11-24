package com.example.data.firebase

import android.content.Context
import com.example.R
import com.example.data.model.AccentColorPreset
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.QuoteItem
import com.example.data.model.UserProfile
import com.example.data.model.WatchStatus
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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
            docRef.update(item.toUpdateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun incrementEpisode(userId: String, item: MediaItem, delta: Int): Result<Unit> {
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
                        batch.set(ref, item.copy(userId = uid, dateAdded = item.dateAdded.ifBlank { todayIso() }).toCreateMap(uid))
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
