package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import com.example.data.model.WatchStatus
import com.example.data.model.WatchlistPriority
import com.example.data.tmdb.TmdbMultiResult
import com.example.data.tmdb.TmdbPreferences
import com.example.data.tmdb.TmdbPrefillResult
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalZenixTokens
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
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
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }

    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var category by remember { mutableStateOf(initialItem?.categoryEnum ?: defaultCategory) }
    var status by remember {
        mutableStateOf(
            initialItem?.statusEnum ?: if (defaultWatchlist) WatchStatus.PLAN_TO_WATCH else WatchStatus.WATCHING
        )
    }
    var posterUrl by remember { mutableStateOf(initialItem?.posterUrl ?: "") }
    var genre by remember { mutableStateOf(initialItem?.genre ?: "") }
    var language by remember {
        mutableStateOf(
            initialItem?.language ?: when (defaultCategory) {
                MediaCategory.ANIME -> "Japanese"
                MediaCategory.K_DRAMA -> "Korean"
                else -> "English"
            }
        )
    }
    var releaseYear by remember { mutableStateOf((initialItem?.releaseYear ?: 2024L).toString()) }
    var platform by remember { mutableStateOf(initialItem?.streamingPlatform ?: "Netflix") }
    var imdbRating by remember { mutableStateOf(if ((initialItem?.imdbRating ?: 0.0) > 0.0) initialItem!!.imdbRating.toString() else "") }
    var personalRating by remember { mutableDoubleStateOf(initialItem?.personalRating ?: 0.0) }
    var runtimeMinutes by remember { mutableStateOf((initialItem?.runtimeMinutes ?: if (defaultCategory == MediaCategory.MOVIE) 120L else 45L).toString()) }
    var director by remember { mutableStateOf(initialItem?.director ?: "") }
    var mainCast by remember { mutableStateOf(initialItem?.mainCast ?: "") }
    var country by remember { mutableStateOf(initialItem?.country ?: "") }
    var startDate by remember { mutableStateOf(initialItem?.startDate ?: "") }
    var completionDate by remember { mutableStateOf(initialItem?.completionDate ?: "") }
    var rewatchCount by remember { mutableStateOf((initialItem?.rewatchCount ?: 0L).toString()) }
    var rewatchFlag by remember { mutableStateOf(initialItem?.rewatchFlag ?: false) }
    var trailerLink by remember { mutableStateOf(initialItem?.trailerLink ?: "") }
    var imdbLink by remember { mutableStateOf(initialItem?.imdbLink ?: "") }
    var personalReview by remember { mutableStateOf(initialItem?.personalReview ?: "") }
    var notes by remember { mutableStateOf(initialItem?.notes ?: "") }

    var totalSeasons by remember { mutableStateOf((initialItem?.totalSeasons ?: 1L).toString()) }
    var currentSeason by remember { mutableStateOf((initialItem?.currentSeason ?: 1L).toString()) }
    var totalEpisodes by remember { mutableStateOf((initialItem?.totalEpisodes ?: 10L).toString()) }
    var episodesWatched by remember { mutableStateOf((initialItem?.episodesWatched ?: 0L).toString()) }

    var watchlistItem by remember { mutableStateOf(initialItem?.watchlistItem ?: defaultWatchlist) }
    var priority by remember { mutableStateOf(initialItem?.priorityEnum ?: WatchlistPriority.MEDIUM) }
    var favourite by remember { mutableStateOf(initialItem?.favourite ?: false) }
    var allTimeFavourite by remember { mutableStateOf(initialItem?.allTimeFavourite ?: false) }
    var hiddenGem by remember { mutableStateOf(initialItem?.hiddenGem ?: false) }
    val selectedMoods = remember { mutableStateListOf<String>().apply { addAll(initialItem?.moodTags ?: emptyList()) } }

    var tmdbQuery by remember { mutableStateOf("") }
    var tmdbLoading by remember { mutableStateOf(false) }
    var tmdbResults by remember { mutableStateOf<List<TmdbMultiResult>>(emptyList()) }
    var tmdbMessage by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AfterglowBackdrop(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlassIconButton(onClick = onDismiss, glowColor = tokens.primaryAccent) {
                            Icon(Icons.Rounded.Close, contentDescription = "Close editor", tint = tokens.textPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isEdit) "Edit ${category.label}" else "Add to Zenix",
                            style = MaterialTheme.typography.headlineMedium,
                            color = tokens.textPrimary
                        )
                    }
                    NeonPrimaryButton(
                        onClick = {
                            val cleanTitle = title.trim()
                            if (cleanTitle.isBlank()) {
                                validationError = "Title is required."
                                return@NeonPrimaryButton
                            }
                            val parsedYear = releaseYear.toLongOrNull() ?: 2024L
                            if (parsedYear !in 1880L..2040L) {
                                validationError = "Release year must be between 1880 and 2040."
                                return@NeonPrimaryButton
                            }
                            val parsedImdb = imdbRating.toDoubleOrNull() ?: 0.0
                            if (parsedImdb !in 0.0..10.0) {
                                validationError = "IMDb rating must be between 0.0 and 10.0."
                                return@NeonPrimaryButton
                            }
                            val parsedRuntime = (runtimeMinutes.toLongOrNull() ?: 0L).coerceIn(0L, 2000L)
                            val parsedSeasons = (totalSeasons.toLongOrNull() ?: 1L).coerceIn(0L, 200L)
                            val parsedCurrSeason = (currentSeason.toLongOrNull() ?: 1L).coerceIn(0L, 200L)
                            val parsedTotalEps = (totalEpisodes.toLongOrNull() ?: 1L).coerceIn(0L, 10000L)
                            val parsedWatchedEps = (episodesWatched.toLongOrNull() ?: 0L).coerceIn(0L, 10000L)
                            val parsedRewatches = (rewatchCount.toLongOrNull() ?: 0L).coerceIn(0L, 500L)

                            val finalItem = (initialItem ?: MediaItem()).copy(
                                title = cleanTitle.take(160),
                                mediaCategory = category.name,
                                posterUrl = posterUrl.trim().take(1000),
                                genre = genre.trim().take(160),
                                language = language.trim().take(60),
                                releaseYear = parsedYear,
                                streamingPlatform = platform.trim().take(80),
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
                                totalSeasons = parsedSeasons,
                                currentSeason = parsedCurrSeason,
                                totalEpisodes = parsedTotalEps,
                                episodesWatched = if (status == WatchStatus.COMPLETED && category.isEpisodic && parsedWatchedEps == 0L) parsedTotalEps else parsedWatchedEps,
                                currentEpisode = parsedWatchedEps,
                                watchlistItem = watchlistItem || status == WatchStatus.PLAN_TO_WATCH,
                                priority = priority.name,
                                dateAdded = initialItem?.dateAdded?.ifBlank { today } ?: today,
                                favourite = favourite || allTimeFavourite,
                                allTimeFavourite = allTimeFavourite,
                                hiddenGem = hiddenGem,
                                moodTags = selectedMoods.toList().take(6)
                            )
                            onSave(finalItem, isEdit)
                        },
                        cornerRadius = 14.dp,
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        modifier = Modifier.testTag("save_media_button")
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
                HorizontalDivider(color = tokens.outlineSubtle)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (validationError != null) {
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = tokens.dangerAccent,
                            cornerRadius = 16.dp,
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Text(
                                text = validationError!!,
                                color = tokens.dangerAccent,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

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
                                                    releaseYear = d.releaseYear.toString()
                                                    runtimeMinutes = d.runtimeMinutes.toString()
                                                    director = d.director
                                                    mainCast = d.mainCast
                                                    country = d.country
                                                    if (d.imdbRating > 0) imdbRating = d.imdbRating.toString()
                                                    platform = d.streamingPlatform
                                                    trailerLink = d.trailerLink
                                                    imdbLink = d.imdbLink
                                                    totalSeasons = d.totalSeasons.toString()
                                                    totalEpisodes = d.totalEpisodes.toString()
                                                    if (notes.isBlank()) notes = d.notes
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

                    Text("Watch Status", style = MaterialTheme.typography.titleSmall, color = tokens.secondaryAccent)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        WatchStatus.entries.forEach { st ->
                            NeonGlassChip(
                                label = st.label,
                                selected = status == st,
                                onClick = {
                                    status = st
                                    if (st == WatchStatus.PLAN_TO_WATCH) watchlistItem = true
                                    if (st == WatchStatus.COMPLETED && category.isEpisodic) {
                                        episodesWatched = totalEpisodes
                                    }
                                },
                                accentColor = statusColor(st)
                            )
                        }
                    }

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

                    NeonGlassTextField(
                        value = posterUrl,
                        onValueChange = { posterUrl = it },
                        label = "Poster Image URL",
                        placeholder = "https://...",
                        focusNeonColor = tokens.primaryAccent,
                        modifier = Modifier.fillMaxWidth()
                    )

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
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        NeonGlassTextField(
                            value = language,
                            onValueChange = { language = it },
                            label = "Language",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NeonGlassTextField(
                            value = runtimeMinutes,
                            onValueChange = { runtimeMinutes = it.filter(Char::isDigit).take(4) },
                            label = if (category.isEpisodic) "Avg Ep Min" else "Runtime (min)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        NeonGlassTextField(
                            value = imdbRating,
                            onValueChange = { imdbRating = it },
                            label = "IMDb (0-10)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = tokens.ratingGold,
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Personal Rating", style = MaterialTheme.typography.titleSmall, color = tokens.ratingGold)
                                Text(
                                    text = if (personalRating <= 0.0) "Unrated" else String.format(Locale.US, "%.1f / 10", personalRating),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = tokens.textPrimary
                                )
                            }
                            if (personalRating > 0.0) {
                                NeonRatingStars(ratingOutOfTen = personalRating, starSize = 18.dp)
                            }
                            Slider(
                                value = personalRating.toFloat(),
                                onValueChange = { personalRating = ((it * 2f).roundToInt() / 2.0) },
                                valueRange = 0f..10f,
                                steps = 19,
                                colors = SliderDefaults.colors(
                                    thumbColor = tokens.secondaryAccent,
                                    activeTrackColor = tokens.primaryAccent,
                                    inactiveTrackColor = tokens.outlineSubtle
                                )
                            )
                        }
                    }

                    NeonGlassTextField(
                        value = platform,
                        onValueChange = { platform = it },
                        label = "Streaming Platform",
                        focusNeonColor = tokens.tertiaryAccent,
                        modifier = Modifier.fillMaxWidth()
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        POPULAR_PLATFORMS.take(7).forEachIndexed { idx, p ->
                            NeonGlassChip(
                                label = p,
                                selected = platform.equals(p, ignoreCase = true),
                                onClick = { platform = p },
                                accentColor = themedPaletteColor(idx + 1)
                            )
                        }
                    }

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
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                    NeonGlassTextField(
                                        value = totalSeasons,
                                        onValueChange = { totalSeasons = it.filter(Char::isDigit).take(3) },
                                        label = "Total Seasons",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    NeonGlassTextField(
                                        value = episodesWatched,
                                        onValueChange = { episodesWatched = it.filter(Char::isDigit).take(4) },
                                        label = "Eps Watched",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                    NeonGlassTextField(
                                        value = totalEpisodes,
                                        onValueChange = { totalEpisodes = it.filter(Char::isDigit).take(4) },
                                        label = "Total Eps",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

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
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = "Start (YYYY-MM-DD)",
                            modifier = Modifier.weight(1f)
                        )
                        NeonGlassTextField(
                            value = completionDate,
                            onValueChange = { completionDate = it },
                            label = "Finish (YYYY-MM-DD)",
                            modifier = Modifier.weight(1f)
                        )
                    }
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

                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = tokens.dangerAccent,
                        contentPadding = PaddingValues(14.dp)
                    ) {
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

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
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
