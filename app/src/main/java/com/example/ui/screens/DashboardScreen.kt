package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DashboardStats
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.UserProfile
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
import com.example.ui.components.neonGlow
import com.example.ui.components.themedCategoryColor
import com.example.ui.components.themedPaletteColor
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
    onAddTitle: (MediaCategory, Boolean) -> Unit,
    onSelectItem: (MediaItem) -> Unit,
    @Suppress("UNUSED_PARAMETER") onStepEpisode: (MediaItem, Int) -> Unit,
    @Suppress("UNUSED_PARAMETER") onToggleFavourite: (MediaItem) -> Unit,
    onOpenWatchlist: () -> Unit,
    onOpenSmartHub: (Int) -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenRankings: () -> Unit = {},
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val topPick = stats.highestRatedTitle
        ?: stats.currentlyWatching.firstOrNull()
        ?: stats.recentlyAdded.firstOrNull()

    val completionRatio = if (stats.totalTitles > 0) {
        (stats.completedCount.toFloat() / stats.totalTitles.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val completionPct = (completionRatio * 100f).roundToInt()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. "Welcome back" + user name (large)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 2. TOP PICK card: poster placeholder, amber "TOP PICK", title, "Type · year · rating", purple "View details"
        item {
            LiquidGlassHeroCard(
                accentColor = NeonAmber,
                contentPadding = PaddingValues(16.dp)
            ) {
                if (topPick != null) {
                    val ratingLabel = if (topPick.personalRating > 0.0) {
                        "★ ${String.format(Locale.US, "%.1f", topPick.personalRating)}"
                    } else if (topPick.imdbRating > 0.0) {
                        "★ ${String.format(Locale.US, "%.1f", topPick.imdbRating)}"
                    } else {
                        "Unrated"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PosterArtwork(
                            posterUrl = topPick.posterUrl,
                            title = topPick.title,
                            category = topPick.categoryEnum,
                            modifier = Modifier.size(width = 84.dp, height = 122.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "TOP PICK",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp,
                                color = NeonAmber
                            )
                            Text(
                                text = topPick.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = tokens.textPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${topPick.categoryEnum.label} · ${topPick.releaseYear} · $ratingLabel",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            NeonPrimaryButton(
                                onClick = { onSelectItem(topPick) },
                                accentColor = NeonPurple,
                                cornerRadius = 14.dp,
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                                modifier = Modifier.testTag("top_pick_view_details")
                            ) {
                                Text("View details", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
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
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "TOP PICK",
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
                                text = "Log your first movie or series to showcase your top pick.",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            NeonPrimaryButton(
                                onClick = { onAddTitle(MediaCategory.MOVIE, false) },
                                accentColor = NeonPurple,
                                cornerRadius = 14.dp,
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                                modifier = Modifier.testTag("top_pick_add_first")
                            ) {
                                Text("Add first title", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. TOTAL WATCH TIME card: big "Xd Xh across N titles", gradient progress bar (purple -> cyan), "X completed" left, "X%" right
        item {
            NeonGlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = NeonPurple,
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(
                    text = "TOTAL WATCH TIME",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.3.sp,
                    color = tokens.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${formatWatchTime(stats.totalWatchTimeMinutes)} across ${stats.totalTitles} ${if (stats.totalTitles == 1) "title" else "titles"}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = tokens.textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                NeonProgressBar(
                    progress = completionRatio,
                    height = 10.dp,
                    startColor = NeonPurple,
                    endColor = NeonCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stats.completedCount} completed",
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.textSecondary
                    )
                    Text(
                        text = "$completionPct%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
        }

        // 4. Two buttons: "Add Movie" (purple filled), "Add Series" (cyan outline)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeonPrimaryButton(
                    onClick = { onAddTitle(MediaCategory.MOVIE, false) },
                    accentColor = NeonPurple,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_add_movie")
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Movie", fontWeight = FontWeight.Bold, maxLines = 1)
                }
                GlassSecondaryButton(
                    onClick = { onAddTitle(MediaCategory.WEB_SERIES, false) },
                    accentBorderColor = NeonCyan,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_add_series")
                ) {
                    Icon(Icons.Rounded.Tv, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Series", fontWeight = FontWeight.SemiBold, maxLines = 1)
                }
            }
        }

        // 5. Stats row (4 cells): Titles (purple), Movies (cyan), Series (pink), Anime/KD (green)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DashboardStatCell(
                    label = "Titles",
                    value = stats.totalTitles.toString(),
                    accentColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
                DashboardStatCell(
                    label = "Movies",
                    value = stats.totalMovies.toString(),
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                DashboardStatCell(
                    label = "Series",
                    value = stats.totalWebSeries.toString(),
                    accentColor = NeonPink,
                    modifier = Modifier.weight(1f)
                )
                DashboardStatCell(
                    label = "Anime/KD",
                    value = stats.totalAnimeAndKDrama.toString(),
                    accentColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 6. Three pill buttons: Random Pick, Watchlist, Analytics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    label = "Analytics",
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

        // 7. "Watch time (minutes)" card with 3 colored progress bars
        item {
            val animeMins = stats.animeWatchTimeMinutes.coerceAtLeast(0L)
            val movieMins = stats.moviesWatchTimeMinutes.coerceAtLeast(0L)
            val seriesMins = stats.seriesWatchTimeMinutes.coerceAtLeast(0L)
            val maxMins = maxOf(animeMins, movieMins, seriesMins, 1L).toFloat()

            NeonGlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = NeonCyan,
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(
                    text = "Watch time (minutes)",
                    style = MaterialTheme.typography.titleLarge,
                    color = tokens.textPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    WatchTimeBarRow(
                        label = "Anime & K-Drama",
                        minutes = animeMins,
                        progress = animeMins.toFloat() / maxMins,
                        startColor = NeonGreen,
                        endColor = NeonCyan
                    )
                    WatchTimeBarRow(
                        label = "Movies",
                        minutes = movieMins,
                        progress = movieMins.toFloat() / maxMins,
                        startColor = NeonCyan,
                        endColor = NeonCyan
                    )
                    WatchTimeBarRow(
                        label = "Web Series",
                        minutes = seriesMins,
                        progress = seriesMins.toFloat() / maxMins,
                        startColor = NeonPink,
                        endColor = NeonPink
                    )
                }
            }
        }

        // 8. "Top rated" list with "See all"
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top rated",
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.textPrimary
                    )
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onOpenRankings)
                            .padding(horizontal = 10.dp, vertical = 8.dp)
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
                        cornerRadius = 24.dp,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(
                            text = "Rate your watched titles to build your personal Top Rated leaderboard.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.textSecondary
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        stats.top5Rated.forEachIndexed { idx, item ->
                            val rankColor = themedPaletteColor(idx)
                            NeonGlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = rankColor,
                                cornerRadius = 24.dp,
                                onClick = { onSelectItem(item) },
                                contentPadding = PaddingValues(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                                        Text(
                                            text = "${item.categoryEnum.label} · ${item.releaseYear}",
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

        // 9. "Recently added": horizontal row of 3 poster cards
        if (stats.recentlyAdded.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Recently added",
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.textPrimary
                    )
                    val recentThree = stats.recentlyAdded.take(3)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(recentThree, key = { "ra_${it.id}" }) { item ->
                            RecentPosterCard(item = item, onClick = { onSelectItem(item) })
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
    NeonGlassCard(
        modifier = modifier,
        accentColor = accentColor,
        cornerRadius = 20.dp,
        glowAlpha = 0.22f,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
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
private fun WatchTimeBarRow(
    label: String,
    minutes: Long,
    progress: Float,
    startColor: Color,
    endColor: Color
) {
    val tokens = LocalZenixTokens.current
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textPrimary
            )
            Text(
                text = "${minutes}m",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = endColor
            )
        }
        NeonProgressBar(
            progress = progress,
            height = 8.dp,
            startColor = startColor,
            endColor = endColor
        )
    }
}

@Composable
private fun RecentPosterCard(
    item: MediaItem,
    onClick: () -> Unit
) {
    val tokens = LocalZenixTokens.current
    val accent = themedCategoryColor(item.categoryEnum)
    val metaLine = if (item.personalRating > 0.0) {
        "${item.releaseYear} · ★ ${String.format(Locale.US, "%.1f", item.personalRating)}"
    } else {
        "${item.releaseYear}"
    }

    NeonGlassCard(
        modifier = Modifier.width(126.dp),
        accentColor = accent,
        cornerRadius = 24.dp,
        onClick = onClick,
        contentPadding = PaddingValues(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
