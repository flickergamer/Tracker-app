package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MediaItem
import com.example.data.model.formatWatchTime
import com.example.ui.theme.GoldRating
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaDetailDialog(
    item: MediaItem,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStepEpisode: (Int) -> Unit,
    onToggleFavourite: () -> Unit,
    onToggleAllTimeFavourite: () -> Unit,
    onToggleHiddenGem: () -> Unit,
    onIncrementRewatch: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close details")
                    }
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Edit title", tint = MaterialTheme.colorScheme.secondary)
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete title", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        PosterArtwork(
                            posterUrl = item.posterUrl,
                            title = item.title,
                            category = item.categoryEnum,
                            modifier = Modifier
                                .width(116.dp)
                                .height(168.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${item.categoryEnum.label} • ${item.releaseYear} • ${item.language.ifBlank { "—" }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (item.streamingPlatform.isNotBlank()) {
                                Text(
                                    text = "On ${item.streamingPlatform}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatusPill(status = item.statusEnum)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (item.personalRating > 0.0) {
                                    RatingPill(rating = item.personalRating, prefix = "You")
                                }
                                if (item.imdbRating > 0.0) {
                                    RatingPill(rating = item.imdbRating, prefix = "IMDb")
                                }
                            }
                        }
                    }

                    // Quick Action Flags
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(onClick = onToggleFavourite) {
                            Icon(
                                imageVector = if (item.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = if (item.favourite) Color(0xFFF472B6) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (item.favourite) "Favourited" else "Favourite")
                        }
                        FilledTonalButton(onClick = onToggleAllTimeFavourite) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (item.allTimeFavourite) GoldRating else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (item.allTimeFavourite) "All-Time Fav" else "Mark All-Time")
                        }
                        FilledTonalButton(onClick = onToggleHiddenGem) {
                            Icon(
                                imageVector = Icons.Rounded.Diamond,
                                contentDescription = null,
                                tint = if (item.hiddenGem) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (item.hiddenGem) "Hidden Gem" else "Mark Gem")
                        }
                        FilledTonalButton(onClick = onIncrementRewatch) {
                            Icon(Icons.Rounded.Repeat, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rewatch (${item.rewatchCount})")
                        }
                    }

                    // Episode Progress Section for Series/Anime/K-Drama
                    if (item.categoryEnum.isEpisodic) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = item.formattedSeriesProgress(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                LinearProgressIndicator(
                                    progress = { item.episodeProgressPercent / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    strokeCap = StrokeCap.Round
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Season ${item.currentSeason}/${item.totalSeasons} (${item.seasonProgressPercent}%)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${item.remainingEpisodes} episodes remaining • Est. total ${formatWatchTime(item.estimatedTotalSeriesMinutes)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilledTonalIconButton(
                                            onClick = { onStepEpisode(-1) },
                                            enabled = item.episodesWatched > 0
                                        ) {
                                            Icon(Icons.Rounded.Remove, contentDescription = "Previous episode")
                                        }
                                        FilledTonalIconButton(
                                            onClick = { onStepEpisode(1) },
                                            enabled = item.totalEpisodes == 0L || item.episodesWatched < item.totalEpisodes
                                        ) {
                                            Icon(Icons.Rounded.Add, contentDescription = "Next episode")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // External Links
                    if (item.trailerLink.isNotBlank() || item.imdbLink.isNotBlank()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (item.trailerLink.isNotBlank()) {
                                Button(
                                    onClick = {
                                        runCatching {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.trailerLink)))
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Watch Trailer")
                                }
                            }
                            if (item.imdbLink.isNotBlank()) {
                                OutlinedButton(
                                    onClick = {
                                        runCatching {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.imdbLink)))
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("IMDb Page")
                                }
                            }
                        }
                    }

                    // Metadata Card
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DetailRow("Genres", item.genre.ifBlank { "—" })
                            DetailRow("Director / Creator", item.director.ifBlank { "—" })
                            DetailRow("Main Cast", item.mainCast.ifBlank { "—" })
                            DetailRow("Country", item.country.ifBlank { "—" })
                            DetailRow("Watched Time", formatWatchTime(item.watchedTimeMinutes))
                            DetailRow("Start Date", item.startDate.ifBlank { "—" })
                            DetailRow("Completion Date", item.completionDate.ifBlank { "—" })
                        }
                    }

                    if (item.moodTags.isNotEmpty()) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            item.moodTags.forEach { mood ->
                                AssistChip(
                                    onClick = {},
                                    leadingIcon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp)) },
                                    label = { Text(mood) }
                                )
                            }
                        }
                    }

                    if (item.personalReview.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Personal Review", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                                Text(item.personalReview, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    if (item.notes.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Notes", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                                Text(item.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete \"${item.title}\"?") },
            text = { Text("This permanently removes the title, ratings, and review from your Afterglow library.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(12.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
