package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AVAILABLE_MOODS
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.POPULAR_GENRES
import com.example.data.model.POPULAR_PLATFORMS
import com.example.data.model.SeasonProgress
import com.example.data.model.WatchStatus
import com.example.data.model.WatchlistPriority
import com.example.data.model.normalizeGenres
import com.example.data.model.normalizePlatform
import com.example.data.tmdb.TmdbMultiResult
import com.example.data.tmdb.TmdbPreferences
import com.example.data.tmdb.TmdbPrefillResult
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalReducedMotion
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

private enum class DatePickerTarget { START, FINISH }

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MediaEditorDialog(
    initialItem: MediaItem?,
    defaultCategory: MediaCategory,
    defaultWatchlist: Boolean,
    onSearchTmdb: suspend (String, String) -> Result<List<TmdbMultiResult>>,
    onFetchTmdbDetails: suspend (String, TmdbMultiResult) -> Result<TmdbPrefillResult>,
    onDismiss: () -> Unit,
    onSave: (MediaItem, Boolean) -> Unit
) {
    val isEdit = initialItem != null && initialItem.id.isNotBlank()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val reducedMotion = LocalReducedMotion.current
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }

    val namedPlatforms = remember { POPULAR_PLATFORMS.filter { it != "Other" } }
    val initPlatformNormalized = remember(initialItem) { initialItem?.normalizedPlatform ?: "" }
    val initIsNamedPlatform = remember(initPlatformNormalized) {
        initPlatformNormalized.isBlank() || namedPlatforms.any { it.equals(initPlatformNormalized, ignoreCase = true) }
    }
    val initMatchedNamedPlatform = remember(initPlatformNormalized) {
        namedPlatforms.firstOrNull { it.equals(initPlatformNormalized, ignoreCase = true) } ?: ""
    }

    val initTitle = initialItem?.title ?: ""
    val initCategory = initialItem?.categoryEnum ?: defaultCategory
    val initStatus = initialItem?.statusEnum ?: if (defaultWatchlist) WatchStatus.PLAN_TO_WATCH else WatchStatus.WATCHING
    val initPosterUrl = initialItem?.posterUrl ?: ""
    val initGenre = initialItem?.normalizedGenre ?: ""
    val initLanguage = initialItem?.language ?: when (defaultCategory) {
        MediaCategory.ANIME -> "Japanese"
        MediaCategory.K_DRAMA -> "Korean"
        else -> "English"
    }
    val initYear = if ((initialItem?.releaseYear ?: 0L) > 0L) initialItem!!.releaseYear.toString() else ""
    val initRuntime = if ((initialItem?.runtimeMinutes ?: 0L) > 0L) initialItem!!.runtimeMinutes.toString() else ""
    val initImdb = if ((initialItem?.imdbRating ?: 0.0) > 0.0) initialItem!!.imdbRating.toString() else ""
    val initPersonalRating = initialItem?.personalRating ?: 0.0
    val initPlatform = if (initIsNamedPlatform) initMatchedNamedPlatform else initPlatformNormalized
    val initCustomPlatform = if (initIsNamedPlatform) "" else initPlatformNormalized
    val initOtherSelected = !initIsNamedPlatform
    val initDirector = initialItem?.director ?: ""
    val initMainCast = initialItem?.mainCast ?: ""
    val initCountry = initialItem?.country ?: ""
    val initStartDate = initialItem?.startDate ?: ""
    val initCompletionDate = initialItem?.completionDate ?: ""
    val initRewatchCount = (initialItem?.rewatchCount ?: 0L).toString()
    val initRewatchFlag = initialItem?.rewatchFlag ?: false
    val initTrailerLink = initialItem?.trailerLink ?: ""
    val initImdbLink = initialItem?.imdbLink ?: ""
    val initReview = initialItem?.personalReview ?: ""
    val initNotes = initialItem?.notes ?: ""
    val initSynopsis = initialItem?.synopsis ?: ""
    val initTotalSeasons = (initialItem?.totalSeasons ?: 1L).toString()
    val initCurrentSeason = (initialItem?.currentSeason ?: 1L).toString()
    val initTotalEpisodes = if ((initialItem?.totalEpisodes ?: 0L) > 0L) initialItem!!.totalEpisodes.toString() else ""
    val initEpisodesWatched = if ((initialItem?.episodesWatched ?: 0L) > 0L) initialItem!!.episodesWatched.toString() else ""
    val initWatchlist = initialItem?.watchlistItem ?: defaultWatchlist
    val initPriority = initialItem?.priorityEnum ?: WatchlistPriority.MEDIUM
    val initFavourite = initialItem?.favourite ?: false
    val initAllTimeFav = initialItem?.allTimeFavourite ?: false
    val initHiddenGem = initialItem?.hiddenGem ?: false
    val initMoods = remember(initialItem) { initialItem?.moodTags ?: emptyList() }

    var title by remember { mutableStateOf(initTitle) }
    var category by remember { mutableStateOf(initCategory) }
    var status by remember { mutableStateOf(initStatus) }
    var posterUrl by remember { mutableStateOf(initPosterUrl) }
    var genre by remember { mutableStateOf(initGenre) }
    var language by remember { mutableStateOf(initLanguage) }
    var releaseYear by remember { mutableStateOf(initYear) }
    var platform by remember { mutableStateOf(initPlatform) }
    var customPlatform by remember { mutableStateOf(initCustomPlatform) }
    var otherPlatformSelected by remember { mutableStateOf(initOtherSelected) }
    var imdbRating by remember { mutableStateOf(initImdb) }
    var personalRating by remember { mutableDoubleStateOf(initPersonalRating) }
    var runtimeMinutes by remember { mutableStateOf(initRuntime) }
    var director by remember { mutableStateOf(initDirector) }
    var mainCast by remember { mutableStateOf(initMainCast) }
    var country by remember { mutableStateOf(initCountry) }
    var startDate by remember { mutableStateOf(initStartDate) }
    var completionDate by remember { mutableStateOf(initCompletionDate) }
    var rewatchCount by remember { mutableStateOf(initRewatchCount) }
    var rewatchFlag by remember { mutableStateOf(initRewatchFlag) }
    var trailerLink by remember { mutableStateOf(initTrailerLink) }
    var imdbLink by remember { mutableStateOf(initImdbLink) }
    var personalReview by remember { mutableStateOf(initReview) }
    var notes by remember { mutableStateOf(initNotes) }
    var synopsis by remember { mutableStateOf(initSynopsis) }

    var totalSeasons by remember { mutableStateOf(initTotalSeasons) }
    var currentSeason by remember { mutableStateOf(initCurrentSeason) }
    var totalEpisodes by remember { mutableStateOf(initTotalEpisodes) }
    var episodesWatched by remember { mutableStateOf(initEpisodesWatched) }
    var capturedSeasons by remember { mutableStateOf(initialItem?.seasons ?: emptyList()) }

    var watchlistItem by remember { mutableStateOf(initWatchlist) }
    var priority by remember { mutableStateOf(initPriority) }
    var favourite by remember { mutableStateOf(initFavourite) }
    var allTimeFavourite by remember { mutableStateOf(initAllTimeFav) }
    var hiddenGem by remember { mutableStateOf(initHiddenGem) }
    val selectedMoods = remember { mutableStateListOf<String>().apply { addAll(initMoods) } }

    var moreDetailsExpanded by remember { mutableStateOf(false) }
    var activeDatePicker by remember { mutableStateOf<DatePickerTarget?>(null) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    var tmdbQuery by remember { mutableStateOf("") }
    var tmdbLoading by remember { mutableStateOf(false) }
    var tmdbResults by remember { mutableStateOf<List<TmdbMultiResult>>(emptyList()) }
    var tmdbMessage by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val effectivePlatform = if (otherPlatformSelected) customPlatform.trim() else platform.trim()
    val hasUnsavedChanges = title != initTitle ||
        category != initCategory ||
        status != initStatus ||
        posterUrl != initPosterUrl ||
        genre != initGenre ||
        language != initLanguage ||
        releaseYear != initYear ||
        effectivePlatform != initPlatformNormalized ||
        imdbRating != initImdb ||
        personalRating != initPersonalRating ||
        runtimeMinutes != initRuntime ||
        director != initDirector ||
        mainCast != initMainCast ||
        country != initCountry ||
        startDate != initStartDate ||
        completionDate != initCompletionDate ||
        rewatchCount != initRewatchCount ||
        rewatchFlag != initRewatchFlag ||
        trailerLink != initTrailerLink ||
        imdbLink != initImdbLink ||
        personalReview != initReview ||
        notes != initNotes ||
        synopsis != initSynopsis ||
        totalSeasons != initTotalSeasons ||
        currentSeason != initCurrentSeason ||
        totalEpisodes != initTotalEpisodes ||
        episodesWatched != initEpisodesWatched ||
        watchlistItem != initWatchlist ||
        priority != initPriority ||
        favourite != initFavourite ||
        allTimeFavourite != initAllTimeFav ||
        hiddenGem != initHiddenGem ||
        selectedMoods.toList() != initMoods

    val attemptClose: () -> Unit = {
        if (hasUnsavedChanges) {
            showDiscardDialog = true
        } else {
            onDismiss()
        }
    }

    val performSave: () -> Unit = {
        val cleanTitle = title.trim()
        if (cleanTitle.isBlank()) {
            validationError = "Title is required."
        } else {
            val yearTrimmed = releaseYear.trim()
            val parsedYear = if (yearTrimmed.isEmpty()) 0L else (yearTrimmed.toLongOrNull() ?: -1L)
            if (yearTrimmed.isNotEmpty() && parsedYear !in 1880L..2040L) {
                validationError = "Release year must be between 1880 and 2040."
            } else {
                val parsedImdb = imdbRating.trim().toDoubleOrNull() ?: 0.0
                if (imdbRating.isNotBlank() && parsedImdb !in 0.0..10.0) {
                    validationError = "IMDb rating must be between 0.0 and 10.0."
                } else {
                    val parsedRuntime = (runtimeMinutes.trim().toLongOrNull() ?: 0L).coerceIn(0L, 2000L)
                    val parsedSeasons = if (category.isEpisodic) {
                        (totalSeasons.trim().toLongOrNull() ?: 1L).coerceIn(1L, 200L)
                    } else 1L
                    val parsedCurrSeason = if (category.isEpisodic) {
                        (currentSeason.trim().toLongOrNull() ?: 1L).coerceIn(1L, 200L)
                    } else 1L
                    val parsedTotalEps = if (category.isEpisodic) {
                        (totalEpisodes.trim().toLongOrNull() ?: 0L).coerceIn(0L, 10000L)
                    } else 0L
                    val rawWatchedEps = if (category.isEpisodic) {
                        (episodesWatched.trim().toLongOrNull() ?: 0L).coerceIn(0L, 10000L)
                    } else 0L
                    val finalWatchedEps = if (status == WatchStatus.COMPLETED && category.isEpisodic && parsedTotalEps > 0L && rawWatchedEps == 0L) {
                        parsedTotalEps
                    } else {
                        rawWatchedEps
                    }
                    val parsedRewatches = (rewatchCount.trim().toLongOrNull() ?: 0L).coerceIn(0L, 500L)

                    val resolvedSeasons: List<SeasonProgress> = if (!category.isEpisodic) {
                        emptyList()
                    } else if (capturedSeasons.isNotEmpty() &&
                        capturedSeasons.size.toLong() == parsedSeasons &&
                        capturedSeasons.sumOf { it.totalEpisodes } == parsedTotalEps
                    ) {
                        if (finalWatchedEps == capturedSeasons.sumOf { it.episodesWatched }) {
                            capturedSeasons
                        } else if (status == WatchStatus.COMPLETED && finalWatchedEps == parsedTotalEps) {
                            capturedSeasons.map { it.copy(episodesWatched = it.totalEpisodes) }
                        } else {
                            emptyList()
                        }
                    } else {
                        emptyList()
                    }

                    val finalItem = (initialItem ?: MediaItem()).copy(
                        title = cleanTitle.take(160),
                        mediaCategory = category.name,
                        posterUrl = posterUrl.trim().take(1000),
                        genre = normalizeGenres(genre).take(160),
                        language = language.trim().take(60),
                        releaseYear = parsedYear,
                        streamingPlatform = normalizePlatform(effectivePlatform).take(80),
                        imdbRating = parsedImdb,
                        personalRating = (personalRating * 10.0).roundToInt() / 10.0,
                        runtimeMinutes = parsedRuntime,
                        director = director.trim().take(160),
                        mainCast = mainCast.trim().take(400),
                        country = country.trim().take(80),
                        watchStatus = status.name,
                        startDate = startDate.trim().take(20),
                        completionDate = if (status == WatchStatus.COMPLETED && completionDate.isBlank()) today else completionDate.trim().take(20),
                        rewatchCount = parsedRewatches,
                        rewatchFlag = rewatchFlag || parsedRewatches > 0L,
                        trailerLink = trailerLink.trim().take(500),
                        imdbLink = imdbLink.trim().take(500),
                        personalReview = personalReview.trim().take(3000),
                        notes = notes.trim().take(2000),
                        synopsis = synopsis.trim().take(1500),
                        totalSeasons = parsedSeasons,
                        currentSeason = parsedCurrSeason,
                        totalEpisodes = parsedTotalEps,
                        episodesWatched = finalWatchedEps,
                        currentEpisode = finalWatchedEps,
                        seasons = resolvedSeasons,
                        watchlistItem = watchlistItem || status == WatchStatus.PLAN_TO_WATCH,
                        priority = priority.name,
                        dateAdded = initialItem?.dateAdded?.ifBlank { today } ?: today,
                        favourite = favourite || allTimeFavourite,
                        allTimeFavourite = allTimeFavourite,
                        hiddenGem = hiddenGem,
                        moodTags = selectedMoods.toList().take(6)
                    )
                    onSave(finalItem, isEdit)
                }
            }
        }
    }

    Dialog(
        onDismissRequest = attemptClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BackHandler(onBack = attemptClose)

        AfterglowBackdrop(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.space4, vertical = spacing.space3),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassIconButton(
                        onClick = attemptClose,
                        glowColor = tokens.primaryAccent,
                        modifier = Modifier.testTag("close_editor_button")
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close editor", tint = tokens.textPrimary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEdit) "Edit ${category.label}" else "Add to ZENIX",
                            style = MaterialTheme.typography.headlineMedium,
                            color = tokens.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Essentials first — optional details below",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary
                        )
                    }
                }
                HorizontalDivider(color = tokens.outlineSubtle)

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(spacing.space4),
                    verticalArrangement = Arrangement.spacedBy(spacing.space4)
                ) {
                    if (validationError != null) {
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = tokens.dangerAccent,
                            cornerRadius = radii.md,
                            contentPadding = PaddingValues(spacing.space3)
                        ) {
                            Text(
                                text = validationError!!,
                                color = tokens.dangerAccent,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // ESSENTIALS SECTION
                    Text(
                        text = "Essentials",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary,
                        modifier = Modifier.zenixHeading()
                    )

                    // TMDB Auto-Fill
                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = tokens.tertiaryAccent,
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Auto-Fill via TMDB (Optional)",
                                style = MaterialTheme.typography.titleSmall,
                                color = tokens.tertiaryAccent
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NeonGlassTextField(
                                    value = tmdbQuery,
                                    onValueChange = { tmdbQuery = it },
                                    placeholder = "Search movie or TV title...",
                                    focusNeonColor = tokens.tertiaryAccent,
                                    modifier = Modifier.weight(1f)
                                )
                                GlassSecondaryButton(
                                    onClick = {
                                        val q = tmdbQuery.trim().ifBlank { title.trim() }
                                        if (q.isBlank()) {
                                            tmdbMessage = "Enter a title to search TMDB."
                                            return@GlassSecondaryButton
                                        }
                                        scope.launch {
                                            tmdbLoading = true
                                            tmdbMessage = null
                                            val key = TmdbPreferences.resolveEffectiveKey(context)
                                            if (key.isBlank()) {
                                                tmdbLoading = false
                                                tmdbMessage = "No TMDB API key configured. Add TMDB_API_KEY in Settings or continue manually below."
                                                return@launch
                                            }
                                            onSearchTmdb(key, q)
                                                .onSuccess { list ->
                                                    tmdbResults = list.take(5)
                                                    if (list.isEmpty()) tmdbMessage = "No matches found. You can enter details manually."
                                                }
                                                .onFailure {
                                                    tmdbMessage = "TMDB lookup failed: ${it.localizedMessage}"
                                                }
                                            tmdbLoading = false
                                        }
                                    },
                                    enabled = !tmdbLoading,
                                    accentBorderColor = tokens.tertiaryAccent,
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                                ) {
                                    if (tmdbLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = tokens.tertiaryAccent)
                                    } else {
                                        Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Lookup")
                                    }
                                }
                            }
                            if (tmdbMessage != null) {
                                Text(
                                    text = tmdbMessage!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textSecondary
                                )
                            }
                            val resultBg = when (tokens.mode) {
                                AppThemeMode.NEON_GLOSSY -> Color.White.copy(alpha = 0.08f)
                                AppThemeMode.MINIMAL_DARK, AppThemeMode.AMOLED_BLACK -> tokens.tonalContainer.copy(alpha = 0.72f)
                            }
                            tmdbResults.forEach { res ->
                                val label = res.title ?: res.name ?: "Untitled"
                                val yr = (res.releaseDate ?: res.firstAirDate)?.take(4) ?: ""
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(resultBg)
                                        .clickable {
                                            scope.launch {
                                                tmdbLoading = true
                                                val key = TmdbPreferences.resolveEffectiveKey(context)
                                                onFetchTmdbDetails(key, res).onSuccess { d ->
                                                    title = d.title
                                                    category = d.category
                                                    posterUrl = d.posterUrl
                                                    genre = d.genre
                                                    language = d.language
                                                    releaseYear = if (d.releaseYear > 0L) d.releaseYear.toString() else ""
                                                    runtimeMinutes = if (d.runtimeMinutes > 0L) d.runtimeMinutes.toString() else ""
                                                    director = d.director
                                                    mainCast = d.mainCast
                                                    country = d.country
                                                    if (d.imdbRating > 0) imdbRating = d.imdbRating.toString()
                                                    val matchedPlat = namedPlatforms.firstOrNull {
                                                        it.equals(d.streamingPlatform, ignoreCase = true)
                                                    }
                                                    if (matchedPlat != null) {
                                                        platform = matchedPlat
                                                        otherPlatformSelected = false
                                                        customPlatform = ""
                                                    } else if (d.streamingPlatform.isNotBlank()) {
                                                        otherPlatformSelected = true
                                                        customPlatform = d.streamingPlatform
                                                        platform = d.streamingPlatform
                                                    }
                                                    trailerLink = d.trailerLink
                                                    imdbLink = d.imdbLink
                                                    totalSeasons = d.totalSeasons.coerceAtLeast(1L).toString()
                                                    totalEpisodes = if (d.totalEpisodes > 0L) d.totalEpisodes.toString() else ""
                                                    capturedSeasons = d.seasons.map {
                                                        SeasonProgress(
                                                            seasonNumber = it.seasonNumber,
                                                            totalEpisodes = it.totalEpisodes,
                                                            episodesWatched = 0L
                                                        )
                                                    }
                                                    if (synopsis.isBlank() && d.synopsis.isNotBlank()) synopsis = d.synopsis
                                                    tmdbResults = emptyList()
                                                    tmdbMessage = "Pre-filled from TMDB!"
                                                }.onFailure {
                                                    tmdbMessage = "Could not load details: ${it.localizedMessage}"
                                                }
                                                tmdbLoading = false
                                            }
                                        }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$label ${if (yr.isNotBlank()) "($yr)" else ""}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = tokens.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = res.mediaType?.uppercase() ?: "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = tokens.tertiaryAccent
                                    )
                                }
                            }
                        }
                    }

                    // Category
                    Text("Category", style = MaterialTheme.typography.titleSmall, color = tokens.secondaryAccent)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MediaCategory.entries.forEach { cat ->
                            NeonGlassChip(
                                label = cat.label,
                                selected = category == cat,
                                onClick = { category = cat },
                                accentColor = themedCategoryColor(cat)
                            )
                        }
                    }

                    // Status (auto-fills Finish date when Completed and empty)
                    Text("Watch Status", style = MaterialTheme.typography.titleSmall, color = tokens.secondaryAccent)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        WatchStatus.entries.forEach { st ->
                            NeonGlassChip(
                                label = st.label,
                                selected = status == st,
                                onClick = {
                                    status = st
                                    if (st == WatchStatus.PLAN_TO_WATCH) watchlistItem = true
                                    if (st == WatchStatus.COMPLETED) {
                                        if (completionDate.isBlank()) {
                                            completionDate = today
                                        }
                                        if (category.isEpisodic && totalEpisodes.isNotBlank()) {
                                            episodesWatched = totalEpisodes
                                        }
                                    }
                                },
                                accentColor = statusColor(st)
                            )
                        }
                    }

                    // Title (Only required field)
                    NeonGlassTextField(
                        value = title,
                        onValueChange = { title = it; validationError = null },
                        label = "Title *",
                        placeholder = "Enter title...",
                        focusNeonColor = tokens.secondaryAccent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("editor_title_input")
                    )

                    // Poster URL
                    NeonGlassTextField(
                        value = posterUrl,
                        onValueChange = { posterUrl = it },
                        label = "Poster Image URL",
                        placeholder = "https://...",
                        focusNeonColor = tokens.primaryAccent,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Personal Rating: 1..10 tap buttons + Clear button + live star preview
                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = tokens.ratingGold,
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Personal Rating", style = MaterialTheme.typography.titleSmall, color = tokens.ratingGold)
                                    val ratingStr = when {
                                        personalRating <= 0.0 -> "Unrated"
                                        personalRating % 1.0 == 0.0 -> "${personalRating.toInt()} / 10"
                                        else -> String.format(Locale.US, "%.1f / 10", personalRating)
                                    }
                                    Text(
                                        text = ratingStr,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = tokens.textPrimary
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (personalRating > 0.0) {
                                        NeonRatingStars(ratingOutOfTen = personalRating, starSize = 16.dp)
                                        TextButton(
                                            onClick = { personalRating = 0.0 },
                                            modifier = Modifier
                                                .defaultMinSize(minWidth = 48.dp, minHeight = 40.dp)
                                                .testTag("clear_rating_button")
                                        ) {
                                            Text("Clear", color = tokens.dangerAccent, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }

                            // Two rows of 5 tap buttons (1..5 and 6..10)
                            for (rowStart in listOf(1, 6)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    for (score in rowStart until rowStart + 5) {
                                        val isSelected = personalRating.roundToInt() == score && personalRating > 0.0
                                        val btnShape = RoundedCornerShape(radii.sm)
                                        val activeGrad = Brush.horizontalGradient(
                                            listOf(tokens.ratingGold, lerp(tokens.ratingGold, Color(0xFFFF8C00), 0.35f))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .defaultMinSize(minHeight = 48.dp)
                                                .clip(btnShape)
                                                .then(
                                                    if (isSelected) {
                                                        Modifier
                                                            .background(activeGrad)
                                                            .border(1.dp, Color.White.copy(alpha = 0.45f), btnShape)
                                                    } else {
                                                        Modifier
                                                            .background(Color.White.copy(alpha = 0.06f))
                                                            .border(1.dp, tokens.outlineSubtle, btnShape)
                                                    }
                                                )
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = ripple(color = tokens.ratingGold),
                                                    role = Role.Button,
                                                    onClick = { personalRating = score.toDouble() }
                                                )
                                                .testTag("rating_btn_$score"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = score.toString(),
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color(0xFF0A0812) else tokens.textPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Streaming Platform: chip selector only + "Other" revealing custom text input
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Streaming Platform", style = MaterialTheme.typography.titleSmall, color = tokens.tertiaryAccent)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            POPULAR_PLATFORMS.forEachIndexed { idx, p ->
                                val isOther = p == "Other"
                                val selected = if (isOther) {
                                    otherPlatformSelected
                                } else {
                                    !otherPlatformSelected && platform.equals(p, ignoreCase = true)
                                }
                                NeonGlassChip(
                                    label = p,
                                    selected = selected,
                                    onClick = {
                                        if (isOther) {
                                            otherPlatformSelected = true
                                            platform = customPlatform
                                        } else {
                                            otherPlatformSelected = false
                                            customPlatform = ""
                                            platform = if (selected) "" else p
                                        }
                                    },
                                    accentColor = themedPaletteColor(idx + 1)
                                )
                            }
                        }
                        AnimatedVisibility(visible = otherPlatformSelected) {
                            NeonGlassTextField(
                                value = customPlatform,
                                onValueChange = {
                                    customPlatform = it
                                    platform = it
                                },
                                label = "Custom Platform",
                                placeholder = "Enter platform name (e.g. MUBI, Crunchyroll)",
                                focusNeonColor = tokens.tertiaryAccent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_platform_input")
                            )
                        }
                    }

                    // Episodic Progress (ONLY shown for Web Series, Anime, K-Drama — hidden for Movie)
                    if (category.isEpisodic) {
                        val catAccent = themedCategoryColor(category)
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = catAccent,
                            contentPadding = PaddingValues(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Season & Episode Progress", style = MaterialTheme.typography.titleSmall, color = catAccent)
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    NeonGlassTextField(
                                        value = currentSeason,
                                        onValueChange = { currentSeason = it.filter(Char::isDigit).take(3) },
                                        label = "Curr Season",
                                        placeholder = "1",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                    NeonGlassTextField(
                                        value = totalSeasons,
                                        onValueChange = { totalSeasons = it.filter(Char::isDigit).take(3) },
                                        label = "Total Seasons",
                                        placeholder = "1",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    NeonGlassTextField(
                                        value = episodesWatched,
                                        onValueChange = { episodesWatched = it.filter(Char::isDigit).take(4) },
                                        label = "Eps Watched",
                                        placeholder = "0",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                    NeonGlassTextField(
                                        value = totalEpisodes,
                                        onValueChange = { totalEpisodes = it.filter(Char::isDigit).take(4) },
                                        label = "Total Eps",
                                        placeholder = "e.g. 12",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // COLLAPSIBLE MORE DETAILS SECTION
                    val chevronRotation by animateFloatAsState(
                        targetValue = if (moreDetailsExpanded) 180f else 0f,
                        animationSpec = if (reducedMotion) snap() else tween(200),
                        label = "more_details_chevron"
                    )
                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = tokens.primaryAccent,
                        cornerRadius = radii.lg,
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .clip(RoundedCornerShape(radii.sm))
                                    .clickable(role = Role.Button) { moreDetailsExpanded = !moreDetailsExpanded }
                                    .padding(horizontal = 4.dp)
                                    .testTag("more_details_toggle"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "More details",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = tokens.textPrimary
                                    )
                                    Text(
                                        text = "Genres, year, runtime, dates, synopsis, cast, collections & notes",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.textSecondary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Rounded.ExpandMore,
                                    contentDescription = if (moreDetailsExpanded) "Collapse more details" else "Expand more details",
                                    tint = tokens.textSecondary,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .graphicsLayer { rotationZ = chevronRotation }
                                )
                            }

                            AnimatedVisibility(
                                visible = moreDetailsExpanded,
                                enter = if (reducedMotion) EnterTransition.None else (fadeIn(tween(180)) + expandVertically(tween(220))),
                                exit = if (reducedMotion) ExitTransition.None else (fadeOut(tween(140)) + shrinkVertically(tween(180)))
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                                    NeonGlassTextField(
                                        value = genre,
                                        onValueChange = { genre = it },
                                        label = "Genres (comma-separated)",
                                        placeholder = "Sci-Fi, Thriller, Drama",
                                        focusNeonColor = tokens.tertiaryAccent,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        POPULAR_GENRES.take(10).forEachIndexed { idx, g ->
                                            val currentList = genre.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                            val selected = currentList.any { it.equals(g, ignoreCase = true) }
                                            NeonGlassChip(
                                                label = g,
                                                selected = selected,
                                                onClick = {
                                                    val updated = if (selected) currentList.filterNot { it.equals(g, true) } else currentList + g
                                                    genre = updated.joinToString(", ")
                                                },
                                                accentColor = themedPaletteColor(idx)
                                            )
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        NeonGlassTextField(
                                            value = releaseYear,
                                            onValueChange = { releaseYear = it.filter(Char::isDigit).take(4) },
                                            label = "Release Year",
                                            placeholder = "e.g. 2024",
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f)
                                        )
                                        NeonGlassTextField(
                                            value = language,
                                            onValueChange = { language = it },
                                            label = "Language",
                                            placeholder = "e.g. English",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        NeonGlassTextField(
                                            value = runtimeMinutes,
                                            onValueChange = { runtimeMinutes = it.filter(Char::isDigit).take(4) },
                                            label = if (category.isEpisodic) "Avg Ep Min" else "Runtime (min)",
                                            placeholder = if (category.isEpisodic) "e.g. 45" else "e.g. 120",
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f)
                                        )
                                        NeonGlassTextField(
                                            value = imdbRating,
                                            onValueChange = { imdbRating = it },
                                            label = "IMDb (0-10)",
                                            placeholder = "e.g. 8.4",
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    // Native DatePickers for Start & Finish Dates
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        DatePickerField(
                                            label = "Start Date",
                                            value = startDate,
                                            placeholder = "Pick start date",
                                            onPick = { activeDatePicker = DatePickerTarget.START },
                                            onClear = { startDate = "" },
                                            modifier = Modifier.weight(1f)
                                        )
                                        DatePickerField(
                                            label = "Finish Date",
                                            value = completionDate,
                                            placeholder = "Pick finish date",
                                            onPick = { activeDatePicker = DatePickerTarget.FINISH },
                                            onClear = { completionDate = "" },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    NeonGlassTextField(
                                        value = synopsis,
                                        onValueChange = { synopsis = it },
                                        label = "Synopsis",
                                        placeholder = "Short story overview...",
                                        singleLine = false,
                                        minLines = 2,
                                        maxLines = 5,
                                        focusNeonColor = tokens.tertiaryAccent,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        NeonGlassTextField(
                                            value = director,
                                            onValueChange = { director = it },
                                            label = "Director / Creator",
                                            modifier = Modifier.weight(1f)
                                        )
                                        NeonGlassTextField(
                                            value = country,
                                            onValueChange = { country = it },
                                            label = "Country",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    NeonGlassTextField(
                                        value = mainCast,
                                        onValueChange = { mainCast = it },
                                        label = "Main Cast",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        NeonGlassTextField(
                                            value = trailerLink,
                                            onValueChange = { trailerLink = it },
                                            label = "Trailer URL",
                                            modifier = Modifier.weight(1f)
                                        )
                                        NeonGlassTextField(
                                            value = imdbLink,
                                            onValueChange = { imdbLink = it },
                                            label = "IMDb URL",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        ToggleRow("Include in Watchlist", watchlistItem) { watchlistItem = it }
                                        if (watchlistItem) {
                                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                WatchlistPriority.entries.forEach { p ->
                                                    NeonGlassChip(
                                                        label = "${p.label} Priority",
                                                        selected = priority == p,
                                                        onClick = { priority = p },
                                                        accentColor = priorityColor(p)
                                                    )
                                                }
                                            }
                                        }
                                        ToggleRow("Favourite", favourite) { favourite = it }
                                        ToggleRow("All-Time Favourite", allTimeFavourite) { allTimeFavourite = it }
                                        ToggleRow("Hidden Gem", hiddenGem) { hiddenGem = it }
                                        ToggleRow("Rewatch Collection", rewatchFlag) { rewatchFlag = it }
                                        if (rewatchFlag) {
                                            NeonGlassTextField(
                                                value = rewatchCount,
                                                onValueChange = { rewatchCount = it.filter(Char::isDigit).take(3) },
                                                label = "Rewatch Count",
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    Text("Mood Tags (up to 6)", style = MaterialTheme.typography.titleSmall, color = tokens.positiveAccent)
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        AVAILABLE_MOODS.forEachIndexed { idx, mood ->
                                            val isSelected = selectedMoods.contains(mood)
                                            NeonGlassChip(
                                                label = mood,
                                                selected = isSelected,
                                                onClick = {
                                                    if (isSelected) selectedMoods.remove(mood)
                                                    else if (selectedMoods.size < 6) selectedMoods.add(mood)
                                                },
                                                accentColor = themedPaletteColor(idx)
                                            )
                                        }
                                    }

                                    NeonGlassTextField(
                                        value = personalReview,
                                        onValueChange = { personalReview = it },
                                        label = "Personal Review",
                                        singleLine = false,
                                        minLines = 3,
                                        maxLines = 6,
                                        focusNeonColor = tokens.secondaryAccent,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    NeonGlassTextField(
                                        value = notes,
                                        onValueChange = { notes = it },
                                        label = "Personal Notes",
                                        singleLine = false,
                                        minLines = 2,
                                        maxLines = 4,
                                        focusNeonColor = tokens.tertiaryAccent,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Fixed Sticky Bottom Save Bar
                HorizontalDivider(color = tokens.outlineSubtle)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(tokens.background.copy(alpha = 0.94f))
                        .padding(horizontal = spacing.space4, vertical = spacing.space3),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassSecondaryButton(
                        onClick = attemptClose,
                        cornerRadius = radii.md,
                        modifier = Modifier.weight(0.38f)
                    ) {
                        Text("Cancel")
                    }
                    NeonPrimaryButton(
                        onClick = performSave,
                        cornerRadius = radii.md,
                        modifier = Modifier
                            .weight(0.62f)
                            .testTag("save_media_button")
                    ) {
                        Icon(Icons.Rounded.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isEdit) "Save Changes" else "Save Title", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (activeDatePicker != null) {
        val currentValue = when (activeDatePicker!!) {
            DatePickerTarget.START -> startDate
            DatePickerTarget.FINISH -> completionDate
        }
        val initialMillis = remember(activeDatePicker, currentValue) {
            parseIsoToUtcMillis(currentValue) ?: System.currentTimeMillis()
        }
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { activeDatePicker = null },
            confirmButton = {
                TextButton(onClick = {
                    val formatted = pickerState.selectedDateMillis?.let { formatUtcMillisToIso(it) } ?: ""
                    when (activeDatePicker!!) {
                        DatePickerTarget.START -> startDate = formatted
                        DatePickerTarget.FINISH -> completionDate = formatted
                    }
                    activeDatePicker = null
                }) {
                    Text("Select", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDatePicker = null }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (showDiscardDialog) {
        NeonGlassDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = "Discard unsaved changes?",
            accentColor = tokens.dangerAccent,
            confirmText = "Discard",
            confirmDangerous = true,
            dismissText = "Keep editing",
            onConfirm = {
                showDiscardDialog = false
                onDismiss()
            }
        ) {
            Text(
                text = "You have unsaved edits for \"${title.ifBlank { "this title" }}\". Leaving now will discard them.",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary
            )
        }
    }
}

@Composable
private fun DatePickerField(
    label: String,
    value: String,
    placeholder: String,
    onPick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val radii = LocalZenixRadii.current
    val shape = RoundedCornerShape(radii.md)
    val bgColor = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color(0xFF0B0717).copy(alpha = 0.80f)
        AppThemeMode.MINIMAL_DARK, AppThemeMode.AMOLED_BLACK -> tokens.tonalContainer
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = tokens.textSecondary,
            modifier = Modifier.padding(start = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .clip(shape)
                .background(bgColor)
                .border(1.dp, tokens.outline, shape)
                .clickable(role = Role.Button, onClick = onPick)
                .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                    tint = tokens.tertiaryAccent,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = value.ifBlank { placeholder },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (value.isBlank()) tokens.textSecondary.copy(alpha = 0.78f) else tokens.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (value.isNotBlank()) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "Clear $label",
                        tint = tokens.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun parseIsoToUtcMillis(iso: String): Long? {
    if (iso.isBlank()) return null
    return runCatching {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(iso.take(10))?.time
    }.getOrNull()
}

private fun formatUtcMillisToIso(millis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(millis))
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val tokens = LocalZenixTokens.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = tokens.textPrimary)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = tokens.primaryButtonText,
                checkedTrackColor = tokens.primaryAccent,
                uncheckedThumbColor = tokens.textSecondary,
                uncheckedTrackColor = tokens.outlineSubtle,
                uncheckedBorderColor = tokens.outline
            )
        )
    }
}
