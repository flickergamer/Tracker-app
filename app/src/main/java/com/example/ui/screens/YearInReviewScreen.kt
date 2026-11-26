package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.UserProfile
import com.example.data.model.WatchStatus
import com.example.ui.components.AfterglowBackdrop
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.HorizontalScrollFadeBox
import com.example.ui.components.LiquidGlassHeroCard
import com.example.ui.components.NeonGlassChip
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.ZenixEmptyState
import com.example.ui.components.zenixHeading
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.util.ShareCardRenderer
import com.example.ui.util.YearReviewSlideData
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun YearInReviewScreen(
    allItems: List<MediaItem>,
    profile: UserProfile,
    onBack: () -> Unit,
    onNotify: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    var busy by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)

    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR).toString() }
    val availableYears = remember(allItems, currentYear) {
        val yrs = allItems.mapNotNull { item ->
            val raw = item.completionDate.ifBlank { item.dateAdded }
            raw.take(4).takeIf { it.length == 4 && it.all(Char::isDigit) }
        }.toMutableSet()
        yrs.add(currentYear)
        yrs.sortedDescending()
    }
    var selectedYear by remember(availableYears) { mutableStateOf(availableYears.firstOrNull() ?: currentYear) }

    val yearItems = remember(allItems, selectedYear) {
        allItems.filter { item ->
            val raw = item.completionDate.ifBlank { item.dateAdded }
            raw.startsWith(selectedYear) || (raw.isBlank() && selectedYear == currentYear)
        }
    }

    val slides = remember(yearItems, selectedYear) {
        buildYearInReviewSlides(yearItems, selectedYear)
    }

    AfterglowBackdrop(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = spacing.space4, vertical = spacing.space2),
            verticalArrangement = Arrangement.spacedBy(spacing.space3)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.space3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(
                    onClick = onBack,
                    glowColor = tokens.primaryAccent,
                    modifier = Modifier.testTag("year_review_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = tokens.textPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$selectedYear in Review",
                        style = MaterialTheme.typography.displayMedium,
                        color = tokens.textPrimary,
                        modifier = Modifier.zenixHeading()
                    )
                    Text(
                        text = "Swipe story cards · ${yearItems.size} titles tracked",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary
                    )
                }
            }

            // Year chips
            val yearScrollState = rememberScrollState()
            HorizontalScrollFadeBox(scrollState = yearScrollState, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.horizontalScroll(yearScrollState),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                ) {
                    availableYears.forEach { yr ->
                        NeonGlassChip(
                            label = yr,
                            selected = selectedYear == yr,
                            onClick = { selectedYear = yr },
                            accentColor = NeonPurple
                        )
                    }
                }
            }

            if (yearItems.isEmpty()) {
                ZenixEmptyState(
                    icon = Icons.Rounded.AutoAwesome,
                    title = "No $selectedYear Story Yet",
                    subtitle = "Add or complete titles in $selectedYear to unlock your personalized Year in Review.",
                    accentColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
            } else {
                val pagerState = rememberPagerState(pageCount = { slides.size })

                // Story step progress indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    slides.forEachIndexed { idx, slide ->
                        val active = idx <= pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (active) Color(slide.accentHex)
                                    else tokens.outlineSubtle
                                )
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("year_review_pager"),
                    pageSpacing = spacing.space3
                ) { page ->
                    val slide = slides[page]
                    val accent = Color(slide.accentHex)
                    LiquidGlassHeroCard(
                        accentColor = accent,
                        contentPadding = PaddingValues(spacing.space5),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(accent.copy(alpha = 0.20f))
                                        .border(1.dp, accent.copy(alpha = 0.75f), RoundedCornerShape(50))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = slide.eyebrow.uppercase(Locale.US),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        color = accent
                                    )
                                }

                                Text(
                                    text = slide.headline,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = tokens.textSecondary
                                )

                                Spacer(modifier = Modifier.height(spacing.space2))

                                Text(
                                    text = slide.valueText,
                                    style = MaterialTheme.typography.displayLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.textPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(radii.md))
                                    .background(Color.White.copy(alpha = 0.06f))
                                    .border(1.dp, tokens.outlineSubtle, RoundedCornerShape(radii.md))
                                    .padding(spacing.space4)
                            ) {
                                Text(
                                    text = slide.detailText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = tokens.textPrimary
                                )
                            }
                        }
                    }
                }

                val currentSlide = slides[pagerState.currentPage.coerceIn(0, slides.lastIndex)]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                ) {
                    GlassSecondaryButton(
                        onClick = {
                            if (!busy) {
                                scope.launch {
                                    busy = true
                                    val bmp = ShareCardRenderer.renderYearInReviewBitmap(
                                        context = context,
                                        yearLabel = selectedYear,
                                        displayName = profile.displayName,
                                        slide = currentSlide
                                    )
                                    ShareCardRenderer.saveBitmapToGallery(
                                        context,
                                        bmp,
                                        "zenix_${selectedYear}_review_${pagerState.currentPage + 1}"
                                    ).onSuccess { onNotify(it) }.onFailure { onNotify("Save failed: ${it.localizedMessage}") }
                                    busy = false
                                }
                            }
                        },
                        accentBorderColor = Color(currentSlide.accentHex),
                        cornerRadius = radii.md,
                        modifier = Modifier
                            .weight(0.45f)
                            .testTag("year_review_save_button")
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Image")
                    }

                    NeonPrimaryButton(
                        onClick = {
                            if (!busy) {
                                scope.launch {
                                    busy = true
                                    val bmp = ShareCardRenderer.renderYearInReviewBitmap(
                                        context = context,
                                        yearLabel = selectedYear,
                                        displayName = profile.displayName,
                                        slide = currentSlide
                                    )
                                    val fname = "zenix_${selectedYear}_review_${pagerState.currentPage + 1}"
                                    ShareCardRenderer.shareBitmap(
                                        context = context,
                                        bitmap = bmp,
                                        fileName = fname,
                                        shareSubject = "My $selectedYear ZENIX Year in Review"
                                    ).onFailure {
                                        ShareCardRenderer.saveBitmapToGallery(context, bmp, fname)
                                            .onSuccess { onNotify(it) }
                                            .onFailure { err -> onNotify("Share failed: ${err.localizedMessage}") }
                                    }
                                    busy = false
                                }
                            }
                        },
                        accentColor = Color(currentSlide.accentHex),
                        cornerRadius = radii.md,
                        modifier = Modifier
                            .weight(0.55f)
                            .testTag("year_review_share_button")
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Card", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun buildYearInReviewSlides(items: List<MediaItem>, year: String): List<YearReviewSlideData> {
    if (items.isEmpty()) return emptyList()

    val totalMinutes = items.sumOf { it.watchedTimeMinutes }
    val totalHours = totalMinutes / 60L
    val remMins = totalMinutes % 60L
    val totalEpisodes = items.filter { it.categoryEnum.isEpisodic }.sumOf { it.episodesWatched }

    val completedItems = items.filter { it.statusEnum == WatchStatus.COMPLETED }
    val compMovies = completedItems.count { it.categoryEnum == MediaCategory.MOVIE }
    val compSeries = completedItems.count { it.categoryEnum == MediaCategory.WEB_SERIES }
    val compAnime = completedItems.count { it.categoryEnum == MediaCategory.ANIME }
    val compKDrama = completedItems.count { it.categoryEnum == MediaCategory.K_DRAMA }

    val genreCounts = mutableMapOf<String, Int>()
    items.forEach { item ->
        item.genreList.forEach { g ->
            genreCounts[g] = (genreCounts[g] ?: 0) + 1
        }
    }
    val topGenres = genreCounts.entries.sortedByDescending { it.value }
    val topGenreName = topGenres.firstOrNull()?.key ?: "Eclectic"
    val runnerUpGenres = topGenres.drop(1).take(2).joinToString(" · ") { "${it.key} (${it.value})" }

    val platformCounts = items
        .map { it.normalizedPlatform }
        .filter { it.isNotBlank() }
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
    val topPlatform = platformCounts.firstOrNull()

    val highestRated = items
        .filter { it.personalRating > 0.0 }
        .maxWithOrNull(compareBy<MediaItem> { it.personalRating }.thenBy { it.imdbRating })

    val longestSeries = items
        .filter { it.categoryEnum.isEpisodic }
        .maxByOrNull { it.episodesWatched * 10000L + it.watchedTimeMinutes }

    val monthKeyFmt = SimpleDateFormat("yyyy-MM", Locale.US)
    val monthFullFmt = SimpleDateFormat("MMMM", Locale.US)
    val monthCounts = mutableMapOf<String, Int>()
    items.forEach { item ->
        val raw = item.completionDate.ifBlank { item.dateAdded }
        if (raw.length >= 7) {
            val ym = raw.take(7)
            monthCounts[ym] = (monthCounts[ym] ?: 0) + 1
        }
    }
    val busiestEntry = monthCounts.entries.maxByOrNull { it.value }
    val busiestMonthName = busiestEntry?.key?.let { ym ->
        runCatching { monthKeyFmt.parse(ym)?.let { monthFullFmt.format(it) } }.getOrNull() ?: ym
    } ?: "—"

    return listOf(
        YearReviewSlideData(
            eyebrow = "Watch Time",
            headline = "Your screen time in $year",
            valueText = if (totalHours > 0L) "${totalHours}h ${remMins}m" else "${totalMinutes}m",
            detailText = "Across ${items.size} titles and $totalEpisodes watched episodes.",
            accentHex = NeonPurple.toArgb()
        ),
        YearReviewSlideData(
            eyebrow = "Completed",
            headline = "Stories you finished",
            valueText = "${completedItems.size} Titles",
            detailText = "$compMovies Movies · $compSeries Web Series · $compAnime Anime · $compKDrama K-Drama.",
            accentHex = NeonCyan.toArgb()
        ),
        YearReviewSlideData(
            eyebrow = "Top Genre",
            headline = "What pulled you in most",
            valueText = topGenreName,
            detailText = if (runnerUpGenres.isNotBlank()) "Followed by $runnerUpGenres." else "Your signature vibe for $year.",
            accentHex = NeonPink.toArgb()
        ),
        YearReviewSlideData(
            eyebrow = "Top Platform",
            headline = "Where you streamed most",
            valueText = topPlatform?.key ?: "Unspecified",
            detailText = if (topPlatform != null) "${topPlatform.value} titles streamed here in $year." else "Tag streaming platforms on titles to see your favorite home.",
            accentHex = NeonGreen.toArgb()
        ),
        YearReviewSlideData(
            eyebrow = "Highest Rated",
            headline = "Your peak pick of $year",
            valueText = highestRated?.title ?: "Unrated",
            detailText = if (highestRated != null) {
                "Scored ★ ${String.format(Locale.US, "%.1f", highestRated.personalRating)}/10 · ${highestRated.categoryEnum.label}."
            } else {
                "Rate your watched titles to crown your #1 pick."
            },
            accentHex = NeonAmber.toArgb()
        ),
        YearReviewSlideData(
            eyebrow = "Longest Series",
            headline = "Your biggest commitment",
            valueText = longestSeries?.title ?: "Movies Focus",
            detailText = if (longestSeries != null) {
                "${longestSeries.episodesWatched} episodes across ${longestSeries.totalSeasons} season(s)."
            } else {
                "No episodic series logged in $year yet."
            },
            accentHex = NeonCyan.toArgb()
        ),
        YearReviewSlideData(
            eyebrow = "Busiest Month",
            headline = "Peak binge energy",
            valueText = busiestMonthName,
            detailText = if (busiestEntry != null) "${busiestEntry.value} titles logged or finished during $busiestMonthName." else "Add watch dates to uncover your busiest month.",
            accentHex = NeonPink.toArgb()
        )
    )
}
