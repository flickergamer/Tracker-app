package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.WatchStatus
import com.example.data.model.WatchlistPriority
import com.example.data.model.formatWatchTime
import com.example.ui.theme.AmoledCard
import com.example.ui.theme.AmoledOutline
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalAppThemeMode
import com.example.ui.theme.LocalReducedMotion
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.MinimalDarkCard
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDropped
import com.example.ui.theme.StatusOnHold
import com.example.ui.theme.StatusPlanToWatch
import com.example.ui.theme.StatusWatching
import java.util.Locale

@Composable
fun statusColor(status: WatchStatus): Color = when (status) {
    WatchStatus.COMPLETED -> StatusCompleted
    WatchStatus.WATCHING -> NeonPurple
    WatchStatus.PLAN_TO_WATCH -> StatusPlanToWatch
    WatchStatus.ON_HOLD -> StatusOnHold
    WatchStatus.DROPPED -> StatusDropped
}

@Composable
fun priorityColor(priority: WatchlistPriority): Color = when (priority) {
    WatchlistPriority.HIGH -> NeonPink
    WatchlistPriority.MEDIUM -> NeonAmber
    WatchlistPriority.LOW -> NeonCyan
}

@Composable
fun PosterArtwork(
    posterUrl: String,
    title: String,
    category: MediaCategory,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tokens = LocalZenixTokens.current
    val radii = LocalZenixRadii.current
    val reducedMotion = LocalReducedMotion.current
    val accent = themedCategoryColor(category)
    val fallbackBrush = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF1A1032),
                accent.copy(alpha = 0.28f),
                Color(0xFF0E091B)
            )
        )
        AppThemeMode.MINIMAL_DARK -> Brush.linearGradient(
            colors = listOf(MinimalDarkCard, Color(0xFF282538))
        )
        AppThemeMode.AMOLED_BLACK -> Brush.linearGradient(
            colors = listOf(AmoledCard, Color(0xFF14141F))
        )
    }
    val borderColor = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color.White.copy(alpha = 0.22f)
        AppThemeMode.MINIMAL_DARK -> MinimalDarkOutline
        AppThemeMode.AMOLED_BLACK -> AmoledOutline
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radii.md))
            .background(fallbackBrush)
            .border(1.dp, borderColor, RoundedCornerShape(radii.md)),
        contentAlignment = Alignment.Center
    ) {
        if (posterUrl.isNotBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(posterUrl)
                    .crossfade(!reducedMotion)
                    .build(),
                contentDescription = "$title poster",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = { PosterPlaceholder(title, category) },
                error = { PosterPlaceholder(title, category) }
            )
        } else {
            PosterPlaceholder(title, category)
        }
    }
}

@Composable
private fun PosterPlaceholder(title: String, category: MediaCategory) {
    val tokens = LocalZenixTokens.current
    val accent = themedCategoryColor(category)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (category == MediaCategory.MOVIE) Icons.Rounded.Movie else Icons.Rounded.Tv,
            contentDescription = null,
            tint = accent.copy(alpha = 0.9f),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = tokens.textPrimary.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StatusPill(status: WatchStatus, modifier: Modifier = Modifier) {
    val tokens = LocalZenixTokens.current
    val c = statusColor(status)
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .then(
                if (tokens.useGlow) Modifier.neonGlow(c, cornerRadius = 12.dp, alpha = 0.24f, spread = 4.dp)
                else Modifier
            )
            .clip(shape)
            .background(c.copy(alpha = if (tokens.isLight) 0.14f else 0.16f))
            .border(1.dp, c.copy(alpha = if (tokens.isLight) 0.45f else 0.60f), shape)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = c,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun RatingPill(rating: Double, prefix: String = "", modifier: Modifier = Modifier) {
    val tokens = LocalZenixTokens.current
    val gold = tokens.ratingGold
    val shape = RoundedCornerShape(50)
    val baseBg = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color(0xFF140D22).copy(alpha = 0.80f)
        AppThemeMode.MINIMAL_DARK -> MinimalDarkCard
        AppThemeMode.AMOLED_BLACK -> AmoledCard
    }

    Box(
        modifier = modifier
            .then(
                if (tokens.useGlow) Modifier.neonGlow(gold, cornerRadius = 12.dp, alpha = 0.26f, spread = 4.dp)
                else Modifier
            )
            .clip(shape)
            .background(baseBg)
            .background(gold.copy(alpha = 0.15f))
            .border(1.dp, gold.copy(alpha = 0.65f), shape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = gold,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = if (prefix.isBlank()) String.format(Locale.US, "%.1f", rating)
                else "$prefix ${String.format(Locale.US, "%.1f", rating)}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = gold
            )
        }
    }
}

