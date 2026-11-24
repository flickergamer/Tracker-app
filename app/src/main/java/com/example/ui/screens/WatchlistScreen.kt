package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.automirrored.rounded.DriveFileMove
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.WatchStatus
import com.example.data.model.WatchlistPriority
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PosterArtwork
import com.example.ui.components.priorityColor

@Composable
fun WatchlistScreen(
    allItems: List<MediaItem>,
    onAddWatchlistItem: () -> Unit,
    onSelectItem: (MediaItem) -> Unit,
    onMoveToLibrary: (MediaItem, WatchStatus) -> Unit,
    onDeleteItem: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<MediaCategory?>(null) }
    var selectedPriority by remember { mutableStateOf<WatchlistPriority?>(null) }
    var sortByPriority by remember { mutableStateOf(true) }
    var moveCandidate by remember { mutableStateOf<MediaItem?>(null) }

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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Watchlist", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onBackground)
                    Text("${filtered.size} upcoming titles", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(
                    onClick = onAddWatchlistItem,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("watchlist_add_button")
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add")
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search watchlist...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sortByPriority,
                    onClick = { sortByPriority = !sortByPriority },
                    label = { Text(if (sortByPriority) "Sort: Priority" else "Sort: Date Added") }
                )
                WatchlistPriority.entries.forEach { p ->
                    FilterChip(
                        selected = selectedPriority == p,
                        onClick = { selectedPriority = if (selectedPriority == p) null else p },
                        label = { Text(p.label) }
                    )
                }
                MediaCategory.entries.forEach { c ->
                    FilterChip(
                        selected = selectedCategory == c,
                        onClick = { selectedCategory = if (selectedCategory == c) null else c },
                        label = { Text(c.label) }
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.padding(16.dp)) {
                EmptyStateView(
                    title = "Your Watchlist Is Empty",
                    subtitle = "Save upcoming movies, series, anime, or K-dramas with priority tags and trailer links.",
                    actionLabel = "Add to Watchlist",
                    onAction = onAddWatchlistItem
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { item ->
                    val pColor = priorityColor(item.priorityEnum)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onSelectItem(item) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = pColor.copy(alpha = 0.16f)
                                        ) {
                                            Text(
                                                text = "${item.priorityEnum.label.uppercase()} PRIORITY",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = pColor,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        IconButton(onClick = { onDeleteItem(item) }) {
                                            Icon(Icons.Rounded.Delete, contentDescription = "Remove from watchlist", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${item.categoryEnum.label} • ${item.releaseYear} • ${item.streamingPlatform.ifBlank { "TBD" }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (item.dateAdded.isNotBlank()) {
                                        Text(
                                            text = "Added ${item.dateAdded}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilledTonalButton(
                                    onClick = { moveCandidate = item },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.AutoMirrored.Rounded.DriveFileMove, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Move to Library")
                                }
                                if (item.trailerLink.isNotBlank()) {
                                    OutlinedButton(
                                        onClick = {
                                            runCatching {
                                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.trailerLink)))
                                            }
                                        }
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
        AlertDialog(
            onDismissRequest = { moveCandidate = null },
            title = { Text("Move \"${candidate.title}\"") },
            text = { Text("Choose the library status for this ${candidate.categoryEnum.label}:") },
            confirmButton = {
                Button(onClick = {
                    onMoveToLibrary(candidate, WatchStatus.WATCHING)
                    moveCandidate = null
                }) {
                    Text("Start Watching")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        onMoveToLibrary(candidate, WatchStatus.COMPLETED)
                        moveCandidate = null
                    }) {
                        Text("Mark Completed")
                    }
                    TextButton(onClick = { moveCandidate = null }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
