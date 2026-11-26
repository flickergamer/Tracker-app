package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.AddToHomeScreen
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.TableView
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.BuildConfig
import com.example.MainActivity
import com.example.R
import com.example.data.export.ImportedBackup
import com.example.data.export.LibrarySerializer
import com.example.data.model.MediaItem
import com.example.data.model.QuoteItem
import com.example.data.model.UserProfile
import com.example.data.tmdb.TmdbPreferences
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonGlassDialog
import com.example.ui.components.NeonGlassTextField
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.ZenixToggleSwitch
import com.example.ui.components.zenixFocusRing
import com.example.ui.components.zenixHeading
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalReducedMotion
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ZenixAccentColor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    userEmail: String?,
    profile: UserProfile,
    currentTheme: AppThemeMode,
    currentAccent: ZenixAccentColor,
    onOpenAppearance: () -> Unit,
    onOpenUpdates: () -> Unit,
    mediaItems: List<MediaItem>,
    quotes: List<QuoteItem>,
    onUpdateProfile: (UserProfile) -> Unit,
    onImportBackup: (ImportedBackup) -> Unit,
    onClearAllData: () -> Unit,
    onNotify: (String) -> Unit,
    onSignOut: () -> Unit,
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current

    var displayName by remember(profile.displayName) { mutableStateOf(profile.displayName) }
    var bio by remember(profile.bio) { mutableStateOf(profile.bio) }
    var compactMode by remember(profile.compactMode) { mutableStateOf(profile.compactMode) }
    var movieGoal by remember(profile.yearlyMovieGoal) { mutableStateOf(profile.yearlyMovieGoal.toString()) }
    var seriesGoal by remember(profile.yearlySeriesGoal) { mutableStateOf(profile.yearlySeriesGoal.toString()) }
    var episodeGoal by remember(profile.yearlyEpisodeGoal) { mutableStateOf(profile.yearlyEpisodeGoal.toString()) }

    val savedUserTmdbKey by TmdbPreferences.observeUserKey(context).collectAsState(initial = "")
    var tmdbKeyInput by remember(savedUserTmdbKey) { mutableStateOf(savedUserTmdbKey) }

    var showClearConfirm1 by remember { mutableStateOf(false) }
    var showClearConfirm2 by remember { mutableStateOf(false) }

    var appearanceExpanded by rememberSaveable { mutableStateOf(true) }
    var updatesExpanded by rememberSaveable { mutableStateOf(false) }
    var profileExpanded by rememberSaveable { mutableStateOf(false) }
    var dataExpanded by rememberSaveable { mutableStateOf(false) }
    var aboutExpanded by rememberSaveable { mutableStateOf(false) }

    val dateStamp = remember { SimpleDateFormat("yyyyMMdd", Locale.US).format(Date()) }
    val canPinShortcut = remember(context) {
        runCatching { ShortcutManagerCompat.isRequestPinShortcutSupported(context) }.getOrDefault(false)
    }

    val exportJsonLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            runCatching {
                val json = LibrarySerializer.exportToJson(profile, mediaItems, quotes)
                context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray(Charsets.UTF_8)) }
            }.onSuccess {
                onNotify("Exported JSON backup (${mediaItems.size} titles, ${quotes.size} quotes)")
            }.onFailure {
                onNotify("JSON export failed: ${it.localizedMessage}")
            }
        }
    }

    val exportCsvLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            runCatching {
                val csv = LibrarySerializer.exportToCsv(mediaItems)
                context.contentResolver.openOutputStream(uri)?.use { it.write(csv.toByteArray(Charsets.UTF_8)) }
            }.onSuccess {
                onNotify("Exported CSV (${mediaItems.size} titles)")
            }.onFailure {
                onNotify("CSV export failed: ${it.localizedMessage}")
            }
        }
    }

    val importJsonLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                val raw = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
                LibrarySerializer.importFromJson(raw).getOrThrow()
            }.onSuccess { backup ->
                onImportBackup(backup)
            }.onFailure {
                onNotify("Import failed: ${it.localizedMessage}")
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = spacing.space4,
            end = spacing.space4,
            top = spacing.space2,
            bottom = bottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.space4)
    ) {
        item {
            Text(
                text = "Settings & Data",
                style = MaterialTheme.typography.displayMedium,
                color = tokens.textPrimary,
                modifier = Modifier.zenixHeading()
            )
        }

        // 1. Appearance (expanded by default)
        item {
            SettingsCard(
                title = "Appearance",
                icon = Icons.Rounded.Palette,
                accentColor = currentAccent.color,
                expanded = appearanceExpanded,
                onToggle = { appearanceExpanded = !appearanceExpanded },
                testTag = "settings_section_appearance"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "${currentTheme.displayName} · ${currentAccent.label}",
                            style = MaterialTheme.typography.titleMedium,
                            color = tokens.textPrimary
                        )
                        Text(
                            text = currentTheme.tagline,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(currentAccent.color)
                            .border(1.5.dp, Color.White.copy(alpha = 0.75f), CircleShape)
                    )
                }

                NeonPrimaryButton(
                    onClick = onOpenAppearance,
                    accentColor = currentAccent.color,
                    cornerRadius = radii.md,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_open_appearance_button")
                ) {
                    Text("Customize Theme & Accent", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(spacing.space2))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Compact Card Density", style = MaterialTheme.typography.bodyMedium, color = tokens.textPrimary)
                        Text("Fit more posters and episode cards per screen", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                    }
                    ZenixToggleSwitch(
                        checked = compactMode,
                        onCheckedChange = {
                            compactMode = it
                            onUpdateProfile(profile.copy(compactMode = it))
                        },
                        accentColor = tokens.primaryAccent,
                        contentDescriptionLabel = "Compact Card Density"
                    )
                }
            }
        }

        // 2. Updates
        item {
            SettingsCard(
                title = "Updates",
                icon = Icons.Rounded.SystemUpdate,
                accentColor = NeonCyan,
                expanded = updatesExpanded,
                onToggle = { updatesExpanded = !updatesExpanded },
                badgeText = "NEW",
                testTag = "settings_section_updates"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Update ${ZenixUpdateConfig.availableVersion} available",
                        style = MaterialTheme.typography.titleSmall,
                        color = tokens.textPrimary
                    )
                    Text(
                        text = "Current version ${ZenixUpdateConfig.currentVersion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary
                    )
                }
                GlassSecondaryButton(
                    onClick = onOpenUpdates,
                    accentBorderColor = NeonCyan,
                    cornerRadius = radii.md,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_open_updates_button")
                ) {
                    Text("View Updates", fontWeight = FontWeight.SemiBold)
                }
                GlassSecondaryButton(
                    onClick = {
                        if (canPinShortcut) {
                            runCatching {
                                val launchIntent = Intent(context, MainActivity::class.java).apply {
                                    action = Intent.ACTION_VIEW
                                }
                                val shortcut = ShortcutInfoCompat.Builder(context, "zenix_home_shortcut")
                                    .setShortLabel("ZENIX")
                                    .setLongLabel("ZENIX Tracker")
                                    .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_launcher))
                                    .setIntent(launchIntent)
                                    .build()
                                ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
                            }.onSuccess {
                                onNotify("Home Screen pin prompt opened for ZENIX")
                            }.onFailure {
                                onNotify("Unable to pin shortcut on this launcher")
                            }
                        } else {
                            onNotify("ZENIX is already installed; pin shortcuts from your launcher if needed.")
                        }
                    },
                    accentBorderColor = NeonPurple,
                    cornerRadius = radii.md,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_install_shortcut_button")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.AddToHomeScreen, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(spacing.space2))
                    Text("Pin ZENIX to Home Screen", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // 3. Profile
        item {
            SettingsCard(
                title = "Profile",
                icon = Icons.Rounded.Person,
                accentColor = tokens.primaryAccent,
                expanded = profileExpanded,
                onToggle = { profileExpanded = !profileExpanded },
                testTag = "settings_section_profile"
            ) {
                if (!userEmail.isNullOrBlank()) {
                    Text(
                        text = "Signed in as $userEmail",
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.tertiaryAccent
                    )
                }
                NeonGlassTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = "Custom Display Name",
                    placeholder = "Enter your display name",
                    focusNeonColor = tokens.primaryAccent,
                    modifier = Modifier.fillMaxWidth()
                )
                NeonGlassTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = "Bio / Cinephile Tagline",
                    placeholder = "Share your favorite genres, directors, or comfort watch...",
                    singleLine = false,
                    minLines = 2,
                    maxLines = 3,
                    focusNeonColor = tokens.primaryAccent,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                    NeonGlassTextField(
                        value = movieGoal,
                        onValueChange = { movieGoal = it.filter(Char::isDigit).take(4) },
                        label = "Movie Goal",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    NeonGlassTextField(
                        value = seriesGoal,
                        onValueChange = { seriesGoal = it.filter(Char::isDigit).take(3) },
                        label = "Series Goal",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    NeonGlassTextField(
                        value = episodeGoal,
                        onValueChange = { episodeGoal = it.filter(Char::isDigit).take(5) },
                        label = "Ep Goal",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                NeonPrimaryButton(
                    onClick = {
                        onUpdateProfile(
                            profile.copy(
                                displayName = displayName.trim().ifBlank { "Cinephile" }.take(60),
                                bio = bio.trim().take(250),
                                compactMode = compactMode,
                                yearlyMovieGoal = (movieGoal.toLongOrNull() ?: 30L).coerceIn(1L, 2000L),
                                yearlySeriesGoal = (seriesGoal.toLongOrNull() ?: 10L).coerceIn(1L, 500L),
                                yearlyEpisodeGoal = (episodeGoal.toLongOrNull() ?: 250L).coerceIn(1L, 20000L)
                            )
                        )
                    },
                    cornerRadius = radii.md,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_profile_button")
                ) {
                    Text("Save Profile & Goals", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 4. Data & Backup
        item {
            SettingsCard(
                title = "Data & Backup",
                icon = Icons.Rounded.Backup,
                accentColor = tokens.positiveAccent,
                expanded = dataExpanded,
                onToggle = { dataExpanded = !dataExpanded },
                testTag = "settings_section_data"
            ) {
                val buildKeyActive = BuildConfig.TMDB_API_KEY.isNotBlank() && BuildConfig.TMDB_API_KEY != "MY_TMDB_API_KEY"
                Text(
                    text = if (buildKeyActive) "TMDB key active via Secrets configuration."
                    else "Optional: Add a personal TMDB v3 API key locally (or via Secrets panel) to search posters, cast & runtime automatically.",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textSecondary
                )
                NeonGlassTextField(
                    value = tmdbKeyInput,
                    onValueChange = { tmdbKeyInput = it },
                    label = "TMDB v3 API Key",
                    placeholder = "Paste personal TMDB v3 key",
                    visualTransformation = PasswordVisualTransformation(),
                    focusNeonColor = tokens.tertiaryAccent,
                    modifier = Modifier.fillMaxWidth()
                )
                GlassSecondaryButton(
                    onClick = {
                        scope.launch {
                            TmdbPreferences.saveUserKey(context, tmdbKeyInput)
                            onNotify(if (tmdbKeyInput.isBlank()) "Cleared custom TMDB key" else "TMDB API key saved locally")
                        }
                    },
                    accentBorderColor = tokens.tertiaryAccent,
                    cornerRadius = radii.md,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save TMDB Key")
                }

                Text(
                    text = "Export your entire ZENIX library (${mediaItems.size} titles, ${quotes.size} quotes) or restore from a JSON backup.",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                    NeonPrimaryButton(
                        onClick = { exportJsonLauncher.launch("zenix_backup_$dateStamp.json") },
                        cornerRadius = radii.md,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(spacing.space1))
                        Text("Export JSON", fontWeight = FontWeight.Bold)
                    }
                    GlassSecondaryButton(
                        onClick = { importJsonLauncher.launch(arrayOf("application/json", "*/*")) },
                        accentBorderColor = tokens.positiveAccent,
                        cornerRadius = radii.md,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(spacing.space1))
                        Text("Import JSON")
                    }
                }
                GlassSecondaryButton(
                    onClick = { exportCsvLauncher.launch("zenix_library_$dateStamp.csv") },
                    accentBorderColor = tokens.tertiaryAccent,
                    cornerRadius = radii.md,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.TableView, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(spacing.space2))
                    Text("Export Library to CSV")
                }
                GlassSecondaryButton(
                    onClick = { showClearConfirm1 = true },
                    accentBorderColor = tokens.dangerAccent,
                    cornerRadius = radii.md,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp), tint = tokens.dangerAccent)
                    Spacer(modifier = Modifier.width(spacing.space2))
                    Text("Clear All Library Data", color = tokens.dangerAccent, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 5. About
        item {
            SettingsCard(
                title = "About",
                icon = Icons.Rounded.Info,
                accentColor = tokens.secondaryAccent,
                expanded = aboutExpanded,
                onToggle = { aboutExpanded = !aboutExpanded },
                testTag = "settings_section_about"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                    Text(
                        text = "ZENIX · Version ${ZenixUpdateConfig.currentVersion}",
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.textPrimary
                    )
                    Text(
                        text = "Personal Cinema & Series Sanctuary with real-time cloud sync.",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary
                    )
                    Text(
                        text = buildString {
                            append("${mediaItems.size} tracked titles · ${quotes.size} quotes")
                            if (!userEmail.isNullOrBlank()) append(" · $userEmail")
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.tertiaryAccent
                    )
                }
                GlassSecondaryButton(
                    onClick = onSignOut,
                    cornerRadius = radii.md,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sign_out_button")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(spacing.space2))
                    Text("Sign Out")
                }
            }
        }
    }

    if (showClearConfirm1) {
        NeonGlassDialog(
            onDismissRequest = { showClearConfirm1 = false },
            title = "Clear All Library Data?",
            accentColor = tokens.dangerAccent,
            confirmText = "Continue",
            confirmDangerous = true,
            onConfirm = { showClearConfirm1 = false; showClearConfirm2 = true }
        ) {
            Text(
                text = "This will delete all ${mediaItems.size} tracked titles and ${quotes.size} quotes from your cloud vault. Consider exporting a JSON backup first.",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary
            )
        }
    }

    if (showClearConfirm2) {
        NeonGlassDialog(
            onDismissRequest = { showClearConfirm2 = false },
            title = "Final Confirmation",
            accentColor = tokens.dangerAccent,
            confirmText = "Erase Everything",
            confirmDangerous = true,
            onConfirm = {
                showClearConfirm2 = false
                onClearAllData()
            }
        ) {
            Text(
                text = "Permanently erase all ZENIX library items and quotes? This cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary
            )
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
    badgeText: String? = null,
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val reducedMotion = LocalReducedMotion.current
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(durationMillis = 200),
        label = "settings_chevron_$title"
    )

    NeonGlassCard(
        modifier = Modifier.fillMaxWidth(),
        accentColor = accentColor,
        cornerRadius = radii.lg,
        contentPadding = PaddingValues(spacing.space4)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .clip(RoundedCornerShape(radii.sm))
                    .zenixFocusRing(accentColor, radii.sm)
                    .clickable(role = Role.Button, onClick = onToggle)
                    .semantics {
                        stateDescription = if (expanded) "Expanded" else "Collapsed"
                    }
                    .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
                    .padding(horizontal = spacing.space1, vertical = spacing.space1),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor)
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.textPrimary,
                        modifier = Modifier.zenixHeading()
                    )
                    if (badgeText != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NeonPink.copy(alpha = 0.20f))
                                .border(1.dp, NeonPink.copy(alpha = 0.85f), RoundedCornerShape(50))
                                .padding(horizontal = spacing.space2, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = NeonPink
                            )
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = if (expanded) "Collapse $title" else "Expand $title",
                    tint = tokens.textSecondary,
                    modifier = Modifier
                        .size(22.dp)
                        .graphicsLayer { rotationZ = chevronRotation }
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = if (reducedMotion) EnterTransition.None else (fadeIn(tween(180)) + expandVertically(tween(220))),
                exit = if (reducedMotion) ExitTransition.None else (fadeOut(tween(140)) + shrinkVertically(tween(180)))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    content()
                }
            }
        }
    }
}
