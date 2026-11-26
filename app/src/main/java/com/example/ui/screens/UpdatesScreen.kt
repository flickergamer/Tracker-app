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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.components.neonGlow
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

data class UpdateFeatureItem(
    val name: String,
    val description: String
)

object ZenixUpdateConfig {
    const val currentVersion: String = "2.4.0"
    const val availableVersion: String = "2.5.0"

    val features: List<UpdateFeatureItem> = listOf(
        UpdateFeatureItem(
            name = "Zenix Neon Glass Interface",
            description = "Refreshed 24dp glass cards, floating navigation pill, and dynamic theme accents."
        ),
        UpdateFeatureItem(
            name = "Live Watch Time Insights",
            description = "Real-time completion percentages and category breakdowns right on your Home tab."
        ),
        UpdateFeatureItem(
            name = "Appearance Customizer",
            description = "Switch live between Neon Glossy, Minimal Dark, and AMOLED Black with 5 accent colors."
        )
    )

    const val bugFixes: String =
        "Faster poster artwork caching, smoother bottom-bar blur fallback, and improved Firestore sync."
}

@Composable
fun UpdatesScreen(
    notifyUpdates: Boolean,
    onToggleNotify: (Boolean) -> Unit,
    onBack: () -> Unit,
    onNotify: (String) -> Unit,
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    var updateApplied by remember { mutableStateOf(false) }

    BackHandler(onBack = onBack)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                    color = tokens.textPrimary
                )
            }
        }

        // Update Availability Hero Card
        item {
            LiquidGlassHeroCard(accentColor = if (updateApplied) NeonGreen else NeonPurple) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .then(
                                    if (tokens.useGlow) {
                                        Modifier.neonGlow(
                                            color = if (updateApplied) NeonGreen else NeonPurple,
                                            cornerRadius = 14.dp,
                                            alpha = 0.35f,
                                            spread = 6.dp
                                        )
                                    } else Modifier
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background((if (updateApplied) NeonGreen else NeonPurple).copy(alpha = 0.18f))
                                .border(
                                    1.dp,
                                    (if (updateApplied) NeonGreen else NeonPurple).copy(alpha = 0.55f),
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (updateApplied) Icons.Rounded.CheckCircle else Icons.Rounded.SystemUpdate,
                                contentDescription = null,
                                tint = if (updateApplied) NeonGreen else NeonPurple,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (updateApplied) {
                                    "ZENIX ${ZenixUpdateConfig.availableVersion} ready"
                                } else {
                                    "Update ${ZenixUpdateConfig.availableVersion} available"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = tokens.textPrimary
                            )
                            Text(
                                text = "Current version ${ZenixUpdateConfig.currentVersion}",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary
                            )
                        }
                    }

                    if (!updateApplied) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NeonPink.copy(alpha = 0.22f))
                                .border(1.dp, NeonPink.copy(alpha = 0.85f), RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
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

                Spacer(modifier = Modifier.height(16.dp))

                NeonPrimaryButton(
                    onClick = {
                        updateApplied = true
                        onNotify("Update ${ZenixUpdateConfig.availableVersion} staged — you're on the latest ZENIX build.")
                    },
                    accentColor = if (updateApplied) NeonGreen else NeonPurple,
                    enabled = !updateApplied,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("updates_update_now_button")
                ) {
                    Icon(
                        imageVector = if (updateApplied) Icons.Rounded.CheckCircle else Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (updateApplied) "Up to date" else "Update now",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // What's new Card
        item {
            NeonGlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = NeonCyan,
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(
                    text = "What's new",
                    style = MaterialTheme.typography.titleLarge,
                    color = tokens.textPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ZenixUpdateConfig.features.forEach { feature ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                            Text(
                                text = "Bug fixes",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = tokens.textPrimary
                            )
                            Text(
                                text = ZenixUpdateConfig.bugFixes,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary
                            )
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
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                    Switch(
                        checked = notifyUpdates,
                        onCheckedChange = onToggleNotify,
                        modifier = Modifier.testTag("notify_updates_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = tokens.primaryAccent,
                            uncheckedThumbColor = tokens.textSecondary,
                            uncheckedTrackColor = tokens.outlineSubtle
                        )
                    )
                }
            }
        }
    }
}
