package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
import com.example.ui.theme.GoldRating
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDropped
import com.example.ui.theme.StatusOnHold
import com.example.ui.theme.StatusPlanToWatch
import com.example.ui.theme.StatusWatching
import java.util.Locale

@Composable
fun statusColor(status: WatchStatus): Color = when (status) {
    WatchStatus.COMPLETED -> StatusCompleted
    WatchStatus.WATCHING -> StatusWatching
    WatchStatus.PLAN_TO_WATCH -> StatusPlanToWatch
    WatchStatus.ON_HOLD -> StatusOnHold
    WatchStatus.DROPPED -> StatusDropped
}

@Composable
fun priorityColor(priority: WatchlistPriority): Color = when (priority) {
    WatchlistPriority.HIGH -> StatusDropped
    WatchlistPriority.MEDIUM -> StatusOnHold
    WatchlistPriority.LOW -> StatusPlanToWatch
}

@Composable
fun PosterArtwork(
    posterUrl: String,
    title: String,
    category: MediaCategory,
    modifier: Modifier = Modifier
) {
    val fallbackBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.surface
        )
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(fallbackBrush),
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
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StatusPill(status: WatchStatus, modifier: Modifier = Modifier) {
    val c = statusColor(status)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = c.copy(alpha = 0.16f),
        modifier = modifier.border(1.dp, c.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = c,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun RatingPill(rating: Double, prefix: String = "", modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = GoldRating.copy(alpha = 0.14f),
        modifier = modifier.border(1.dp, GoldRating.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = GoldRating,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = if (prefix.isBlank()) String.format(Locale.US, "%.1f", rating)
                else "$prefix ${String.format(Locale.US, "%.1f", rating)}",
                style = MaterialTheme.typography.labelSmall,
                color = GoldRating
            )
        }
    }
}

@Composable
fun StatKpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.16f)),
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("media_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(if (compact) 10.dp else 12.dp)) {
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
                IconButton(
                    onClick = onToggleFavourite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = if (item.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (item.favourite) "Unfavourite ${item.title}" else "Favourite ${item.title}",
                        tint = if (item.favourite) Color(0xFFF472B6) else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
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
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildString {
                    append(item.releaseYear)
                    if (item.streamingPlatform.isNotBlank()) append(" • ${item.streamingPlatform}")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (item.genre.isNotBlank()) {
                Text(
                    text = item.genre,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f),
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("series_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(if (compact) 12.dp else 14.dp)) {
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
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${item.categoryEnum.label} • ${item.releaseYear} • ${item.streamingPlatform.ifBlank { "Library" }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = onToggleFavourite) {
                            Icon(
                                imageVector = if (item.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Toggle favourite",
                                tint = if (item.favourite) Color(0xFFF472B6) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.formattedSeriesProgress(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { item.episodeProgressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    FilledTonalIconButton(
                        onClick = { onStepEpisode(-1) },
                        enabled = item.episodesWatched > 0,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Rounded.Remove, contentDescription = "Decrease episode")
                    }
                    Text(
                        text = "${item.episodesWatched}/${item.totalEpisodes}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FilledTonalIconButton(
                        onClick = { onStepEpisode(1) },
                        enabled = item.totalEpisodes == 0L || item.episodesWatched < item.totalEpisodes,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("plus_ep_${item.id}")
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Watch next episode")
                    }
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
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
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
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            movies.forEach { movie ->
                Row(
                    modifier = Modifier
                        .clickable { onSelect(movie) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(170.dp)
                    )
                    Text(
                        text = movie.releaseYear.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(60.dp)
                    )
                    Text(
                        text = movie.genre.ifBlank { "—" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(130.dp)
                    )
                    Text(
                        text = movie.streamingPlatform.ifBlank { "—" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        color = GoldRating,
                        modifier = Modifier.width(75.dp)
                    )
                    Text(
                        text = if (movie.imdbRating > 0) String.format(Locale.US, "%.1f", movie.imdbRating) else "—",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(65.dp)
                    )
                    Text(
                        text = formatWatchTime(movie.runtimeMinutes),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(75.dp)
                    )
                    Row(modifier = Modifier.width(96.dp)) {
                        IconButton(onClick = { onEdit(movie) }) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Edit ${movie.title}", tint = MaterialTheme.colorScheme.secondary)
                        }
                        IconButton(onClick = { onDelete(movie) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete ${movie.title}", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
            }
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, widthDp: Int) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.width(widthDp.dp)
    )
}

@Composable
fun EmptyStateView(
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_empty_reel),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (actionLabel != null && onAction != null) {
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(actionLabel)
                }
            }
        }
    }
}
