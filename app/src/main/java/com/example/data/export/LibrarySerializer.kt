package com.example.data.export

import com.example.data.model.AccentColorPreset
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.QuoteItem
import com.example.data.model.UserProfile
import com.example.data.model.WatchStatus
import com.example.data.model.WatchlistPriority
import org.json.JSONArray
import org.json.JSONObject

data class ImportedBackup(
    val profile: UserProfile?,
    val mediaItems: List<MediaItem>,
    val quotes: List<QuoteItem>
)

object LibrarySerializer {
    fun exportToJson(
        profile: UserProfile?,
        mediaItems: List<MediaItem>,
        quotes: List<QuoteItem>
    ): String {
        val root = JSONObject()
        root.put("app", "Afterglow")
        root.put("schemaVersion", 1)
        root.put("exportedAtMillis", System.currentTimeMillis())

        if (profile != null) {
            root.put(
                "profile",
                JSONObject().apply {
                    put("displayName", profile.displayName)
                    put("bio", profile.bio)
                    put("accentColorKey", profile.accentColorKey)
                    put("compactMode", profile.compactMode)
                    put("yearlyMovieGoal", profile.yearlyMovieGoal)
                    put("yearlySeriesGoal", profile.yearlySeriesGoal)
                    put("yearlyEpisodeGoal", profile.yearlyEpisodeGoal)
                }
            )
        }

        val itemsArray = JSONArray()
        mediaItems.forEach { item ->
            itemsArray.put(
                JSONObject().apply {
                    put("title", item.title)
                    put("mediaCategory", item.mediaCategory)
                    put("posterUrl", item.posterUrl)
                    put("genre", item.genre)
                    put("language", item.language)
                    put("releaseYear", item.releaseYear)
                    put("streamingPlatform", item.streamingPlatform)
                    put("imdbRating", item.imdbRating)
                    put("personalRating", item.personalRating)
                    put("runtimeMinutes", item.runtimeMinutes)
                    put("director", item.director)
                    put("mainCast", item.mainCast)
                    put("country", item.country)
                    put("watchStatus", item.watchStatus)
                    put("startDate", item.startDate)
                    put("completionDate", item.completionDate)
                    put("rewatchCount", item.rewatchCount)
                    put("rewatchFlag", item.rewatchFlag)
                    put("trailerLink", item.trailerLink)
                    put("imdbLink", item.imdbLink)
                    put("personalReview", item.personalReview)
                    put("notes", item.notes)
                    put("totalSeasons", item.totalSeasons)
                    put("currentSeason", item.currentSeason)
                    put("totalEpisodes", item.totalEpisodes)
                    put("episodesWatched", item.episodesWatched)
                    put("currentEpisode", item.currentEpisode)
                    put("watchlistItem", item.watchlistItem)
                    put("priority", item.priority)
                    put("dateAdded", item.dateAdded)
                    put("favourite", item.favourite)
                    put("allTimeFavourite", item.allTimeFavourite)
                    put("manualRank", item.manualRank)
                    put("hiddenGem", item.hiddenGem)
                    put("moodTags", JSONArray(item.moodTags))
                }
            )
        }
        root.put("mediaItems", itemsArray)

        val quotesArray = JSONArray()
        quotes.forEach { q ->
            quotesArray.put(
                JSONObject().apply {
                    put("quoteText", q.quoteText)
                    put("sourceTitle", q.sourceTitle)
                    put("characterName", q.characterName)
                    put("mediaItemId", q.mediaItemId)
                    put("mediaCategory", q.mediaCategory)
                }
            )
        }
        root.put("quotes", quotesArray)
        return root.toString(2)
    }

