package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.DriveFileMove
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.WatchStatus
import com.example.data.model.WatchlistPriority
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.HorizontalScrollFadeBox
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassChip
import com.example.ui.components.NeonGlassDialog
import com.example.ui.components.NeonGlassTextField
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.PosterArtwork
import com.example.ui.components.StaggeredFadeItem
import com.example.ui.components.ZenixEmptyState
import com.example.ui.components.priorityColor
import com.example.ui.components.themedCategoryColor
import com.example.ui.components.zenixHeading
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonPurple

@Composable
fun WatchlistScreen(
    allItems: List<MediaItem>,
    onAddWatchlistItem: () -> Unit,
    onSelectItem: (MediaItem) -> Unit,
    onMoveToLibrary: (MediaItem, WatchStatus) -> Unit,
    onDeleteItem: (MediaItem) -> Unit,
    onBack: () -> Unit = {},
    onTryRandomPicker: () -> Unit = {},
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<MediaCategory?>(null) }
    var selectedPriority by remember { mutableStateOf<WatchlistPriority?>(null) }
    var sortByPriority by remember { mutableStateOf(true) }
    var moveCandidate by remember { mutableStateOf<MediaItem?>(null) }

    BackHandler(onBack = onBack)

    val watchlistItems = remember(allItems) {
        allItems.filter { it.watchlistItem || it.statusEnum == WatchStatus.PLAN_TO_WATCH }
    }

    val filtered = remember(watchlistItems, searchQuery, selectedCategory, selectedPriority, sortByPriority) {
        val q = searchQuery.trim().lowercase()
        val list = watchlistItems.filter { item ->
            val matchesQ = q.isBlank() || item.title.lowercase().contains(q) ||
                item.genre.lowercase().contains(q) || item.streamingPlatform.lowercase().contains(q)
            val matchesCat = selectedCategory == null || item.categoryEnum == selectedCategory
            val matchesPri = selectedPriority == null || item.priorityEnum == selectedPriority
            matchesQ && matchesCat && matchesPri
        }
        if (sortByPriority) {
            list.sortedWith(compareBy<MediaItem> { it.priorityEnum.sortWeight }.thenByDescending { it.dateAdded })
        } else {
            list.sortedByDescending { it.dateAdded }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space4, vertical = spacing.space2),
            verticalArrangement = Arrangement.spacedBy(spacing.space3)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                ) {
                    GlassIconButton(
                        onClick = onBack,
                        glowColor = tokens.primaryAccent,
                        size = 48.dp,
                        modifier = Modifier.testTag("watchlist_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = tokens.textPrimary
                        )
                    }
                    Column {
                        Text(
                            text = "Watchlist",
                            style = MaterialTheme.typography.displayMedium,
                            color = tokens.textPrimary,
                            modifier = Modifier.zenixHeading()
                        )
                        if (watchlistItems.isNotEmpty()) {
                            Text(
                                text = "${filtered.size} upcoming titles",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary
                            )
                        }
                    }
                }

                if (watchlistItems.isNotEmpty()) {
                    NeonPrimaryButton(
                        onClick = onAddWatchlistItem,
                        cornerRadius = radii.sm,
                        contentPadding = PaddingValues(horizontal = spacing.space4, vertical = spacing.space2),
                        modifier = Modifier.testTag("watchlist_add_button")
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(spacing.space1))
                        Text("Add", fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (watchlistItems.isNotEmpty()) {
                NeonGlassTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search watchlist...",
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    focusNeonColor = tokens.tertiaryAccent,
                    modifier = Modifier.fillMaxWidth()
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
                            label = if (sortByPriority) "Sort: Priority" else "Sort: Date Added",
                            selected = sortByPriority,
                            onClick = { sortByPriority = !sortByPriority },
                            accentColor = tokens.tertiaryAccent
                        )
                        WatchlistPriority.entries.forEach { p ->
                            NeonGlassChip(
                                label = p.label,
                                selected = selectedPriority == p,
                                onClick = { selectedPriority = if (selectedPriority == p) null else p },
                                accentColor = priorityColor(p)
                            )
                        }
                        MediaCategory.entries.forEach { c ->
                            NeonGlassChip(
                                label = c.label,
                                selected = selectedCategory == c,
                                onClick = { selectedCategory = if (selectedCategory == c) null else c },
                                accentColor = themedCategoryColor(c)
                            )
                        }
                    }
                }
            }
        }

        if (watchlistItems.isEmpty()) {
            ZenixEmptyState(
                icon = Icons.Rounded.Bookmark,
                title = "Your watchlist is empty",
                subtitle = "Save movies and shows you want to watch next, and they will show up here.",
                accentColor = NeonPurple,
                primaryActionLabel = "Add your first title",
                onPrimaryAction = onAddWatchlistItem,
                secondaryActionLabel = "Try Random Picker",
                onSecondaryAction = onTryRandomPicker,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomPadding)
            )
        } else if (filtered.isEmpty()) {
            ZenixEmptyState(
                icon = Icons.Rounded.SearchOff,
                title = "No matching titles",
                subtitle = "Try clearing your search or priority/category filters to view your saved watchlist.",
                accentColor = tokens.tertiaryAccent,
                primaryActionLabel = "Reset Filters",
                onPrimaryAction = {
                    searchQuery = ""
                    selectedCategory = null
                    selectedPriority = null
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomPadding)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = spacing.space4,
                    end = spacing.space4,
                    top = spacing.space2,
                    bottom = bottomPadding
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.space4)
            ) {
                itemsIndexed(filtered, key = { _, item -> item.id }) { idx, item ->
                    val pColor = priorityColor(item.priorityEnum)
                    StaggeredFadeItem(index = idx) {
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = pColor,
                            cornerRadius = radii.lg,
                            onClick = { onSelectItem(item) },
                            contentPadding = PaddingValues(spacing.space3)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(spacing.space3)) {
                                    PosterArtwork(
                                        posterUrl = item.posterUrl,
                                        title = item.title,
                                        category = item.categoryEnum,
                                        modifier = Modifier.size(width = 68.dp, height = 98.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50))
                                                    .background(pColor.copy(alpha = 0.16f))
                                                    .border(1.dp, pColor.copy(alpha = 0.55f), RoundedCornerShape(50))
                                                    .padding(horizontal = spacing.space2, vertical = spacing.space1)
                                            ) {
                                                Text(
                                                    text = "${item.priorityEnum.label.uppercase()} PRIORITY",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = pColor
                                                )
                                            }
                                            IconButton(onClick = { onDeleteItem(item) }) {
                                                Icon(Icons.Rounded.Delete, contentDescription = "Remove ${item.title} from watchlist", tint = tokens.dangerAccent)
                                            }
                                        }
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
                                                item.normalizedPlatform.ifBlank { "TBD" }
                                            ).joinToString(" · "),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = tokens.textSecondary
                                        )
                                        if (item.dateAdded.isNotBlank()) {
                                            Text(
                                                text = "Added ${item.dateAdded}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = tokens.textSecondary
                                            )
                                        }
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                                    NeonPrimaryButton(
                                        onClick = { moveCandidate = item },
                                        cornerRadius = radii.sm,
                                        contentPadding = PaddingValues(horizontal = spacing.space3, vertical = spacing.space2),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.AutoMirrored.Rounded.DriveFileMove, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(spacing.space2))
                                        Text("Move to Library", fontWeight = FontWeight.Bold)
                                    }
                                    if (item.trailerLink.isNotBlank()) {
                                        GlassSecondaryButton(
                                            onClick = {
                                                runCatching {
                                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.trailerLink)))
                                                }
                                            },
                                            accentBorderColor = tokens.tertiaryAccent,
                                            cornerRadius = radii.sm,
                                            contentPadding = PaddingValues(horizontal = spacing.space3, vertical = spacing.space2)
                                        ) {
                                            Icon(Icons.Rounded.PlayArrow, contentDescription = "Watch trailer for ${item.title}", modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(spacing.space1))
                                            Text("Trailer")
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

    if (moveCandidate != null) {
        val candidate = moveCandidate!!
        NeonGlassDialog(
            onDismissRequest = { moveCandidate = null },
            title = "Move \"${candidate.title}\"",
            accentColor = tokens.primaryAccent,
            confirmText = "Start Watching",
            onConfirm = {
                onMoveToLibrary(candidate, WatchStatus.WATCHING)
                moveCandidate = null
            },
            secondaryText = "Mark Completed",
            onSecondary = {
                onMoveToLibrary(candidate, WatchStatus.COMPLETED)
                moveCandidate = null
            }
        ) {
            Text(
                text = "Choose the library status for this ${candidate.categoryEnum.label}:",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary
            )
        }
    }
}
