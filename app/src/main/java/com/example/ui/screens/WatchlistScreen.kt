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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.DriveFileMove
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.WatchStatus
import com.example.data.model.WatchlistPriority
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassChip
import com.example.ui.components.NeonGlassDialog
import com.example.ui.components.NeonGlassTextField
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.PosterArtwork
import com.example.ui.components.neonGlow
import com.example.ui.components.priorityColor
import com.example.ui.components.themedCategoryColor
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonCyan
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                            color = tokens.textPrimary
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
                        cornerRadius = 14.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        modifier = Modifier.testTag("watchlist_add_button")
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
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

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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

        if (watchlistItems.isEmpty()) {
            WatchlistEmptyState(
                onAddFirstTitle = onAddWatchlistItem,
                onTryRandomPicker = onTryRandomPicker,
                bottomPadding = bottomPadding,
                modifier = Modifier.fillMaxSize()
            )
        } else if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding)
            ) {
                EmptyStateView(
                    title = "No Matching Titles",
                    subtitle = "Try clearing your search or priority/category filters to view your saved watchlist.",
                    actionLabel = "Reset Filters",
                    accentColor = tokens.tertiaryAccent,
                    onAction = {
                        searchQuery = ""
                        selectedCategory = null
                        selectedPriority = null
                    }
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { item ->
                    val pColor = priorityColor(item.priorityEnum)
                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = pColor,
                        cornerRadius = 24.dp,
                        onClick = { onSelectItem(item) },
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                PosterArtwork(
                                    posterUrl = item.posterUrl,
                                    title = item.title,
                                    category = item.categoryEnum,
                                    modifier = Modifier.size(width = 68.dp, height = 98.dp)
                                )
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                                                .padding(horizontal = 9.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "${item.priorityEnum.label.uppercase()} PRIORITY",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = pColor
                                            )
                                        }
                                        IconButton(onClick = { onDeleteItem(item) }) {
                                            Icon(Icons.Rounded.Delete, contentDescription = "Remove from watchlist", tint = tokens.dangerAccent)
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
                                        text = "${item.categoryEnum.label} · ${item.releaseYear} · ${item.streamingPlatform.ifBlank { "TBD" }}",
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
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                NeonPrimaryButton(
                                    onClick = { moveCandidate = item },
                                    cornerRadius = 14.dp,
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.AutoMirrored.Rounded.DriveFileMove, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
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
                                        cornerRadius = 14.dp,
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        Icon(Icons.Rounded.PlayArrow, contentDescription = "Trailer", modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
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

@Composable
private fun WatchlistEmptyState(
    onAddFirstTitle: () -> Unit,
    onTryRandomPicker: () -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    Box(
        modifier = modifier
            .padding(start = 24.dp, end = 24.dp, bottom = bottomPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .then(
                        if (tokens.useGlow) {
                            Modifier.neonGlow(
                                color = NeonPurple,
                                cornerRadius = 42.dp,
                                alpha = 0.45f,
                                spread = 14.dp
                            )
                        } else Modifier
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                NeonPurple.copy(alpha = 0.28f),
                                NeonCyan.copy(alpha = 0.20f)
                            )
                        )
                    )
                    .border(1.2.dp, NeonPurple.copy(alpha = 0.65f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Bookmark,
                    contentDescription = "Empty watchlist bookmark",
                    tint = NeonPurple,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Your watchlist is empty",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = tokens.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Save movies and shows you want to watch next, and they will show up here.",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            NeonPrimaryButton(
                onClick = onAddFirstTitle,
                accentColor = NeonPurple,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("watchlist_empty_add_button")
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add your first title", fontWeight = FontWeight.Bold)
            }

            GlassSecondaryButton(
                onClick = onTryRandomPicker,
                accentBorderColor = NeonCyan,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("watchlist_empty_random_button")
            ) {
                Icon(Icons.Rounded.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Random Picker", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
