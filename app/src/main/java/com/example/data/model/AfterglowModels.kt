package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlin.math.roundToInt

enum class MediaCategory(val label: String) {
    MOVIE("Movie"),
    WEB_SERIES("Web Series"),
    ANIME("Anime"),
    K_DRAMA("K-Drama");

    val isEpisodic: Boolean
        get() = this != MOVIE

    companion object {
        fun fromRaw(raw: String?): MediaCategory =
            entries.firstOrNull { it.name == raw } ?: MOVIE
    }
}

enum class WatchStatus(val label: String) {
    COMPLETED("Completed"),
    WATCHING("Currently Watching"),
    PLAN_TO_WATCH("Plan to Watch"),
    ON_HOLD("On Hold"),
    DROPPED("Dropped");

    companion object {
        fun fromRaw(raw: String?): WatchStatus =
            entries.firstOrNull { it.name == raw } ?: PLAN_TO_WATCH
    }
}

enum class WatchlistPriority(val label: String, val sortWeight: Int) {
    HIGH("High", 0),
    MEDIUM("Medium", 1),
    LOW("Low", 2);

    companion object {
        fun fromRaw(raw: String?): WatchlistPriority =
            entries.firstOrNull { it.name == raw } ?: MEDIUM
    }
}

enum class AccentColorPreset(val label: String) {
    AFTERGLOW_PURPLE("Zenix Purple"),
    MIDNIGHT_ROSE("Midnight Rose"),
    CELESTIAL_INDIGO("Celestial Indigo"),
    NEON_AMETHYST("Neon Amethyst");

    companion object {
        fun fromRaw(raw: String?): AccentColorPreset =
            entries.firstOrNull { it.name == raw } ?: AFTERGLOW_PURPLE
    }
}

val AVAILABLE_MOODS = listOf(
    "Mind-Bending",
    "Feel-Good",
    "Dark & Gritty",
    "Emotional",
    "Adrenaline",
    "Cozy",
    "Suspense",
    "Romantic"
)

val POPULAR_PLATFORMS = listOf(
    "Netflix",
    "Prime Video",
    "Disney+",
    "Apple TV+",
    "Max",
    "Paramount+",
    "Crunchyroll",
    "Viki",
    "Hulu",
    "Fubo",
    "Theater",
    "Other"
)

val POPULAR_GENRES = listOf(
    "Action",
    "Adventure",
    "Animation",
    "Comedy",
    "Crime",
    "Drama",
    "Fantasy",
    "Horror",
    "Mystery",
    "Romance",
    "Sci-Fi",
    "Thriller",
    "Slice of Life",
    "Documentary"
)

fun normalizeGenreToken(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isBlank()) return ""
    return when (trimmed.lowercase()) {
        "action & adventure", "action and adventure" -> "Action"
        "sci-fi & fantasy", "sci-fi and fantasy", "science fiction", "sci fi", "scifi" -> "Sci-Fi"
        "war & politics", "war and politics" -> "War"
        "mystery & thriller", "crime & mystery" -> "Mystery"
        "kids", "family & kids" -> "Family"
        "tv movie" -> "Drama"
        "soap" -> "Drama"
        "reality" -> "Reality"
        "talk" -> "Talk"
        "news" -> "Documentary"
        "slice of life", "slice-of-life" -> "Slice of Life"
        "romantic" -> "Romance"
        else -> {
            val match = POPULAR_GENRES.firstOrNull { it.equals(trimmed, ignoreCase = true) }
            match ?: trimmed.split(" ")
                .filter { it.isNotBlank() }
                .joinToString(" ") { word ->
                    word.replaceFirstChar { ch -> if (ch.isLowerCase()) ch.titlecase() else ch.toString() }
                }
        }
    }
}

fun normalizeGenres(raw: String): String {
    if (raw.isBlank()) return ""
    val seen = LinkedHashSet<String>()
    val result = mutableListOf<String>()
    raw.split(",").forEach { part ->
        val token = normalizeGenreToken(part)
        if (token.isNotBlank()) {
            val key = token.lowercase()
            if (seen.add(key)) {
                result.add(token)
            }
        }
    }
    return result.joinToString(", ")
}

