package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.DashboardStats
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.UserProfile
import com.example.data.model.WatchStatus
import com.example.data.model.formatWatchTime
import com.example.ui.components.ChartSlice
import com.example.ui.components.DonutChartCard
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GoalRingCard
import com.example.ui.components.HorizontalBarChartCard
import com.example.ui.components.LineActivityChartCard
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonRatingStars
import com.example.ui.components.RatingPill
import com.example.ui.components.StatKpiCard
import com.example.ui.components.statusColor
import com.example.ui.theme.GoldRating
import com.example.ui.theme.MutedGreyText
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonFuchsia
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OffWhiteText
import java.util.Locale

@Composable
fun StatisticsScreen(
    stats: DashboardStats,
    profile: UserProfile,
    allItems: List<MediaItem>,
    onSelectItem: (MediaItem) -> Unit,
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val moviesWatched = allItems.count { it.categoryEnum == MediaCategory.MOVIE && it.statusEnum == WatchStatus.COMPLETED }
    val seriesCompleted = allItems.count { it.categoryEnum.isEpisodic && it.statusEnum == WatchStatus.COMPLETED }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Statistics & Analytics",
                    style = MaterialTheme.typography.displayMedium,
                    color = OffWhiteText
                )
                Text(
                    text = "Computed live from ${allItems.size} library records",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreyText
                )
            }
        }

        if (allItems.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No Analytics Data Yet",
                    subtitle = "Once you add movies, series, anime, or K-dramas, your real-time charts and watch habits will appear here.",
                    accentColor = NeonCyan
                )
            }
        } else {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatKpiCard("Movies Watched", moviesWatched.toString(), Icons.Rounded.Movie, NeonPurple, modifier = Modifier.weight(1f))
                        StatKpiCard("Series Completed", seriesCompleted.toString(), Icons.Rounded.Tv, NeonGreen, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatKpiCard("Episodes Watched", stats.totalEpisodesWatched.toString(), Icons.Rounded.CheckCircle, NeonCyan, modifier = Modifier.weight(1f))
                        StatKpiCard("Total Watch Time", formatWatchTime(stats.totalWatchTimeMinutes), Icons.Rounded.Schedule, NeonFuchsia, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatKpiCard(
                            "Average Rating",
                            if (stats.averagePersonalRating <= 0.0) "—" else String.format(Locale.US, "%.2f", stats.averagePersonalRating),
                            Icons.Rounded.Star,
                            GoldRating,
                            modifier = Modifier.weight(1f)
                        )
                        StatKpiCard("Top Genre", stats.topGenre, Icons.Rounded.AutoAwesome, NeonPink, modifier = Modifier.weight(1f))
                    }
                }
            }

            // Highest Rated Highlight
            stats.highestRatedTitle?.let { top ->
                item {
                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = GoldRating,
                        cornerRadius = 22.dp,
                        onClick = { onSelectItem(top) },
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Highest Rated Title", style = MaterialTheme.typography.labelMedium, color = GoldRating)
                                Text(top.title, style = MaterialTheme.typography.titleLarge, color = OffWhiteText)
                                Text("${top.categoryEnum.label} • ${top.releaseYear}", style = MaterialTheme.typography.bodySmall, color = MutedGreyText)
                                NeonRatingStars(ratingOutOfTen = top.personalRating)
                            }
                            RatingPill(rating = top.personalRating)
                        }
                    }
                }
            }

            // Progress Charts (Yearly Goals)
            item {
                Text("Goal Progress", style = MaterialTheme.typography.titleLarge, color = OffWhiteText)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GoalRingCard("Movies", stats.completedMoviesThisYear, profile.yearlyMovieGoal.toInt(), NeonPurple, Modifier.weight(1f))
                    GoalRingCard("Series", stats.completedSeriesThisYear, profile.yearlySeriesGoal.toInt(), NeonGreen, Modifier.weight(1f))
                    GoalRingCard("Episodes", stats.episodesWatchedTotal, profile.yearlyEpisodeGoal.toInt(), NeonCyan, Modifier.weight(1f))
                }
            }

            // Status Distribution Donut
            item {
                DonutChartCard(
                    title = "Status Distribution",
                    centerLabel = stats.totalTitles.toString(),
                    centerSubLabel = "TITLES",
                    slices = stats.statusDistribution.map { (st, count) ->
                        ChartSlice(st.label, count.toFloat(), statusColor(st))
                    },
                    accentColor = NeonFuchsia
                )
            }

            // Genre Distribution Bar Chart
            item {
                HorizontalBarChartCard(
                    title = "Genre Distribution",
                    items = stats.genreDistribution,
                    barColor = NeonPurple
                )
            }

            // Platform Distribution Bar Chart
            item {
                HorizontalBarChartCard(
                    title = "Platform Distribution",
                    items = stats.platformDistribution,
                    barColor = NeonCyan
                )
            }

            // Monthly Watching Activity Line Chart
            item {
                LineActivityChartCard(
                    title = "Monthly Watching Activity",
                    points = stats.monthlyActivity,
                    lineColor = NeonPink
                )
            }

            // Yearly Watching History Line Chart
            item {
                LineActivityChartCard(
                    title = "Yearly Completion History",
                    points = stats.yearlyHistory,
                    lineColor = NeonGreen
                )
            }
        }
    }
}
