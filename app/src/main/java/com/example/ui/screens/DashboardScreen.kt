package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DashboardStats
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.UserProfile
import com.example.data.model.WatchStatus
import com.example.data.model.formatWatchTime
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.LiquidGlassHeroCard
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassChip
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.NeonProgressBar
import com.example.ui.components.PosterArtwork
import com.example.ui.components.RatingPill
import com.example.ui.components.StaggeredFadeItem
import com.example.ui.components.rememberAnimatedIntCount
import com.example.ui.components.rememberAnimatedLongCount
import com.example.ui.components.themedCategoryColor
import com.example.ui.components.themedPaletteColor
import com.example.ui.components.zenixFocusRing
import com.example.ui.components.zenixHeading
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    profile: UserProfile,
    stats: DashboardStats,
    onOpenAddPicker: () -> Unit,
    onSelectItem: (MediaItem) -> Unit,
    onStepEpisode: (MediaItem, Int) -> Unit,
    @Suppress("UNUSED_PARAMETER") onToggleFavourite: (MediaItem) -> Unit,
    onOpenWatchlist: () -> Unit,
    onOpenSmartHub: (Int) -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenRankings: () -> Unit = {},
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val haptics = LocalHapticFeedback.current

    val spotlightItem = stats.nextUpTitle
    val isContinuing = spotlightItem?.statusEnum == WatchStatus.WATCHING
    val spotlightLabel = when (spotlightItem?.statusEnum) {
        WatchStatus.WATCHING -> "CONTINUE WATCHING"
        WatchStatus.ON_HOLD -> "ON HOLD"
        else -> "NEXT UP"
    }

    val completionRatio = remember(stats.totalTitles, stats.completedCount) {
        if (stats.totalTitles > 0) {
            (stats.completedCount.toFloat() / stats.totalTitles.toFloat()).coerceIn(0f, 1f)
        } else 0f
    }

    val animatedWatchTime = rememberAnimatedLongCount(stats.totalWatchTimeMinutes)
    val animatedTotalTitles = rememberAnimatedIntCount(stats.totalTitles)
    val animatedCompleted = rememberAnimatedIntCount(stats.completedCount)
    val animatedMovies = rememberAnimatedIntCount(stats.totalMovies)
    val animatedSeries = rememberAnimatedIntCount(stats.totalWebSeries)
    val animatedAnime = rememberAnimatedIntCount(stats.totalAnime)
    val animatedKDrama = rememberAnimatedIntCount(stats.totalKDrama)
    val animatedPct = rememberAnimatedIntCount((completionRatio * 100f).roundToInt())

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = spacing.space4,
            end = spacing.space4,
            top = spacing.space2,
            bottom = bottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.space4)
    ) {
        // 1. "Welcome back" + user name (large)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.textSecondary
                )
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.displayLarge,
                    color = tokens.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.zenixHeading()
                )
            }
        }

        // 2. CONTINUE WATCHING / NEXT UP card
        item {
            val spotlightAccent = if (isContinuing) NeonCyan else NeonAmber
            LiquidGlassHeroCard(
                accentColor = spotlightAccent,
                contentPadding = PaddingValues(spacing.space4)
            ) {
                if (spotlightItem != null) {
                    val ratingLabel = if (spotlightItem.personalRating > 0.0) {
                        "★ ${String.format(Locale.US, "%.1f", spotlightItem.personalRating)}"
                    } else if (spotlightItem.imdbRating > 0.0) {
                        "★ ${String.format(Locale.US, "%.1f", spotlightItem.imdbRating)}"
                    } else {
                        null
                    }
                    val metaText = if (spotlightItem.categoryEnum.isEpisodic) {
                        listOfNotNull(
                            spotlightItem.categoryEnum.label,
                            spotlightItem.progressHint()
                        ).joinToString(" · ")
                    } else {
                        listOfNotNull(
                            spotlightItem.categoryEnum.label,
                            spotlightItem.formattedYearOrBlank().takeIf { it.isNotBlank() },
                            ratingLabel ?: "Unrated"
                        ).joinToString(" · ")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space3),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PosterArtwork(
                            posterUrl = spotlightItem.posterUrl,
                            title = spotlightItem.title,
                            category = spotlightItem.categoryEnum,
                            modifier = Modifier.size(width = 84.dp, height = 122.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(spacing.space2)
                        ) {
                            Text(
                                text = spotlightLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp,
                                color = spotlightAccent
                            )
                            Text(
                                text = spotlightItem.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = tokens.textPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = metaText,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isContinuing && spotlightItem.categoryEnum.isEpisodic) NeonCyan else tokens.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NeonPrimaryButton(
                                    onClick = { onSelectItem(spotlightItem) },
                                    accentColor = NeonPurple,
                                    cornerRadius = radii.sm,
                                    contentPadding = PaddingValues(horizontal = spacing.space4, vertical = spacing.space2),
                                    modifier = Modifier.testTag("continue_watching_view_details")
                                ) {
                                    Text("View details", fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                                val canStepEp = spotlightItem.categoryEnum.isEpisodic &&
                                    (spotlightItem.totalEpisodes == 0L || spotlightItem.episodesWatched < spotlightItem.totalEpisodes)
                                if (canStepEp) {
                                    GlassSecondaryButton(
                                        onClick = {
                                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onStepEpisode(spotlightItem, 1)
                                        },
                                        accentBorderColor = NeonCyan,
                                        cornerRadius = radii.sm,
                                        contentPadding = PaddingValues(horizontal = spacing.space3, vertical = spacing.space2),
                                        modifier = Modifier.testTag("continue_watching_plus_ep")
                                    ) {
                                        Text("+1 Ep", fontWeight = FontWeight.Bold, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space3),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PosterArtwork(
                            posterUrl = "",
                            title = "ZENIX",
                            category = MediaCategory.MOVIE,
                            modifier = Modifier.size(width = 84.dp, height = 122.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(spacing.space2)
                        ) {
                            Text(
                                text = "NEXT UP",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp,
                                color = NeonAmber
                            )
                            Text(
                                text = "Your vault is ready",
                                style = MaterialTheme.typography.titleLarge,
                                color = tokens.textPrimary
                            )
                            Text(
                                text = "Log your first movie, series, anime, or K-drama to get started.",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary,
                                maxLines = 2
                            )
                            NeonPrimaryButton(
                                onClick = onOpenAddPicker,
                                accentColor = NeonPurple,
                                cornerRadius = radii.sm,
                                contentPadding = PaddingValues(horizontal = spacing.space4, vertical = spacing.space2),
                                modifier = Modifier.testTag("top_pick_add_first")
                            ) {
                                Text("Add first title", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. TOTAL WATCH TIME card
        item {
            NeonGlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = NeonPurple,
                cornerRadius = radii.lg,
                contentPadding = PaddingValues(spacing.space4)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    Text(
                        text = "TOTAL WATCH TIME",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.3.sp,
                        color = tokens.textSecondary
                    )
                    Text(
                        text = "${formatWatchTime(animatedWatchTime)} across $animatedTotalTitles ${if (stats.totalTitles == 1) "title" else "titles"}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                    NeonProgressBar(
                        progress = completionRatio,
                        height = 10.dp,
                        startColor = NeonPurple,
                        endColor = NeonCyan
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$animatedCompleted completed",
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.textSecondary
                        )
                        Text(
                            text = "$animatedPct%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                }
            }
        }

        // 4. Single "+ Add" button -> opens category bottom sheet
        item {
            NeonPrimaryButton(
                onClick = onOpenAddPicker,
                accentColor = NeonPurple,
                cornerRadius = radii.md,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_add_button")
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(spacing.space2))
                Text("Add", fontWeight = FontWeight.Bold)
            }
        }

        // 5. Stats cells (Titles, Movies, Series + Anime, K-Drama)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.space2)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                ) {
                    DashboardStatCell(
                        label = "Titles",
                        value = animatedTotalTitles.toString(),
                        accentColor = NeonPurple,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCell(
                        label = "Movies",
                        value = animatedMovies.toString(),
                        accentColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCell(
                        label = "Series",
                        value = animatedSeries.toString(),
                        accentColor = NeonPink,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                ) {
                    DashboardStatCell(
                        label = "Anime",
                        value = animatedAnime.toString(),
                        accentColor = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCell(
                        label = "K-Drama",
                        value = animatedKDrama.toString(),
                        accentColor = NeonAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 6. Three pill buttons: Random Pick, Watchlist, Statistics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.space2)
            ) {
                NeonGlassChip(
                    label = "Random Pick",
                    selected = false,
                    onClick = { onOpenSmartHub(0) },
                    accentColor = NeonPurple,
                    leadingIcon = Icons.Rounded.Casino,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_random_picker_chip")
                )
                NeonGlassChip(
                    label = "Watchlist",
                    selected = false,
                    onClick = onOpenWatchlist,
                    accentColor = NeonCyan,
                    leadingIcon = Icons.Rounded.Bookmark,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_watchlist_chip")
                )
                NeonGlassChip(
                    label = "Statistics",
                    selected = false,
                    onClick = onOpenStatistics,
                    accentColor = NeonPink,
                    leadingIcon = Icons.Rounded.BarChart,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_analytics_chip")
                )
            }
        }

        // 8. "Top rated" list with "See all" and 50ms staggered fade-in
        item {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top rated",
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.textPrimary,
                        modifier = Modifier.zenixHeading()
                    )
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                            .clip(RoundedCornerShape(radii.sm))
                            .zenixFocusRing(NeonCyan, radii.sm)
                            .clickable(role = Role.Button, onClick = onOpenRankings)
                            .padding(horizontal = spacing.space3, vertical = spacing.space2)
                            .testTag("dashboard_see_all_rankings"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "See all",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonCyan
                        )
                    }
                }

                if (stats.top5Rated.isEmpty()) {
                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = NeonAmber,
                        cornerRadius = radii.lg,
                        contentPadding = PaddingValues(spacing.space4)
                    ) {
                        Text(
                            text = "Rate your watched titles to build your personal Top Rated leaderboard.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.textSecondary
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                        stats.top5Rated.forEachIndexed { idx, item ->
                            val rankColor = themedPaletteColor(idx)
                            StaggeredFadeItem(index = idx) {
                                NeonGlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    accentColor = rankColor,
                                    cornerRadius = radii.lg,
                                    onClick = { onSelectItem(item) },
                                    contentPadding = PaddingValues(spacing.space3)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                                    ) {
                                        Text(
                                            text = "${idx + 1}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = rankColor,
                                            modifier = Modifier.width(24.dp)
                                        )
                                        PosterArtwork(
                                            posterUrl = item.posterUrl,
                                            title = item.title,
                                            category = item.categoryEnum,
                                            modifier = Modifier.size(width = 46.dp, height = 64.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = tokens.textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            val topMeta = listOfNotNull(
                                                item.categoryEnum.label,
                                                item.formattedYearOrBlank().takeIf { it.isNotBlank() }
                                            ).joinToString(" · ")
                                            Text(
                                                text = topMeta,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = tokens.textSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        RatingPill(rating = item.personalRating)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 9. "Recently added": horizontal row of 3 poster cards with staggered fade-in
        if (stats.recentlyAdded.isNotEmpty()) {
            item {
                val recentThree = remember(stats.recentlyAdded) { stats.recentlyAdded.take(3) }
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    Text(
                        text = "Recently added",
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.textPrimary,
                        modifier = Modifier.zenixHeading()
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.space3)) {
                        itemsIndexed(recentThree, key = { _, it -> "ra_${it.id}" }) { idx, item ->
                            StaggeredFadeItem(index = idx) {
                                RecentPosterCard(item = item, onClick = { onSelectItem(item) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardStatCell(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    NeonGlassCard(
        modifier = modifier,
        accentColor = accentColor,
        cornerRadius = radii.md,
        glowAlpha = 0.22f,
        contentPadding = PaddingValues(horizontal = spacing.space2, vertical = spacing.space3)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.space1)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = tokens.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RecentPosterCard(
    item: MediaItem,
    onClick: () -> Unit
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val accent = themedCategoryColor(item.categoryEnum)
    val yearPart = item.formattedYearOrBlank().takeIf { it.isNotBlank() }
    val ratingPart = if (item.personalRating > 0.0) {
        "★ ${String.format(Locale.US, "%.1f", item.personalRating)}"
    } else null
    val metaLine = listOfNotNull(yearPart, ratingPart).joinToString(" · ").ifBlank { item.categoryEnum.label }

    NeonGlassCard(
        modifier = Modifier.width(126.dp),
        accentColor = accent,
        cornerRadius = radii.lg,
        onClick = onClick,
        contentPadding = PaddingValues(spacing.space3)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.space2)) {
            PosterArtwork(
                posterUrl = item.posterUrl,
                title = item.title,
                category = item.categoryEnum,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(142.dp)
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = tokens.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = metaLine,
                style = MaterialTheme.typography.labelSmall,
                color = if (item.personalRating > 0.0) NeonAmber else tokens.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
