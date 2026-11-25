package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.ui.components.MediaGridCard
import com.example.ui.components.MovieTableView
import com.example.ui.components.NeonGlassChip
import com.example.ui.components.NeonGlassTextField
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.SeriesProgressCard
import com.example.ui.components.statusColor
import com.example.ui.components.themedCategoryColor
import com.example.ui.theme.LocalZenixTokens

enum class LibraryMode { MOVIES, WEB_SERIES, ANIME, K_DRAMA }
enum class MediaSortOption(val label: String) {
    RECENT("Recently Updated"),
    PERSONAL_RATING("Personal Rating"),
    IMDB_RATING("IMDb Rating"),
    YEAR_DESC("Release Year"),
    TITLE_ASC("Title A–Z"),
    RUNTIME_DESC("Runtime")
}

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

    val availableGenres = remember(baseItems) {
        baseItems.flatMap { it.genreList }.distinct().sorted()
    }
    val availableLanguages = remember(baseItems) {
        baseItems.mapNotNull { it.language.trim().takeIf(String::isNotBlank) }.distinct().sorted()
    }
    val availableYears = remember(baseItems) {
        baseItems.map { it.releaseYear }.distinct().sortedDescending()
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

    val defaultAddCategory = when (mode) {
        LibraryMode.MOVIES -> MediaCategory.MOVIE
        LibraryMode.WEB_SERIES -> MediaCategory.WEB_SERIES
        LibraryMode.ANIME -> MediaCategory.ANIME
        LibraryMode.K_DRAMA -> MediaCategory.K_DRAMA
    }

    val headerTitle = when (mode) {
        LibraryMode.MOVIES -> "Movies"
        LibraryMode.WEB_SERIES -> "Web Series"
        LibraryMode.ANIME -> "Anime"
        LibraryMode.K_DRAMA -> "K-Drama"
    }

    val tokens = LocalZenixTokens.current
    val modeNeon = themedCategoryColor(defaultAddCategory)

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(headerTitle, style = MaterialTheme.typography.displayMedium, color = tokens.textPrimary)
                    Text(
                        "${filteredItems.size} of ${baseItems.size} titles",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    GlassIconButton(
                        onClick = { isTableView = !isTableView },
                        glowColor = modeNeon,
                        size = 46.dp
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
                        cornerRadius = 14.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                        accentBorderColor = modeNeon,
                        cornerRadius = 14.dp,
                        contentPadding = PaddingValues(horizontal = 13.dp, vertical = 12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.Sort, contentDescription = "Sort", modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier
                            .background(tokens.dialogBackground)
                            .border(1.dp, modeNeon.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        MediaSortOption.entries.forEach { opt ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        opt.label,
                                        color = if (sortOption == opt) modeNeon else tokens.textPrimary
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
                        accentBorderColor = tokens.secondaryAccent,
                        cornerRadius = 14.dp,
                        contentPadding = PaddingValues(horizontal = 13.dp, vertical = 12.dp)
                    ) {
                        Icon(Icons.Rounded.FilterList, contentDescription = "Filters", modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false },
                        modifier = Modifier
                            .background(tokens.dialogBackground)
                            .border(1.dp, tokens.secondaryAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
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

            // Status filter chips
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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

        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding)
            ) {
                EmptyStateView(
                    title = if (baseItems.isEmpty()) "No $headerTitle Yet" else "No Matching Titles",
                    subtitle = if (baseItems.isEmpty())
                        "Start curating your $headerTitle collection with posters, ratings, reviews, and progress."
                    else
                        "Try clearing your search or active filter chips.",
                    actionLabel = if (baseItems.isEmpty()) "Add ${defaultAddCategory.label}" else "Reset Filters",
                    accentColor = modeNeon,
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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding)
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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems, key = { it.id }) { movie ->
                    MediaGridCard(
                        item = movie,
                        compact = compact,
                        onClick = { onSelect(movie) },
                        onToggleFavourite = { onToggleFavourite(movie) },
                        onEdit = { onEdit(movie) }
                    )
                }
            }
        } else {
            // Web Series, Anime, or K-Drama with live Episode Progress cards + quick Edit button
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems, key = { it.id }) { series ->
                    SeriesProgressCard(
                        item = series,
                        compact = compact,
                        onClick = { onSelect(series) },
                        onStepEpisode = { delta -> onStepEpisode(series, delta) },
                        onToggleFavourite = { onToggleFavourite(series) },
                        onEdit = { onEdit(series) }
                    )
                }
            }
        }
    }
}