@Composable
fun StatKpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color = NeonPurple,
    subtitle: String? = null,
    pastelFill: Color? = null,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    NeonGlassCard(
        modifier = modifier,
        accentColor = accentColor,
        cornerRadius = 24.dp,
        glowAlpha = 0.24f,
        pastelFill = pastelFill,
        contentPadding = PaddingValues(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .then(
                            if (tokens.useGlow) Modifier.neonGlow(accentColor, cornerRadius = 11.dp, alpha = 0.34f, spread = 4.dp)
                            else Modifier
                        )
                        .clip(RoundedCornerShape(11.dp))
                        .background(
                            if (tokens.isLight && pastelFill != null) Color.White.copy(alpha = 0.72f)
                            else accentColor.copy(alpha = 0.20f)
                        )
                        .border(1.dp, accentColor.copy(alpha = if (tokens.isLight) 0.35f else 0.55f), RoundedCornerShape(11.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        maxLines = 1
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = tokens.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MediaGridCard(
    item: MediaItem,
    compact: Boolean,
    onClick: () -> Unit,
    onToggleFavourite: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val accent = if (isSelected) NeonCyan else themedCategoryColor(item.categoryEnum)
    val overlayBg = Color(0xFF0A0714).copy(alpha = 0.72f)
    val overlayBorder = Color.White.copy(alpha = 0.26f)
    val haptics = LocalHapticFeedback.current
    val yearText = item.formattedYearOrBlank()
    val platformText = item.normalizedPlatform
    val subLine = listOfNotNull(
        yearText.takeIf { it.isNotBlank() },
        platformText.takeIf { it.isNotBlank() }
    ).joinToString(" • ").ifBlank { item.categoryEnum.label }

    NeonGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, NeonCyan, RoundedCornerShape(24.dp))
                } else Modifier
            )
            .testTag("media_card_${item.id}"),
        accentColor = accent,
        cornerRadius = 24.dp,
        glowAlpha = if (isSelected) 0.42f else 0.24f,
        onClick = onClick,
        onLongClick = if (onLongClick != null) {
            {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongClick()
            }
        } else null,
        contentPadding = PaddingValues(if (compact) 10.dp else 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 150.dp else 185.dp)
        ) {
            PosterArtwork(
                posterUrl = item.posterUrl,
                title = item.title,
                category = item.categoryEnum,
                modifier = Modifier.fillMaxSize()
            )
            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) NeonCyan else overlayBg)
                        .border(1.5.dp, if (isSelected) Color.White else overlayBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Selected",
                            tint = Color(0xFF0A0812),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onEdit != null) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(overlayBg)
                                .border(1.dp, overlayBorder, CircleShape)
                                .clickable(role = Role.Button, onClick = onEdit),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Edit ${item.title}",
                                tint = tokens.textPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(overlayBg)
                            .border(
                                1.dp,
                                if (item.favourite) tokens.dangerAccent.copy(alpha = 0.7f) else overlayBorder,
                                CircleShape
                            )
                            .clickable(role = Role.Button) {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onToggleFavourite()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (item.favourite) "Unfavourite ${item.title}" else "Favourite ${item.title}",
                            tint = if (item.favourite) tokens.dangerAccent else tokens.textPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            color = tokens.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subLine,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (item.personalRating > 0.0) {
                Spacer(modifier = Modifier.width(6.dp))
                RatingPill(rating = item.personalRating)
            }
        }
        if (item.normalizedGenre.isNotBlank()) {
            Text(
                text = item.normalizedGenre,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusPill(status = item.statusEnum)
            if (item.runtimeMinutes > 0 && item.categoryEnum == MediaCategory.MOVIE) {
                Text(
                    text = formatWatchTime(item.runtimeMinutes),
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.textSecondary
                )
            }
        }
    }
}

