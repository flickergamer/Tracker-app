package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MediaItem
import com.example.data.model.formatWatchTime
import com.example.ui.theme.LocalZenixTokens

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
    val tokens = LocalZenixTokens.current
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val categoryAccent = themedCategoryColor(item.categoryEnum)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AfterglowBackdrop(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassIconButton(onClick = onDismiss, glowColor = tokens.primaryAccent) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close details", tint = tokens.textPrimary)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassIconButton(onClick = onEdit, glowColor = tokens.tertiaryAccent) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Edit title", tint = tokens.tertiaryAccent)
                        }
                        GlassIconButton(onClick = { showDeleteConfirm = true }, glowColor = tokens.dangerAccent) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete title", tint = tokens.dangerAccent)
                        }
                    }
                }
                HorizontalDivider(color = tokens.outlineSubtle)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LiquidGlassHeroCard {
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            PosterArtwork(
                                posterUrl = item.posterUrl,
                                title = item.title,
                                category = item.categoryEnum,
                                modifier = Modifier
                                    .width(116.dp)
                                    .height(168.dp)
                                    .then(
                                        if (tokens.useGlow) Modifier.neonGlow(categoryAccent, cornerRadius = 16.dp, alpha = 0.34f, spread = 8.dp)
                                        else Modifier
                                    )
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = tokens.textPrimary
                                )
                                Text(
                                    text = "${item.categoryEnum.label} • ${item.releaseYear} • ${item.language.ifBlank { "—" }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textSecondary
                                )
                                if (item.streamingPlatform.isNotBlank()) {
                                    Text(
                                        text = "On ${item.streamingPlatform}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = tokens.tertiaryAccent
                                    )
                                }
                                StatusPill(status = item.statusEnum)
                                if (item.personalRating > 0.0) {
                                    NeonRatingStars(ratingOutOfTen = item.personalRating)
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
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NeonGlassChip(
                            label = if (item.favourite) "Favourited" else "Favourite",
                            selected = item.favourite,
                            onClick = onToggleFavourite,
                            accentColor = tokens.dangerAccent,
                            leadingIcon = if (item.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                        )
                        NeonGlassChip(
                            label = if (item.allTimeFavourite) "All-Time Fav" else "Mark All-Time",
                            selected = item.allTimeFavourite,
                            onClick = onToggleAllTimeFavourite,
                            accentColor = tokens.ratingGold,
                            leadingIcon = Icons.Filled.Star
                        )
                        NeonGlassChip(
                            label = if (item.hiddenGem) "Hidden Gem" else "Mark Gem",
                            selected = item.hiddenGem,
                            onClick = onToggleHiddenGem,
                            accentColor = tokens.tertiaryAccent,
                            leadingIcon = Icons.Rounded.Diamond
                        )
                        NeonGlassChip(
                            label = "Rewatch (${item.rewatchCount})",
                            selected = item.rewatchCount > 0L,
                            onClick = onIncrementRewatch,
                            accentColor = tokens.positiveAccent,
                            leadingIcon = Icons.Rounded.Repeat
                        )
                    }

                    if (item.categoryEnum.isEpisodic) {
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = categoryAccent,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = item.formattedSeriesProgress(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = categoryAccent
                                )
                                NeonProgressBar(
                                    progress = item.episodeProgressPercent / 100f,
                                    height = 9.dp,
                                    startColor = categoryAccent,
                                    endColor = tokens.secondaryAccent
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Season ${item.currentSeason}/${item.totalSeasons} (${item.seasonProgressPercent}%)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = tokens.textPrimary
                                        )
                                        Text(
                                            text = "${item.remainingEpisodes} episodes remaining • Est. total ${formatWatchTime(item.estimatedTotalSeriesMinutes)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = tokens.textSecondary
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        GlassIconButton(
                                            onClick = { if (item.episodesWatched > 0) onStepEpisode(-1) },
                                            glowColor = categoryAccent,
                                            size = 44.dp
                                        ) {
                                            Icon(Icons.Rounded.Remove, contentDescription = "Previous episode", tint = tokens.textPrimary)
                                        }
                                        GlassIconButton(
                                            onClick = {
                                                if (item.totalEpisodes == 0L || item.episodesWatched < item.totalEpisodes) {
                                                    onStepEpisode(1)
                                                }
                                            },
                                            glowColor = categoryAccent,
                                            size = 44.dp
                                        ) {
                                            Icon(Icons.Rounded.Add, contentDescription = "Next episode", tint = categoryAccent)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (item.trailerLink.isNotBlank() || item.imdbLink.isNotBlank()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (item.trailerLink.isNotBlank()) {
                                NeonPrimaryButton(
                                    onClick = {
                                        runCatching {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.trailerLink)))
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Watch Trailer", fontWeight = FontWeight.Bold)
                                }
                            }
                            if (item.imdbLink.isNotBlank()) {
                                GlassSecondaryButton(
                                    onClick = {
                                        runCatching {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.imdbLink)))
                                        }
                                    },
                                    accentBorderColor = tokens.ratingGold,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("IMDb Page")
                                }
                            }
                        }
                    }

                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = tokens.primaryAccent,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                NeonGlassChip(
                                    label = mood,
                                    selected = false,
                                    onClick = {},
                                    accentColor = tokens.secondaryAccent,
                                    leadingIcon = Icons.Rounded.AutoAwesome
                                )
                            }
                        }
                    }

                    if (item.personalReview.isNotBlank()) {
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = tokens.secondaryAccent,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Personal Review", style = MaterialTheme.typography.titleMedium, color = tokens.secondaryAccent)
                                Text(item.personalReview, style = MaterialTheme.typography.bodyMedium, color = tokens.textPrimary)
                            }
                        }
                    }

                    if (item.notes.isNotBlank()) {
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = tokens.tertiaryAccent,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Notes", style = MaterialTheme.typography.titleMedium, color = tokens.tertiaryAccent)
                                Text(item.notes, style = MaterialTheme.typography.bodyMedium, color = tokens.textSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showDeleteConfirm) {
        NeonGlassDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = "Delete \"${item.title}\"?",
            accentColor = tokens.dangerAccent,
            confirmText = "Delete",
            confirmDangerous = true,
            onConfirm = {
                showDeleteConfirm = false
                onDelete()
            }
        ) {
            Text(
                text = "This permanently removes the title, ratings, and review from your Zenix library.",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    val tokens = LocalZenixTokens.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
        Spacer(modifier = Modifier.width(12.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = tokens.textPrimary)
    }
}
