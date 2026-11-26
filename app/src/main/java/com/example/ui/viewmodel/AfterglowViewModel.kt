package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.export.ImportedBackup
import com.example.data.firebase.AfterglowRepository
import com.example.data.model.AchievementBadge
import com.example.data.model.DashboardStats
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.QuoteItem
import com.example.data.model.UiState
import com.example.data.model.UserProfile
import com.example.data.model.WatchStatus
import com.example.data.tmdb.TmdbMultiResult
import com.example.data.tmdb.TmdbPrefillResult
import com.example.data.tmdb.TmdbRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class AfterglowUiEvent(
    val message: String,
    val undoLabel: String? = null,
    val onUndo: (() -> Unit)? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class AfterglowViewModel(
    private val repository: AfterglowRepository,
    private val currentUserId: String,
    private val defaultDisplayName: String?
) : ViewModel() {

    private val tmdbRepository = TmdbRepository()
    private val syncTrigger = MutableStateFlow(0)

    private val _events = MutableSharedFlow<AfterglowUiEvent>(extraBufferCapacity = 12)
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.ensureUserProfile(currentUserId, defaultDisplayName)
        }
    }

    fun retrySync() {
        syncTrigger.value += 1
        viewModelScope.launch {
            repository.ensureUserProfile(currentUserId, defaultDisplayName)
        }
    }

    val profileState: StateFlow<UiState<UserProfile>> = syncTrigger
        .flatMapLatest {
            repository.observeProfile(currentUserId)
                .map<UserProfile?, UiState<UserProfile>> { profile ->
                    UiState.Success(
                        profile ?: UserProfile(
                            userId = currentUserId,
                            displayName = defaultDisplayName?.takeIf { it.isNotBlank() } ?: "Cinephile"
                        )
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    Log.w(TAG, "Error observing profile", rootError)
                    emit(UiState.Error(rootError.message ?: "Failed to load profile"))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val mediaItemsState: StateFlow<UiState<List<MediaItem>>> = syncTrigger
        .flatMapLatest {
            repository.observeMediaItems(currentUserId)
                .map<List<MediaItem>, UiState<List<MediaItem>>> { items ->
                    UiState.Success(items.sortedByDescending { it.updatedAt ?: it.createdAt ?: Timestamp(0, 0) })
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    Log.w(TAG, "Error observing media items", rootError)
                    emit(UiState.Error(rootError.message ?: "Failed to load library"))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val quotesState: StateFlow<UiState<List<QuoteItem>>> = syncTrigger
        .flatMapLatest {
            repository.observeQuotes(currentUserId)
                .map<List<QuoteItem>, UiState<List<QuoteItem>>> { quotes ->
                    UiState.Success(quotes.sortedByDescending { it.createdAt ?: Timestamp(0, 0) })
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val rootError = firestoreError ?: error
                    Log.w(TAG, "Error observing quotes", rootError)
                    emit(UiState.Error(rootError.message ?: "Failed to load quotes"))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val dashboardStats: StateFlow<DashboardStats> = mediaItemsState
        .map { state ->
            val items = (state as? UiState.Success)?.data ?: emptyList()
            computeDashboardStats(items)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DashboardStats()
        )

    val achievements: StateFlow<List<AchievementBadge>> = combine(mediaItemsState, quotesState) { mState, qState ->
        val items = (mState as? UiState.Success)?.data ?: emptyList()
        val quotes = (qState as? UiState.Success)?.data ?: emptyList()
        computeAchievements(items, quotes)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = emptyList()
    )

    private fun emitMessage(message: String) {
        _events.tryEmit(AfterglowUiEvent(message = message))
    }

    fun saveMediaItem(item: MediaItem, isEdit: Boolean) {
        viewModelScope.launch {
            val res = if (isEdit && item.id.isNotBlank()) {
                repository.updateMediaItem(currentUserId, item).map { item.id }
            } else {
                repository.createMediaItem(currentUserId, item)
            }
            res.onSuccess {
                emitMessage(if (isEdit) "Saved \"${item.title}\"" else "Added \"${item.title}\" to ZENIX")
            }.onFailure { err ->
                emitMessage(err.localizedMessage ?: "Failed to save \"${item.title}\"")
            }
        }
    }

    fun deleteMediaItem(item: MediaItem) {
        viewModelScope.launch {
            val snapshot = item.copy()
            repository.deleteMediaItem(currentUserId, item.id)
                .onSuccess {
                    _events.tryEmit(
                        AfterglowUiEvent(
                            message = "Deleted \"${snapshot.title}\"",
                            undoLabel = "Undo",
                            onUndo = { restoreMediaItem(snapshot) }
                        )
                    )
                }
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to delete item") }
        }
    }

    private fun restoreMediaItem(item: MediaItem) {
        viewModelScope.launch {
            repository.createMediaItem(currentUserId, item.copy(id = ""))
                .onSuccess { emitMessage("Restored \"${item.title}\"") }
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to restore \"${item.title}\"") }
        }
    }

    fun stepEpisode(item: MediaItem, delta: Int) {
        viewModelScope.launch {
            repository.incrementEpisode(currentUserId, item, delta)
                .onSuccess {
                    if (delta > 0 && item.totalEpisodes > 0 && item.episodesWatched + delta >= item.totalEpisodes) {
                        emitMessage("Completed \"${item.title}\"!")
                    }
                }
                .onFailure { emitMessage(it.localizedMessage ?: "Could not update episode progress") }
        }
    }

    fun moveWatchlistToLibrary(item: MediaItem, status: WatchStatus) {
        viewModelScope.launch {
            repository.moveWatchlistToLibrary(currentUserId, item, status)
                .onSuccess { emitMessage("Moved \"${item.title}\" to ${status.label}") }
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to move item") }
        }
    }

    fun toggleFavourite(item: MediaItem) {
        viewModelScope.launch {
            repository.updateQuickFlags(currentUserId, item.id, favourite = !item.favourite)
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to update favourite") }
        }
    }

    fun toggleAllTimeFavourite(item: MediaItem) {
        viewModelScope.launch {
            val next = !item.allTimeFavourite
            repository.updateQuickFlags(
                currentUserId,
                item.id,
                allTimeFavourite = next,
                favourite = if (next) true else item.favourite
            ).onFailure { emitMessage(it.localizedMessage ?: "Failed to update all-time favourite") }
        }
    }

    fun toggleHiddenGem(item: MediaItem) {
        viewModelScope.launch {
            repository.updateQuickFlags(currentUserId, item.id, hiddenGem = !item.hiddenGem)
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to update hidden gem") }
        }
    }

    fun toggleRewatchFlag(item: MediaItem) {
        viewModelScope.launch {
            repository.updateQuickFlags(currentUserId, item.id, rewatchFlag = !item.rewatchFlag)
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to update rewatch list") }
        }
    }

    fun incrementRewatch(item: MediaItem) {
        viewModelScope.launch {
            repository.updateQuickFlags(
                currentUserId,
                item.id,
                rewatchCount = item.rewatchCount + 1L,
                rewatchFlag = true
            ).onSuccess {
                emitMessage("Logged rewatch #${item.rewatchCount + 1} for \"${item.title}\"")
            }.onFailure {
                emitMessage(it.localizedMessage ?: "Failed to log rewatch")
            }
        }
    }

    fun resumeDroppedItem(item: MediaItem) {
        viewModelScope.launch {
            repository.updateQuickFlags(currentUserId, item.id, watchStatus = WatchStatus.WATCHING)
                .onSuccess { emitMessage("Resumed \"${item.title}\"") }
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to resume title") }
        }
    }

    fun updateManualRank(item: MediaItem, newRank: Long) {
        viewModelScope.launch {
            repository.updateQuickFlags(currentUserId, item.id, manualRank = newRank)
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to update rank") }
        }
    }

    fun saveQuote(quote: QuoteItem, isEdit: Boolean) {
        viewModelScope.launch {
            val res = if (isEdit && quote.id.isNotBlank()) {
                repository.updateQuote(currentUserId, quote)
            } else {
                repository.addQuote(currentUserId, quote)
            }
            res.onSuccess {
                emitMessage(if (isEdit) "Quote saved" else "Added quote to collection")
            }.onFailure {
                emitMessage(it.localizedMessage ?: "Failed to save quote")
            }
        }
    }

    fun deleteQuote(quote: QuoteItem) {
        viewModelScope.launch {
            val snapshot = quote.copy()
            repository.deleteQuote(currentUserId, quote.id)
                .onSuccess {
                    _events.tryEmit(
                        AfterglowUiEvent(
                            message = "Deleted quote",
                            undoLabel = "Undo",
                            onUndo = { restoreQuote(snapshot) }
                        )
                    )
                }
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to delete quote") }
        }
    }

    private fun restoreQuote(quote: QuoteItem) {
        viewModelScope.launch {
            repository.addQuote(currentUserId, quote.copy(id = ""))
                .onSuccess { emitMessage("Restored quote") }
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to restore quote") }
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateProfile(currentUserId, profile)
                .onSuccess { emitMessage("Saved preferences") }
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to save preferences") }
        }
    }

    fun importBackup(backup: ImportedBackup) {
        viewModelScope.launch {
            repository.importData(currentUserId, backup.mediaItems, backup.quotes, backup.profile)
                .onSuccess { count -> emitMessage("Imported $count records to your cloud library") }
                .onFailure { emitMessage(it.localizedMessage ?: "Import failed") }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllUserData(currentUserId)
                .onSuccess { emitMessage("All library items and quotes cleared") }
                .onFailure { emitMessage(it.localizedMessage ?: "Failed to clear data") }
        }
    }

    suspend fun searchTmdb(apiKey: String, query: String): Result<List<TmdbMultiResult>> =
        tmdbRepository.search(apiKey, query)

    suspend fun fetchTmdbDetails(apiKey: String, item: TmdbMultiResult): Result<TmdbPrefillResult> =
        tmdbRepository.fetchDetails(apiKey, item)

    private fun computeDashboardStats(items: List<MediaItem>): DashboardStats {
        if (items.isEmpty()) return DashboardStats()

        val currentYearStr = Calendar.getInstance().get(Calendar.YEAR).toString()
        val totalMovies = items.count { it.categoryEnum == MediaCategory.MOVIE }
        val totalWebSeries = items.count { it.categoryEnum == MediaCategory.WEB_SERIES }
        val totalAnimeKDrama = items.count {
            it.categoryEnum == MediaCategory.ANIME || it.categoryEnum == MediaCategory.K_DRAMA
        }

        val completed = items.count { it.statusEnum == WatchStatus.COMPLETED }
        val watching = items.count { it.statusEnum == WatchStatus.WATCHING }
        val planToWatch = items.count { it.statusEnum == WatchStatus.PLAN_TO_WATCH || it.watchlistItem }
        val onHold = items.count { it.statusEnum == WatchStatus.ON_HOLD }
        val dropped = items.count { it.statusEnum == WatchStatus.DROPPED }

        val rated = items.filter { it.personalRating > 0.0 }
        val avgRating = if (rated.isEmpty()) 0.0 else rated.sumOf { it.personalRating } / rated.size

        val moviesMins = items.filter { it.categoryEnum == MediaCategory.MOVIE }.sumOf { it.watchedTimeMinutes }
        val seriesMins = items.filter { it.categoryEnum == MediaCategory.WEB_SERIES }.sumOf { it.watchedTimeMinutes }
        val animeMins = items.filter {
            it.categoryEnum == MediaCategory.ANIME || it.categoryEnum == MediaCategory.K_DRAMA
        }.sumOf { it.watchedTimeMinutes }
        val totalMins = moviesMins + seriesMins + animeMins
        val totalEps = items.filter { it.categoryEnum.isEpisodic }.sumOf { it.episodesWatched }

        val genreCounts = mutableMapOf<String, Int>()
        items.forEach { item ->
            item.genreList.forEach { g ->
                genreCounts[g] = (genreCounts[g] ?: 0) + 1
            }
        }
        val sortedGenres = genreCounts.entries.sortedByDescending { it.value }.map { it.key to it.value }

        val platformCounts = items
            .map { it.streamingPlatform.trim().ifBlank { "Unspecified" } }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .map { it.key to it.value }

        val statusDist = WatchStatus.entries.map { st ->
            st to items.count { it.statusEnum == st }
        }.filter { it.second > 0 }

        val monthFormatter = SimpleDateFormat("MMM yy", Locale.US)
        val monthCounts = LinkedHashMap<String, Int>()
        val yearCounts = mutableMapOf<String, Int>()

        items.forEach { item ->
            val dateStr = item.completionDate.ifBlank { item.startDate }.ifBlank { item.dateAdded }
            val monthLabel = parseMonthLabel(dateStr, item.createdAt, monthFormatter)
            if (monthLabel != null) {
                monthCounts[monthLabel] = (monthCounts[monthLabel] ?: 0) + 1
            }
            val yr = dateStr.take(4).takeIf { it.length == 4 && it.all(Char::isDigit) }
                ?: item.releaseYear.toString()
            if (item.statusEnum == WatchStatus.COMPLETED) {
                yearCounts[yr] = (yearCounts[yr] ?: 0) + 1
            }
        }

        val top5 = items
            .filter { it.personalRating > 0.0 }
            .sortedWith(
                compareByDescending<MediaItem> { it.personalRating }
                    .thenByDescending { if (it.statusEnum == WatchStatus.COMPLETED) 1 else 0 }
                    .thenByDescending { it.completionDate }
                    .thenByDescending {
                        if (it.statusEnum == WatchStatus.COMPLETED && it.completionDate.isBlank()) {
                            (it.updatedAt ?: it.createdAt)?.seconds ?: 0L
                        } else 0L
                    }
                    .thenBy { it.title.lowercase(Locale.US) }
            )
            .take(5)

        val watchingNow = items
            .filter { it.statusEnum == WatchStatus.WATCHING }
            .sortedByDescending { (it.updatedAt ?: it.createdAt)?.seconds ?: 0L }
        val recentAdded = items.sortedByDescending { it.createdAt ?: Timestamp(0, 0) }.take(6)
        val recentCompleted = items
            .filter { it.statusEnum == WatchStatus.COMPLETED }
            .sortedWith(compareByDescending<MediaItem> { it.completionDate }.thenByDescending { it.updatedAt ?: Timestamp(0, 0) })
            .take(6)
        val nextUp = items
            .filter { it.watchlistItem || it.statusEnum == WatchStatus.PLAN_TO_WATCH || it.statusEnum == WatchStatus.ON_HOLD }
            .sortedWith(
                compareBy<MediaItem> { if (it.statusEnum == WatchStatus.PLAN_TO_WATCH || it.watchlistItem) 0 else 1 }
                    .thenBy { it.priorityEnum.sortWeight }
                    .thenByDescending { it.dateAdded }
                    .thenByDescending { (it.createdAt ?: it.updatedAt)?.seconds ?: 0L }
            )
            .firstOrNull()
            ?: items.filter { it != top5.firstOrNull() }
                .maxByOrNull { (it.createdAt ?: it.updatedAt)?.seconds ?: 0L }

        val completedMoviesYr = items.count {
            it.categoryEnum == MediaCategory.MOVIE &&
                it.statusEnum == WatchStatus.COMPLETED &&
                (it.completionDate.startsWith(currentYearStr) || it.completionDate.isBlank())
        }
        val completedSeriesYr = items.count {
            it.categoryEnum.isEpisodic &&
                it.statusEnum == WatchStatus.COMPLETED &&
                (it.completionDate.startsWith(currentYearStr) || it.completionDate.isBlank())
        }

        return DashboardStats(
            totalTitles = items.size,
            totalMovies = totalMovies,
            totalWebSeries = totalWebSeries,
            totalAnimeAndKDrama = totalAnimeKDrama,
            completedCount = completed,
            watchingCount = watching,
            planToWatchCount = planToWatch,
            onHoldCount = onHold,
            droppedCount = dropped,
            averagePersonalRating = avgRating,
            totalWatchTimeMinutes = totalMins,
            totalEpisodesWatched = totalEps,
            moviesWatchTimeMinutes = moviesMins,
            seriesWatchTimeMinutes = seriesMins,
            animeWatchTimeMinutes = animeMins,
            topGenre = sortedGenres.firstOrNull()?.first ?: "—",
            highestRatedTitle = top5.firstOrNull(),
            nextUpTitle = nextUp,
            genreDistribution = sortedGenres.take(8),
            platformDistribution = platformCounts.take(7),
            statusDistribution = statusDist,
            monthlyActivity = monthCounts.entries.toList().takeLast(6).map { it.key to it.value },
            yearlyHistory = yearCounts.entries.sortedBy { it.key }.takeLast(6).map { it.key to it.value },
            top5Rated = top5,
            currentlyWatching = watchingNow,
            recentlyAdded = recentAdded,
            recentlyCompleted = recentCompleted,
            completedMoviesThisYear = completedMoviesYr,
            completedSeriesThisYear = completedSeriesYr,
            episodesWatchedTotal = totalEps.toInt()
        )
    }

    private fun parseMonthLabel(dateStr: String, fallbackTs: Timestamp?, fmt: SimpleDateFormat): String? {
        if (dateStr.length >= 7) {
            try {
                val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr.take(10))
                if (parsed != null) return fmt.format(parsed)
            } catch (_: Exception) {
            }
        }
        return fallbackTs?.toDate()?.let { fmt.format(it) }
    }

    private fun computeAchievements(items: List<MediaItem>, quotes: List<QuoteItem>): List<AchievementBadge> {
        val completedMovies = items.count { it.categoryEnum == MediaCategory.MOVIE && it.statusEnum == WatchStatus.COMPLETED }
        val completedSeries = items.count { it.categoryEnum.isEpisodic && it.statusEnum == WatchStatus.COMPLETED }
        val completedAnime = items.count { it.categoryEnum == MediaCategory.ANIME && it.statusEnum == WatchStatus.COMPLETED }
        val ratedCount = items.count { it.personalRating > 0.0 }
        val reviewCount = items.count { it.personalReview.isNotBlank() }

        return listOf(
            AchievementBadge("first_frame", "First Frame", "Add your first title to Zenix", items.size, 1),
            AchievementBadge("binge_watcher", "Binge Watcher", "Complete 3 series or dramas", completedSeries, 3),
            AchievementBadge("anime_explorer", "Anime Explorer", "Complete 5 anime series", completedAnime, 5),
            AchievementBadge("series_master", "Series Master", "Complete 10 series", completedSeries, 10),
            AchievementBadge("movie_lover", "Movie Lover", "Complete 50 movies", completedMovies, 50),
            AchievementBadge("cinema_expert", "Cinema Expert", "Rate 100 titles", ratedCount, 100),
            AchievementBadge("critics_voice", "Critic's Voice", "Write 5 personal reviews", reviewCount, 5),
            AchievementBadge("quote_keeper", "Quote Keeper", "Save 5 memorable quotes", quotes.size, 5)
        )
    }

    private companion object {
        const val TAG = "AfterglowVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