@Composable
fun SeriesProgressCard(
    item: MediaItem,
    compact: Boolean,
    onClick: () -> Unit,
    onStepEpisode: (Int) -> Unit,
    onToggleFavourite: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val accent = if (isSelected) NeonCyan else themedCategoryColor(item.categoryEnum)
    val barEnd = if (tokens.mode == AppThemeMode.NEON_GLOSSY) {
        if (accent == NeonCyan) NeonGreen else NeonCyan
    } else tokens.primaryAccent
    val haptics = LocalHapticFeedback.current
    val metaLine = listOfNotNull(
        item.categoryEnum.label,
        item.formattedYearOrBlank().takeIf { it.isNotBlank() },
        item.normalizedPlatform.ifBlank { "Library" }
    ).joinToString(" • ")

    NeonGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, NeonCyan, RoundedCornerShape(24.dp))
                } else Modifier
            )
            .testTag("series_card_${item.id}"),
        accentColor = accent,
        cornerRadius = 24.dp,
        glowAlpha = if (isSelected) 0.42f else 0.25f,
        onClick = onClick,
        onLongClick = if (onLongClick != null) {
            {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongClick()
            }
        } else null,
        contentPadding = PaddingValues(if (compact) 12.dp else 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            PosterArtwork(
                posterUrl = item.posterUrl,
                title = item.title,
                category = item.categoryEnum,
                modifier = Modifier
                    .width(74.dp)
                    .height(106.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = tokens.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = metaLine,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (isSelectionMode) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NeonCyan else Color(0xFF0A0714).copy(alpha = 0.72f))
                                .border(1.5.dp, if (isSelected) Color.White else tokens.outline, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = "Selected",
                                    tint = Color(0xFF0A0812),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (onEdit != null) {
                                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                                    Icon(
                                        imageVector = Icons.Rounded.Edit,
                                        contentDescription = "Edit ${item.title}",
                                        tint = tokens.textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onToggleFavourite()
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (item.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Toggle favourite",
                                    tint = if (item.favourite) tokens.dangerAccent else tokens.textSecondary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.formattedCompactSeriesProgress(),
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                NeonProgressBar(
                    progress = item.episodeProgressPercent / 100f,
                    height = 8.dp,
                    startColor = accent,
                    endColor = barEnd
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.remainingEpisodes} eps left • ${formatWatchTime(item.watchedTimeMinutes)} watched",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.textSecondary
                    )
                    if (item.personalRating > 0.0) {
                        RatingPill(rating = item.personalRating)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f, fill = false)) {
                StatusPill(status = item.statusEnum)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(
                    onClick = {
                        if (item.episodesWatched > 0) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onStepEpisode(-1)
                        }
                    },
                    glowColor = accent,
                    size = 38.dp
                ) {
                    Icon(
                        Icons.Rounded.Remove,
                        contentDescription = "Decrease episode",
                        tint = if (item.episodesWatched > 0) tokens.textPrimary else tokens.textSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "${item.episodesWatched}/${item.totalEpisodes}",
                    style = MaterialTheme.typography.labelMedium,
                    color = tokens.textPrimary
                )
                GlassIconButton(
                    onClick = {
                        if (item.totalEpisodes == 0L || item.episodesWatched < item.totalEpisodes) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onStepEpisode(1)
                        }
                    },
                    glowColor = accent,
                    size = 38.dp,
                    modifier = Modifier.testTag("plus_ep_${item.id}")
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = "Watch next episode",
                        tint = if (item.totalEpisodes == 0L || item.episodesWatched < item.totalEpisodes) accent else tokens.textSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MovieTableView(
    movies: List<MediaItem>,
    onSelect: (MediaItem) -> Unit,
    onEdit: (MediaItem) -> Unit,
    onDelete: (MediaItem) -> Unit,
    accentColor: Color = NeonPurple,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val headerBg = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color.White.copy(alpha = 0.08f)
        AppThemeMode.MINIMAL_DARK -> MinimalDarkCard
        AppThemeMode.AMOLED_BLACK -> AmoledCard
    }

    NeonGlassCard(
        modifier = modifier.fillMaxWidth(),
        accentColor = accentColor,
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier
                    .background(headerBg)
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeaderCell("Title", 170)
                TableHeaderCell("Year", 60)
                TableHeaderCell("Genre", 130)
                TableHeaderCell("Platform", 100)
                TableHeaderCell("Status", 125)
                TableHeaderCell("Personal", 75)
                TableHeaderCell("IMDb", 65)
                TableHeaderCell("Runtime", 75)
                TableHeaderCell("Actions", 96)
            }
            HorizontalDivider(color = tokens.outlineSubtle)
            movies.forEach { movie ->
                Row(
                    modifier = Modifier
                        .clickable { onSelect(movie) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = tokens.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(170.dp)
                    )
                    Text(
                        text = movie.formattedYearOrBlank().ifBlank { "—" },
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.textSecondary,
                        modifier = Modifier.width(60.dp)
                    )
                    Text(
                        text = movie.normalizedGenre.ifBlank { "—" },
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(130.dp)
                    )
                    Text(
                        text = movie.normalizedPlatform.ifBlank { "—" },
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(100.dp)
                    )
                    Box(modifier = Modifier.width(125.dp)) {
                        StatusPill(status = movie.statusEnum)
                    }
                    Text(
                        text = if (movie.personalRating > 0) String.format(Locale.US, "%.1f", movie.personalRating) else "—",
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.ratingGold,
                        modifier = Modifier.width(75.dp)
                    )
                    Text(
                        text = if (movie.imdbRating > 0) String.format(Locale.US, "%.1f", movie.imdbRating) else "—",
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.textSecondary,
                        modifier = Modifier.width(65.dp)
                    )
                    Text(
                        text = if (movie.categoryEnum.isEpisodic) {
                            "${movie.episodesWatched}/${movie.totalEpisodes} ep"
                        } else if (movie.runtimeMinutes > 0L) {
                            formatWatchTime(movie.runtimeMinutes)
                        } else "—",
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.textSecondary,
                        modifier = Modifier.width(75.dp)
                    )
                    Row(modifier = Modifier.width(96.dp)) {
                        IconButton(onClick = { onEdit(movie) }) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Edit ${movie.title}", tint = tokens.tertiaryAccent)
                        }
                        IconButton(onClick = { onDelete(movie) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete ${movie.title}", tint = tokens.dangerAccent)
                        }
                    }
                }
                HorizontalDivider(color = tokens.outlineSubtle.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, widthDp: Int) {
    val tokens = LocalZenixTokens.current
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = tokens.secondaryAccent,
        modifier = Modifier.width(widthDp.dp)
    )
}

@Composable
fun EmptyStateView(
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    accentColor: Color = NeonPurple,
    icon: ImageVector = Icons.Rounded.AutoAwesome,
    modifier: Modifier = Modifier
) {
    ZenixEmptyState(
        icon = icon,
        title = title,
        subtitle = subtitle,
        accentColor = accentColor,
        primaryActionLabel = actionLabel,
        onPrimaryAction = onAction,
        modifier = modifier
    )
}
