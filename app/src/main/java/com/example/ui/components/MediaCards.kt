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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.R
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.WatchStatus
import com.example.data.model.WatchlistPriority
import com.example.data.model.formatWatchTime
import com.example.ui.theme.AndroidOutlineVariant
import com.example.ui.theme.AndroidPrimary
import com.example.ui.theme.AndroidSurface
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalAppThemeMode
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PastelAccentCoral
import com.example.ui.theme.PastelAccentGold
import com.example.ui.theme.PastelAccentMint
import com.example.ui.theme.PastelAccentPlum
import com.example.ui.theme.PastelAccentTeal
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelOutline
import com.example.ui.theme.PastelOutlineStrong
import com.example.ui.theme.PastelSky
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDropped
import com.example.ui.theme.StatusOnHold
import com.example.ui.theme.StatusPlanToWatch
import com.example.ui.theme.StatusWatching
import java.util.Locale

@Composable
fun statusColor(status: WatchStatus): Color = when (LocalAppThemeMode.current) {
    AppThemeMode.NEON -> when (status) {
        WatchStatus.COMPLETED -> StatusCompleted
        WatchStatus.WATCHING -> StatusWatching
        WatchStatus.PLAN_TO_WATCH -> StatusPlanToWatch
        WatchStatus.ON_HOLD -> StatusOnHold
        WatchStatus.DROPPED -> StatusDropped
    }
    AppThemeMode.ANDROID_UI -> when (status) {
        WatchStatus.COMPLETED -> Color(0xFF81C784)
        WatchStatus.WATCHING -> AndroidPrimary
        WatchStatus.PLAN_TO_WATCH -> Color(0xFF80D8FF)
        WatchStatus.ON_HOLD -> Color(0xFFFFD54F)
        WatchStatus.DROPPED -> Color(0xFFF2B8B5)
    }
    AppThemeMode.MINIMAL_PASTEL -> when (status) {
        WatchStatus.COMPLETED -> PastelAccentMint
        WatchStatus.WATCHING -> PastelAccentPlum
        WatchStatus.PLAN_TO_WATCH -> PastelAccentTeal
        WatchStatus.ON_HOLD -> PastelAccentGold
        WatchStatus.DROPPED -> PastelAccentCoral
    }
}

@Composable
fun priorityColor(priority: WatchlistPriority): Color = when (LocalAppThemeMode.current) {
    AppThemeMode.NEON -> when (priority) {
        WatchlistPriority.HIGH -> NeonPink
        WatchlistPriority.MEDIUM -> StatusOnHold
        WatchlistPriority.LOW -> NeonCyan
    }
    AppThemeMode.ANDROID_UI -> when (priority) {
        WatchlistPriority.HIGH -> Color(0xFFF2B8B5)
        WatchlistPriority.MEDIUM -> Color(0xFFFFD54F)
        WatchlistPriority.LOW -> Color(0xFF80D8FF)
    }
    AppThemeMode.MINIMAL_PASTEL -> when (priority) {
        WatchlistPriority.HIGH -> PastelAccentCoral
        WatchlistPriority.MEDIUM -> PastelAccentGold
        WatchlistPriority.LOW -> PastelAccentTeal
    }
}

@Composable
fun PosterArtwork(
    posterUrl: String,
    title: String,
    category: MediaCategory,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val accent = themedCategoryColor(category)
    val fallbackBrush = when (tokens.mode) {
        AppThemeMode.NEON -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF1A1032),
                accent.copy(alpha = 0.25f),
                Color(0xFF0E091B)
            )
        )
        AppThemeMode.ANDROID_UI -> Brush.linearGradient(
            colors = listOf(AndroidSurface, Color(0xFF332D41))
        )
        AppThemeMode.MINIMAL_PASTEL -> Brush.linearGradient(
            colors = listOf(PastelLavender, PastelSky)
        )
    }
    val borderColor = when (tokens.mode) {
        AppThemeMode.NEON -> Color.White.copy(alpha = 0.22f)
        AppThemeMode.ANDROID_UI -> AndroidOutlineVariant
        AppThemeMode.MINIMAL_PASTEL -> PastelOutline
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(fallbackBrush)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (posterUrl.isNotBlank()) {
            SubcomposeAsyncImage(
                model = posterUrl,
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
            maxLines = 1
        )
    }
}

