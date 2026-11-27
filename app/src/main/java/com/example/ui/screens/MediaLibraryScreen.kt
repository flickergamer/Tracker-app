package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.NeutralIconButton
import com.example.ui.components.SeriesProgressCard
import com.example.ui.components.StaggeredFadeItem
import com.example.ui.components.neonGlow
import com.example.ui.components.pressScale
import com.example.ui.components.statusColor
import com.example.ui.components.themedCategoryColor
import com.example.ui.components.zenixFocusRing
import com.example.ui.components.zenixHeading
import com.example.ui.theme.LocalReducedMotion
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ThemePreferences
import kotlinx.coroutines.launch

enum class LibraryMode(val storageKey: String) {
    MOVIES("movies"),
    WEB_SERIES("web_series"),
    ANIME("anime"),
    K_DRAMA("k_drama")
}

enum class MediaSortOption(val label: String) {
    RECENT("Recently added"),
    RATING("Rating"),
    TITLE_ASC("Title A-Z"),
    YEAR_DESC("Year"),
    WATCH_TIME("Watch time");

    companion object {
        fun fromKey(raw: String?): MediaSortOption = when (raw) {
            "PERSONAL_RATING", "IMDB_RATING" -> RATING
            "RUNTIME_DESC" -> WATCH_TIME
            else -> entries.firstOrNull { it.name == raw } ?: RECENT
        }
    }
}

