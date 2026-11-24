package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
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
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.DashboardStats
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.UserProfile
import com.example.data.model.formatWatchTime
import com.example.ui.components.ChartSlice
import com.example.ui.components.DonutChartCard
import com.example.ui.components.EmptyStateView
import com.example.ui.components.HorizontalBarChartCard
import com.example.ui.components.PosterArtwork
import com.example.ui.components.RatingPill
import com.example.ui.components.SeriesProgressCard
import com.example.ui.components.StatKpiCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.GoldRating
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
    onOpenSmartHub: (Int) -> Unit,
    onOpenStatistics: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Luxury Hero Welcome Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_cinema),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize(),
                    alpha = 0.45f
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.94f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Welcome back, ${profile.displayName}",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (stats.totalTitles == 0)
                            "Your personal cinema vault awaits. Log your first film, series, anime, or K-drama."
                        else
                            "${stats.totalTitles} titles tracked • ${formatWatchTime(stats.totalWatchTimeMinutes)} total watch time • ${stats.completedCount} completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onAddTitle(MediaCategory.MOVIE, false) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("dashboard_add_movie")
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Movie")
                        }
                        FilledTonalButton(
                            onClick = { onAddTitle(MediaCategory.WEB_SERIES, false) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Tv, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Series")
                        }
                        FilledTonalButton(
                            onClick = { onAddTitle(MediaCategory.MOVIE, true) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Watchlist")
                        }
                        FilledTonalButton(
                            onClick = { onOpenSmartHub(0) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Random Pick")
                        }
                    }
                }
            }
        }

        // Smart Features Launcher Strip
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Smart Discovery & Collections",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SmartShortcutChip("Random Picker", Icons.Rounded.Casino) { onOpenSmartHub(0) }
                    SmartShortcutChip("Mood Match", Icons.Rounded.AutoAwesome) { onOpenSmartHub(1) }
                    SmartShortcutChip("Hidden Gems", Icons.Rounded.Star) { onOpenSmartHub(2) }
                    SmartShortcutChip("Badges & Goals", Icons.Rounded.EmojiEvents) { onOpenSmartHub(3) }
                    SmartShortcutChip("Quotes", Icons.Rounded.FormatQuote) { onOpenSmartHub(4) }
                    SmartShortcutChip("History", Icons.Rounded.History) { onOpenSmartHub(5) }
                }
            }
        }

        // All 10 Automatic Statistics Cards (2-column responsive grid rows)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overview Metrics",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onOpenStatistics) {
                    Text("Full Analytics")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatKpiCard("Total Titles", stats.totalTitles.toString(), Icons.Rounded.AutoAwesome, MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                    StatKpiCard("Total Movies", stats.totalMovies.toString(), Icons.Rounded.Movie, MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatKpiCard(
                        "Web Series",
                        stats.totalWebSeries.toString(),
                        Icons.Rounded.Tv,
                        StatusWatching,
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
                        MaterialTheme.colorScheme.secondary,
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
                    barColor = MaterialTheme.colorScheme.primary,
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
                        ChartSlice("Movies", stats.totalMovies.toFloat(), MaterialTheme.colorScheme.primary),
                        ChartSlice("Web Series", stats.totalWebSeries.toFloat(), MaterialTheme.colorScheme.secondary),
                        ChartSlice("Anime & K-Drama", stats.totalAnimeAndKDrama.toFloat(), StatusPlanToWatch)
                    )
                )
            }

            // Genre Distribution
            item {
                HorizontalBarChartCard(
                    title = "Genre Distribution",
                    items = stats.genreDistribution.take(6),
                    barColor = MaterialTheme.colorScheme.secondary,
                    emptyMessage = "Add genres to titles to see your genre profile."
                )
            }

            // Currently Watching
            if (stats.currentlyWatching.isNotEmpty()) {
                item {
                    Text(
                        text = "Currently Watching",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        stats.top5Rated.forEachIndexed { idx, item ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onSelectItem(item) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "#${idx + 1}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    PosterArtwork(
                                        posterUrl = item.posterUrl,
                                        title = item.title,
                                        category = item.categoryEnum,
                                        modifier = Modifier.size(width = 44.dp, height = 62.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${item.categoryEnum.label} • ${item.releaseYear}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
private fun SmartShortcutChip(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun CompactDashboardItemRow(item: MediaItem, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PosterArtwork(
                posterUrl = item.posterUrl,
                title = item.title,
                category = item.categoryEnum,
                modifier = Modifier.size(width = 48.dp, height = 68.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${item.releaseYear} • ${item.streamingPlatform}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                StatusPill(status = item.statusEnum)
            }
            if (item.personalRating > 0) RatingPill(item.personalRating)
        }
    }
}

@Composable
private fun DashboardMiniCard(item: MediaItem, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .width(142.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            PosterArtwork(
                posterUrl = item.posterUrl,
                title = item.title,
                category = item.categoryEnum,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.releaseYear.toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (item.personalRating > 0) RatingPill(item.personalRating)
            }
        }
    }
}
