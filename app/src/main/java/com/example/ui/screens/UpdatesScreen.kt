package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GlassIconButton
import com.example.ui.components.LiquidGlassHeroCard
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.StaggeredFadeItem
import com.example.ui.components.ZenixToggleSwitch
import com.example.ui.components.neonGlow
import com.example.ui.components.zenixHeading
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val APP_VERSION: String = "2.5.0"

data class UpdateFeatureItem(
    val name: String,
    val description: String
)

object ZenixUpdateConfig {
    const val currentVersion: String = APP_VERSION
    const val latestVersion: String = "2.5.0"
    const val releaseDate: String = "2026-11-27"

    val features: List<UpdateFeatureItem> = listOf(
        UpdateFeatureItem(
            name = "Full-Screen Details & Season Steppers",
            description = "Dedicated media detail page with synopsis, rewatch stepper, and per-season episode progress."
        ),
        UpdateFeatureItem(
            name = "Year in Review & Card Sharing",
            description = "Swipeable story recap cards and PNG image sharing or saving to your device gallery."
        ),
        UpdateFeatureItem(
            name = "Cloud Backup Sync & Bulk Actions",
            description = "Account cloud backup controls in Settings and long-press multi-select."
        )
    )

    val bugFixes: List<String> = listOf(
        "Split Anime and K-Drama counts across Home and Statistics with exact category labels.",
        "Converted /10 ratings accurately to /5 partial stars and upgraded activity charts to vertical bar charts.",
        "Normalized genres and streaming platforms, added native date pickers, and hid blank release years."
    )

    fun compareSemver(versionA: String, versionB: String): Int {
        val partsA = parseSemverParts(versionA)
        val partsB = parseSemverParts(versionB)
        val maxLen = maxOf(partsA.size, partsB.size)
        for (i in 0 until maxLen) {
            val a = partsA.getOrElse(i) { 0 }
            val b = partsB.getOrElse(i) { 0 }
            if (a != b) return a.compareTo(b)
        }
        return 0
    }

    private fun parseSemverParts(raw: String): List<Int> =
        raw.trim()
            .removePrefix("v")
            .removePrefix("V")
            .substringBefore("-")
            .substringBefore("+")
            .split(".")
            .map { seg -> seg.trim().toIntOrNull() ?: 0 }

    fun isUpdateAvailable(installedVersion: String = APP_VERSION): Boolean =
        compareSemver(latestVersion, installedVersion) > 0

    fun shouldShowUpdateBadge(
        lastSeenVersion: String?,
        installedVersion: String = APP_VERSION
    ): Boolean = isUpdateAvailable(installedVersion) && lastSeenVersion != latestVersion
}

typealias UPDATE_CONFIG = ZenixUpdateConfig

