package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.AVAILABLE_MOODS
import com.example.data.model.AchievementBadge
import com.example.data.model.DashboardStats
import com.example.data.model.MediaCategory
import com.example.data.model.MediaItem
import com.example.data.model.QuoteItem
import com.example.data.model.UserProfile
import com.example.data.model.WatchStatus
import com.example.data.model.formatWatchTime
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GoalRingCard
import com.example.ui.components.HorizontalScrollFadeBox
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassChip
import com.example.ui.components.NeonGlassDialog
import com.example.ui.components.NeonGlassTextField
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.NeonProgressBar
import com.example.ui.components.PosterArtwork
import com.example.ui.components.RatingPill
import com.example.ui.components.themedCategoryColor
import com.example.ui.components.themedPaletteColor
import com.example.ui.components.zenixHeading
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SmartHubScreen(
    initialTab: Int,
    allItems: List<MediaItem>,
    quotes: List<QuoteItem>,
    achievements: List<AchievementBadge>,
    stats: DashboardStats,
    profile: UserProfile,
    onSelectItem: (MediaItem) -> Unit,
    onResumeDropped: (MediaItem) -> Unit,
    onIncrementRewatch: (MediaItem) -> Unit,
    onSaveQuote: (QuoteItem, Boolean) -> Unit,
    onDeleteQuote: (QuoteItem) -> Unit,
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val tabs = listOf(
        "Random Picker",
        "Mood Match",
        "Collections",
        "Badges & Goals",
        "Quotes",
        "History & Reviews"
    )
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab.coerceIn(0, tabs.lastIndex)) }

    var randomCategoryFilter by remember { mutableStateOf<MediaCategory?>(null) }
    var randomOnlyUnwatched by remember { mutableStateOf(true) }
    var pickedRandomItem by remember { mutableStateOf<MediaItem?>(null) }

    var selectedMood by remember { mutableStateOf(AVAILABLE_MOODS.first()) }
    var collectionMode by remember { mutableIntStateOf(0) }

    var editingQuote by remember { mutableStateOf<QuoteItem?>(null) }
    var showQuoteDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space4, vertical = spacing.space3),
            verticalArrangement = Arrangement.spacedBy(spacing.space3)
        ) {
            Text(
                text = "Smart Discovery",
                style = MaterialTheme.typography.displayMedium,
                color = tokens.textPrimary,
                modifier = Modifier.zenixHeading()
            )
            val tabScrollState = rememberScrollState()
            HorizontalScrollFadeBox(
                scrollState = tabScrollState,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.horizontalScroll(tabScrollState),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                ) {
                    tabs.forEachIndexed { idx, label ->
                        NeonGlassChip(
                            label = label,
                            selected = selectedTab == idx,
                            onClick = { selectedTab = idx },
                            accentColor = themedPaletteColor(idx)
                        )
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(start = spacing.space4, end = spacing.space4, top = spacing.space2, bottom = bottomPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.space4),
            modifier = Modifier.fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> {
                    item {
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = tokens.tertiaryAccent,
                            cornerRadius = radii.lg,
                            contentPadding = PaddingValues(spacing.space4)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                                Text("Random Tonight Picker", style = MaterialTheme.typography.titleLarge, color = tokens.textPrimary)
                                Text(
                                    "Can't decide what to watch? Spin your personal vault or watchlist.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.textSecondary
                                )
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    NeonGlassChip(
                                        label = "Unwatched / Watchlist Only",
                                        selected = randomOnlyUnwatched,
                                        onClick = { randomOnlyUnwatched = !randomOnlyUnwatched },
                                        accentColor = tokens.tertiaryAccent
                                    )
                                    NeonGlassChip(
                                        label = "Any Category",
                                        selected = randomCategoryFilter == null,
                                        onClick = { randomCategoryFilter = null },
                                        accentColor = tokens.primaryAccent
                                    )
                                    MediaCategory.entries.forEach { c ->
                                        NeonGlassChip(
                                            label = c.label,
                                            selected = randomCategoryFilter == c,
                                            onClick = { randomCategoryFilter = c },
                                            accentColor = themedCategoryColor(c)
                                        )
                                    }
                                }
                                val pool = allItems.filter { item ->
                                    val matchesCat = randomCategoryFilter == null || item.categoryEnum == randomCategoryFilter
                                    val matchesStatus = !randomOnlyUnwatched || item.statusEnum == WatchStatus.PLAN_TO_WATCH || item.watchlistItem || item.statusEnum == WatchStatus.ON_HOLD
                                    matchesCat && matchesStatus
                                }
                                NeonPrimaryButton(
                                    onClick = {
                                        pickedRandomItem = if (pool.isNotEmpty()) pool[Random.nextInt(pool.size)] else null
                                    },
                                    enabled = pool.isNotEmpty(),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Rounded.Casino, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (pool.isEmpty()) "No Matching Titles in Pool" else "Pick Random Title (${pool.size})", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    pickedRandomItem?.let { picked ->
                        item {
                            SmartMediaCard(item = picked, badge = "TONIGHT'S PICK", onClick = { onSelectItem(picked) })
                        }
                    }
                }

                1 -> {
                    item {
                        Text("Pick Your Mood", style = MaterialTheme.typography.titleLarge, color = tokens.textPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AVAILABLE_MOODS.forEachIndexed { idx, mood ->
                                NeonGlassChip(
                                    label = mood,
                                    selected = selectedMood == mood,
                                    onClick = { selectedMood = mood },
                                    accentColor = themedPaletteColor(idx)
                                )
                            }
                        }
                    }
                    val moodMatches = allItems.filter { item ->
                        item.moodTags.any { it.equals(selectedMood, true) } || moodMatchesGenre(selectedMood, item.genre)
                    }.sortedByDescending { it.personalRating }

                    if (moodMatches.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "No \"$selectedMood\" Matches Yet",
                                subtitle = "Tag titles with moods in the editor or add matching genres like Thriller, Comedy, or Romance.",
                                accentColor = tokens.dangerAccent
                            )
                        }
                    } else {
                        items(moodMatches, key = { "mood_${it.id}" }) { item ->
                            SmartMediaCard(item = item, badge = selectedMood.uppercase(), onClick = { onSelectItem(item) })
                        }
                    }
                }

                2 -> {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NeonGlassChip(label = "Hidden Gems", selected = collectionMode == 0, onClick = { collectionMode = 0 }, accentColor = tokens.tertiaryAccent)
                            NeonGlassChip(label = "Rewatch", selected = collectionMode == 1, onClick = { collectionMode = 1 }, accentColor = tokens.positiveAccent)
                            NeonGlassChip(label = "Dropped", selected = collectionMode == 2, onClick = { collectionMode = 2 }, accentColor = tokens.dangerAccent)
                        }
                    }
                    val list = when (collectionMode) {
                        0 -> allItems.filter { it.hiddenGem || (it.personalRating >= 8.0 && it.imdbRating in 0.1..7.4) }
                        1 -> allItems.filter { it.rewatchFlag || it.rewatchCount > 0L }
                        else -> allItems.filter { it.statusEnum == WatchStatus.DROPPED }
                    }
                    if (list.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "Collection Empty",
                                subtitle = when (collectionMode) {
                                    0 -> "Mark underrated favourites as Hidden Gems to build your curated vault."
                                    1 -> "Mark titles for Rewatch or log a rewatch count to track your comfort classics."
                                    else -> "No dropped titles. Everything you started is still going strong!"
                                },
                                accentColor = tokens.primaryAccent
                            )
                        }
                    } else {
                        items(list, key = { "col_${it.id}" }) { item ->
                            val accent = when (collectionMode) {
                                0 -> tokens.tertiaryAccent
                                1 -> tokens.positiveAccent
                                else -> tokens.dangerAccent
                            }
                            NeonGlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = accent,
                                cornerRadius = 22.dp,
                                onClick = { onSelectItem(item) },
                                contentPadding = PaddingValues(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    PosterArtwork(item.posterUrl, item.title, item.categoryEnum, Modifier.size(56.dp, 80.dp))
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(item.title, style = MaterialTheme.typography.titleMedium, color = tokens.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        val colMeta = listOfNotNull(
                                            item.categoryEnum.label,
                                            item.formattedYearOrBlank().takeIf { it.isNotBlank() }
                                        ).joinToString(" • ")
                                        Text(colMeta, style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                        if (collectionMode == 1) {
                                            Text("Rewatched ${item.rewatchCount}×", style = MaterialTheme.typography.labelMedium, color = tokens.positiveAccent)
                                        }
                                        if (collectionMode == 2 && item.notes.isNotBlank()) {
                                            Text(item.notes, style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                    when (collectionMode) {
                                        1 -> GlassSecondaryButton(
                                            onClick = { onIncrementRewatch(item) },
                                            accentBorderColor = tokens.positiveAccent,
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Icon(Icons.Rounded.Repeat, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("+1")
                                        }
                                        2 -> GlassSecondaryButton(
                                            onClick = { onResumeDropped(item) },
                                            accentBorderColor = tokens.tertiaryAccent,
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Resume")
                                        }
                                        else -> Icon(Icons.Rounded.Diamond, contentDescription = null, tint = tokens.tertiaryAccent)
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    item {
                        Text("Watching Goals", style = MaterialTheme.typography.titleLarge, color = tokens.textPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            GoalRingCard("Movies", stats.completedMoviesThisYear, profile.yearlyMovieGoal.toInt(), tokens.primaryAccent, Modifier.weight(1f))
                            GoalRingCard("Series", stats.completedSeriesThisYear, profile.yearlySeriesGoal.toInt(), tokens.positiveAccent, Modifier.weight(1f))
                            GoalRingCard("Episodes", stats.episodesWatchedTotal, profile.yearlyEpisodeGoal.toInt(), tokens.tertiaryAccent, Modifier.weight(1f))
                        }
                    }
                    item {
                        val unlocked = achievements.count { it.unlocked }
                        Text(
                            text = "Achievement Badges ($unlocked/${achievements.size} Unlocked)",
                            style = MaterialTheme.typography.titleLarge,
                            color = tokens.textPrimary
                        )
                    }
                    items(achievements, key = { it.id }) { badge ->
                        val accent = if (badge.unlocked) tokens.ratingGold else tokens.primaryAccent
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = accent,
                            cornerRadius = 22.dp,
                            contentPadding = PaddingValues(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = if (badge.unlocked) Icons.Rounded.CheckCircle else Icons.Rounded.EmojiEvents,
                                            contentDescription = null,
                                            tint = if (badge.unlocked) tokens.ratingGold else tokens.textSecondary
                                        )
                                        Column {
                                            Text(badge.title, style = MaterialTheme.typography.titleMedium, color = tokens.textPrimary)
                                            Text(badge.description, style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                        }
                                    }
                                    Text("${badge.current.coerceAtMost(badge.target)}/${badge.target}", style = MaterialTheme.typography.labelMedium, color = accent)
                                }
                                NeonProgressBar(
                                    progress = badge.progress,
                                    height = 7.dp,
                                    startColor = if (badge.unlocked) tokens.ratingGold else tokens.primaryAccent,
                                    endColor = if (badge.unlocked) tokens.positiveAccent else tokens.secondaryAccent
                                )
                            }
                        }
                    }
                }

                4 -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Favourite Quotes", style = MaterialTheme.typography.titleLarge, color = tokens.textPrimary)
                            NeonPrimaryButton(
                                onClick = { editingQuote = null; showQuoteDialog = true },
                                cornerRadius = 14.dp,
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Quote", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (quotes.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "No Saved Quotes Yet",
                                subtitle = "Collect unforgettable dialogue from movies, series, anime, and K-dramas.",
                                actionLabel = "Add First Quote",
                                accentColor = tokens.secondaryAccent,
                                onAction = { editingQuote = null; showQuoteDialog = true }
                            )
                        }
                    } else {
                        items(quotes, key = { it.id }) { q ->
                            val qAccent = themedCategoryColor(q.categoryEnum)
                            NeonGlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = qAccent,
                                cornerRadius = 22.dp,
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Rounded.FormatQuote, contentDescription = null, tint = qAccent)
                                    Text(
                                        text = "\"${q.quoteText}\"",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontStyle = FontStyle.Italic,
                                        color = tokens.textPrimary
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = buildString {
                                                if (q.characterName.isNotBlank()) append("${q.characterName} — ")
                                                append("${q.sourceTitle} (${q.categoryEnum.label})")
                                            },
                                            style = MaterialTheme.typography.labelMedium,
                                            color = qAccent,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Row {
                                            IconButton(
                                                onClick = { editingQuote = q; showQuoteDialog = true },
                                                modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                            ) {
                                                Icon(Icons.Rounded.Edit, contentDescription = "Edit quote", tint = tokens.tertiaryAccent)
                                            }
                                            IconButton(
                                                onClick = { onDeleteQuote(q) },
                                                modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                                            ) {
                                                Icon(Icons.Rounded.Delete, contentDescription = "Delete quote", tint = tokens.dangerAccent)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                5 -> {
                    val reviewed = allItems.filter { it.personalReview.isNotBlank() }
                    item {
                        Text("Recently Watched History", style = MaterialTheme.typography.titleLarge, color = tokens.textPrimary)
                    }
                    val history = allItems.filter { it.statusEnum == WatchStatus.COMPLETED || it.statusEnum == WatchStatus.WATCHING }.take(10)
                    if (history.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "No Watch History Yet",
                                subtitle = "Start or complete titles to build your chronological viewing log.",
                                accentColor = tokens.primaryAccent
                            )
                        }
                    } else {
                        items(history, key = { "hist_${it.id}" }) { item ->
                            SmartMediaCard(
                                item = item,
                                badge = item.completionDate.ifBlank { item.startDate.ifBlank { item.statusEnum.label } },
                                onClick = { onSelectItem(item) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Personal Reviews (${reviewed.size})", style = MaterialTheme.typography.titleLarge, color = tokens.textPrimary)
                    }
                    items(reviewed, key = { "rev_${it.id}" }) { item ->
                        NeonGlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = themedCategoryColor(item.categoryEnum),
                            cornerRadius = 22.dp,
                            onClick = { onSelectItem(item) },
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.title, style = MaterialTheme.typography.titleMedium, color = tokens.textPrimary, modifier = Modifier.weight(1f))
                                    if (item.personalRating > 0) RatingPill(item.personalRating)
                                }
                                Text(item.personalReview, style = MaterialTheme.typography.bodyMedium, color = tokens.textSecondary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showQuoteDialog) {
        QuoteEditorDialog(
            initial = editingQuote,
            onDismiss = { showQuoteDialog = false },
            onSave = { quote, isEdit ->
                onSaveQuote(quote, isEdit)
                showQuoteDialog = false
            }
        )
    }
}

@Composable
private fun SmartMediaCard(item: MediaItem, badge: String, onClick: () -> Unit) {
    val tokens = LocalZenixTokens.current
    val accent = themedCategoryColor(item.categoryEnum)
    NeonGlassCard(
        modifier = Modifier.fillMaxWidth(),
        accentColor = accent,
        cornerRadius = 22.dp,
        onClick = onClick,
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PosterArtwork(item.posterUrl, item.title, item.categoryEnum, Modifier.size(62.dp, 88.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(badge, style = MaterialTheme.typography.labelSmall, color = accent)
                Text(item.title, style = MaterialTheme.typography.titleMedium, color = tokens.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                val smartMeta = listOfNotNull(
                    item.categoryEnum.label,
                    item.formattedYearOrBlank().takeIf { it.isNotBlank() },
                    formatWatchTime(item.runtimeMinutes).takeIf { item.runtimeMinutes > 0L }
                ).joinToString(" • ")
                Text(
                    text = smartMeta,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textSecondary
                )
            }
            if (item.personalRating > 0) RatingPill(item.personalRating)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuoteEditorDialog(
    initial: QuoteItem?,
    onDismiss: () -> Unit,
    onSave: (QuoteItem, Boolean) -> Unit
) {
    val tokens = LocalZenixTokens.current
    val isEdit = initial != null && initial.id.isNotBlank()
    var quoteText by remember { mutableStateOf(initial?.quoteText ?: "") }
    var sourceTitle by remember { mutableStateOf(initial?.sourceTitle ?: "") }
    var characterName by remember { mutableStateOf(initial?.characterName ?: "") }
    var category by remember { mutableStateOf(initial?.categoryEnum ?: MediaCategory.MOVIE) }

    NeonGlassDialog(
        onDismissRequest = onDismiss,
        title = if (isEdit) "Edit Quote" else "Save Favourite Quote",
        accentColor = tokens.secondaryAccent,
        confirmText = "Save",
        confirmEnabled = quoteText.isNotBlank() && sourceTitle.isNotBlank(),
        onConfirm = {
            if (quoteText.isNotBlank() && sourceTitle.isNotBlank()) {
                onSave(
                    (initial ?: QuoteItem()).copy(
                        quoteText = quoteText.trim().take(600),
                        sourceTitle = sourceTitle.trim().take(160),
                        characterName = characterName.trim().take(120),
                        mediaCategory = category.name
                    ),
                    isEdit
                )
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NeonGlassTextField(
                value = quoteText,
                onValueChange = { quoteText = it },
                label = "Quote *",
                singleLine = false,
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
            NeonGlassTextField(
                value = sourceTitle,
                onValueChange = { sourceTitle = it },
                label = "Movie / Series Title *",
                modifier = Modifier.fillMaxWidth()
            )
            NeonGlassTextField(
                value = characterName,
                onValueChange = { characterName = it },
                label = "Character (Optional)",
                modifier = Modifier.fillMaxWidth()
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MediaCategory.entries.forEach { c ->
                    NeonGlassChip(
                        label = c.label,
                        selected = category == c,
                        onClick = { category = c },
                        accentColor = themedCategoryColor(c)
                    )
                }
            }
        }
    }
}

private fun moodMatchesGenre(mood: String, genre: String): Boolean {
    val g = genre.lowercase()
    return when (mood) {
        "Mind-Bending" -> g.contains("sci-fi") || g.contains("mystery")
        "Feel-Good" -> g.contains("comedy") || g.contains("animation") || g.contains("slice of life")
        "Dark & Gritty" -> g.contains("crime") || g.contains("thriller") || g.contains("horror")
        "Emotional" -> g.contains("drama")
        "Adrenaline" -> g.contains("action") || g.contains("adventure")
        "Cozy" -> g.contains("slice of life") || g.contains("fantasy")
        "Suspense" -> g.contains("thriller") || g.contains("mystery")
        "Romantic" -> g.contains("romance")
        else -> false
    }
}
