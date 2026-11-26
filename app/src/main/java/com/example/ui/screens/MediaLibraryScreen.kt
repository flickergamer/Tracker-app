package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.WatchStatus
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.HorizontalScrollFadeBox
import com.example.ui.components.MediaGridCard
import com.example.ui.components.MovieTableView
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassChip
import com.example.ui.components.NeonGlassDialog
import com.example.ui.components.NeonGlassTextField
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.SeriesProgressCard
import com.example.ui.components.StaggeredFadeItem
import com.example.ui.components.SwipeableMediaCard
import com.example.ui.components.statusColor
import com.example.ui.components.themedCategoryColor
import com.example.ui.components.zenixHeading
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonCyan

enum class LibraryMode { MOVIES, WEB_SERIES, ANIME, K_DRAMA }
enum class MediaSortOption(val label: String) {
    RECENT("Recently Updated"),
    PERSONAL_RATING("Personal Rating"),
    IMDB_RATING("IMDb Rating"),
    YEAR_DESC("Release Year"),
    TITLE_ASC("Title A–Z"),
    RUNTIME_DESC("Runtime")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaLibraryScreen(
    mode: LibraryMode,
    allItems: List<MediaItem>,
    compact: Boolean,
    onAdd: (MediaCategory) -> Unit,
    onSelect: (MediaItem) -> Unit,
    onEdit: (MediaItem) -> Unit,
    onDelete: (MediaItem) -> Unit,
    onStepEpisode: (MediaItem, Int) -> Unit,
    onToggleFavourite: (MediaItem) -> Unit,
    onBulkUpdateStatus: (List<MediaItem>, WatchStatus) -> Unit = { _, _ -> },
    onBulkAddToWatchlist: (List<MediaItem>) -> Unit = {},
    onBulkDelete: (List<MediaItem>) -> Unit = {},
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember(mode) { mutableStateOf("") }
    var selectedStatus by remember(mode) { mutableStateOf<WatchStatus?>(null) }
    var selectedGenre by remember(mode) { mutableStateOf<String?>(null) }
    var selectedLanguage by remember(mode) { mutableStateOf<String?>(null) }
    var selectedYear by remember(mode) { mutableStateOf<Long?>(null) }
    var sortOption by remember(mode) { mutableStateOf(MediaSortOption.RECENT) }
    var isTableView by remember(mode) { mutableStateOf(false) }

    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    var selectedIds by remember(mode) { mutableStateOf<Set<String>>(emptySet()) }
    var showBulkStatusDialog by remember { mutableStateOf(false) }
    var showBulkDeleteConfirm by remember { mutableStateOf(false) }
    var pendingBulkStatus by remember { mutableStateOf(WatchStatus.COMPLETED) }

    val baseItems = remember(allItems, mode) {
        allItems.filter { item ->
            when (mode) {
                LibraryMode.MOVIES -> item.categoryEnum == MediaCategory.MOVIE
                LibraryMode.WEB_SERIES -> item.categoryEnum == MediaCategory.WEB_SERIES
                LibraryMode.ANIME -> item.categoryEnum == MediaCategory.ANIME
                LibraryMode.K_DRAMA -> item.categoryEnum == MediaCategory.K_DRAMA
            }
        }
    }

    LaunchedEffect(baseItems) {
        val validIds = baseItems.map { it.id }.toSet()
        if (selectedIds.any { it !in validIds }) {
            selectedIds = selectedIds.intersect(validIds)
        }
    }

    val isSelectionMode = selectedIds.isNotEmpty()
    BackHandler(enabled = isSelectionMode) {
        selectedIds = emptySet()
    }

    val availableGenres = remember(baseItems) {
        baseItems.flatMap { it.genreList }.distinct().sorted()
    }
    val availableLanguages = remember(baseItems) {
        baseItems.mapNotNull { it.language.trim().takeIf(String::isNotBlank) }.distinct().sorted()
    }
    val availableYears = remember(baseItems) {
        baseItems.mapNotNull { it.releaseYear.takeIf { yr -> yr > 0L } }.distinct().sortedDescending()
    }

    val filteredItems = remember(
        baseItems,
        searchQuery,
        selectedStatus,
        selectedGenre,
        selectedLanguage,
        selectedYear,
        sortOption
    ) {
        val q = searchQuery.trim().lowercase()
        val list = baseItems.filter { item ->
            val matchesQuery = q.isBlank() ||
                item.title.lowercase().contains(q) ||
                item.director.lowercase().contains(q) ||
                item.mainCast.lowercase().contains(q) ||
                item.genre.lowercase().contains(q) ||
                item.streamingPlatform.lowercase().contains(q)
            val matchesStatus = selectedStatus == null || item.statusEnum == selectedStatus
            val matchesGenre = selectedGenre == null || item.genreList.any { it.equals(selectedGenre, true) }
            val matchesLang = selectedLanguage == null || item.language.equals(selectedLanguage, true)
            val matchesYear = selectedYear == null || item.releaseYear == selectedYear
            matchesQuery && matchesStatus && matchesGenre && matchesLang && matchesYear
        }

        when (sortOption) {
            MediaSortOption.RECENT -> list
            MediaSortOption.PERSONAL_RATING -> list.sortedByDescending { it.personalRating }
            MediaSortOption.IMDB_RATING -> list.sortedByDescending { it.imdbRating }
            MediaSortOption.YEAR_DESC -> list.sortedByDescending { it.releaseYear }
            MediaSortOption.TITLE_ASC -> list.sortedBy { it.title.lowercase() }
            MediaSortOption.RUNTIME_DESC -> list.sortedByDescending { it.runtimeMinutes }
        }
    }

    val selectedItems = remember(filteredItems, baseItems, selectedIds) {
        if (selectedIds.isEmpty()) emptyList()
        else baseItems.filter { it.id in selectedIds }
    }

    val toggleSelection: (String) -> Unit = { id ->
        selectedIds = if (id in selectedIds) selectedIds - id else selectedIds + id
    }

    val defaultAddCategory = when (mode) {
        LibraryMode.MOVIES -> MediaCategory.MOVIE
        LibraryMode.WEB_SERIES -> MediaCategory.WEB_SERIES
        LibraryMode.ANIME -> MediaCategory.ANIME
        LibraryMode.K_DRAMA -> MediaCategory.K_DRAMA
    }

    val headerTitle = when (mode) {
        LibraryMode.MOVIES -> "Movies"
        LibraryMode.WEB_SERIES -> "Series"
        LibraryMode.ANIME -> "Anime"
        LibraryMode.K_DRAMA -> "K-Drama"
    }

    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val modeNeon = themedCategoryColor(defaultAddCategory)

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space4, vertical = spacing.space3),
            verticalArrangement = Arrangement.spacedBy(spacing.space3)
        ) {
            if (isSelectionMode) {
                NeonGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = NeonCyan,
                    cornerRadius = radii.lg,
                    contentPadding = PaddingValues(spacing.space3)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${selectedIds.size} selected",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.textPrimary
                                )
                                Text(
                                    text = "Tap cards to toggle selection",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tokens.textSecondary
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                                GlassIconButton(
                                    onClick = {
                                        val allFilteredIds = filteredItems.map { it.id }.toSet()
                                        selectedIds = if (selectedIds.containsAll(allFilteredIds)) {
                                            emptySet()
                                        } else {
                                            allFilteredIds
                                        }
                                    },
                                    glowColor = NeonCyan,
                                    size = 48.dp,
                                    modifier = Modifier.testTag("bulk_select_all_button")
                                ) {
                                    Icon(
                                        Icons.Rounded.SelectAll,
                                        contentDescription = "Select all",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                GlassIconButton(
                                    onClick = { selectedIds = emptySet() },
                                    glowColor = tokens.dangerAccent,
                                    size = 48.dp,
                                    modifier = Modifier.testTag("bulk_cancel_button")
                                ) {
                                    Icon(
                                        Icons.Rounded.Close,
                                        contentDescription = "Cancel selection",
                                        tint = tokens.textPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                        ) {
                            GlassSecondaryButton(
                                onClick = { showBulkStatusDialog = true },
                                accentBorderColor = NeonCyan,
                                cornerRadius = radii.sm,
                                contentPadding = PaddingValues(horizontal = spacing.space2, vertical = spacing.space2),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("bulk_status_button")
                            ) {
                                Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Status", maxLines = 1)
                            }
                            GlassSecondaryButton(
                                onClick = {
                                    onBulkAddToWatchlist(selectedItems)
                                    selectedIds = emptySet()
                                },
                                accentBorderColor = tokens.primaryAccent,
                                cornerRadius = radii.sm,
                                contentPadding = PaddingValues(horizontal = spacing.space2, vertical = spacing.space2),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("bulk_watchlist_button")
                            ) {
                                Icon(Icons.Rounded.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Watchlist", maxLines = 1)
                            }
                            GlassSecondaryButton(
                                onClick = { showBulkDeleteConfirm = true },
                                accentBorderColor = tokens.dangerAccent,
                                cornerRadius = radii.sm,
                                contentPadding = PaddingValues(horizontal = spacing.space2, vertical = spacing.space2),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("bulk_delete_button")
                            ) {
                                Icon(Icons.Rounded.Delete, contentDescription = null, tint = tokens.dangerAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", color = tokens.dangerAccent, maxLines = 1)
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.displayMedium,
                            color = tokens.textPrimary,
                            modifier = Modifier.zenixHeading()
                        )
                        Text(
                            text = "${filteredItems.size} of ${baseItems.size} titles",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2), verticalAlignment = Alignment.CenterVertically) {
                        GlassIconButton(
                            onClick = { isTableView = !isTableView },
                            glowColor = modeNeon,
                            size = 48.dp
                        ) {
                            Icon(
                                imageVector = if (isTableView) Icons.Rounded.GridView else Icons.Rounded.TableRows,
                                contentDescription = if (isTableView) "Switch to card view" else "Switch to table view",
                                tint = modeNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        NeonPrimaryButton(
                            onClick = { onAdd(defaultAddCategory) },
                            cornerRadius = radii.sm,
                            contentPadding = PaddingValues(horizontal = spacing.space4, vertical = spacing.space3),
                            modifier = Modifier.testTag("library_add_button")
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeonGlassTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search title, cast, genre...",
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        focusNeonColor = modeNeon,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("library_search_input")
                    )

                    Box {
                        GlassSecondaryButton(
                            onClick = { showSortMenu = true },
                            accentBorderColor = tokens.tertiaryAccent,
                            cornerRadius = radii.sm,
                            contentPadding = PaddingValues(horizontal = spacing.space3, vertical = spacing.space3)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Sort, contentDescription = "Sort titles", modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier
                                .background(tokens.dialogBackground)
                                .border(1.dp, tokens.tertiaryAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        ) {
                            MediaSortOption.entries.forEach { opt ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            opt.label,
                                            color = if (sortOption == opt) tokens.tertiaryAccent else tokens.textPrimary
                                        )
                                    },
                                    onClick = { sortOption = opt; showSortMenu = false }
                                )
                            }
                        }
                    }

                    Box {
                        GlassSecondaryButton(
                            onClick = { showFilterMenu = true },
                            accentBorderColor = tokens.tertiaryAccent,
                            cornerRadius = radii.sm,
                            contentPadding = PaddingValues(horizontal = spacing.space3, vertical = spacing.space3)
                        ) {
                            Icon(Icons.Rounded.FilterList, contentDescription = "Filter titles", modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false },
                            modifier = Modifier
                                .background(tokens.dialogBackground)
                                .border(1.dp, tokens.tertiaryAccent.copy(alpha = 0.5f), RoundedCornerShape(radii.sm))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Reset All Filters", color = tokens.dangerAccent) },
                                onClick = {
                                    selectedStatus = null; selectedGenre = null; selectedLanguage = null; selectedYear = null
                                    showFilterMenu = false
                                }
                            )
                            if (availableGenres.isNotEmpty()) {
                                availableGenres.take(8).forEach { g ->
                                    DropdownMenuItem(
                                        text = { Text("Genre: $g", color = tokens.textPrimary) },
                                        onClick = { selectedGenre = g; showFilterMenu = false }
                                    )
                                }
                            }
                            if (availableLanguages.isNotEmpty()) {
                                availableLanguages.take(6).forEach { l ->
                                    DropdownMenuItem(
                                        text = { Text("Language: $l", color = tokens.textPrimary) },
                                        onClick = { selectedLanguage = l; showFilterMenu = false }
                                    )
                                }
                            }
                            if (availableYears.isNotEmpty()) {
                                availableYears.take(6).forEach { yr ->
                                    DropdownMenuItem(
                                        text = { Text("Year: $yr", color = tokens.textPrimary) },
                                        onClick = { selectedYear = yr; showFilterMenu = false }
                                    )
                                }
                            }
                        }
                    }
                }

                // Status filter chips with both-edge scroll fade
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
                            label = "All Statuses",
                            selected = selectedStatus == null,
                            onClick = { selectedStatus = null },
                            accentColor = modeNeon
                        )
                        WatchStatus.entries.forEach { st ->
                            NeonGlassChip(
                                label = st.label,
                                selected = selectedStatus == st,
                                onClick = { selectedStatus = if (selectedStatus == st) null else st },
                                accentColor = statusColor(st)
                            )
                        }
                        if (selectedGenre != null) {
                            NeonGlassChip(
                                label = "Genre: $selectedGenre ×",
                                selected = true,
                                onClick = { selectedGenre = null },
                                accentColor = tokens.tertiaryAccent
                            )
                        }
                        if (selectedLanguage != null) {
                            NeonGlassChip(
                                label = "Lang: $selectedLanguage ×",
                                selected = true,
                                onClick = { selectedLanguage = null },
                                accentColor = tokens.dangerAccent
                            )
                        }
                        if (selectedYear != null) {
                            NeonGlassChip(
                                label = "Year: $selectedYear ×",
                                selected = true,
                                onClick = { selectedYear = null },
                                accentColor = tokens.secondaryAccent
                            )
                        }
                    }
                }
            }
        }

        if (filteredItems.isEmpty()) {
            val emptyIcon = if (baseItems.isNotEmpty()) {
                Icons.Rounded.SearchOff
            } else when (mode) {
                LibraryMode.MOVIES -> Icons.Rounded.Movie
                LibraryMode.WEB_SERIES -> Icons.Rounded.Tv
                LibraryMode.ANIME -> Icons.Rounded.AutoAwesome
                LibraryMode.K_DRAMA -> Icons.Rounded.Favorite
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = spacing.space4, end = spacing.space4, top = spacing.space2, bottom = bottomPadding)
            ) {
                EmptyStateView(
                    title = if (baseItems.isEmpty()) "No $headerTitle Yet" else "No Matching Titles",
                    subtitle = if (baseItems.isEmpty())
                        "Start curating your $headerTitle collection with posters, ratings, reviews, and progress."
                    else
                        "Try clearing your search or active filter chips.",
                    actionLabel = if (baseItems.isEmpty()) "Add ${defaultAddCategory.label}" else "Reset Filters",
                    accentColor = modeNeon,
                    icon = emptyIcon,
                    onAction = {
                        if (baseItems.isEmpty()) onAdd(defaultAddCategory)
                        else {
                            searchQuery = ""; selectedStatus = null; selectedGenre = null; selectedLanguage = null; selectedYear = null
                        }
                    }
                )
            }
        } else if (isTableView) {
            LazyColumn(
                contentPadding = PaddingValues(start = spacing.space4, end = spacing.space4, top = spacing.space2, bottom = bottomPadding)
            ) {
                item {
                    MovieTableView(
                        movies = filteredItems,
                        onSelect = onSelect,
                        onEdit = onEdit,
                        onDelete = onDelete,
                        accentColor = modeNeon
                    )
                }
            }
        } else if (mode == LibraryMode.MOVIES) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 158.dp),
                contentPadding = PaddingValues(start = spacing.space4, end = spacing.space4, top = spacing.space2, bottom = bottomPadding),
                verticalArrangement = Arrangement.spacedBy(spacing.space3),
                horizontalArrangement = Arrangement.spacedBy(spacing.space3),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(filteredItems, key = { _, item -> item.id }) { idx, movie ->
                    StaggeredFadeItem(index = idx) {
                        SwipeableMediaCard(
                            enabled = !isSelectionMode,
                            isFavourite = movie.favourite,
                            onToggleFavourite = { onToggleFavourite(movie) },
                            onDelete = { onDelete(movie) }
                        ) {
                            MediaGridCard(
                                item = movie,
                                compact = compact,
                                onClick = {
                                    if (isSelectionMode) toggleSelection(movie.id) else onSelect(movie)
                                },
                                onToggleFavourite = { onToggleFavourite(movie) },
                                onEdit = { onEdit(movie) },
                                onLongClick = { toggleSelection(movie.id) },
                                isSelectionMode = isSelectionMode,
                                isSelected = movie.id in selectedIds
                            )
                        }
                    }
                }
            }
        } else {
            // Web Series, Anime, or K-Drama with live Episode Progress cards + quick Edit button + swipe + multi-select
            LazyColumn(
                contentPadding = PaddingValues(start = spacing.space4, end = spacing.space4, top = spacing.space2, bottom = bottomPadding),
                verticalArrangement = Arrangement.spacedBy(spacing.space4),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(filteredItems, key = { _, item -> item.id }) { idx, series ->
                    StaggeredFadeItem(index = idx) {
                        SwipeableMediaCard(
                            enabled = !isSelectionMode,
                            isFavourite = series.favourite,
                            onToggleFavourite = { onToggleFavourite(series) },
                            onDelete = { onDelete(series) }
                        ) {
                            SeriesProgressCard(
                                item = series,
                                compact = compact,
                                onClick = {
                                    if (isSelectionMode) toggleSelection(series.id) else onSelect(series)
                                },
                                onStepEpisode = { delta -> onStepEpisode(series, delta) },
                                onToggleFavourite = { onToggleFavourite(series) },
                                onEdit = { onEdit(series) },
                                onLongClick = { toggleSelection(series.id) },
                                isSelectionMode = isSelectionMode,
                                isSelected = series.id in selectedIds
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBulkStatusDialog) {
        NeonGlassDialog(
            onDismissRequest = { showBulkStatusDialog = false },
            title = "Change Status (${selectedItems.size} titles)",
            accentColor = NeonCyan,
            confirmText = "Apply",
            onConfirm = {
                onBulkUpdateStatus(selectedItems, pendingBulkStatus)
                showBulkStatusDialog = false
                selectedIds = emptySet()
            }
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WatchStatus.entries.forEach { st ->
                    NeonGlassChip(
                        label = st.label,
                        selected = pendingBulkStatus == st,
                        onClick = { pendingBulkStatus = st },
                        accentColor = statusColor(st)
                    )
                }
            }
        }
    }

    if (showBulkDeleteConfirm) {
        NeonGlassDialog(
            onDismissRequest = { showBulkDeleteConfirm = false },
            title = "Delete ${selectedItems.size} titles?",
            accentColor = tokens.dangerAccent,
            confirmText = "Delete",
            confirmDangerous = true,
            onConfirm = {
                onBulkDelete(selectedItems)
                showBulkDeleteConfirm = false
                selectedIds = emptySet()
            }
        ) {
            Text(
                text = "These ${selectedItems.size} titles will be deleted from your library. You can immediately undo via the snackbar.",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary
            )
        }
    }
}
