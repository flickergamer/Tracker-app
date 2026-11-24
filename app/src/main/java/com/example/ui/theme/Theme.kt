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
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun AfterglowTheme(
    accentPreset: AccentColorPreset = AccentColorPreset.AFTERGLOW_PURPLE,
    content: @Composable () -> Unit
) {
    val (primary, secondary) = when (accentPreset) {
        AccentColorPreset.AFTERGLOW_PURPLE -> NeonPurple to NeonFuchsia
        AccentColorPreset.MIDNIGHT_ROSE -> MidnightRosePrimary to MidnightRoseSecondary
        AccentColorPreset.CELESTIAL_INDIGO -> CelestialIndigoPrimary to CelestialIndigoSecondary
        AccentColorPreset.NEON_AMETHYST -> NeonAmethystPrimary to NeonAmethystSecondary
    }

    val colorScheme = darkColorScheme(
        primary = primary,
        onPrimary = NeonButtonDarkText,
        primaryContainer = primary.copy(alpha = 0.24f),
        onPrimaryContainer = OffWhiteText,
        secondary = secondary,
        onSecondary = NeonButtonDarkText,
        secondaryContainer = ElevatedPlum,
        onSecondaryContainer = OffWhiteText,
        tertiary = NeonCyan,
        onTertiary = NeonButtonDarkText,
        background = VoidBackground,
        onBackground = OffWhiteText,
        surface = DarkPurpleCard,
        onSurface = OffWhiteText,
        surfaceVariant = ElevatedPlum,
        onSurfaceVariant = MutedGreyText,
        surfaceContainerLowest = VoidBackground,
        surfaceContainerLow = DarkGlassSurface,
        surfaceContainer = DarkPurpleCard,
        surfaceContainerHigh = ElevatedPlum,
        surfaceContainerHighest = Color(0xFF291D45),
        outline = Color.White.copy(alpha = 0.22f),
        outlineVariant = Color.White.copy(alpha = 0.12f),
        error = NeonPink,
        onError = Color(0xFF280618)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AfterglowTypography,
        shapes = AfterglowShapes,
        content = content
    )
}
