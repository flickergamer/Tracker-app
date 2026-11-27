package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.WatchStatus
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
import com.example.ui.components.zenixFocusRing
import com.example.ui.components.zenixHeading
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ThemePreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GlobalSearchScreen(
    allItems: List<MediaItem>,
    onBack: () -> Unit,
    onSelectItem: (MediaItem) -> Unit,
    initialCategory: MediaCategory? = null,
    initialWatchlistOnly: Boolean = false,
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current

    var query by remember { mutableStateOf("") }
    var selectedCategory by remember(initialCategory) { mutableStateOf<MediaCategory?>(initialCategory) }
    var watchlistOnly by remember(initialWatchlistOnly) { mutableStateOf(initialWatchlistOnly) }
    var favouritesOnly by remember { mutableStateOf(false) }
    var watchingOnly by remember { mutableStateOf(false) }

    val recentSearches by ThemePreferences.observeRecentSearches(context).collectAsState(initial = emptyList())

    BackHandler(onBack = onBack)

    val results = remember(allItems, query, selectedCategory, watchlistOnly, favouritesOnly, watchingOnly) {
        val q = query.trim().lowercase()
        allItems.filter { item ->
            val matchesCat = selectedCategory == null || item.categoryEnum == selectedCategory
            val matchesWatchlist = !watchlistOnly || item.watchlistItem || item.statusEnum == WatchStatus.PLAN_TO_WATCH
            val matchesFav = !favouritesOnly || item.favourite
            val matchesWatching = !watchingOnly || item.statusEnum == WatchStatus.WATCHING
            val matchesQuery = q.isBlank() ||
                item.title.lowercase().contains(q) ||
                item.genre.lowercase().contains(q) ||
                item.streamingPlatform.lowercase().contains(q) ||
                item.director.lowercase().contains(q) ||
                item.mainCast.lowercase().contains(q)
            matchesCat && matchesWatchlist && matchesFav && matchesWatching && matchesQuery
        }.let { filtered ->
            if (q.isBlank()) filtered.sortedByDescending { it.personalRating } else filtered
        }
    }

    val hasActiveFilter = query.isNotBlank() || selectedCategory != null || watchlistOnly || favouritesOnly || watchingOnly

    Column(
        modifier = modifier.fillMaxSize()
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        if (query.trim().length >= 2) {
                            scope.launch { ThemePreferences.saveRecentSearch(context, query) }
                        }
                    }
                ),
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
                        selected = selectedCategory == null && !watchlistOnly && !favouritesOnly && !watchingOnly,
                        onClick = {
                            selectedCategory = null
                            watchlistOnly = false
                            favouritesOnly = false
                            watchingOnly = false
                        },
                        accentColor = NeonCyan
                    )
                    NeonGlassChip(
                        label = "Watchlist",
                        selected = watchlistOnly,
                        onClick = { watchlistOnly = !watchlistOnly },
                        leadingIcon = Icons.Rounded.Bookmark,
                        accentColor = NeonCyan
                    )
                    NeonGlassChip(
                        label = "Favourites",
                        selected = favouritesOnly,
                        onClick = { favouritesOnly = !favouritesOnly },
                        leadingIcon = Icons.Rounded.Favorite,
                        accentColor = NeonPink
                    )
                    NeonGlassChip(
                        label = "Watching",
                        selected = watchingOnly,
                        onClick = { watchingOnly = !watchingOnly },
                        leadingIcon = Icons.Rounded.PlayCircle,
                        accentColor = NeonPurple
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

            if (query.isBlank() && recentSearches.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent searches",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.textSecondary
                        )
                        Box(
                            modifier = Modifier
                                .defaultMinSize(minWidth = 48.dp, minHeight = 32.dp)
                                .clip(RoundedCornerShape(radii.sm))
                                .zenixFocusRing(NeonCyan, radii.sm)
                                .clickable(role = Role.Button) {
                                    scope.launch { ThemePreferences.clearRecentSearches(context) }
                                }
                                .padding(horizontal = spacing.space2, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Clear",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonCyan
                            )
                        }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                        verticalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        recentSearches.forEach { term ->
                            NeonGlassChip(
                                label = term,
                                selected = false,
                                onClick = { query = term },
                                leadingIcon = Icons.Rounded.History,
                                accentColor = NeonCyan,
                                neutralUnselected = true
                            )
                        }
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
                        "Nothing matched your search or active filters."
                    },
                    accentColor = NeonCyan,
                    primaryActionLabel = if (hasActiveFilter) "Clear filters" else null,
                    onPrimaryAction = if (hasActiveFilter) {
                        {
                            query = ""
                            selectedCategory = null
                            watchlistOnly = false
                            favouritesOnly = false
                            watchingOnly = false
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
                            onClick = {
                                if (query.trim().length >= 2) {
                                    scope.launch { ThemePreferences.saveRecentSearch(context, query) }
                                }
                                onSelectItem(item)
                            },
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
                                        text = listOfNotNull(
                                            item.categoryEnum.label,
                                            item.formattedYearOrBlank().takeIf { it.isNotBlank() },
                                            item.normalizedPlatform.takeIf { it.isNotBlank() }
                                        ).joinToString(" · "),
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
