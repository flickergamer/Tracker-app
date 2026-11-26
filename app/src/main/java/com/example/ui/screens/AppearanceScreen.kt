package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ripple
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
import com.example.ui.components.pressScale
import com.example.ui.components.zenixFocusRing
import com.example.ui.components.zenixHeading
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.AmoledCard
import com.example.ui.theme.AmoledOutline
import com.example.ui.theme.AmoledSurface
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
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
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    var pendingTheme by remember(currentTheme) { mutableStateOf(currentTheme) }
    var pendingAccent by remember(currentAccent) { mutableStateOf(currentAccent) }

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
                    modifier = Modifier.testTag("appearance_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back to settings",
                        tint = tokens.textPrimary
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.displayMedium,
                        color = tokens.textPrimary,
                        modifier = Modifier.zenixHeading()
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
                cornerRadius = radii.lg,
                contentPadding = PaddingValues(spacing.space4)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.textPrimary,
                        modifier = Modifier.zenixHeading()
                    )

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
                cornerRadius = radii.lg,
                contentPadding = PaddingValues(spacing.space4)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Accent color",
                            style = MaterialTheme.typography.titleLarge,
                            color = tokens.textPrimary,
                            modifier = Modifier.zenixHeading()
                        )
                        Text(
                            text = pendingAccent.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = pendingAccent.color,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ZenixAccentColor.entries.forEach { option ->
                            val isSelected = pendingAccent == option
                            val interactionSource = remember { MutableInteractionSource() }
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .pressScale(interactionSource, 0.96f)
                                    .clip(CircleShape)
                                    .zenixFocusRing(Color.White, 25.dp)
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = ripple(bounded = false, radius = 25.dp, color = option.color),
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
        }

        // Apply Theme CTA
        item {
            val isAlreadyActive = pendingTheme == currentTheme && pendingAccent == currentAccent
            NeonPrimaryButton(
                onClick = { onApplyAppearance(pendingTheme, pendingAccent) },
                accentColor = pendingAccent.color,
                cornerRadius = radii.md,
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
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val shape = RoundedCornerShape(radii.md)
    val interactionSource = remember { MutableInteractionSource() }
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
            .pressScale(interactionSource, 0.96f)
            .then(
                if (selected && tokens.useGlow) {
                    Modifier.neonGlow(previewAccent, cornerRadius = radii.md, alpha = 0.28f, spread = 5.dp)
                } else Modifier
            )
            .clip(shape)
            .background(rowBg)
            .border(if (selected) 1.5.dp else 1.dp, borderColor, shape)
            .zenixFocusRing(previewAccent, radii.md)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = previewAccent),
                role = Role.RadioButton,
                onClick = onSelect
            )
            .padding(spacing.space3)
            .testTag("theme_option_${mode.key}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.space3)
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
    val radii = LocalZenixRadii.current
    val shape = RoundedCornerShape(radii.sm)
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