    fun importFromJson(rawJson: String): Result<ImportedBackup> = try {
        val root = JSONObject(rawJson)
        val profileObj = root.optJSONObject("profile")
        val profile = profileObj?.let {
            UserProfile(
                displayName = it.optString("displayName", "Cinephile").ifBlank { "Cinephile" }.take(60),
                bio = it.optString("bio", "").take(250),
                accentColorKey = AccentColorPreset.fromRaw(it.optString("accentColorKey")).name,
                compactMode = it.optBoolean("compactMode", false),
                yearlyMovieGoal = it.optLong("yearlyMovieGoal", 30L).coerceIn(1L, 2000L),
                yearlySeriesGoal = it.optLong("yearlySeriesGoal", 10L).coerceIn(1L, 500L),
                yearlyEpisodeGoal = it.optLong("yearlyEpisodeGoal", 250L).coerceIn(1L, 20000L)
            )
        }

        val itemsArray = root.optJSONArray("mediaItems") ?: JSONArray()
        val items = mutableListOf<MediaItem>()
        for (i in 0 until itemsArray.length()) {
            val obj = itemsArray.optJSONObject(i) ?: continue
            val title = obj.optString("title", "").trim()
            if (title.isBlank()) continue
            val moodArr = obj.optJSONArray("moodTags") ?: JSONArray()
            val moods = mutableListOf<String>()
            for (m in 0 until moodArr.length()) {
                moodArr.optString(m)?.takeIf { it.isNotBlank() }?.let { moods.add(it) }
            }
            items.add(
                MediaItem(
                    title = title.take(160),
                    mediaCategory = MediaCategory.fromRaw(obj.optString("mediaCategory")).name,
                    posterUrl = obj.optString("posterUrl", ""),
                    genre = obj.optString("genre", ""),
                    language = obj.optString("language", "English"),
                    releaseYear = obj.optLong("releaseYear", 2024L).coerceIn(1880L, 2040L),
                    streamingPlatform = obj.optString("streamingPlatform", ""),
                    imdbRating = obj.optDouble("imdbRating", 0.0).coerceIn(0.0, 10.0),
                    personalRating = obj.optDouble("personalRating", 0.0).coerceIn(0.0, 10.0),
                    runtimeMinutes = obj.optLong("runtimeMinutes", 0L).coerceIn(0L, 2000L),
                    director = obj.optString("director", ""),
                    mainCast = obj.optString("mainCast", ""),
                    country = obj.optString("country", ""),
                    watchStatus = WatchStatus.fromRaw(obj.optString("watchStatus")).name,
                    startDate = obj.optString("startDate", ""),
                    completionDate = obj.optString("completionDate", ""),
                    rewatchCount = obj.optLong("rewatchCount", 0L).coerceIn(0L, 500L),
                    rewatchFlag = obj.optBoolean("rewatchFlag", false),
                    trailerLink = obj.optString("trailerLink", ""),
                    imdbLink = obj.optString("imdbLink", ""),
                    personalReview = obj.optString("personalReview", ""),
                    notes = obj.optString("notes", ""),
                    totalSeasons = obj.optLong("totalSeasons", 1L).coerceIn(0L, 200L),
                    currentSeason = obj.optLong("currentSeason", 1L).coerceIn(0L, 200L),
                    totalEpisodes = obj.optLong("totalEpisodes", 1L).coerceIn(0L, 10000L),
                    episodesWatched = obj.optLong("episodesWatched", 0L).coerceIn(0L, 10000L),
                    currentEpisode = obj.optLong("currentEpisode", 0L).coerceIn(0L, 10000L),
                    watchlistItem = obj.optBoolean("watchlistItem", false),
                    priority = WatchlistPriority.fromRaw(obj.optString("priority")).name,
                    dateAdded = obj.optString("dateAdded", ""),
                    favourite = obj.optBoolean("favourite", false),
                    allTimeFavourite = obj.optBoolean("allTimeFavourite", false),
                    manualRank = obj.optLong("manualRank", 0L).coerceIn(0L, 500L),
                    hiddenGem = obj.optBoolean("hiddenGem", false),
                    moodTags = moods.take(6)
                )
            )
        }

        val quotesArray = root.optJSONArray("quotes") ?: JSONArray()
        val quotes = mutableListOf<QuoteItem>()
        for (i in 0 until quotesArray.length()) {
            val obj = quotesArray.optJSONObject(i) ?: continue
            val text = obj.optString("quoteText", "").trim()
            val source = obj.optString("sourceTitle", "").trim()
            if (text.isBlank() || source.isBlank()) continue
            quotes.add(
                QuoteItem(
                    quoteText = text.take(600),
                    sourceTitle = source.take(160),
                    characterName = obj.optString("characterName", "").take(120),
                    mediaItemId = obj.optString("mediaItemId", "").take(128),
                    mediaCategory = MediaCategory.fromRaw(obj.optString("mediaCategory")).name
                )
            )
        }

        Result.success(ImportedBackup(profile, items, quotes))
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun exportToCsv(mediaItems: List<MediaItem>): String {
        val sb = StringBuilder()
        sb.appendLine(
            "Title,Category,Status,Release Year,Genre,Language,Platform,IMDb Rating,Personal Rating,Runtime (min),Director,Cast,Country,Seasons,Current Season,Total Episodes,Episodes Watched,Watchlist,Priority,Favourite,All-Time Favourite,Rewatch Count,Start Date,Completion Date,Review"
        )
        mediaItems.forEach { item ->
            val cols = listOf(
                item.title,
                item.categoryEnum.label,
                item.statusEnum.label,
                item.releaseYear.toString(),
                item.genre,
                item.language,
                item.streamingPlatform,
                item.imdbRating.toString(),
                item.personalRating.toString(),
                item.runtimeMinutes.toString(),
                item.director,
                item.mainCast,
                item.country,
                item.totalSeasons.toString(),
                item.currentSeason.toString(),
                item.totalEpisodes.toString(),
                item.episodesWatched.toString(),
                item.watchlistItem.toString(),
                item.priorityEnum.label,
                item.favourite.toString(),
                item.allTimeFavourite.toString(),
                item.rewatchCount.toString(),
                item.startDate,
                item.completionDate,
                item.personalReview
            )
            sb.appendLine(cols.joinToString(",") { csvEscape(it) })
        }
        return sb.toString()
    }

    private fun csvEscape(value: String): String {
        val cleaned = value.replace("\r\n", " ").replace("\n", " ")
        return if (cleaned.contains(",") || cleaned.contains("\"")) {
            "\"${cleaned.replace("\"", "\"\"")}\""
        } else {
            cleaned
        }
    }
}
