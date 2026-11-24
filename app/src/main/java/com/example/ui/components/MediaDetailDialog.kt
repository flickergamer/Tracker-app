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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MediaItem
import com.example.data.model.formatWatchTime
import com.example.ui.theme.GoldRating
import com.example.ui.theme.MutedGreyText
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonFuchsia
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OffWhiteText

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
    val categoryNeon = categoryNeonColor(item.categoryEnum)

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
                    GlassIconButton(onClick = onDismiss, glowColor = NeonPurple) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close details", tint = OffWhiteText)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassIconButton(onClick = onEdit, glowColor = NeonCyan) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Edit title", tint = NeonCyan)
                        }
                        GlassIconButton(onClick = { showDeleteConfirm = true }, glowColor = NeonPink) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete title", tint = NeonPink)
                        }
                    }
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

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
                                    .neonGlow(categoryNeon, cornerRadius = 16.dp, alpha = 0.34f, spread = 8.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = OffWhiteText
                                )
                                Text(
                                    text = "${item.categoryEnum.label} • ${item.releaseYear} • ${item.language.ifBlank { "—" }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedGreyText
                                )
                                if (item.streamingPlatform.isNotBlank()) {
                                    Text(
                                        text = "On ${item.streamingPlatform}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = NeonCyan
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

                    // Quick Action Flags
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NeonGlassChip(
                            label = if (item.favourite) "Favourited" else "Favourite",
                            selected = item.favourite,
                            onClick = onToggleFavourite,
                            accentColor = NeonPink,
                            leadingIcon = if (item.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                        )
                        NeonGlassChip(
                            label = if (item.allTimeFavourite) "All-Time Fav" else "Mark All-Time",
                            selected = item.allTimeFavourite,
                            onClick = onToggleAllTimeFavourite,
                            accentColor = GoldRating,
                            leadingIcon = Icons.Filled.Star
                        )
                        NeonGlassChip(
                            label = if (item.hiddenGem) "Hidden Gem" else "Mark Gem",
                            selected = item.hiddenGem,
                            onClick = onToggleHiddenGem,
                            accentColor = NeonCyan,
                            leadingIcon = Icons.Rounded.Diamond
                        )
                        NeonGlassChip(
                            label = "Rewatch (${item.rewatchCount})",
                            selected = item.rewatchCount > 0L,
                            onClick = onIncrementRewatch,
                            accentColor = NeonGreen,
                            leadingIcon = Icons.Rounded.Repeat
                        )
                    }

                    // Episode Progress Section for Series/Anime/K-Drama
                    if (item.categoryEnum.isEpisodic) {
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = categoryNeon,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = item.formattedSeriesProgress(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = categoryNeon
                                )
                                NeonProgressBar(
                                    progress = item.episodeProgressPercent / 100f,
                                    height = 9.dp,
                                    startColor = categoryNeon,
                                    endColor = NeonFuchsia
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
                                            color = OffWhiteText
                                        )
                                        Text(
                                            text = "${item.remainingEpisodes} episodes remaining • Est. total ${formatWatchTime(item.estimatedTotalSeriesMinutes)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MutedGreyText
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        GlassIconButton(
                                            onClick = { if (item.episodesWatched > 0) onStepEpisode(-1) },
                                            glowColor = categoryNeon,
                                            size = 44.dp
                                        ) {
                                            Icon(Icons.Rounded.Remove, contentDescription = "Previous episode", tint = OffWhiteText)
                                        }
                                        GlassIconButton(
                                            onClick = {
                                                if (item.totalEpisodes == 0L || item.episodesWatched < item.totalEpisodes) {
                                                    onStepEpisode(1)
                                                }
                                            },
                                            glowColor = categoryNeon,
                                            size = 44.dp
                                        ) {
                                            Icon(Icons.Rounded.Add, contentDescription = "Next episode", tint = categoryNeon)
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
                                    accentBorderColor = GoldRating,
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
                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = NeonPurple,
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
                                    accentColor = NeonFuchsia,
                                    leadingIcon = Icons.Rounded.AutoAwesome
                                )
                            }
                        }
                    }

                    if (item.personalReview.isNotBlank()) {
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = NeonFuchsia,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Personal Review", style = MaterialTheme.typography.titleMedium, color = NeonFuchsia)
                                Text(item.personalReview, style = MaterialTheme.typography.bodyMedium, color = OffWhiteText)
                            }
                        }
                    }

                    if (item.notes.isNotBlank()) {
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = NeonCyan,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Notes", style = MaterialTheme.typography.titleMedium, color = NeonCyan)
                                Text(item.notes, style = MaterialTheme.typography.bodyMedium, color = MutedGreyText)
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
            accentColor = NeonPink,
            confirmText = "Delete",
            confirmDangerous = true,
            onConfirm = {
                showDeleteConfirm = false
                onDelete()
            }
        ) {
            Text(
                text = "This permanently removes the title, ratings, and review from your Afterglow library.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGreyText
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MutedGreyText)
        Spacer(modifier = Modifier.width(12.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = OffWhiteText)
    }
}