private val WatchStatus.shortChipLabel: String
    get() = when (this) {
        WatchStatus.COMPLETED -> "Completed"
        WatchStatus.WATCHING -> "Watching"
        WatchStatus.PLAN_TO_WATCH -> "Plan to Watch"
        WatchStatus.ON_HOLD -> "On Hold"
        WatchStatus.DROPPED -> "Dropped"
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val reducedMotion = LocalReducedMotion.current

    val persistedListView by ThemePreferences.observeLibraryListView(context, mode.storageKey)
        .collectAsStateWithLifecycle(initialValue = null)
    val persistedSortKey by ThemePreferences.observeLibrarySort(context, mode.storageKey)
        .collectAsStateWithLifecycle(initialValue = null)

    var selectedStatus by remember(mode) { mutableStateOf<WatchStatus?>(null) }
    var selectedGenre by remember(mode) { mutableStateOf<String?>(null) }
    var selectedPlatform by remember(mode) { mutableStateOf<String?>(null) }
    var minRating by remember(mode) { mutableDoubleStateOf(0.0) }
    var sortOption by remember(mode) { mutableStateOf(MediaSortOption.RECENT) }
    var isListView by remember(mode) { mutableStateOf(false) }

    LaunchedEffect(mode, persistedListView) {
        persistedListView?.let { isListView = it }
    }
    LaunchedEffect(mode, persistedSortKey) {
        persistedSortKey?.let { sortOption = MediaSortOption.fromKey(it) }
    }

    var showFilterSheet by remember { mutableStateOf(false) }

    var selectedIds by remember(mode) { mutableStateOf<Set<String>>(emptySet()) }
    var showBulkStatusDialog by remember { mutableStateOf(false) }
    var showBulkDeleteConfirm by remember { mutableStateOf(false) }
    var pendingBulkStatus by remember { mutableStateOf(WatchStatus.COMPLETED) }

    var fabVisible by remember(mode) { mutableStateOf(true) }

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

    val statusCounts = remember(baseItems) {
        baseItems.groupingBy { it.statusEnum }.eachCount()
    }

    val availableGenres = remember(baseItems) {
        baseItems.flatMap { it.genreList }.distinct().sorted()
    }
    val availablePlatforms = remember(baseItems) {
        baseItems.mapNotNull { it.normalizedPlatform.takeIf(String::isNotBlank) }.distinct().sorted()
    }

    val hasActiveFilters = selectedGenre != null || selectedPlatform != null || minRating > 0.0

    val filteredItems = remember(
        baseItems,
        selectedStatus,
        selectedGenre,
        selectedPlatform,
        minRating,
        sortOption
    ) {
        val list = baseItems.filter { item ->
            val matchesStatus = selectedStatus == null || item.statusEnum == selectedStatus
            val matchesGenre = selectedGenre == null || item.genreList.any { it.equals(selectedGenre, true) }
            val matchesPlatform = selectedPlatform == null || item.normalizedPlatform.equals(selectedPlatform, true)
            val effectiveRating = if (item.personalRating > 0.0) item.personalRating else item.imdbRating
            val matchesRating = minRating <= 0.0 || effectiveRating >= minRating
            matchesStatus && matchesGenre && matchesPlatform && matchesRating
        }

        when (sortOption) {
            MediaSortOption.RECENT -> list.sortedWith(
                compareByDescending<MediaItem> { it.dateAdded }
                    .thenByDescending { it.createdAt?.seconds ?: it.updatedAt?.seconds ?: 0L }
            )
            MediaSortOption.RATING -> list.sortedWith(
                compareByDescending<MediaItem> { it.personalRating }
                    .thenByDescending { it.imdbRating }
                    .thenBy { it.title.lowercase() }
            )
            MediaSortOption.TITLE_ASC -> list.sortedBy { it.title.lowercase() }
            MediaSortOption.YEAR_DESC -> list.sortedWith(
                compareByDescending<MediaItem> { it.releaseYear }
                    .thenBy { it.title.lowercase() }
            )
            MediaSortOption.WATCH_TIME -> list.sortedWith(
                compareByDescending<MediaItem> {
                    if (it.watchedTimeMinutes > 0L) it.watchedTimeMinutes else it.runtimeMinutes
                }.thenBy { it.title.lowercase() }
            )
        }
    }

    LaunchedEffect(filteredItems.isEmpty(), isSelectionMode) {
        if (filteredItems.isEmpty()) {
            fabVisible = true
        }
    }

    val nestedScrollConnection = remember(mode) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -8f) {
                    fabVisible = false
                } else if (available.y > 8f) {
                    fabVisible = true
                }
                return Offset.Zero
            }
        }
    }

    val selectedItems = remember(baseItems, selectedIds) {
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

    val countSubtitle = if (selectedStatus != null || hasActiveFilters) {
        "${filteredItems.size} of ${baseItems.size} ${if (baseItems.size == 1) "title" else "titles"}"
    } else {
        "${baseItems.size} ${if (baseItems.size == 1) "title" else "titles"}"
    }

    val modeNeon = themedCategoryColor(defaultAddCategory)
    val contentBottomPadding = bottomPadding + 76.dp

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = spacing.space4, end = spacing.space4, top = spacing.space1)
            ) {
                if (isSelectionMode) {
                    NeonGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = spacing.space4),
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
                    // 2. Page header: Title + count on left, ONLY list/grid view toggle on right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = headerTitle,
                                style = MaterialTheme.typography.headlineLarge,
                                fontSize = 24.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.zenixHeading()
                            )
                            Text(
                                text = countSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        NeutralIconButton(
                            onClick = {
                                val next = !isListView
                                isListView = next
                                scope.launch {
                                    ThemePreferences.saveLibraryListView(context, mode.storageKey, next)
                                }
                            },
                            modifier = Modifier.testTag("library_view_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isListView) Icons.Rounded.GridView else Icons.AutoMirrored.Rounded.ViewList,
                                contentDescription = if (isListView) "Switch to grid view" else "Switch to list view",
                                tint = tokens.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.space3))

                    // 3. Status chips (scrollable with right-edge fade) + fixed Filter button on right
                    val chipScrollState = rememberScrollState()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        HorizontalScrollFadeBox(
                            scrollState = chipScrollState,
                            fadeLeft = false,
                            fadeRight = true,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.horizontalScroll(chipScrollState),
                                horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NeonGlassChip(
                                    label = "All ${baseItems.size}",
                                    selected = selectedStatus == null,
                                    onClick = { selectedStatus = null },
                                    accentColor = tokens.primaryAccent,
                                    neutralUnselected = true
                                )
                                WatchStatus.entries.forEach { st ->
                                    val count = statusCounts[st] ?: 0
                                    if (count > 0) {
                                        NeonGlassChip(
                                            label = "${st.shortChipLabel} $count",
                                            selected = selectedStatus == st,
                                            onClick = { selectedStatus = if (selectedStatus == st) null else st },
                                            accentColor = tokens.primaryAccent,
                                            neutralUnselected = true
                                        )
                                    }
                                }
                            }
                        }

                        NeutralIconButton(
                            onClick = { showFilterSheet = true },
                            showBadgeDot = hasActiveFilters,
                            badgeColor = tokens.primaryAccent,
                            modifier = Modifier.testTag("library_filter_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Tune,
                                contentDescription = "Filter and sort titles",
                                tint = tokens.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Active filter summary pills (shown only when sheet filters are active)
                    if (hasActiveFilters) {
                        Spacer(modifier = Modifier.height(spacing.space2))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedGenre != null) {
                                ActiveFilterPill(
                                    label = "$selectedGenre ×",
                                    accentColor = tokens.primaryAccent,
                                    onRemove = { selectedGenre = null }
                                )
                            }
                            if (selectedPlatform != null) {
                                ActiveFilterPill(
                                    label = "$selectedPlatform ×",
                                    accentColor = tokens.primaryAccent,
                                    onRemove = { selectedPlatform = null }
                                )
                            }
                            if (minRating > 0.0) {
                                ActiveFilterPill(
                                    label = "★ ${minRating.toInt()}+ ×",
                                    accentColor = tokens.primaryAccent,
                                    onRemove = { minRating = 0.0 }
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 44.dp)
                                    .clip(RoundedCornerShape(radii.sm))
                                    .clickable(role = Role.Button) {
                                        selectedGenre = null
                                        selectedPlatform = null
                                        minRating = 0.0
                                    }
                                    .padding(horizontal = spacing.space2),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Clear all",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = tokens.primaryAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.space4))
                }
            }

            // 4. Content (cards / list / empty state)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .nestedScroll(nestedScrollConnection)
            ) {
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
                            .padding(
                                start = spacing.space4,
                                end = spacing.space4,
                                bottom = contentBottomPadding
                            )
                    ) {
                        EmptyStateView(
                            title = if (baseItems.isEmpty()) "No $headerTitle Yet" else "No Matching Titles",
                            subtitle = if (baseItems.isEmpty())
                                "Start curating your $headerTitle collection with posters, ratings, reviews, and progress."
                            else
                                "Try clearing your active filters or switching status.",
                            actionLabel = if (baseItems.isEmpty()) "Add ${defaultAddCategory.label}" else "Reset Filters",
                            accentColor = modeNeon,
                            icon = emptyIcon,
                            onAction = {
                                if (baseItems.isEmpty()) {
                                    onAdd(defaultAddCategory)
                                } else {
                                    selectedStatus = null
                                    selectedGenre = null
                                    selectedPlatform = null
                                    minRating = 0.0
                                }
                            }
                        )
                    }
                } else if (isListView) {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = spacing.space4,
                            end = spacing.space4,
                            bottom = contentBottomPadding
                        ),
                        modifier = Modifier.fillMaxSize()
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
                        contentPadding = PaddingValues(
                            start = spacing.space4,
                            end = spacing.space4,
                            bottom = contentBottomPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(spacing.space3),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space3),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(filteredItems, key = { _, item -> item.id }) { idx, movie ->
                            StaggeredFadeItem(index = idx) {
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
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = spacing.space4,
                            end = spacing.space4,
                            bottom = contentBottomPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(spacing.space4),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(filteredItems, key = { _, item -> item.id }) { idx, series ->
                            StaggeredFadeItem(index = idx) {
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

        // 5. Floating "+" Button (56dp, accent color, bottom-right above bottom nav, hide on scroll down)
        val fabVisibleEffective = fabVisible && !isSelectionMode
        val fabBottomModifier = if (bottomPadding >= 64.dp) {
            Modifier.padding(end = spacing.space4, bottom = bottomPadding + 4.dp)
        } else {
            Modifier
                .navigationBarsPadding()
                .padding(end = spacing.space5, bottom = spacing.space5)
        }

        AnimatedVisibility(
            visible = fabVisibleEffective,
            enter = if (reducedMotion) EnterTransition.None else {
                fadeIn(tween(200)) + scaleIn(tween(200), initialScale = 0.8f)
            },
            exit = if (reducedMotion) ExitTransition.None else {
                fadeOut(tween(180)) + scaleOut(tween(180), targetScale = 0.8f)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .then(fabBottomModifier)
        ) {
            val fabInteraction = remember { MutableInteractionSource() }
            val accent = tokens.primaryAccent
            val deepAccent = lerp(accent, Color(0xFF340B6E), 0.28f)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .pressScale(fabInteraction, 0.96f)
                    .then(
                        if (tokens.useGlow) {
                            Modifier.neonGlow(accent, cornerRadius = 28.dp, alpha = 0.44f, spread = 10.dp)
                        } else Modifier
                    )
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(accent, deepAccent)))
                    .border(
                        1.dp,
                        if (tokens.useGloss) Color.White.copy(alpha = 0.36f) else Color.Transparent,
                        CircleShape
                    )
                    .zenixFocusRing(Color.White, 28.dp)
                    .clickable(
                        interactionSource = fabInteraction,
                        indication = ripple(color = tokens.primaryButtonText),
                        role = Role.Button,
                        onClick = { onAdd(defaultAddCategory) }
                    )
                    .testTag("library_add_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add ${defaultAddCategory.label}",
                    tint = tokens.primaryButtonText,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }

    if (showFilterSheet) {
        LibraryFilterBottomSheet(
            currentSort = sortOption,
            currentGenre = selectedGenre,
            currentPlatform = selectedPlatform,
            currentMinRating = minRating,
            availableGenres = availableGenres,
            availablePlatforms = availablePlatforms,
            onDismiss = { showFilterSheet = false },
            onApply = { newSort, newGenre, newPlatform, newMinRating ->
                sortOption = newSort
                selectedGenre = newGenre
                selectedPlatform = newPlatform
                minRating = newMinRating
                showFilterSheet = false
                scope.launch {
                    ThemePreferences.saveLibrarySort(context, mode.storageKey, newSort.name)
                }
            }
        )
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

@Composable
private fun ActiveFilterPill(
    label: String,
    accentColor: Color,
    onRemove: () -> Unit
) {
    val tokens = LocalZenixTokens.current
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(50)

    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = 44.dp)
            .pressScale(interactionSource, 0.96f)
            .zenixFocusRing(accentColor, 22.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, color = accentColor),
                role = Role.Button,
                onClick = onRemove
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(accentColor.copy(alpha = 0.16f))
                .border(1.dp, accentColor.copy(alpha = 0.55f), shape)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = tokens.textPrimary,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun LibraryFilterBottomSheet(
    currentSort: MediaSortOption,
    currentGenre: String?,
    currentPlatform: String?,
    currentMinRating: Double,
    availableGenres: List<String>,
    availablePlatforms: List<String>,
    onDismiss: () -> Unit,
    onApply: (MediaSortOption, String?, String?, Double) -> Unit
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var draftSort by remember(currentSort) { mutableStateOf(currentSort) }
    var draftGenre by remember(currentGenre) { mutableStateOf(currentGenre) }
    var draftPlatform by remember(currentPlatform) { mutableStateOf(currentPlatform) }
    var draftMinRating by remember(currentMinRating) { mutableDoubleStateOf(currentMinRating) }

    val ratingOptions = remember {
        listOf(
            0.0 to "Any",
            6.0 to "6+",
            7.0 to "7+",
            8.0 to "8+",
            9.0 to "9+"
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = tokens.dialogBackground,
        contentColor = tokens.textPrimary,
        shape = RoundedCornerShape(topStart = radii.lg, topEnd = radii.lg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = spacing.space4)
                .padding(bottom = spacing.space4),
            verticalArrangement = Arrangement.spacedBy(spacing.space4)
        ) {
            Text(
                text = "Filter & Sort",
                style = MaterialTheme.typography.headlineMedium,
                color = tokens.textPrimary,
                modifier = Modifier.zenixHeading()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.space4)
            ) {
                // Sort by
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space2)) {
                    Text(
                        text = "Sort by",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.textPrimary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                        verticalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        MediaSortOption.entries.forEach { opt ->
                            NeonGlassChip(
                                label = opt.label,
                                selected = draftSort == opt,
                                onClick = { draftSort = opt },
                                accentColor = tokens.primaryAccent,
                                neutralUnselected = true
                            )
                        }
                    }
                }

                // Genre chips
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space2)) {
                    Text(
                        text = "Genre",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.textPrimary
                    )
                    if (availableGenres.isEmpty()) {
                        Text(
                            text = "No genres available yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                            verticalArrangement = Arrangement.spacedBy(spacing.space2)
                        ) {
                            availableGenres.forEach { genre ->
                                val isSel = draftGenre.equals(genre, ignoreCase = true)
                                NeonGlassChip(
                                    label = genre,
                                    selected = isSel,
                                    onClick = { draftGenre = if (isSel) null else genre },
                                    accentColor = tokens.primaryAccent,
                                    neutralUnselected = true
                                )
                            }
                        }
                    }
                }

                // Platform chips
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space2)) {
                    Text(
                        text = "Platform",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.textPrimary
                    )
                    if (availablePlatforms.isEmpty()) {
                        Text(
                            text = "No platforms tagged yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                            verticalArrangement = Arrangement.spacedBy(spacing.space2)
                        ) {
                            availablePlatforms.forEach { platform ->
                                val isSel = draftPlatform.equals(platform, ignoreCase = true)
                                NeonGlassChip(
                                    label = platform,
                                    selected = isSel,
                                    onClick = { draftPlatform = if (isSel) null else platform },
                                    accentColor = tokens.primaryAccent,
                                    neutralUnselected = true
                                )
                            }
                        }
                    }
                }

                // Minimum rating
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space2)) {
                    Text(
                        text = "Minimum rating",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.textPrimary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                        verticalArrangement = Arrangement.spacedBy(spacing.space2)
                    ) {
                        ratingOptions.forEach { (value, label) ->
                            NeonGlassChip(
                                label = label,
                                selected = draftMinRating == value,
                                onClick = { draftMinRating = value },
                                accentColor = tokens.primaryAccent,
                                neutralUnselected = true
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.space3)
            ) {
                GlassSecondaryButton(
                    onClick = {
                        draftSort = MediaSortOption.RECENT
                        draftGenre = null
                        draftPlatform = null
                        draftMinRating = 0.0
                    },
                    cornerRadius = radii.md,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("filter_sheet_reset_button")
                ) {
                    Text("Reset", fontWeight = FontWeight.SemiBold)
                }
                NeonPrimaryButton(
                    onClick = { onApply(draftSort, draftGenre, draftPlatform, draftMinRating) },
                    accentColor = tokens.primaryAccent,
                    cornerRadius = radii.md,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("filter_sheet_apply_button")
                ) {
                    Text("Apply", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
