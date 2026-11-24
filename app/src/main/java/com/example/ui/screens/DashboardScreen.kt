package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.DashboardStats
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.UserProfile
import com.example.data.model.formatWatchTime
import com.example.ui.components.ChartSlice
import com.example.ui.components.DonutChartCard
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.HorizontalBarChartCard
import com.example.ui.components.LiquidGlassHeroCard
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassChip
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.PosterArtwork
import com.example.ui.components.RatingPill
import com.example.ui.components.SeriesProgressCard
import com.example.ui.components.StatKpiCard
import com.example.ui.components.StatusPill
import com.example.ui.components.categoryNeonColor
import com.example.ui.components.neonPaletteColor
import com.example.ui.theme.GoldRating
import com.example.ui.theme.MutedGreyText
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonFuchsia
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OffWhiteText
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDropped
import com.example.ui.theme.StatusOnHold
import com.example.ui.theme.StatusPlanToWatch
import com.example.ui.theme.StatusWatching
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    profile: UserProfile,
    stats: DashboardStats,
    onAddTitle: (MediaCategory, Boolean) -> Unit,
    onSelectItem: (MediaItem) -> Unit,
    onStepEpisode: (MediaItem, Int) -> Unit,
    onToggleFavourite: (MediaItem) -> Unit,
    onOpenWatchlist: () -> Unit,
    onOpenSmartHub: (Int) -> Unit,
    onOpenStatistics: () -> Unit,
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Liquid Glass Hero Welcome Card (28dp rounded, gradient fill, 35% border, purple glow, glossy top half)
        item {
            LiquidGlassHeroCard {
                Text(
                    text = "Welcome back, ${profile.displayName}",
                    style = MaterialTheme.typography.displayMedium,
                    color = OffWhiteText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (stats.totalTitles == 0)
                        "Your personal cinema vault awaits. Log your first film, series, anime, or K-drama."
                    else
                        "${stats.totalTitles} titles tracked • ${formatWatchTime(stats.totalWatchTimeMinutes)} total watch time • ${stats.completedCount} completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedGreyText
                )
                Spacer(modifier = Modifier.height(14.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeonPrimaryButton(
                        onClick = { onAddTitle(MediaCategory.MOVIE, false) },
                        modifier = Modifier.testTag("dashboard_add_movie")
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Movie", fontWeight = FontWeight.Bold)
                    }
                    GlassSecondaryButton(
                        onClick = { onAddTitle(MediaCategory.WEB_SERIES, false) }
                    ) {
                        Icon(Icons.Rounded.Tv, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Series")
                    }
                    GlassSecondaryButton(
                        onClick = { onOpenSmartHub(0) },
                        accentBorderColor = NeonCyan
                    ) {
                        Icon(Icons.Rounded.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Random Pick")
                    }
                }
            }
        }

        // Quick Navigation & Smart Discovery Chips (Watchlist + Analytics open directly from Dashboard chips)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Quick Hubs & Discovery",
                    style = MaterialTheme.typography.titleLarge,
                    color = OffWhiteText
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeonGlassChip(
                        label = "Watchlist (${stats.planToWatchCount})",
                        selected = true,
                        onClick = onOpenWatchlist,
                        accentColor = NeonCyan,
                        leadingIcon = Icons.Rounded.Bookmark,
                        modifier = Modifier.testTag("dashboard_watchlist_chip")
                    )
                    NeonGlassChip(
                        label = "Analytics",
                        selected = true,
                        onClick = onOpenStatistics,
                        accentColor = NeonFuchsia,
                        leadingIcon = Icons.Rounded.BarChart,
                        modifier = Modifier.testTag("dashboard_analytics_chip")
                    )
                    NeonGlassChip(
                        label = "Random Picker",
                        selected = false,
                        onClick = { onOpenSmartHub(0) },
                        accentColor = NeonPurple,
                        leadingIcon = Icons.Rounded.Casino
                    )
                    NeonGlassChip(
                        label = "Mood Match",
                        selected = false,
                        onClick = { onOpenSmartHub(1) },
                        accentColor = NeonPink,
                        leadingIcon = Icons.Rounded.AutoAwesome
                    )
                    NeonGlassChip(
                        label = "Hidden Gems",
                        selected = false,
                        onClick = { onOpenSmartHub(2) },
                        accentColor = NeonCyan,
                        leadingIcon = Icons.Rounded.Star
                    )
                    NeonGlassChip(
                        label = "Badges & Goals",
                        selected = false,
                        onClick = { onOpenSmartHub(3) },
                        accentColor = NeonGreen,
                        leadingIcon = Icons.Rounded.EmojiEvents
                    )
                    NeonGlassChip(
                        label = "Quotes",
                        selected = false,
                        onClick = { onOpenSmartHub(4) },
                        accentColor = NeonFuchsia,
                        leadingIcon = Icons.Rounded.FormatQuote
                    )
                    NeonGlassChip(
                        label = "History",
                        selected = false,
                        onClick = { onOpenSmartHub(5) },
                        accentColor = NeonPurple,
                        leadingIcon = Icons.Rounded.History
                    )
                }
            }
        }

        // All 10 Automatic Statistics Cards (22dp neon glass cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overview Metrics",
                    style = MaterialTheme.typography.titleLarge,
                    color = OffWhiteText
                )
                NeonGlassChip(
                    label = "Full Analytics",
                    selected = false,
                    onClick = onOpenStatistics,
                    accentColor = NeonCyan
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatKpiCard("Total Titles", stats.totalTitles.toString(), Icons.Rounded.AutoAwesome, NeonPurple, modifier = Modifier.weight(1f))
                    StatKpiCard("Total Movies", stats.totalMovies.toString(), Icons.Rounded.Movie, NeonFuchsia, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatKpiCard(
                        "Web Series",
                        stats.totalWebSeries.toString(),
                        Icons.Rounded.Tv,
                        NeonCyan,
                        subtitle = "+${stats.totalAnimeAndKDrama} Anime/KD",
                        modifier = Modifier.weight(1f)
                    )
                    StatKpiCard("Completed", stats.completedCount.toString(), Icons.Rounded.CheckCircle, StatusCompleted, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatKpiCard("Currently Watching", stats.watchingCount.toString(), Icons.Rounded.PlayCircle, StatusWatching, modifier = Modifier.weight(1f))
                    StatKpiCard("Plan to Watch", stats.planToWatchCount.toString(), Icons.Rounded.BookmarkAdd, StatusPlanToWatch, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatKpiCard("On Hold", stats.onHoldCount.toString(), Icons.Rounded.PauseCircle, StatusOnHold, modifier = Modifier.weight(1f))
                    StatKpiCard("Dropped", stats.droppedCount.toString(), Icons.Rounded.Close, StatusDropped, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatKpiCard(
                        "Avg Personal Rating",
                        if (stats.averagePersonalRating <= 0.0) "—" else String.format(Locale.US, "%.1f / 10", stats.averagePersonalRating),
                        Icons.Rounded.Star,
                        GoldRating,
                        modifier = Modifier.weight(1f)
                    )
                    StatKpiCard(
                        "Total Watch Time",
                        formatWatchTime(stats.totalWatchTimeMinutes),
                        Icons.Rounded.Schedule,
                        NeonGreen,
                        subtitle = "${stats.totalEpisodesWatched} eps",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (stats.totalTitles == 0) {
            item {
                EmptyStateView(
                    title = "Start Building Your Afterglow",
                    subtitle = "Add your first movie, web series, anime, or K-drama to unlock live watch time analytics, genre charts, and rankings.",
                    actionLabel = "Add First Title",
                    onAction = { onAddTitle(MediaCategory.MOVIE, false) }
                )
            }
        } else {
            // Watch Time Overview
            item {
                HorizontalBarChartCard(
                    title = "Watch Time Overview (Minutes)",
                    items = listOf(
                        "Movies" to stats.moviesWatchTimeMinutes.toInt(),
                        "Web Series" to stats.seriesWatchTimeMinutes.toInt(),
                        "Anime & K-Drama" to stats.animeWatchTimeMinutes.toInt()
                    ).filter { it.second > 0 },
                    barColor = NeonPurple,
                    emptyMessage = "Complete movies or log episodes to calculate watch time."
                )
            }

            // Movies vs Series Distribution
            item {
                DonutChartCard(
                    title = "Movies vs Series Distribution",
                    centerLabel = stats.totalTitles.toString(),
                    centerSubLabel = "TITLES",
                    slices = listOf(
                        ChartSlice("Movies", stats.totalMovies.toFloat(), NeonPurple),
                        ChartSlice("Web Series", stats.totalWebSeries.toFloat(), NeonCyan),
                        ChartSlice("Anime & K-Drama", stats.totalAnimeAndKDrama.toFloat(), NeonPink)
                    ),
                    accentColor = NeonCyan
                )
            }

            // Genre Distribution
            item {
                HorizontalBarChartCard(
                    title = "Genre Distribution",
                    items = stats.genreDistribution.take(6),
                    barColor = NeonPink,
                    emptyMessage = "Add genres to titles to see your genre profile."
                )
            }

            // Currently Watching
            if (stats.currentlyWatching.isNotEmpty()) {
                item {
                    Text(
                        text = "Currently Watching",
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhiteText
                    )
                }
                items(stats.currentlyWatching, key = { "cw_${it.id}" }) { item ->
                    if (item.categoryEnum.isEpisodic) {
                        SeriesProgressCard(
                            item = item,
                            compact = profile.compactMode,
                            onClick = { onSelectItem(item) },
                            onStepEpisode = { delta -> onStepEpisode(item, delta) },
                            onToggleFavourite = { onToggleFavourite(item) }
                        )
                    } else {
                        CompactDashboardItemRow(item = item, onClick = { onSelectItem(item) })
                    }
                }
            }

            // Top 5 Highest Rated
            if (stats.top5Rated.isNotEmpty()) {
                item {
                    Text(
                        text = "Top 5 Highest Rated",
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhiteText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        stats.top5Rated.forEachIndexed { idx, item ->
                            val accent = neonPaletteColor(idx)
                            NeonGlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = accent,
                                cornerRadius = 22.dp,
                                onClick = { onSelectItem(item) },
                                contentPadding = PaddingValues(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "#${idx + 1}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = accent
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
                                            color = OffWhiteText,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${item.categoryEnum.label} • ${item.releaseYear}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MutedGreyText
                                        )
                                    }
                                    RatingPill(rating = item.personalRating)
                                }
                            }
                        }
                    }
                }
            }

            // Recently Added
            if (stats.recentlyAdded.isNotEmpty()) {
                item {
                    Text(
                        text = "Recently Added",
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhiteText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(stats.recentlyAdded, key = { "ra_${it.id}" }) { item ->
                            DashboardMiniCard(item = item, onClick = { onSelectItem(item) })
                        }
                    }
                }
            }

            // Recently Completed
            if (stats.recentlyCompleted.isNotEmpty()) {
                item {
                    Text(
                        text = "Recently Completed",
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhiteText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(stats.recentlyCompleted, key = { "rc_${it.id}" }) { item ->
                            DashboardMiniCard(item = item, onClick = { onSelectItem(item) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactDashboardItemRow(item: MediaItem, onClick: () -> Unit) {
    NeonGlassCard(
        modifier = Modifier.fillMaxWidth(),
        accentColor = categoryNeonColor(item.categoryEnum),
        cornerRadius = 22.dp,
        onClick = onClick,
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PosterArtwork(
                posterUrl = item.posterUrl,
                title = item.title,
                category = item.categoryEnum,
                modifier = Modifier.size(width = 50.dp, height = 70.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, color = OffWhiteText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${item.releaseYear} • ${item.streamingPlatform}", style = MaterialTheme.typography.bodySmall, color = MutedGreyText)
                StatusPill(status = item.statusEnum)
            }
            if (item.personalRating > 0) RatingPill(item.personalRating)
        }
    }
}

@Composable
private fun DashboardMiniCard(item: MediaItem, onClick: () -> Unit) {
    NeonGlassCard(
        modifier = Modifier.width(146.dp),
        accentColor = categoryNeonColor(item.categoryEnum),
        cornerRadius = 22.dp,
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
                    .height(156.dp)
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = OffWhiteText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.releaseYear.toString(), style = MaterialTheme.typography.labelSmall, color = MutedGreyText)
                if (item.personalRating > 0) RatingPill(item.personalRating)
            }
        }
    }
}
