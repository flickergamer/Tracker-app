package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.formatWatchTime
import com.example.ui.components.AfterglowBackdrop
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.LiquidGlassHeroCard
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassChip
import com.example.ui.components.NeonGlassDialog
import com.example.ui.components.NeonGlassTextField
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.NeonProgressBar
import com.example.ui.components.NeonRatingStars
import com.example.ui.components.PosterArtwork
import com.example.ui.components.RatingPill
import com.example.ui.components.StatusPill
import com.example.ui.components.neonGlow
import com.example.ui.components.themedCategoryColor
import com.example.ui.components.zenixHeading
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.util.ShareCardRenderer
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaDetailScreen(
    item: MediaItem,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStepSeasonEpisode: (Long, Int) -> Unit,
    onAddSeason: (Long) -> Unit,
    onToggleFavourite: () -> Unit,
    onToggleAllTimeFavourite: () -> Unit,
    onToggleHiddenGem: () -> Unit,
    onIncrementRewatch: () -> Unit,
    onDecrementRewatch: () -> Unit,
    onNotify: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val categoryAccent = themedCategoryColor(item.categoryEnum)

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showAddSeasonDialog by remember { mutableStateOf(false) }
    var newSeasonEpInput by remember { mutableStateOf("12") }
    var sharingBusy by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)

    AfterglowBackdrop(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space4, vertical = spacing.space2),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.space3),
                    modifier = Modifier.weight(1f)
                ) {
                    GlassIconButton(
                        onClick = onBack,
                        glowColor = tokens.primaryAccent,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = tokens.textPrimary
                        )
                    }
                    Text(
                        text = item.categoryEnum.label,
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.zenixHeading()
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                    GlassIconButton(
                        onClick = {
                            if (!sharingBusy) {
                                scope.launch {
                                    sharingBusy = true
                                    val bmp = ShareCardRenderer.renderMediaShareBitmap(context, item)
                                    val safeTitle = item.title.replace(Regex("[^a-zA-Z0-9_-]"), "_").ifBlank { "zenix_card" }
                                    ShareCardRenderer.shareBitmap(
                                        context = context,
                                        bitmap = bmp,
                                        fileName = "zenix_$safeTitle",
                                        shareSubject = "Check out ${item.title} on ZENIX"
                                    ).onFailure {
                                        ShareCardRenderer.saveBitmapToGallery(context, bmp, "zenix_$safeTitle")
                                            .onSuccess { msg -> onNotify(msg) }
                                            .onFailure { err -> onNotify("Share failed: ${err.localizedMessage}") }
                                    }
                                    sharingBusy = false
                                }
                            }
                        },
                        glowColor = tokens.secondaryAccent,
                        modifier = Modifier.testTag("detail_share_button")
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = "Share ${item.title}", tint = tokens.secondaryAccent)
                    }
                    GlassIconButton(
                        onClick = onEdit,
                        glowColor = tokens.tertiaryAccent,
                        modifier = Modifier.testTag("detail_edit_button")
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit ${item.title}", tint = tokens.tertiaryAccent)
                    }
                    GlassIconButton(
                        onClick = { showDeleteConfirm = true },
                        glowColor = tokens.dangerAccent,
                        modifier = Modifier.testTag("detail_delete_button")
                    ) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete ${item.title}", tint = tokens.dangerAccent)
                    }
                }
            }
            HorizontalDivider(color = tokens.outlineSubtle)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.space4),
                verticalArrangement = Arrangement.spacedBy(spacing.space4)
            ) {
                // Hero Poster + Core Metadata Card
                LiquidGlassHeroCard(accentColor = categoryAccent) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        PosterArtwork(
                            posterUrl = item.posterUrl,
                            title = item.title,
                            category = item.categoryEnum,
                            modifier = Modifier
                                .width(120.dp)
                                .height(176.dp)
                                .then(
                                    if (tokens.useGlow) {
                                        Modifier.neonGlow(categoryAccent, cornerRadius = 16.dp, alpha = 0.34f, spread = 8.dp)
                                    } else Modifier
                                )
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = tokens.textPrimary
                            )
                            val metaLine = listOfNotNull(
                                item.categoryEnum.label,
                                item.formattedYearOrBlank().takeIf { it.isNotBlank() },
                                item.language.takeIf { it.isNotBlank() },
                                if (item.runtimeMinutes > 0L) {
                                    if (item.categoryEnum == MediaCategory.MOVIE) formatWatchTime(item.runtimeMinutes)
                                    else "${item.runtimeMinutes}m/ep"
                                } else null
                            ).joinToString(" · ")
                            Text(
                                text = metaLine,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary
                            )
                            if (item.normalizedPlatform.isNotBlank()) {
                                Text(
                                    text = "On ${item.normalizedPlatform}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = tokens.tertiaryAccent
                                )
                            }
                            StatusPill(status = item.statusEnum)
                            if (item.personalRating > 0.0) {
                                NeonRatingStars(ratingOutOfTen = item.personalRating, starSize = 17.dp)
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

                // Collection toggles + Save Image shortcut
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
                        label = "Save Card",
                        selected = false,
                        onClick = {
                            if (!sharingBusy) {
                                scope.launch {
                                    sharingBusy = true
                                    val bmp = ShareCardRenderer.renderMediaShareBitmap(context, item)
                                    val safeTitle = item.title.replace(Regex("[^a-zA-Z0-9_-]"), "_").ifBlank { "zenix_card" }
                                    ShareCardRenderer.saveBitmapToGallery(context, bmp, "zenix_$safeTitle")
                                        .onSuccess { onNotify(it) }
                                        .onFailure { onNotify("Save failed: ${it.localizedMessage}") }
                                    sharingBusy = false
                                }
                            }
                        },
                        accentColor = tokens.primaryAccent,
                        leadingIcon = Icons.Rounded.Download
                    )
                }

                // Rewatch Stepper Card
                NeonGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = tokens.positiveAccent,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.Repeat, contentDescription = null, tint = tokens.positiveAccent)
                            Column {
                                Text("Rewatches", style = MaterialTheme.typography.titleSmall, color = tokens.textPrimary)
                                Text(
                                    text = if (item.rewatchCount == 0L) "No rewatches logged"
                                    else "Rewatched ${item.rewatchCount} ${if (item.rewatchCount == 1L) "time" else "times"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textSecondary
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassIconButton(
                                onClick = onDecrementRewatch,
                                glowColor = tokens.positiveAccent,
                                size = 40.dp
                            ) {
                                Icon(
                                    Icons.Rounded.Remove,
                                    contentDescription = "Decrease rewatch count",
                                    tint = if (item.rewatchCount > 0L) tokens.textPrimary else tokens.textSecondary.copy(alpha = 0.4f)
                                )
                            }
                            Text(
                                text = item.rewatchCount.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = tokens.textPrimary
                            )
                            GlassIconButton(
                                onClick = onIncrementRewatch,
                                glowColor = tokens.positiveAccent,
                                size = 40.dp
                            ) {
                                Icon(
                                    Icons.Rounded.Add,
                                    contentDescription = "Increase rewatch count",
                                    tint = tokens.positiveAccent
                                )
                            }
                        }
                    }
                }

                // Season-wise Progress for Episodic Titles
                if (item.categoryEnum.isEpisodic) {
                    val seasonsList = item.effectiveSeasons
                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = categoryAccent,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Season-wise Progress",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = categoryAccent
                                    )
                                    Text(
                                        text = "${item.episodesWatched}/${item.totalEpisodes} episodes · ${item.episodeProgressPercent}% · Est. ${formatWatchTime(item.estimatedTotalSeriesMinutes)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.textSecondary
                                    )
                                }
                                GlassSecondaryButton(
                                    onClick = {
                                        newSeasonEpInput = (seasonsList.lastOrNull()?.totalEpisodes?.takeIf { it > 0L } ?: 12L).toString()
                                        showAddSeasonDialog = true
                                    },
                                    accentBorderColor = categoryAccent,
                                    cornerRadius = radii.sm,
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    modifier = Modifier.testTag("add_season_button")
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Season", style = MaterialTheme.typography.labelLarge)
                                }
                            }

                            NeonProgressBar(
                                progress = item.episodeProgressPercent / 100f,
                                height = 9.dp,
                                startColor = categoryAccent,
                                endColor = tokens.secondaryAccent
                            )

                            seasonsList.forEach { season ->
                                HorizontalDivider(color = tokens.outlineSubtle.copy(alpha = 0.5f))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Season ${season.seasonNumber}",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = tokens.textPrimary
                                            )
                                            Text(
                                                text = if (season.totalEpisodes > 0L) {
                                                    "${season.episodesWatched}/${season.totalEpisodes} eps (${season.progressPercent}%)"
                                                } else {
                                                    "${season.episodesWatched} eps watched"
                                                },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = tokens.textSecondary
                                            )
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            GlassIconButton(
                                                onClick = { if (season.episodesWatched > 0L) onStepSeasonEpisode(season.seasonNumber, -1) },
                                                glowColor = categoryAccent,
                                                size = 38.dp
                                            ) {
                                                Icon(
                                                    Icons.Rounded.Remove,
                                                    contentDescription = "Decrease season ${season.seasonNumber} episode",
                                                    tint = if (season.episodesWatched > 0L) tokens.textPrimary else tokens.textSecondary.copy(alpha = 0.4f),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            GlassIconButton(
                                                onClick = {
                                                    if (season.totalEpisodes == 0L || season.episodesWatched < season.totalEpisodes) {
                                                        onStepSeasonEpisode(season.seasonNumber, 1)
                                                    }
                                                },
                                                glowColor = categoryAccent,
                                                size = 38.dp,
                                                modifier = Modifier.testTag("season_${season.seasonNumber}_plus")
                                            ) {
                                                Icon(
                                                    Icons.Rounded.Add,
                                                    contentDescription = "Increase season ${season.seasonNumber} episode",
                                                    tint = if (season.totalEpisodes == 0L || season.episodesWatched < season.totalEpisodes) categoryAccent else tokens.textSecondary.copy(alpha = 0.4f),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                    if (season.totalEpisodes > 0L) {
                                        NeonProgressBar(
                                            progress = season.progressPercent / 100f,
                                            height = 6.dp,
                                            startColor = categoryAccent,
                                            endColor = tokens.tertiaryAccent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Synopsis
                if (item.synopsis.isNotBlank()) {
                    NeonGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = tokens.tertiaryAccent,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Synopsis", style = MaterialTheme.typography.titleMedium, color = tokens.tertiaryAccent)
                            Text(item.synopsis, style = MaterialTheme.typography.bodyMedium, color = tokens.textPrimary)
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

                // Full Metadata Card
                NeonGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = tokens.primaryAccent,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailRow("Genres", item.normalizedGenre.ifBlank { "—" })
                        DetailRow("Director / Creator", item.director.ifBlank { "—" })
                        DetailRow("Main Cast", item.mainCast.ifBlank { "—" })
                        DetailRow("Country", item.country.ifBlank { "—" })
                        DetailRow("Watched Time", formatWatchTime(item.watchedTimeMinutes))
                        DetailRow("Start Date", item.startDate.ifBlank { "—" })
                        DetailRow("Finish Date", item.completionDate.ifBlank { "—" })
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
                text = "This removes \"${item.title}\", its ratings, and season progress from your library (you can Undo from the toast).",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary
            )
        }
    }

    if (showAddSeasonDialog) {
        val nextNum = (item.effectiveSeasons.maxOfOrNull { it.seasonNumber } ?: 0L) + 1L
        NeonGlassDialog(
            onDismissRequest = { showAddSeasonDialog = false },
            title = "Add Season $nextNum",
            accentColor = categoryAccent,
            confirmText = "Add Season",
            onConfirm = {
                val epCount = (newSeasonEpInput.toLongOrNull() ?: 12L).coerceIn(1L, 5000L)
                showAddSeasonDialog = false
                onAddSeason(epCount)
            }
        ) {
            Text(
                text = "Add Season $nextNum directly to \"${item.title}\" without duplicating the title.",
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary
            )
            NeonGlassTextField(
                value = newSeasonEpInput,
                onValueChange = { newSeasonEpInput = it.filter(Char::isDigit).take(4) },
                label = "Episodes in Season $nextNum",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
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
