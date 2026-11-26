package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.DashboardStats
import com.example.data.model.MediaItem
import com.example.data.model.UserProfile
import com.example.data.model.formatWatchTime
import com.example.ui.components.ChartSlice
import com.example.ui.components.DonutChartCard
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GoalRingCard
import com.example.ui.components.HorizontalBarChartCard
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.NeonProgressBar
import com.example.ui.components.NeonRatingStars
import com.example.ui.components.RatingPill
import com.example.ui.components.StatKpiCard
import com.example.ui.components.VerticalBarChartCard
import com.example.ui.components.rememberAnimatedLongCount
import com.example.ui.components.statusColor
import com.example.ui.components.themedPastelFill
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

@Composable
fun StatisticsScreen(
    stats: DashboardStats,
    profile: UserProfile,
    allItems: List<MediaItem>,
    onSelectItem: (MediaItem) -> Unit,
    onAddTitle: () -> Unit = {},
    onOpenYearInReview: () -> Unit = {},
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val animatedMovieMins = rememberAnimatedLongCount(stats.moviesWatchTimeMinutes.coerceAtLeast(0L))
    val animatedSeriesMins = rememberAnimatedLongCount(stats.seriesWatchTimeMinutes.coerceAtLeast(0L))
    val animatedAnimeMins = rememberAnimatedLongCount(stats.animeWatchTimeMinutes.coerceAtLeast(0L))
    val animatedKDramaMins = rememberAnimatedLongCount(stats.kDramaWatchTimeMinutes.coerceAtLeast(0L))

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = spacing.space4, end = spacing.space4, top = spacing.space2, bottom = bottomPadding),
        verticalArrangement = Arrangement.spacedBy(spacing.space4)
    ) {
        item {
            Column {
                Text(
                    text = "Statistics",
                    style = MaterialTheme.typography.displayMedium,
                    color = tokens.textPrimary,
                    modifier = Modifier.zenixHeading()
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
                    title = "No Statistics Data Yet",
                    subtitle = "Once you add movies, series, anime, or K-dramas, your real-time charts and watch habits will appear here.",
                    actionLabel = "Add First Title",
                    onAction = onAddTitle,
                    accentColor = tokens.tertiaryAccent,
                    icon = Icons.Rounded.BarChart
                )
            }
        } else {
            item {
                NeonGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = NeonPink,
                    cornerRadius = radii.lg,
                    onClick = onOpenYearInReview,
                    contentPadding = PaddingValues(spacing.space4)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(spacing.space3),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Celebration,
                                contentDescription = null,
                                tint = NeonPink,
                                modifier = Modifier.size(28.dp)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Year in Review",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.textPrimary
                                )
                                Text(
                                    text = "Swipeable story cards & shareable stats recap",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textSecondary
                                )
                            }
                        }
                        NeonPrimaryButton(
                            onClick = onOpenYearInReview,
                            accentColor = NeonPink,
                            cornerRadius = radii.sm,
                            contentPadding = PaddingValues(horizontal = spacing.space3, vertical = spacing.space2),
                            modifier = Modifier.testTag("statistics_year_in_review_button")
                        ) {
                            Text("Open", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.space3)) {
                        StatKpiCard(
                            title = "Movies Completed",
                            value = stats.completedMovies.toString(),
                            icon = Icons.Rounded.Movie,
                            accentColor = tokens.primaryAccent,
                            pastelFill = themedPastelFill(0),
                            modifier = Modifier.weight(1f)
                        )
                        StatKpiCard(
                            title = "Web Series Completed",
                            value = stats.completedWebSeries.toString(),
                            icon = Icons.Rounded.Tv,
                            accentColor = tokens.positiveAccent,
                            pastelFill = themedPastelFill(3),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.space3)) {
                        StatKpiCard(
                            title = "Anime Completed",
                            value = stats.completedAnime.toString(),
                            icon = Icons.Rounded.AutoAwesome,
                            accentColor = NeonGreen,
                            pastelFill = themedPastelFill(1),
                            modifier = Modifier.weight(1f)
                        )
                        StatKpiCard(
                            title = "K-Dramas Completed",
                            value = stats.completedKDrama.toString(),
                            icon = Icons.Rounded.Favorite,
                            accentColor = NeonPink,
                            pastelFill = themedPastelFill(2),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.space3)) {
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
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.space3)) {
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

            // Watch time (minutes) breakdown card
            item {
                val maxMins = remember(stats) {
                    maxOf(
                        stats.moviesWatchTimeMinutes.coerceAtLeast(0L),
                        stats.seriesWatchTimeMinutes.coerceAtLeast(0L),
                        stats.animeWatchTimeMinutes.coerceAtLeast(0L),
                        stats.kDramaWatchTimeMinutes.coerceAtLeast(0L),
                        1L
                    ).toFloat()
                }
                NeonGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = NeonCyan,
                    cornerRadius = radii.lg,
                    contentPadding = PaddingValues(spacing.space4)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                        Text(
                            text = "Watch time (minutes)",
                            style = MaterialTheme.typography.titleLarge,
                            color = tokens.textPrimary,
                            modifier = Modifier.zenixHeading()
                        )
                        StatWatchTimeBarRow(
                            label = "Movies",
                            minutes = animatedMovieMins,
                            progress = stats.moviesWatchTimeMinutes.coerceAtLeast(0L).toFloat() / maxMins,
                            startColor = NeonCyan,
                            endColor = NeonPurple
                        )
                        StatWatchTimeBarRow(
                            label = "Web Series",
                            minutes = animatedSeriesMins,
                            progress = stats.seriesWatchTimeMinutes.coerceAtLeast(0L).toFloat() / maxMins,
                            startColor = NeonPink,
                            endColor = NeonPink
                        )
                        StatWatchTimeBarRow(
                            label = "Anime",
                            minutes = animatedAnimeMins,
                            progress = stats.animeWatchTimeMinutes.coerceAtLeast(0L).toFloat() / maxMins,
                            startColor = NeonGreen,
                            endColor = NeonCyan
                        )
                        StatWatchTimeBarRow(
                            label = "K-Drama",
                            minutes = animatedKDramaMins,
                            progress = stats.kDramaWatchTimeMinutes.coerceAtLeast(0L).toFloat() / maxMins,
                            startColor = NeonAmber,
                            endColor = NeonPink
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
                        cornerRadius = radii.lg,
                        onClick = { onSelectItem(top) },
                        contentPadding = PaddingValues(spacing.space4)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                                Text("Highest Rated Title", style = MaterialTheme.typography.labelMedium, color = tokens.ratingGold)
                                Text(top.title, style = MaterialTheme.typography.titleLarge, color = tokens.textPrimary)
                                val topMeta = listOfNotNull(
                                    top.categoryEnum.label,
                                    top.formattedYearOrBlank().takeIf { it.isNotBlank() }
                                ).joinToString(" • ")
                                Text(topMeta, style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                NeonRatingStars(ratingOutOfTen = top.personalRating)
                            }
                            RatingPill(rating = top.personalRating)
                        }
                    }
                }
            }

            // Progress Charts (Yearly Goals)
            item {
                Text(
                    text = "Goal Progress",
                    style = MaterialTheme.typography.titleLarge,
                    color = tokens.textPrimary,
                    modifier = Modifier.zenixHeading()
                )
                Spacer(modifier = Modifier.height(spacing.space3))
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    GoalRingCard("Movies", stats.completedMoviesThisYear, profile.yearlyMovieGoal.toInt(), tokens.primaryAccent, Modifier.weight(1f))
                    GoalRingCard("Web Series", stats.completedSeriesThisYear, profile.yearlySeriesGoal.toInt(), tokens.positiveAccent, Modifier.weight(1f))
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

            // Monthly Watching Activity Bar Chart
            item {
                VerticalBarChartCard(
                    title = "Monthly Watching Activity",
                    points = stats.monthlyActivity,
                    barColor = tokens.dangerAccent,
                    minPointsRequired = 2,
                    insufficientDataMessage = "Add finish dates to see your monthly trend"
                )
            }

            // Completed per year Bar Chart
            item {
                VerticalBarChartCard(
                    title = "Completed per year",
                    points = stats.yearlyHistory,
                    barColor = tokens.positiveAccent,
                    emptyMessage = "Mark titles as Completed to see your yearly breakdown."
                )
            }
        }
    }
}

@Composable
private fun StatWatchTimeBarRow(
    label: String,
    minutes: Long,
    progress: Float,
    startColor: Color,
    endColor: Color
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
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
