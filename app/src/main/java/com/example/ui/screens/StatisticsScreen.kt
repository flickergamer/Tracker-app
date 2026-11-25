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
import com.example.ui.components.themedPastelFill
import com.example.ui.theme.LocalZenixTokens
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
    val tokens = LocalZenixTokens.current
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
                    color = tokens.textPrimary
                )
                Text(
                    text = "Computed live from ${allItems.size} library records",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textSecondary
                )
            }
        }

        if (allItems.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No Analytics Data Yet",
                    subtitle = "Once you add movies, series, anime, or K-dramas, your real-time charts and watch habits will appear here.",
                    accentColor = tokens.tertiaryAccent
                )
            }
        } else {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatKpiCard(
                            title = "Movies Watched",
                            value = moviesWatched.toString(),
                            icon = Icons.Rounded.Movie,
                            accentColor = tokens.primaryAccent,
                            pastelFill = themedPastelFill(0),
                            modifier = Modifier.weight(1f)
                        )
                        StatKpiCard(
                            title = "Series Completed",
                            value = seriesCompleted.toString(),
                            icon = Icons.Rounded.Tv,
                            accentColor = tokens.positiveAccent,
                            pastelFill = themedPastelFill(3),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatKpiCard(
                            title = "Episodes Watched",
                            value = stats.totalEpisodesWatched.toString(),
                            icon = Icons.Rounded.CheckCircle,
                            accentColor = tokens.tertiaryAccent,
                            pastelFill = themedPastelFill(1),
                            modifier = Modifier.weight(1f)
                        )
                        StatKpiCard(
                            title = "Total Watch Time",
                            value = formatWatchTime(stats.totalWatchTimeMinutes),
                            icon = Icons.Rounded.Schedule,
                            accentColor = tokens.secondaryAccent,
                            pastelFill = themedPastelFill(2),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatKpiCard(
                            title = "Average Rating",
                            value = if (stats.averagePersonalRating <= 0.0) "—" else String.format(Locale.US, "%.2f", stats.averagePersonalRating),
                            icon = Icons.Rounded.Star,
                            accentColor = tokens.ratingGold,
                            pastelFill = themedPastelFill(0),
                            modifier = Modifier.weight(1f)
                        )
                        StatKpiCard(
                            title = "Top Genre",
                            value = stats.topGenre,
                            icon = Icons.Rounded.AutoAwesome,
                            accentColor = tokens.dangerAccent,
                            pastelFill = themedPastelFill(1),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Highest Rated Highlight
            stats.highestRatedTitle?.let { top ->
                item {
                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = tokens.ratingGold,
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
                                Text("Highest Rated Title", style = MaterialTheme.typography.labelMedium, color = tokens.ratingGold)
                                Text(top.title, style = MaterialTheme.typography.titleLarge, color = tokens.textPrimary)
                                Text("${top.categoryEnum.label} • ${top.releaseYear}", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                NeonRatingStars(ratingOutOfTen = top.personalRating)
                            }
                            RatingPill(rating = top.personalRating)
                        }
                    }
                }
            }

            // Progress Charts (Yearly Goals)
            item {
                Text("Goal Progress", style = MaterialTheme.typography.titleLarge, color = tokens.textPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GoalRingCard("Movies", stats.completedMoviesThisYear, profile.yearlyMovieGoal.toInt(), tokens.primaryAccent, Modifier.weight(1f))
                    GoalRingCard("Series", stats.completedSeriesThisYear, profile.yearlySeriesGoal.toInt(), tokens.positiveAccent, Modifier.weight(1f))
                    GoalRingCard("Episodes", stats.episodesWatchedTotal, profile.yearlyEpisodeGoal.toInt(), tokens.tertiaryAccent, Modifier.weight(1f))
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
                    accentColor = tokens.secondaryAccent
                )
            }

            // Genre Distribution Bar Chart
            item {
                HorizontalBarChartCard(
                    title = "Genre Distribution",
                    items = stats.genreDistribution,
                    barColor = tokens.primaryAccent
                )
            }

            // Platform Distribution Bar Chart
            item {
                HorizontalBarChartCard(
                    title = "Platform Distribution",
                    items = stats.platformDistribution,
                    barColor = tokens.tertiaryAccent
                )
            }

            // Monthly Watching Activity Line Chart
            item {
                LineActivityChartCard(
                    title = "Monthly Watching Activity",
                    points = stats.monthlyActivity,
                    lineColor = tokens.dangerAccent
                )
            }

            // Yearly Watching History Line Chart
            item {
                LineActivityChartCard(
                    title = "Yearly Completion History",
                    points = stats.yearlyHistory,
                    lineColor = tokens.positiveAccent
                )
            }
        }
    }
}
