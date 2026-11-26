package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.HorizontalScrollFadeBox
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassChip
import com.example.ui.components.NeonRatingStars
import com.example.ui.components.PosterArtwork
import com.example.ui.components.RatingPill
import com.example.ui.components.StaggeredFadeItem
import com.example.ui.components.ZenixToggleSwitch
import com.example.ui.components.themedPaletteColor
import com.example.ui.components.zenixHeading
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens

enum class RankingCategoryTab(val label: String) {
    TOP_10_MOVIES("Top 10 Movies"),
    TOP_10_SERIES("Top 10 Series"),
    TOP_10_ANIME("Top 10 Anime"),
    FAV_MOVIES("Favourite Movies"),
    FAV_SERIES("Favourite Series"),
    ALL_TIME("All-Time Favourites")
}

@Composable
fun RankingsScreen(
    allItems: List<MediaItem>,
    onSelectItem: (MediaItem) -> Unit,
    onUpdateManualRank: (MediaItem, Long) -> Unit,
    onToggleFavourite: (MediaItem) -> Unit,
    onToggleAllTime: (MediaItem) -> Unit,
    onOpenLibrary: () -> Unit = {},
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    var selectedTab by remember { mutableStateOf(RankingCategoryTab.TOP_10_MOVIES) }
    var autoRankByRating by remember { mutableStateOf(true) }

    val rankedList = remember(allItems, selectedTab, autoRankByRating) {
        val pool = when (selectedTab) {
            RankingCategoryTab.TOP_10_MOVIES -> allItems.filter { it.categoryEnum == MediaCategory.MOVIE }
            RankingCategoryTab.TOP_10_SERIES -> allItems.filter { it.categoryEnum == MediaCategory.WEB_SERIES || it.categoryEnum == MediaCategory.K_DRAMA }
            RankingCategoryTab.TOP_10_ANIME -> allItems.filter { it.categoryEnum == MediaCategory.ANIME }
            RankingCategoryTab.FAV_MOVIES -> allItems.filter { it.categoryEnum == MediaCategory.MOVIE && it.favourite }
            RankingCategoryTab.FAV_SERIES -> allItems.filter { it.categoryEnum.isEpisodic && it.favourite }
            RankingCategoryTab.ALL_TIME -> allItems.filter { it.allTimeFavourite }
        }

        val sorted = if (autoRankByRating) {
            pool.sortedWith(compareByDescending<MediaItem> { it.personalRating }.thenByDescending { it.imdbRating })
        } else {
            pool.sortedWith(
                compareBy<MediaItem> { if (it.manualRank > 0L) it.manualRank else Long.MAX_VALUE }
                    .thenByDescending { it.personalRating }
            )
        }
        if (selectedTab in listOf(RankingCategoryTab.TOP_10_MOVIES, RankingCategoryTab.TOP_10_SERIES, RankingCategoryTab.TOP_10_ANIME)) {
            sorted.take(10)
        } else {
            sorted
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space4, vertical = spacing.space3),
            verticalArrangement = Arrangement.spacedBy(spacing.space3)
        ) {
            Text(
                text = "Favourites & Rankings",
                style = MaterialTheme.typography.displayMedium,
                color = tokens.textPrimary,
                modifier = Modifier.zenixHeading()
            )

            val tabScrollState = rememberScrollState()
            HorizontalScrollFadeBox(
                scrollState = tabScrollState,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.horizontalScroll(tabScrollState),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                ) {
                    RankingCategoryTab.entries.forEachIndexed { idx, tab ->
                        NeonGlassChip(
                            label = tab.label,
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            accentColor = themedPaletteColor(idx)
                        )
                    }
                }
            }

            NeonGlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = tokens.secondaryAccent,
                cornerRadius = radii.md,
                contentPadding = PaddingValues(horizontal = spacing.space4, vertical = spacing.space3)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (autoRankByRating) "Auto-Rank by Personal Rating" else "Manual Custom Order",
                            style = MaterialTheme.typography.titleSmall,
                            color = tokens.textPrimary
                        )
                        Text(
                            text = if (autoRankByRating) "Ordered highest to lowest rating" else "Use arrows to customize rank #1–10",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary
                        )
                    }
                    ZenixToggleSwitch(
                        checked = autoRankByRating,
                        onCheckedChange = { autoRankByRating = it },
                        accentColor = tokens.primaryAccent,
                        contentDescriptionLabel = "Auto-rank by personal rating"
                    )
                }
            }
        }

        if (rankedList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = spacing.space4, end = spacing.space4, top = spacing.space2, bottom = bottomPadding)
            ) {
                EmptyStateView(
                    title = "No Titles in ${selectedTab.label}",
                    subtitle = "Rate titles or mark favourites and all-time favourites in your library to curate this leaderboard.",
                    actionLabel = "Browse Library",
                    onAction = onOpenLibrary,
                    accentColor = tokens.ratingGold,
                    icon = Icons.Rounded.EmojiEvents
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = spacing.space4, end = spacing.space4, top = spacing.space2, bottom = bottomPadding),
                verticalArrangement = Arrangement.spacedBy(spacing.space4)
            ) {
                itemsIndexed(rankedList, key = { _, item -> item.id }) { index, item ->
                    val accent = if (index < 3) tokens.ratingGold else themedPaletteColor(index)
                    StaggeredFadeItem(index = index) {
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = accent,
                            cornerRadius = radii.lg,
                            onClick = { onSelectItem(item) },
                            contentPadding = PaddingValues(spacing.space3)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = accent,
                                    modifier = Modifier.width(38.dp)
                                )
                                PosterArtwork(
                                    posterUrl = item.posterUrl,
                                    title = item.title,
                                    category = item.categoryEnum,
                                    modifier = Modifier.size(width = 52.dp, height = 74.dp)
                                )
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = tokens.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${item.categoryEnum.label} • ${item.releaseYear}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.textSecondary
                                    )
                                    if (item.personalRating > 0) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            NeonRatingStars(ratingOutOfTen = item.personalRating, starSize = 13.dp)
                                            RatingPill(rating = item.personalRating)
                                        }
                                    }
                                }
                                if (!autoRankByRating) {
                                    Column {
                                        IconButton(
                                            onClick = {
                                                if (index > 0) {
                                                    val prev = rankedList[index - 1]
                                                    onUpdateManualRank(item, index.toLong())
                                                    onUpdateManualRank(prev, (index + 1).toLong())
                                                }
                                            },
                                            enabled = index > 0,
                                            modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                        ) {
                                            Icon(Icons.Rounded.ArrowUpward, contentDescription = "Move ${item.title} up", tint = tokens.textPrimary)
                                        }
                                        IconButton(
                                            onClick = {
                                                if (index < rankedList.lastIndex) {
                                                    val next = rankedList[index + 1]
                                                    onUpdateManualRank(item, (index + 2).toLong())
                                                    onUpdateManualRank(next, (index + 1).toLong())
                                                }
                                            },
                                            enabled = index < rankedList.lastIndex,
                                            modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                        ) {
                                            Icon(Icons.Rounded.ArrowDownward, contentDescription = "Move ${item.title} down", tint = tokens.textPrimary)
                                        }
                                    }
                                } else {
                                    Row {
                                        IconButton(
                                            onClick = { onToggleFavourite(item) },
                                            modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Favorite,
                                                contentDescription = if (item.favourite) "Remove ${item.title} from favourites" else "Add ${item.title} to favourites",
                                                tint = if (item.favourite) tokens.dangerAccent else tokens.textSecondary.copy(alpha = 0.6f)
                                            )
                                        }
                                        IconButton(
                                            onClick = { onToggleAllTime(item) },
                                            modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = if (item.allTimeFavourite) "Remove ${item.title} from all-time favourites" else "Mark ${item.title} as all-time favourite",
                                                tint = if (item.allTimeFavourite) tokens.ratingGold else tokens.textSecondary.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
