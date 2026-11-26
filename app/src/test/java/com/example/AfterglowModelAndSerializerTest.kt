package com.example

import com.example.data.export.LibrarySerializer
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.QuoteItem
import com.example.data.model.SeasonProgress
import com.example.data.model.UserProfile
import com.example.data.model.WatchStatus
import com.example.data.model.WatchlistPriority
import com.example.data.model.formatWatchTime
import com.example.data.model.normalizeGenres
import com.example.data.model.normalizePlatform
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
    fun `genre and platform normalization maps verbose provider and TMDB strings cleanly`() {
        assertEquals("Action, Sci-Fi, Drama", normalizeGenres("Action & Adventure, Science Fiction, Drama, Action"))
        assertEquals("Sci-Fi, Fantasy", normalizeGenres("Sci-Fi & Fantasy, Fantasy"))
        assertEquals("Paramount+", normalizePlatform("Paramount Plus Premium"))
        assertEquals("Disney+", normalizePlatform("Disney Plus"))
        assertEquals("Prime Video", normalizePlatform("Amazon Prime Video"))
        assertEquals("Fubo", normalizePlatform("fuboTV"))
    }

    @Test
    fun `effectiveSeasons and withSyncedSeasons keep totals consistent and releaseYear zero formats blank`() {
        val item = MediaItem(
            id = "a1",
            title = "Frieren",
            mediaCategory = MediaCategory.ANIME.name,
            releaseYear = 0L,
            seasons = listOf(
                SeasonProgress(seasonNumber = 1L, totalEpisodes = 28L, episodesWatched = 28L),
                SeasonProgress(seasonNumber = 2L, totalEpisodes = 12L, episodesWatched = 3L)
            )
        )
        val synced = item.withSyncedSeasons(item.effectiveSeasons)
        assertEquals("", synced.formattedYearOrBlank())
        assertEquals(2L, synced.totalSeasons)
        assertEquals(40L, synced.totalEpisodes)
        assertEquals(31L, synced.episodesWatched)
        assertEquals(2L, synced.currentSeason)
        assertEquals("Ep 31/40 · S2", synced.progressHint())
    }

    @Test
    fun `json export and import round trip preserves items quotes profile synopsis and seasons`() {
        val profile = UserProfile(
            displayName = "Elena",
            bio = "Sci-fi fan",
            yearlyMovieGoal = 45,
            yearlySeriesGoal = 12,
            yearlyEpisodeGoal = 300
        )
        val movie = MediaItem(
            id = "m_interstellar",
            title = "Interstellar",
            mediaCategory = MediaCategory.MOVIE.name,
            genre = "Sci-Fi & Fantasy, Drama",
            streamingPlatform = "Paramount Plus Premium",
            releaseYear = 2014,
            personalRating = 9.5,
            runtimeMinutes = 169,
            synopsis = "A team of explorers travel through a wormhole in space.",
            watchStatus = WatchStatus.COMPLETED.name,
            favourite = true,
            allTimeFavourite = true,
            priority = WatchlistPriority.HIGH.name,
            moodTags = listOf("Mind-Bending", "Emotional")
        )
        val series = MediaItem(
            id = "s_dark",
            title = "Dark",
            mediaCategory = MediaCategory.WEB_SERIES.name,
            seasons = listOf(SeasonProgress(1L, 10L, 10L), SeasonProgress(2L, 8L, 4L)),
            totalSeasons = 2L,
            totalEpisodes = 18L,
            episodesWatched = 14L,
            watchStatus = WatchStatus.WATCHING.name
        )
        val quote = QuoteItem(
            quoteText = "Love is the one thing we're capable of perceiving that transcends dimensions of time and space.",
            sourceTitle = "Interstellar",
            characterName = "Brand",
            mediaCategory = MediaCategory.MOVIE.name
        )

        val json = LibrarySerializer.exportToJson(profile, listOf(movie, series), listOf(quote))
        val parsed = LibrarySerializer.importFromJson(json).getOrThrow()

        assertEquals("Elena", parsed.profile?.displayName)
        assertEquals(45L, parsed.profile?.yearlyMovieGoal)
        assertEquals(2, parsed.mediaItems.size)
        val parsedMovie = parsed.mediaItems.first { it.title == "Interstellar" }
        assertEquals("m_interstellar", parsedMovie.id)
        assertEquals("Sci-Fi, Fantasy, Drama", parsedMovie.genre)
        assertEquals("Paramount+", parsedMovie.streamingPlatform)
        assertEquals("A team of explorers travel through a wormhole in space.", parsedMovie.synopsis)
        assertEquals(9.5, parsedMovie.personalRating, 0.01)
        assertEquals(listOf("Mind-Bending", "Emotional"), parsedMovie.moodTags)

        val parsedSeries = parsed.mediaItems.first { it.title == "Dark" }
        assertEquals(2, parsedSeries.seasons.size)
        assertEquals(4L, parsedSeries.seasons[1].episodesWatched)

        assertEquals(1, parsed.quotes.size)
        assertEquals("Brand", parsed.quotes.first().characterName)

        val csv = LibrarySerializer.exportToCsv(listOf(movie.copy(releaseYear = 0L)))
        assertTrue(csv.contains("Interstellar"))
        assertTrue(!csv.contains(",0,"))
    }
}