@Composable
fun UpdatesScreen(
    notifyUpdates: Boolean,
    onToggleNotify: (Boolean) -> Unit,
    onBack: () -> Unit,
    onNotify: (String) -> Unit,
    lastSeenVersion: String? = null,
    onMarkUpdatesSeen: (String) -> Unit = {},
    onUpdateApplied: (String) -> Unit = {},
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val scope = rememberCoroutineScope()

    var isUpdating by remember { mutableStateOf(false) }
    var updateApplied by remember { mutableStateOf(false) }

    val updateAvailable = remember(updateApplied) {
        ZenixUpdateConfig.isUpdateAvailable(APP_VERSION) && !updateApplied
    }
    val showNewBadge = remember(updateAvailable, lastSeenVersion) {
        updateAvailable && lastSeenVersion != ZenixUpdateConfig.latestVersion
    }

    LaunchedEffect(ZenixUpdateConfig.latestVersion) {
        onMarkUpdatesSeen(ZenixUpdateConfig.latestVersion)
    }

    BackHandler(onBack = onBack)

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.space3)
            ) {
                GlassIconButton(
                    onClick = onBack,
                    glowColor = tokens.primaryAccent,
                    size = 48.dp,
                    modifier = Modifier.testTag("updates_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = tokens.textPrimary
                    )
                }
                Text(
                    text = "Updates",
                    style = MaterialTheme.typography.displayMedium,
                    color = tokens.textPrimary,
                    modifier = Modifier.zenixHeading()
                )
            }
        }

        // Update Status Hero Card
        item {
            val heroAccent = if (!updateAvailable) NeonGreen else NeonPurple
            LiquidGlassHeroCard(
                accentColor = heroAccent,
                contentPadding = PaddingValues(spacing.space4)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .then(
                                        if (tokens.useGlow) {
                                            Modifier.neonGlow(
                                                color = heroAccent,
                                                cornerRadius = radii.sm,
                                                alpha = 0.35f,
                                                spread = 6.dp
                                            )
                                        } else Modifier
                                    )
                                    .clip(RoundedCornerShape(radii.sm))
                                    .background(heroAccent.copy(alpha = 0.18f))
                                    .border(
                                        1.dp,
                                        heroAccent.copy(alpha = 0.55f),
                                        RoundedCornerShape(radii.sm)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (!updateAvailable) Icons.Rounded.CheckCircle else Icons.Rounded.SystemUpdate,
                                    contentDescription = null,
                                    tint = heroAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (!updateAvailable) {
                                        "You're up to date"
                                    } else {
                                        "Update ${ZenixUpdateConfig.latestVersion} available"
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    color = tokens.textPrimary
                                )
                                Text(
                                    text = if (!updateAvailable) {
                                        "Installed v$APP_VERSION · Released ${ZenixUpdateConfig.releaseDate}"
                                    } else {
                                        "Installed version v$APP_VERSION"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textSecondary
                                )
                            }
                        }

                        if (showNewBadge) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(NeonPink.copy(alpha = 0.22f))
                                    .border(1.dp, NeonPink.copy(alpha = 0.85f), RoundedCornerShape(50))
                                    .padding(horizontal = spacing.space3, vertical = spacing.space1)
                            ) {
                                Text(
                                    text = "NEW",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = NeonPink
                                )
                            }
                        }
                    }

                    if (updateAvailable) {
                        NeonPrimaryButton(
                            onClick = {
                                if (!isUpdating) {
                                    isUpdating = true
                                    scope.launch {
                                        delay(450L)
                                        onMarkUpdatesSeen(ZenixUpdateConfig.latestVersion)
                                        onUpdateApplied(ZenixUpdateConfig.latestVersion)
                                        updateApplied = true
                                        isUpdating = false
                                        onNotify("Updated to v${ZenixUpdateConfig.latestVersion}")
                                    }
                                }
                            },
                            accentColor = NeonPurple,
                            enabled = !isUpdating,
                            cornerRadius = radii.md,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("updates_update_now_button")
                        ) {
                            if (isUpdating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF0A0812)
                                )
                                Spacer(modifier = Modifier.width(spacing.space2))
                                Text("Updating...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(spacing.space2))
                                Text("Update now", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Release notes Card
        item {
            NeonGlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = NeonCyan,
                cornerRadius = radii.lg,
                contentPadding = PaddingValues(spacing.space4)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    Text(
                        text = if (!updateAvailable) {
                            "What's new in this version"
                        } else {
                            "What's new in v${ZenixUpdateConfig.latestVersion}"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.textPrimary,
                        modifier = Modifier.zenixHeading()
                    )

                    ZenixUpdateConfig.features.forEachIndexed { idx, feature ->
                        StaggeredFadeItem(index = idx) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .size(9.dp)
                                        .then(
                                            if (tokens.useGlow) {
                                                Modifier.neonGlow(NeonCyan, cornerRadius = 5.dp, alpha = 0.55f, spread = 4.dp)
                                            } else Modifier
                                        )
                                        .clip(CircleShape)
                                        .background(NeonCyan)
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = feature.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = tokens.textPrimary
                                    )
                                    Text(
                                        text = feature.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.textSecondary
                                    )
                                }
                            }
                        }
                    }

                    ZenixUpdateConfig.bugFixes.forEachIndexed { idx, fixText ->
                        StaggeredFadeItem(index = ZenixUpdateConfig.features.size + idx) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .size(9.dp)
                                        .then(
                                            if (tokens.useGlow) {
                                                Modifier.neonGlow(NeonGreen, cornerRadius = 5.dp, alpha = 0.55f, spread = 4.dp)
                                            } else Modifier
                                        )
                                        .clip(CircleShape)
                                        .background(NeonGreen)
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    if (idx == 0) {
                                        Text(
                                            text = "Bug fixes",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = tokens.textPrimary
                                        )
                                    }
                                    Text(
                                        text = fixText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Notify me about updates toggle
        item {
            NeonGlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = tokens.primaryAccent,
                cornerRadius = radii.lg,
                contentPadding = PaddingValues(spacing.space4)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsActive,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Notify me about updates",
                                style = MaterialTheme.typography.titleSmall,
                                color = tokens.textPrimary
                            )
                            Text(
                                text = "Show release highlights when a new ZENIX version arrives",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary
                            )
                        }
                    }
                    ZenixToggleSwitch(
                        checked = notifyUpdates,
                        onCheckedChange = onToggleNotify,
                        accentColor = tokens.primaryAccent,
                        contentDescriptionLabel = "Notify me about updates",
                        modifier = Modifier.testTag("notify_updates_switch")
                    )
                }
            }
        }
    }
}
