package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.AccentColorPreset

val AfterglowShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun AfterglowTheme(
    accentPreset: AccentColorPreset = AccentColorPreset.AFTERGLOW_PURPLE,
    content: @Composable () -> Unit
) {
    val (primary, secondary) = when (accentPreset) {
        AccentColorPreset.AFTERGLOW_PURPLE -> AfterglowPurple to LavenderAccent
        AccentColorPreset.MIDNIGHT_ROSE -> MidnightRosePrimary to MidnightRoseSecondary
        AccentColorPreset.CELESTIAL_INDIGO -> CelestialIndigoPrimary to CelestialIndigoSecondary
        AccentColorPreset.NEON_AMETHYST -> NeonAmethystPrimary to NeonAmethystSecondary
    }

    val colorScheme = darkColorScheme(
        primary = primary,
        onPrimary = Color(0xFF140826),
        primaryContainer = primary.copy(alpha = 0.22f),
        onPrimaryContainer = secondary,
        secondary = secondary,
        onSecondary = Color(0xFF1A0F2E),
        secondaryContainer = ElevatedPlum,
        onSecondaryContainer = OffWhiteText,
        tertiary = GoldRating,
        onTertiary = Color(0xFF1F1600),
        background = DeepCharcoal,
        onBackground = OffWhiteText,
        surface = DarkPurpleCard,
        onSurface = OffWhiteText,
        surfaceVariant = ElevatedPlum,
        onSurfaceVariant = MutedGreyText,
        surfaceContainerLowest = DeepCharcoal,
        surfaceContainerLow = Color(0xFF110D1A),
        surfaceContainer = DarkPurpleCard,
        surfaceContainerHigh = ElevatedPlum,
        surfaceContainerHighest = Color(0xFF28203B),
        outline = SubtleBorder,
        outlineVariant = SubtleBorder.copy(alpha = 0.55f),
        error = StatusDropped,
        onError = Color(0xFF2B0808)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AfterglowTypography,
        shapes = AfterglowShapes,
        content = content
    )
}
