package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GlassIconButton
import com.example.ui.components.HorizontalScrollFadeBox
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassChip
import com.example.ui.components.NeonGlassTextField
import com.example.ui.components.PosterArtwork
import com.example.ui.components.RatingPill
import com.example.ui.components.StaggeredFadeItem
import com.example.ui.components.StatusPill
import com.example.ui.components.ZenixEmptyState
import com.example.ui.components.themedCategoryColor
import com.example.ui.components.zenixHeading
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonCyan

@Composable
fun GlobalSearchScreen(
    allItems: List<MediaItem>,
    onBack: () -> Unit,
    onSelectItem: (MediaItem) -> Unit,
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current

    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<MediaCategory?>(null) }

    BackHandler(onBack = onBack)

    val results = remember(allItems, query, selectedCategory) {
        val q = query.trim().lowercase()
        allItems.filter { item ->
            val matchesCat = selectedCategory == null || item.categoryEnum == selectedCategory
            val matchesQuery = q.isBlank() ||
                item.title.lowercase().contains(q) ||
                item.genre.lowercase().contains(q) ||
                item.streamingPlatform.lowercase().contains(q) ||
                item.director.lowercase().contains(q) ||
                item.mainCast.lowercase().contains(q)
            matchesCat && matchesQuery
        }.let { filtered ->
            if (q.isBlank()) filtered.sortedByDescending { it.personalRating } else filtered
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space4, vertical = spacing.space2),
            verticalArrangement = Arrangement.spacedBy(spacing.space3)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.space3)
            ) {
                GlassIconButton(
                    onClick = onBack,
                    glowColor = tokens.tertiaryAccent,
                    size = 48.dp,
                    modifier = Modifier.testTag("global_search_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = tokens.textPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Search Library",
                        style = MaterialTheme.typography.displayMedium,
                        color = tokens.textPrimary,
                        modifier = Modifier.zenixHeading()
                    )
                    Text(
                        text = "${results.size} ${if (results.size == 1) "title" else "titles"} across your vault",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary
                    )
                }
            }

            NeonGlassTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search titles, genres, cast, platforms...",
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = if (query.isNotEmpty()) {
                    {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Clear search",
                                tint = tokens.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else null,
                focusNeonColor = NeonCyan,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_search_input")
            )

            val chipScrollState = rememberScrollState()
            HorizontalScrollFadeBox(
                scrollState = chipScrollState,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.horizontalScroll(chipScrollState),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                ) {
                    NeonGlassChip(
                        label = "All",
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        accentColor = NeonCyan
                    )
                    MediaCategory.entries.forEach { cat ->
                        NeonGlassChip(
                            label = cat.label,
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                            accentColor = themedCategoryColor(cat)
                        )
                    }
                }
            }
        }

        if (results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = spacing.space4,
                        end = spacing.space4,
                        top = spacing.space2,
                        bottom = bottomPadding
                    )
            ) {
                ZenixEmptyState(
                    icon = Icons.Rounded.SearchOff,
                    title = if (allItems.isEmpty()) "Library is Empty" else "No Results Found",
                    subtitle = if (allItems.isEmpty()) {
                        "Add a movie, series, anime, or K-drama to start searching."
                    } else {
                        "Nothing matched your search or category filter."
                    },
                    accentColor = NeonCyan,
                    primaryActionLabel = if (query.isNotBlank() || selectedCategory != null) "Clear filters" else null,
                    onPrimaryAction = if (query.isNotBlank() || selectedCategory != null) {
                        {
                            query = ""
                            selectedCategory = null
                        }
                    } else null
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = spacing.space4,
                    end = spacing.space4,
                    top = spacing.space2,
                    bottom = bottomPadding
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.space3)
            ) {
                itemsIndexed(results, key = { _, it -> "gs_${it.id}" }) { idx, item ->
                    val accent = themedCategoryColor(item.categoryEnum)
                    StaggeredFadeItem(index = idx) {
                        NeonGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("global_search_result_${item.id}"),
                            accentColor = accent,
                            cornerRadius = radii.lg,
                            onClick = { onSelectItem(item) },
                            contentPadding = PaddingValues(spacing.space3)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                            ) {
                                PosterArtwork(
                                    posterUrl = item.posterUrl,
                                    title = item.title,
                                    category = item.categoryEnum,
                                    modifier = Modifier.size(width = 52.dp, height = 74.dp)
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(spacing.space1)
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = tokens.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = buildString {
                                            append("${item.categoryEnum.label} · ${item.releaseYear}")
                                            if (item.streamingPlatform.isNotBlank()) append(" · ${item.streamingPlatform}")
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                                    ) {
                                        StatusPill(status = item.statusEnum)
                                        if (item.personalRating > 0.0) {
                                            RatingPill(rating = item.personalRating)
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
