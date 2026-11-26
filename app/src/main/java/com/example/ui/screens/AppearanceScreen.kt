package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.components.FloatingBottomBarClearance
import com.example.ui.components.GlassIconButton
import com.example.ui.components.NeonGlassCard
import com.example.ui.components.NeonPrimaryButton
import com.example.ui.components.neonGlow
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.AmoledCard
import com.example.ui.theme.AmoledOutline
import com.example.ui.theme.AmoledSurface
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.MinimalDarkBackground
import com.example.ui.theme.MinimalDarkCard
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.VoidBackground
import com.example.ui.theme.ZenixAccentColor

@Composable
fun AppearanceScreen(
    currentTheme: AppThemeMode,
    currentAccent: ZenixAccentColor,
    onApplyAppearance: (AppThemeMode, ZenixAccentColor) -> Unit,
    onBack: () -> Unit,
    bottomPadding: Dp = FloatingBottomBarClearance,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    var pendingTheme by remember(currentTheme) { mutableStateOf(currentTheme) }
    var pendingAccent by remember(currentAccent) { mutableStateOf(currentAccent) }

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
                    modifier = Modifier.testTag("appearance_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back to settings",
                        tint = tokens.textPrimary
                    )
                }
                Column {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.displayMedium,
                        color = tokens.textPrimary
                    )
                    Text(
                        text = "Customize your ZENIX surface & neon accent",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary
                    )
                }
            }
        }

        // 3 Theme Cards (Neon Glossy, Minimal Dark, AMOLED Black)
        item {
            NeonGlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = pendingAccent.color,
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleLarge,
                    color = tokens.textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppThemeMode.entries.forEach { mode ->
                        AppearanceThemeCard(
                            mode = mode,
                            previewAccent = pendingAccent.color,
                            selected = pendingTheme == mode,
                            onSelect = { pendingTheme = mode }
                        )
                    }
                }
            }
        }

        // 5 Accent Color Circles
        item {
            NeonGlassCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = pendingAccent.color,
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Accent color",
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.textPrimary
                    )
                    Text(
                        text = pendingAccent.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = pendingAccent.color,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ZenixAccentColor.entries.forEach { option ->
                        val isSelected = pendingAccent == option
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .clickable(
                                    role = Role.RadioButton,
                                    onClick = { pendingAccent = option }
                                )
                                .semantics {
                                    contentDescription = "${option.label} accent color"
                                }
                                .testTag("accent_circle_${option.key}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .then(
                                        if (tokens.useGlow && isSelected) {
                                            Modifier.neonGlow(
                                                color = option.color,
                                                cornerRadius = 21.dp,
                                                alpha = 0.50f,
                                                spread = 6.dp
                                            )
                                        } else Modifier
                                    )
                                    .clip(CircleShape)
                                    .then(
                                        if (isSelected) {
                                            Modifier
                                                .border(2.5.dp, Color.White, CircleShape)
                                                .padding(4.dp)
                                        } else Modifier
                                    )
                                    .clip(CircleShape)
                                    .background(option.color),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Apply Theme CTA
        item {
            val isAlreadyActive = pendingTheme == currentTheme && pendingAccent == currentAccent
            NeonPrimaryButton(
                onClick = { onApplyAppearance(pendingTheme, pendingAccent) },
                accentColor = pendingAccent.color,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_theme_button")
            ) {
                Text(
                    text = if (isAlreadyActive) "Apply theme (Active)" else "Apply theme",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AppearanceThemeCard(
    mode: AppThemeMode,
    previewAccent: Color,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val tokens = LocalZenixTokens.current
    val shape = RoundedCornerShape(18.dp)
    val borderColor = if (selected) previewAccent else tokens.outlineSubtle
    val rowBg = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> if (selected) previewAccent.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.05f)
        AppThemeMode.MINIMAL_DARK -> if (selected) previewAccent.copy(alpha = 0.14f) else MinimalDarkSurface
        AppThemeMode.AMOLED_BLACK -> if (selected) previewAccent.copy(alpha = 0.14f) else AmoledSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 68.dp)
            .then(
                if (selected && tokens.useGlow) {
                    Modifier.neonGlow(previewAccent, cornerRadius = 18.dp, alpha = 0.28f, spread = 5.dp)
                } else Modifier
            )
            .clip(shape)
            .background(rowBg)
            .border(if (selected) 1.5.dp else 1.dp, borderColor, shape)
            .clickable(onClick = onSelect)
            .padding(12.dp)
            .testTag("theme_option_${mode.key}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppearanceMiniPreview(mode = mode, accentColor = previewAccent)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = mode.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = tokens.textPrimary
            )
            Text(
                text = mode.tagline,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary
            )
        }
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = previewAccent,
                unselectedColor = tokens.textSecondary
            )
        )
    }
}

@Composable
private fun AppearanceMiniPreview(mode: AppThemeMode, accentColor: Color) {
    val shape = RoundedCornerShape(12.dp)
    when (mode) {
        AppThemeMode.NEON_GLOSSY -> {
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 48.dp)
                    .clip(shape)
                    .background(VoidBackground)
                    .border(1.dp, accentColor.copy(alpha = 0.72f), shape)
                    .padding(6.dp)
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(width = 24.dp, height = 14.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(accentColor.copy(alpha = 0.75f), NeonCyan.copy(alpha = 0.55f))
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 18.dp, height = 14.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.White.copy(alpha = 0.14f))
                                .border(0.6.dp, NeonPink.copy(alpha = 0.7f), RoundedCornerShape(5.dp))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .size(width = 18.dp, height = 6.dp)
                                .clip(CircleShape)
                                .background(Brush.horizontalGradient(listOf(accentColor, NeonPink)))
                        )
                    }
                }
            }
        }

        AppThemeMode.MINIMAL_DARK -> {
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 48.dp)
                    .clip(shape)
                    .background(MinimalDarkBackground)
                    .border(1.dp, MinimalDarkOutline, shape)
                    .padding(6.dp)
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(width = 24.dp, height = 14.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(MinimalDarkCard)
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 18.dp, height = 14.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(accentColor.copy(alpha = 0.85f))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(MinimalDarkCard),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .size(width = 18.dp, height = 6.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                    }
                }
            }
        }

        AppThemeMode.AMOLED_BLACK -> {
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 48.dp)
                    .clip(shape)
                    .background(AmoledBackground)
                    .border(1.dp, AmoledOutline, shape)
                    .padding(6.dp)
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(width = 24.dp, height = 14.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(AmoledCard)
                                .border(0.6.dp, AmoledOutline, RoundedCornerShape(5.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 18.dp, height = 14.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(accentColor)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(AmoledCard)
                            .border(0.5.dp, AmoledOutline, CircleShape),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .size(width = 18.dp, height = 6.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                    }
                }
            }
        }
    }
}
