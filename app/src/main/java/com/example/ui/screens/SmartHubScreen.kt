package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.ui.components.GoalRingCard
import com.example.ui.components.PosterArtwork
import com.example.ui.components.RatingPill
import com.example.ui.theme.GoldRating
import com.example.ui.theme.StatusCompleted
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
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        "Random Picker",
        "Mood Match",
        "Collections",
        "Badges & Goals",
        "Quotes",
        "History & Reviews"
    )
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab.coerceIn(0, tabs.lastIndex)) }

    // Random picker state
    var randomCategoryFilter by remember { mutableStateOf<MediaCategory?>(null) }
    var randomOnlyUnwatched by remember { mutableStateOf(true) }
    var pickedRandomItem by remember { mutableStateOf<MediaItem?>(null) }

    // Mood state
    var selectedMood by remember { mutableStateOf(AVAILABLE_MOODS.first()) }

    // Collections sub-filter
    var collectionMode by remember { mutableIntStateOf(0) } // 0 = Hidden Gems, 1 = Rewatch, 2 = Dropped

    // Quote dialog state
    var editingQuote by remember { mutableStateOf<QuoteItem?>(null) }
    var showQuoteDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Smart Discovery", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onBackground)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { idx, label ->
                    FilterChip(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        label = { Text(label) }
                    )
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> { // 1. Random Movie/Title Picker
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Random Tonight Picker", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    "Can't decide what to watch? Spin your personal vault or watchlist.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilterChip(selected = randomOnlyUnwatched, onClick = { randomOnlyUnwatched = !randomOnlyUnwatched }, label = { Text("Unwatched / Watchlist Only") })
                                    FilterChip(selected = randomCategoryFilter == null, onClick = { randomCategoryFilter = null }, label = { Text("Any Category") })
                                    MediaCategory.entries.forEach { c ->
                                        FilterChip(selected = randomCategoryFilter == c, onClick = { randomCategoryFilter = c }, label = { Text(c.label) })
                                    }
                                }
                                val pool = allItems.filter { item ->
                                    val matchesCat = randomCategoryFilter == null || item.categoryEnum == randomCategoryFilter
                                    val matchesStatus = !randomOnlyUnwatched || item.statusEnum == WatchStatus.PLAN_TO_WATCH || item.watchlistItem || item.statusEnum == WatchStatus.ON_HOLD
                                    matchesCat && matchesStatus
                                }
                                Button(
                                    onClick = {
                                        pickedRandomItem = if (pool.isNotEmpty()) pool[Random.nextInt(pool.size)] else null
                                    },
                                    enabled = pool.isNotEmpty(),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Rounded.Casino, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (pool.isEmpty()) "No Matching Titles in Pool" else "Pick Random Title (${pool.size})")
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

                1 -> { // 2. Mood-Based Suggestions
                    item {
                        Text("Pick Your Mood", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AVAILABLE_MOODS.forEach { mood ->
                                FilterChip(selected = selectedMood == mood, onClick = { selectedMood = mood }, label = { Text(mood) })
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
                                subtitle = "Tag titles with moods in the editor or add matching genres like Thriller, Comedy, or Romance."
                            )
                        }
                    } else {
                        items(moodMatches, key = { "mood_${it.id}" }) { item ->
                            SmartMediaCard(item = item, badge = selectedMood.uppercase(), onClick = { onSelectItem(item) })
                        }
                    }
                }

                2 -> { // 3, 4, 5: Hidden Gems, Rewatch Collection, Dropped Collection
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = collectionMode == 0, onClick = { collectionMode = 0 }, label = { Text("Hidden Gems") })
                            FilterChip(selected = collectionMode == 1, onClick = { collectionMode = 1 }, label = { Text("Rewatch") })
                            FilterChip(selected = collectionMode == 2, onClick = { collectionMode = 2 }, label = { Text("Dropped") })
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
                                }
                            )
                        }
                    } else {
                        items(list, key = { "col_${it.id}" }) { item ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { onSelectItem(item) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    PosterArtwork(item.posterUrl, item.title, item.categoryEnum, Modifier.size(56.dp, 80.dp))
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(item.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${item.categoryEnum.label} • ${item.releaseYear}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        if (collectionMode == 1) {
                                            Text("Rewatched ${item.rewatchCount}×", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                                        }
                                        if (collectionMode == 2 && item.notes.isNotBlank()) {
                                            Text(item.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                    when (collectionMode) {
                                        1 -> FilledTonalButton(onClick = { onIncrementRewatch(item) }) {
                                            Icon(Icons.Rounded.Repeat, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("+1")
                                        }
                                        2 -> FilledTonalButton(onClick = { onResumeDropped(item) }) {
                                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Resume")
                                        }
                                        else -> Icon(Icons.Rounded.Diamond, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> { // 8 & 9: Achievement Badges & Watching Goals
                    item {
                        Text("Watching Goals", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            GoalRingCard("Movies", stats.completedMoviesThisYear, profile.yearlyMovieGoal.toInt(), MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                            GoalRingCard("Series", stats.completedSeriesThisYear, profile.yearlySeriesGoal.toInt(), StatusCompleted, Modifier.weight(1f))
                            GoalRingCard("Episodes", stats.episodesWatchedTotal, profile.yearlyEpisodeGoal.toInt(), MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                        }
                    }
                    item {
                        val unlocked = achievements.count { it.unlocked }
                        Text(
                            text = "Achievement Badges ($unlocked/${achievements.size} Unlocked)",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    items(achievements, key = { it.id }) { badge ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (badge.unlocked) GoldRating.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(18.dp)
                                )
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (badge.unlocked) Icons.Rounded.CheckCircle else Icons.Rounded.EmojiEvents,
                                            contentDescription = null,
                                            tint = if (badge.unlocked) GoldRating else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Column {
                                            Text(badge.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                            Text(badge.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text("${badge.current.coerceAtMost(badge.target)}/${badge.target}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                                }
                                LinearProgressIndicator(
                                    progress = { badge.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (badge.unlocked) GoldRating else MaterialTheme.colorScheme.primary,
                                    strokeCap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                4 -> { // 7. Favourite Quotes Collection
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Favourite Quotes", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                            Button(onClick = { editingQuote = null; showQuoteDialog = true }, shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Quote")
                            }
                        }
                    }
                    if (quotes.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "No Saved Quotes Yet",
                                subtitle = "Collect unforgettable dialogue from movies, series, anime, and K-dramas.",
                                actionLabel = "Add First Quote",
                                onAction = { editingQuote = null; showQuoteDialog = true }
                            )
                        }
                    } else {
                        items(quotes, key = { it.id }) { q ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Rounded.FormatQuote, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text(
                                        text = "\"${q.quoteText}\"",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurface
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
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Row {
                                            IconButton(onClick = { editingQuote = q; showQuoteDialog = true }) {
                                                Icon(Icons.Rounded.Edit, contentDescription = "Edit quote")
                                            }
                                            IconButton(onClick = { onDeleteQuote(q) }) {
                                                Icon(Icons.Rounded.Delete, contentDescription = "Delete quote", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                5 -> { // 6 & 10: Personal Review System & Recently Watched History
                    val reviewed = allItems.filter { it.personalReview.isNotBlank() }
                    item {
                        Text("Recently Watched History", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                    }
                    val history = allItems.filter { it.statusEnum == WatchStatus.COMPLETED || it.statusEnum == WatchStatus.WATCHING }.take(10)
                    if (history.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "No Watch History Yet",
                                subtitle = "Start or complete titles to build your chronological viewing log."
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
                        Text("Personal Reviews (${reviewed.size})", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                    }
                    items(reviewed, key = { "rev_${it.id}" }) { item ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { onSelectItem(item) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                    if (item.personalRating > 0) RatingPill(item.personalRating)
                                }
                                Text(item.personalReview, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PosterArtwork(item.posterUrl, item.title, item.categoryEnum, Modifier.size(62.dp, 88.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(badge, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Text(item.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    text = "${item.categoryEnum.label} • ${item.releaseYear} • ${formatWatchTime(item.runtimeMinutes)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
    val isEdit = initial != null && initial.id.isNotBlank()
    var quoteText by remember { mutableStateOf(initial?.quoteText ?: "") }
    var sourceTitle by remember { mutableStateOf(initial?.sourceTitle ?: "") }
    var characterName by remember { mutableStateOf(initial?.characterName ?: "") }
    var category by remember { mutableStateOf(initial?.categoryEnum ?: MediaCategory.MOVIE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Edit Quote" else "Save Favourite Quote") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = quoteText,
                    onValueChange = { quoteText = it },
                    label = { Text("Quote *") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sourceTitle,
                    onValueChange = { sourceTitle = it },
                    label = { Text("Movie / Series Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = characterName,
                    onValueChange = { characterName = it },
                    label = { Text("Character (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MediaCategory.entries.forEach { c ->
                        FilterChip(selected = category == c, onClick = { category = c }, label = { Text(c.label) })
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
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
                },
                enabled = quoteText.isNotBlank() && sourceTitle.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
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