fun normalizePlatform(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isBlank()) return ""
    val lower = trimmed.lowercase()
    return when {
        lower.contains("paramount") -> "Paramount+"
        lower.contains("disney") || lower == "jiohotstar" || lower == "hotstar" -> "Disney+"
        lower.contains("prime video") || lower == "amazon prime" || lower == "amazon" -> "Prime Video"
        lower == "fubotv" || lower == "fubo tv" || lower == "fubo" -> "Fubo"
        lower.contains("apple tv") -> "Apple TV+"
        lower == "hbo max" || lower == "max" || lower == "hbo" -> "Max"
        lower.startsWith("netflix") -> "Netflix"
        lower.contains("crunchyroll") -> "Crunchyroll"
        lower == "hulu" -> "Hulu"
        lower.contains("viki") -> "Viki"
        lower == "theater" || lower == "theatre" || lower == "cinema" -> "Theater"
        else -> trimmed
    }
}

@Keep
@IgnoreExtraProperties
data class SeasonProgress(
    val seasonNumber: Long = 1L,
    val totalEpisodes: Long = 0L,
    val episodesWatched: Long = 0L
) {
    val progressPercent: Int
        get() = if (totalEpisodes <= 0L) 0 else ((episodesWatched.toDouble() / totalEpisodes.toDouble()) * 100.0)
            .roundToInt()
            .coerceIn(0, 100)

    fun toMap(): Map<String, Any> {
        val safeSeason = seasonNumber.coerceIn(1L, 200L)
        val safeTotal = totalEpisodes.coerceIn(0L, 10000L)
        val safeWatched = episodesWatched.coerceIn(0L, if (safeTotal > 0L) safeTotal else 10000L)
        return mapOf(
            "seasonNumber" to safeSeason,
            "totalEpisodes" to safeTotal,
            "episodesWatched" to safeWatched
        )
    }
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Cinephile",
    val bio: String = "",
    val accentColorKey: String = AccentColorPreset.AFTERGLOW_PURPLE.name,
    val compactMode: Boolean = false,
    val yearlyMovieGoal: Long = 30L,
    val yearlySeriesGoal: Long = 10L,
    val yearlyEpisodeGoal: Long = 250L,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    val accentPreset: AccentColorPreset
        get() = AccentColorPreset.fromRaw(accentColorKey)

    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "displayName" to displayName.trim().ifBlank { "Cinephile" }.take(60),
        "bio" to bio.trim().take(250),
        "accentColorKey" to AccentColorPreset.fromRaw(accentColorKey).name,
        "compactMode" to compactMode,
        "yearlyMovieGoal" to yearlyMovieGoal.coerceIn(1L, 2000L),
        "yearlySeriesGoal" to yearlySeriesGoal.coerceIn(1L, 500L),
        "yearlyEpisodeGoal" to yearlyEpisodeGoal.coerceIn(1L, 20000L),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun toUpdateMap(): Map<String, Any> = mapOf(
        "displayName" to displayName.trim().ifBlank { "Cinephile" }.take(60),
        "bio" to bio.trim().take(250),
        "accentColorKey" to AccentColorPreset.fromRaw(accentColorKey).name,
        "compactMode" to compactMode,
        "yearlyMovieGoal" to yearlyMovieGoal.coerceIn(1L, 2000L),
        "yearlySeriesGoal" to yearlySeriesGoal.coerceIn(1L, 500L),
        "yearlyEpisodeGoal" to yearlyEpisodeGoal.coerceIn(1L, 20000L),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class MediaItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val mediaCategory: String = MediaCategory.MOVIE.name,
    val posterUrl: String = "",
    val backdropUrl: String = "",
    val genre: String = "",
    val language: String = "",
    val releaseYear: Long = 0L,
    val streamingPlatform: String = "",
    val imdbRating: Double = 0.0,
    val personalRating: Double = 0.0,
    val runtimeMinutes: Long = 0L,
    val director: String = "",
    val mainCast: String = "",
    val country: String = "",
    val watchStatus: String = WatchStatus.PLAN_TO_WATCH.name,
    val startDate: String = "",
    val completionDate: String = "",
    val rewatchCount: Long = 0L,
    val rewatchFlag: Boolean = false,
    val trailerLink: String = "",
    val imdbLink: String = "",
    val personalReview: String = "",
    val notes: String = "",
    val synopsis: String = "",
    val totalSeasons: Long = 1L,
    val currentSeason: Long = 1L,
    val totalEpisodes: Long = 0L,
    val episodesWatched: Long = 0L,
    val currentEpisode: Long = 0L,
    val seasons: List<SeasonProgress> = emptyList(),
    val watchlistItem: Boolean = false,
    val priority: String = WatchlistPriority.MEDIUM.name,
    val dateAdded: String = "",
    val favourite: Boolean = false,
    val allTimeFavourite: Boolean = false,
    val manualRank: Long = 0L,
    val hiddenGem: Boolean = false,
    val moodTags: List<String> = emptyList(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    val categoryEnum: MediaCategory get() = MediaCategory.fromRaw(mediaCategory)
    val statusEnum: WatchStatus get() = WatchStatus.fromRaw(watchStatus)
    val priorityEnum: WatchlistPriority get() = WatchlistPriority.fromRaw(priority)

    val normalizedPlatform: String
        get() = normalizePlatform(streamingPlatform)

    val normalizedGenre: String
        get() = normalizeGenres(genre)

    val genreList: List<String>
        get() = normalizedGenre.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    val effectiveSeasons: List<SeasonProgress>
        get() {
            if (!categoryEnum.isEpisodic) return emptyList()
            if (seasons.isNotEmpty()) {
                return seasons.sortedBy { it.seasonNumber }
            }
            val safeSeasonsCount = totalSeasons.coerceAtLeast(1L).coerceAtMost(50L).toInt()
            if (safeSeasonsCount == 1) {
                return listOf(
                    SeasonProgress(
                        seasonNumber = 1L,
                        totalEpisodes = totalEpisodes.coerceAtLeast(0L),
                        episodesWatched = episodesWatched.coerceIn(0L, if (totalEpisodes > 0L) totalEpisodes else 10000L)
                    )
                )
            }
            val avgPerSeason = if (totalEpisodes > 0L) (totalEpisodes / safeSeasonsCount).coerceAtLeast(1L) else 0L
            var remainingTotal = totalEpisodes.coerceAtLeast(0L)
            var remainingWatched = episodesWatched.coerceAtLeast(0L)
            return (1..safeSeasonsCount).map { sNum ->
                val seasonTotal = if (sNum == safeSeasonsCount) {
                    remainingTotal
                } else {
                    val v = avgPerSeason.coerceAtMost(remainingTotal)
                    remainingTotal -= v
                    v
                }
                val seasonWatched = if (seasonTotal > 0L) {
                    val w = remainingWatched.coerceAtMost(seasonTotal)
                    remainingWatched -= w
                    w
                } else if (sNum == currentSeason.toInt()) {
                    val w = remainingWatched
                    remainingWatched = 0L
                    w
                } else 0L
                SeasonProgress(
                    seasonNumber = sNum.toLong(),
                    totalEpisodes = seasonTotal,
                    episodesWatched = seasonWatched
                )
            }
        }

    val episodeProgressPercent: Int
        get() {
            if (!categoryEnum.isEpisodic) {
                return if (statusEnum == WatchStatus.COMPLETED) 100 else 0
            }
            if (totalEpisodes <= 0L) return if (statusEnum == WatchStatus.COMPLETED) 100 else 0
            return ((episodesWatched.toDouble() / totalEpisodes.toDouble()) * 100.0)
                .roundToInt()
                .coerceIn(0, 100)
        }

    val remainingEpisodes: Long
        get() = if (!categoryEnum.isEpisodic) 0L else (totalEpisodes - episodesWatched).coerceAtLeast(0L)

    val seasonProgressPercent: Int
        get() {
            if (!categoryEnum.isEpisodic || totalSeasons <= 0L) return 0
            return ((currentSeason.toDouble() / totalSeasons.toDouble()) * 100.0)
                .roundToInt()
                .coerceIn(0, 100)
        }

    val watchedTimeMinutes: Long
        get() {
            val baseRuntime = runtimeMinutes.coerceAtLeast(0L)
            val multiplier = 1L + rewatchCount.coerceAtLeast(0L)
            return if (categoryEnum == MediaCategory.MOVIE) {
                if (statusEnum == WatchStatus.COMPLETED || rewatchCount > 0L) {
                    baseRuntime * multiplier
                } else 0L
            } else {
                val epMinutes = if (baseRuntime > 0L) baseRuntime else 42L
                (episodesWatched.coerceAtLeast(0L) * epMinutes) *
                    (if (statusEnum == WatchStatus.COMPLETED) multiplier else 1L)
            }
        }

    val estimatedTotalSeriesMinutes: Long
        get() {
            if (!categoryEnum.isEpisodic) return runtimeMinutes.coerceAtLeast(0L)
            val epMinutes = if (runtimeMinutes > 0L) runtimeMinutes else 42L
            return totalEpisodes.coerceAtLeast(0L) * epMinutes
        }

    fun formattedYearOrBlank(): String =
        if (releaseYear > 0L) releaseYear.toString() else ""

    fun formattedSeriesProgress(): String =
        "$title • Season ${currentSeason.coerceAtLeast(1)} • Episode $episodesWatched/$totalEpisodes • $episodeProgressPercent% Progress"

    fun formattedCompactSeriesProgress(): String =
        "S${currentSeason.coerceAtLeast(1)} · E$episodesWatched/$totalEpisodes · $episodeProgressPercent%"

    fun progressHint(): String = when {
        !categoryEnum.isEpisodic -> statusEnum.label
        totalEpisodes > 0L -> "Ep $episodesWatched/$totalEpisodes · S${currentSeason.coerceAtLeast(1)}"
        else -> "S${currentSeason.coerceAtLeast(1)} · Ep $episodesWatched"
    }

    fun withSyncedSeasons(updatedSeasons: List<SeasonProgress>): MediaItem {
        val sorted = updatedSeasons.sortedBy { it.seasonNumber }
        val sumTotal = sorted.sumOf { it.totalEpisodes.coerceAtLeast(0L) }
        val sumWatched = sorted.sumOf { it.episodesWatched.coerceAtLeast(0L) }
        val activeSeason = sorted.firstOrNull { it.totalEpisodes == 0L || it.episodesWatched < it.totalEpisodes }
            ?: sorted.lastOrNull()
        return copy(
            seasons = sorted,
            totalSeasons = sorted.size.toLong().coerceAtLeast(1L),
            currentSeason = activeSeason?.seasonNumber ?: 1L,
            totalEpisodes = sumTotal,
            episodesWatched = sumWatched,
            currentEpisode = activeSeason?.episodesWatched ?: sumWatched
        )
    }

    fun toCreateMap(uid: String): Map<String, Any> = buildBaseMap(uid) + mapOf(
        "userId" to uid,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun toUpdateMap(): Map<String, Any> = buildBaseMap(userId)
        .filterKeys { it != "userId" } + mapOf("updatedAt" to FieldValue.serverTimestamp())

    private fun buildBaseMap(uid: String): Map<String, Any> {
        val safeCat = MediaCategory.fromRaw(mediaCategory)
        val safeStatus = WatchStatus.fromRaw(watchStatus)
        val safePriority = WatchlistPriority.fromRaw(priority)
        val safeTotalSeasons = totalSeasons.coerceIn(0L, 200L)
        val safeCurrSeason = currentSeason.coerceIn(0L, 200L)
        val safeTotalEps = totalEpisodes.coerceIn(0L, 10000L)
        val safeWatchedEps = episodesWatched.coerceIn(0L, 10000L)
        val safeCurrEp = currentEpisode.coerceIn(0L, 10000L)
        val safeYear = if (releaseYear <= 0L) 0L else releaseYear.coerceIn(1880L, 2040L)
        return mapOf(
            "userId" to uid,
            "title" to title.trim().ifBlank { "Untitled" }.take(160),
            "mediaCategory" to safeCat.name,
            "posterUrl" to posterUrl.trim().take(1000),
            "backdropUrl" to backdropUrl.trim().take(1000),
            "genre" to normalizeGenres(genre).take(160),
            "language" to language.trim().take(60),
            "releaseYear" to safeYear,
            "streamingPlatform" to normalizePlatform(streamingPlatform).take(80),
            "imdbRating" to imdbRating.coerceIn(0.0, 10.0),
            "personalRating" to personalRating.coerceIn(0.0, 10.0),
            "runtimeMinutes" to runtimeMinutes.coerceIn(0L, 2000L),
            "director" to director.trim().take(160),
            "mainCast" to mainCast.trim().take(400),
            "country" to country.trim().take(80),
            "watchStatus" to safeStatus.name,
            "startDate" to startDate.trim().take(20),
            "completionDate" to completionDate.trim().take(20),
            "rewatchCount" to rewatchCount.coerceIn(0L, 500L),
            "rewatchFlag" to rewatchFlag,
            "trailerLink" to trailerLink.trim().take(500),
            "imdbLink" to imdbLink.trim().take(500),
            "personalReview" to personalReview.trim().take(3000),
            "notes" to notes.trim().take(2000),
            "synopsis" to synopsis.trim().take(1500),
            "totalSeasons" to safeTotalSeasons,
            "currentSeason" to safeCurrSeason,
            "totalEpisodes" to safeTotalEps,
            "episodesWatched" to safeWatchedEps,
            "currentEpisode" to safeCurrEp,
            "seasons" to seasons.take(100).map { it.toMap() },
            "watchlistItem" to watchlistItem,
            "priority" to safePriority.name,
            "dateAdded" to dateAdded.trim().take(20),
            "favourite" to favourite,
            "allTimeFavourite" to allTimeFavourite,
            "manualRank" to manualRank.coerceIn(0L, 500L),
            "hiddenGem" to hiddenGem,
            "moodTags" to moodTags.map { it.trim().take(40) }.filter { it.isNotEmpty() }.take(6)
        )
    }
}

@Keep
@IgnoreExtraProperties
data class QuoteItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val quoteText: String = "",
    val sourceTitle: String = "",
    val characterName: String = "",
    val mediaItemId: String = "",
    val mediaCategory: String = MediaCategory.MOVIE.name,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    val categoryEnum: MediaCategory get() = MediaCategory.fromRaw(mediaCategory)

    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "quoteText" to quoteText.trim().take(600),
        "sourceTitle" to sourceTitle.trim().take(160),
        "characterName" to characterName.trim().take(120),
        "mediaItemId" to mediaItemId.trim().take(128),
        "mediaCategory" to MediaCategory.fromRaw(mediaCategory).name,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun toUpdateMap(): Map<String, Any> = mapOf(
        "quoteText" to quoteText.trim().take(600),
        "sourceTitle" to sourceTitle.trim().take(160),
        "characterName" to characterName.trim().take(120),
        "mediaItemId" to mediaItemId.trim().take(128),
        "mediaCategory" to MediaCategory.fromRaw(mediaCategory).name,
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val current: Int,
    val target: Int
) {
    val unlocked: Boolean get() = current >= target
    val progress: Float get() = if (target <= 0) 0f else (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
}

data class DashboardStats(
    val totalTitles: Int = 0,
    val totalMovies: Int = 0,
    val totalWebSeries: Int = 0,
    val totalAnime: Int = 0,
    val totalKDrama: Int = 0,
    val completedMovies: Int = 0,
    val completedWebSeries: Int = 0,
    val completedAnime: Int = 0,
    val completedKDrama: Int = 0,
    val completedCount: Int = 0,
    val watchingCount: Int = 0,
    val planToWatchCount: Int = 0,
    val onHoldCount: Int = 0,
    val droppedCount: Int = 0,
    val averagePersonalRating: Double = 0.0,
    val totalWatchTimeMinutes: Long = 0L,
    val totalEpisodesWatched: Long = 0L,
    val moviesWatchTimeMinutes: Long = 0L,
    val seriesWatchTimeMinutes: Long = 0L,
    val animeWatchTimeMinutes: Long = 0L,
    val kDramaWatchTimeMinutes: Long = 0L,
    val topGenre: String = "—",
    val highestRatedTitle: MediaItem? = null,
    val nextUpTitle: MediaItem? = null,
    val genreDistribution: List<Pair<String, Int>> = emptyList(),
    val platformDistribution: List<Pair<String, Int>> = emptyList(),
    val statusDistribution: List<Pair<WatchStatus, Int>> = emptyList(),
    val monthlyActivity: List<Pair<String, Int>> = emptyList(),
    val yearlyHistory: List<Pair<String, Int>> = emptyList(),
    val top5Rated: List<MediaItem> = emptyList(),
    val currentlyWatching: List<MediaItem> = emptyList(),
    val recentlyAdded: List<MediaItem> = emptyList(),
    val recentlyCompleted: List<MediaItem> = emptyList(),
    val completedMoviesThisYear: Int = 0,
    val completedSeriesThisYear: Int = 0,
    val episodesWatchedTotal: Int = 0
) {
    val totalAnimeAndKDrama: Int get() = totalAnime + totalKDrama
    val animeAndKDramaWatchTimeMinutes: Long get() = animeWatchTimeMinutes + kDramaWatchTimeMinutes
}

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

fun formatWatchTime(totalMinutes: Long): String {
    if (totalMinutes <= 0L) return "0m"
    val days = totalMinutes / (60L * 24L)
    val hours = (totalMinutes % (60L * 24L)) / 60L
    val mins = totalMinutes % 60L
    return when {
        days > 0L -> "${days}d ${hours}h"
        hours > 0L -> "${hours}h ${mins}m"
        else -> "${mins}m"
    }
}