@Composable
fun RatingPill(rating: Double, prefix: String = "", modifier: Modifier = Modifier) {
    val tokens = LocalZenixTokens.current
    val gold = tokens.ratingGold
    val shape = RoundedCornerShape(50)
    val baseBg = when (tokens.mode) {
        AppThemeMode.NEON -> Color(0xFF140D22).copy(alpha = 0.80f)
        AppThemeMode.ANDROID_UI -> AndroidSurface
        AppThemeMode.MINIMAL_PASTEL -> Color(0xFFFFF8E8)
    }

    Box(
        modifier = modifier
            .then(
                if (tokens.useGlow) Modifier.neonGlow(gold, cornerRadius = 12.dp, alpha = 0.26f, spread = 4.dp)
                else Modifier
            )
            .clip(shape)
            .background(baseBg)
            .background(gold.copy(alpha = if (tokens.isLight) 0.12f else 0.15f))
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
        cornerRadius = 22.dp,
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
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val accent = themedCategoryColor(item.categoryEnum)
    val overlayBg = if (tokens.isLight) Color.White.copy(alpha = 0.90f) else Color(0xFF0A0714).copy(alpha = 0.72f)
    val overlayBorder = if (tokens.isLight) PastelOutlineStrong else Color.White.copy(alpha = 0.26f)

    NeonGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("media_card_${item.id}"),
        accentColor = accent,
        cornerRadius = 22.dp,
        glowAlpha = 0.24f,
        onClick = onClick,
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
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (onEdit != null) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(overlayBg)
                            .border(1.dp, overlayBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit ${item.title}",
                            tint = tokens.textPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onToggleFavourite,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(overlayBg)
                        .border(
                            1.dp,
                            if (item.favourite) tokens.dangerAccent.copy(alpha = 0.7f) else overlayBorder,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (item.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (item.favourite) "Unfavourite ${item.title}" else "Favourite ${item.title}",
                        tint = if (item.favourite) tokens.dangerAccent else tokens.textPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            if (item.personalRating > 0.0) {
                RatingPill(
                    rating = item.personalRating,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                )
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
        Text(
            text = buildString {
                append(item.releaseYear)
                if (item.streamingPlatform.isNotBlank()) append(" • ${item.streamingPlatform}")
            },
            style = MaterialTheme.typography.bodySmall,
            color = tokens.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (item.genre.isNotBlank()) {
            Text(
                text = item.genre,
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
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val accent = themedCategoryColor(item.categoryEnum)
    val barEnd = if (tokens.mode == AppThemeMode.NEON) {
        if (accent == NeonCyan) NeonGreen else NeonCyan
    } else tokens.tertiaryAccent

    NeonGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("series_card_${item.id}"),
        accentColor = accent,
        cornerRadius = 22.dp,
        glowAlpha = 0.25f,
        onClick = onClick,
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
                            text = "${item.categoryEnum.label} • ${item.releaseYear} • ${item.streamingPlatform.ifBlank { "Library" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
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
                        IconButton(onClick = onToggleFavourite, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = if (item.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Toggle favourite",
                                tint = if (item.favourite) tokens.dangerAccent else tokens.textSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.formattedSeriesProgress(),
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    maxLines = 2,
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
            StatusPill(status = item.statusEnum)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(
                    onClick = { if (item.episodesWatched > 0) onStepEpisode(-1) },
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
        AppThemeMode.NEON -> Color.White.copy(alpha = 0.08f)
        AppThemeMode.ANDROID_UI -> AndroidSurface
        AppThemeMode.MINIMAL_PASTEL -> PastelLavender.copy(alpha = 0.55f)
    }

    NeonGlassCard(
        modifier = modifier.fillMaxWidth(),
        accentColor = accentColor,
        cornerRadius = 22.dp,
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
                        text = movie.releaseYear.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.textSecondary,
                        modifier = Modifier.width(60.dp)
                    )
                    Text(
                        text = movie.genre.ifBlank { "—" },
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(130.dp)
                    )
                    Text(
                        text = movie.streamingPlatform.ifBlank { "—" },
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
                        text = if (movie.categoryEnum.isEpisodic) "${movie.episodesWatched}/${movie.totalEpisodes} ep" else formatWatchTime(movie.runtimeMinutes),
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
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    NeonGlassCard(
        modifier = modifier.fillMaxWidth(),
        accentColor = accentColor,
        cornerRadius = 22.dp,
        contentPadding = PaddingValues(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_empty_reel),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, tokens.outlineSubtle, RoundedCornerShape(16.dp))
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = tokens.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary,
                textAlign = TextAlign.Center
            )
            if (actionLabel != null && onAction != null) {
                NeonPrimaryButton(
                    onClick = onAction,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(actionLabel, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
