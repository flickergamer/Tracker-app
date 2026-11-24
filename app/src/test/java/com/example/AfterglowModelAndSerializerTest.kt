package com.example

import com.example.data.export.LibrarySerializer
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.QuoteItem
import com.example.data.model.UserProfile
import com.example.data.model.WatchStatus
import com.example.data.model.WatchlistPriority
import com.example.data.model.formatWatchTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AfterglowModelAndSerializerTest {

    @Test
    fun `series progress calculations match expected percentages and remaining episodes`() {
        val series = MediaItem(
            id = "s1",
            title = "Dark",
            mediaCategory = MediaCategory.WEB_SERIES.name,
            totalSeasons = 3,
            currentSeason = 1,
            totalEpisodes = 10,
            episodesWatched = 8,
            currentEpisode = 8,
            runtimeMinutes = 50,
            watchStatus = WatchStatus.WATCHING.name
        )
        assertEquals(80, series.episodeProgressPercent)
        assertEquals(2L, series.remainingEpisodes)
        assertEquals(33, series.seasonProgressPercent)
        assertEquals(400L, series.watchedTimeMinutes)
        assertEquals("Dark • Season 1 • Episode 8/10 • 80% Progress", series.formattedSeriesProgress())
        assertEquals("6h 40m", formatWatchTime(series.watchedTimeMinutes))
    }

    @Test
    fun `json export and import round trip preserves items quotes and profile`() {
        val profile = UserProfile(
            displayName = "Elena",
            bio = "Sci-fi fan",
            yearlyMovieGoal = 45,
            yearlySeriesGoal = 12,
            yearlyEpisodeGoal = 300
        )
        val movie = MediaItem(
            title = "Interstellar",
            mediaCategory = MediaCategory.MOVIE.name,
            genre = "Sci-Fi, Drama",
            releaseYear = 2014,
            personalRating = 9.5,
            runtimeMinutes = 169,
            watchStatus = WatchStatus.COMPLETED.name,
            favourite = true,
            allTimeFavourite = true,
            priority = WatchlistPriority.HIGH.name,
            moodTags = listOf("Mind-Bending", "Emotional")
        )
        val quote = QuoteItem(
            quoteText = "Love is the one thing we're capable of perceiving that transcends dimensions of time and space.",
            sourceTitle = "Interstellar",
            characterName = "Brand",
            mediaCategory = MediaCategory.MOVIE.name
        )

        val json = LibrarySerializer.exportToJson(profile, listOf(movie), listOf(quote))
        val parsed = LibrarySerializer.importFromJson(json).getOrThrow()

        assertEquals("Elena", parsed.profile?.displayName)
        assertEquals(45L, parsed.profile?.yearlyMovieGoal)
        assertEquals(1, parsed.mediaItems.size)
        assertEquals("Interstellar", parsed.mediaItems.first().title)
        assertEquals(9.5, parsed.mediaItems.first().personalRating, 0.01)
        assertEquals(listOf("Mind-Bending", "Emotional"), parsed.mediaItems.first().moodTags)
        assertEquals(1, parsed.quotes.size)
        assertEquals("Brand", parsed.quotes.first().characterName)

        val csv = LibrarySerializer.exportToCsv(listOf(movie))
        assertTrue(csv.contains("Interstellar"))
        assertTrue(csv.contains("Sci-Fi, Drama"))
    }
}
