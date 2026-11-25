package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.AccentColorPreset

val AfterglowShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

val AndroidUiShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Immutable
data class ZenixThemeTokens(
    val mode: AppThemeMode,
    val isLight: Boolean,
    val useGlow: Boolean,
    val useGloss: Boolean,
    val useBlur: Boolean,
    val background: Color,
    val surface: Color,
    val cardBackground: Color,
    val tonalContainer: Color,
    val dialogBackground: Color,
    val drawerBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val tertiaryAccent: Color,
    val positiveAccent: Color,
    val warningAccent: Color,
    val dangerAccent: Color,
    val ratingGold: Color,
    val outline: Color,
    val outlineSubtle: Color,
    val primaryButtonBg: Color,
    val primaryButtonText: Color,
    val secondaryButtonBg: Color,
    val secondaryButtonText: Color,
    val chipUnselectedBg: Color,
    val chipSelectedBg: Color,
    val chipSelectedText: Color,
    val chipRadius: Dp,
    val cardRadius: Dp,
    val heroRadius: Dp,
    val cardElevation: Dp,
    val fontFamily: FontFamily
)

val LocalAppThemeMode = staticCompositionLocalOf { AppThemeMode.NEON }
val LocalZenixTokens = staticCompositionLocalOf {
    buildStaticTokens(AppThemeMode.NEON, AccentColorPreset.AFTERGLOW_PURPLE)
}

private fun buildStaticTokens(
    mode: AppThemeMode,
    accentPreset: AccentColorPreset
): ZenixThemeTokens = when (mode) {
    AppThemeMode.NEON -> {
        val (primary, secondary) = when (accentPreset) {
            AccentColorPreset.AFTERGLOW_PURPLE -> NeonPurple to NeonFuchsia
            AccentColorPreset.MIDNIGHT_ROSE -> MidnightRosePrimary to MidnightRoseSecondary
            AccentColorPreset.CELESTIAL_INDIGO -> CelestialIndigoPrimary to CelestialIndigoSecondary
            AccentColorPreset.NEON_AMETHYST -> NeonAmethystPrimary to NeonAmethystSecondary
        }
        ZenixThemeTokens(
            mode = AppThemeMode.NEON,
            isLight = false,
            useGlow = true,
            useGloss = true,
            useBlur = true,
            background = VoidBackground,
            surface = DarkGlassSurface,
            cardBackground = DarkPurpleCard,
            tonalContainer = ElevatedPlum,
            dialogBackground = Color(0xFF100A21),
            drawerBackground = Color(0xFF0B0718),
            textPrimary = OffWhiteText,
            textSecondary = MutedGreyText,
            primaryAccent = primary,
            secondaryAccent = secondary,
            tertiaryAccent = NeonCyan,
            positiveAccent = NeonGreen,
            warningAccent = StatusOnHold,
            dangerAccent = NeonPink,
            ratingGold = GoldRating,
            outline = Color.White.copy(alpha = 0.28f),
            outlineSubtle = Color.White.copy(alpha = 0.14f),
            primaryButtonBg = NeonButtonDeep,
            primaryButtonText = NeonButtonDarkText,
            secondaryButtonBg = Color.White.copy(alpha = 0.12f),
            secondaryButtonText = OffWhiteText,
            chipUnselectedBg = Color(0xFF0E0A1C).copy(alpha = 0.55f),
            chipSelectedBg = NeonPurple,
            chipSelectedText = Color.White,
            chipRadius = 50.dp,
            cardRadius = 22.dp,
            heroRadius = 28.dp,
            cardElevation = 0.dp,
            fontFamily = SpaceGroteskFontFamily
        )
    }

    AppThemeMode.ANDROID_UI -> ZenixThemeTokens(
        mode = AppThemeMode.ANDROID_UI,
        isLight = false,
        useGlow = false,
        useGloss = false,
        useBlur = false,
        background = AndroidBackground,
        surface = AndroidSurface,
        cardBackground = AndroidCardSurface,
        tonalContainer = AndroidSecondaryContainer,
        dialogBackground = AndroidSurface,
        drawerBackground = AndroidSurface,
        textPrimary = AndroidTextPrimary,
        textSecondary = AndroidTextSecondary,
        primaryAccent = AndroidPrimary,
        secondaryAccent = AndroidTertiary,
        tertiaryAccent = Color(0xFF80D8FF),
        positiveAccent = Color(0xFF81C784),
        warningAccent = Color(0xFFFFD54F),
        dangerAccent = Color(0xFFF2B8B5),
        ratingGold = Color(0xFFFFD54F),
        outline = AndroidOutline,
        outlineSubtle = AndroidOutlineVariant,
        primaryButtonBg = AndroidPrimary,
        primaryButtonText = AndroidOnPrimary,
        secondaryButtonBg = AndroidSecondaryContainer,
        secondaryButtonText = AndroidOnSecondaryContainer,
        chipUnselectedBg = Color.Transparent,
        chipSelectedBg = AndroidSecondaryContainer,
        chipSelectedText = AndroidOnSecondaryContainer,
        chipRadius = 8.dp,
        cardRadius = 20.dp,
        heroRadius = 24.dp,
        cardElevation = 0.dp,
        fontFamily = RobotoFontFamily
    )

    AppThemeMode.MINIMAL_PASTEL -> ZenixThemeTokens(
        mode = AppThemeMode.MINIMAL_PASTEL,
        isLight = true,
        useGlow = false,
        useGloss = false,
        useBlur = false,
        background = PastelBackground,
        surface = Color.White,
        cardBackground = Color.White,
        tonalContainer = PastelLavender,
        dialogBackground = Color.White,
        drawerBackground = PastelBackground,
        textPrimary = PastelTextPrimary,
        textSecondary = PastelTextSecondary,
        primaryAccent = PastelAccentPlum,
        secondaryAccent = PastelAccentRose,
        tertiaryAccent = PastelAccentTeal,
        positiveAccent = PastelAccentMint,
        warningAccent = PastelAccentGold,
        dangerAccent = PastelAccentCoral,
        ratingGold = PastelAccentGold,
        outline = PastelOutlineStrong,
        outlineSubtle = PastelOutline,
        primaryButtonBg = PastelTextPrimary,
        primaryButtonText = Color.White,
        secondaryButtonBg = Color.White,
        secondaryButtonText = PastelTextPrimary,
        chipUnselectedBg = Color.White,
        chipSelectedBg = PastelLavender,
        chipSelectedText = PastelTextPrimary,
        chipRadius = 50.dp,
        cardRadius = 22.dp,
        heroRadius = 28.dp,
        cardElevation = 5.dp,
        fontFamily = DmSansFontFamily
    )
}

@Composable
private fun rememberAnimatedTokens(
    target: ZenixThemeTokens
): ZenixThemeTokens {
    val animSpec = tween<Color>(durationMillis = 240)
    val bg by animateColorAsState(target.background, animSpec, label = "theme_bg")
    val surface by animateColorAsState(target.surface, animSpec, label = "theme_surface")
    val cardBg by animateColorAsState(target.cardBackground, animSpec, label = "theme_card")
    val tonal by animateColorAsState(target.tonalContainer, animSpec, label = "theme_tonal")
    val dialogBg by animateColorAsState(target.dialogBackground, animSpec, label = "theme_dialog")
    val drawerBg by animateColorAsState(target.drawerBackground, animSpec, label = "theme_drawer")
    val textPrimary by animateColorAsState(target.textPrimary, animSpec, label = "theme_tp")
    val textSecondary by animateColorAsState(target.textSecondary, animSpec, label = "theme_ts")
    val primaryAccent by animateColorAsState(target.primaryAccent, animSpec, label = "theme_pa")
    val secondaryAccent by animateColorAsState(target.secondaryAccent, animSpec, label = "theme_sa")
    val tertiaryAccent by animateColorAsState(target.tertiaryAccent, animSpec, label = "theme_ta")

    return target.copy(
        background = bg,
        surface = surface,
        cardBackground = cardBg,
        tonalContainer = tonal,
        dialogBackground = dialogBg,
        drawerBackground = drawerBg,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        primaryAccent = primaryAccent,
        secondaryAccent = secondaryAccent,
        tertiaryAccent = tertiaryAccent
    )
}

@Composable
fun AfterglowTheme(
    themeMode: AppThemeMode = AppThemeMode.NEON,
    accentPreset: AccentColorPreset = AccentColorPreset.AFTERGLOW_PURPLE,
    content: @Composable () -> Unit
) {
    val tokens = rememberAnimatedTokens(buildStaticTokens(themeMode, accentPreset))

    val colorScheme = when (themeMode) {
        AppThemeMode.NEON -> darkColorScheme(
            primary = tokens.primaryAccent,
            onPrimary = NeonButtonDarkText,
            primaryContainer = tokens.primaryAccent.copy(alpha = 0.24f),
            onPrimaryContainer = OffWhiteText,
            secondary = tokens.secondaryAccent,
            onSecondary = NeonButtonDarkText,
            secondaryContainer = ElevatedPlum,
            onSecondaryContainer = OffWhiteText,
            tertiary = NeonCyan,
            onTertiary = NeonButtonDarkText,
            background = tokens.background,
            onBackground = tokens.textPrimary,
            surface = tokens.cardBackground,
            onSurface = tokens.textPrimary,
            surfaceVariant = ElevatedPlum,
            onSurfaceVariant = tokens.textSecondary,
            surfaceContainerLowest = VoidBackground,
            surfaceContainerLow = DarkGlassSurface,
            surfaceContainer = DarkPurpleCard,
            surfaceContainerHigh = ElevatedPlum,
            surfaceContainerHighest = Color(0xFF291D45),
            outline = tokens.outline,
            outlineVariant = tokens.outlineSubtle,
            error = NeonPink,
            onError = Color(0xFF280618)
        )

        AppThemeMode.ANDROID_UI -> darkColorScheme(
            primary = AndroidPrimary,
            onPrimary = AndroidOnPrimary,
            primaryContainer = AndroidPrimaryContainer,
            onPrimaryContainer = AndroidOnPrimaryContainer,
            secondary = AndroidTertiary,
            onSecondary = AndroidOnPrimary,
            secondaryContainer = AndroidSecondaryContainer,
            onSecondaryContainer = AndroidOnSecondaryContainer,
            tertiary = tokens.tertiaryAccent,
            onTertiary = AndroidOnPrimary,
            background = tokens.background,
            onBackground = tokens.textPrimary,
            surface = tokens.surface,
            onSurface = tokens.textPrimary,
            surfaceVariant = AndroidCardSurface,
            onSurfaceVariant = tokens.textSecondary,
            surfaceContainerLowest = Color(0xFF0F0D13),
            surfaceContainerLow = Color(0xFF1D1B20),
            surfaceContainer = AndroidSurface,
            surfaceContainerHigh = AndroidCardSurface,
            surfaceContainerHighest = AndroidSurfaceHighest,
            outline = AndroidOutline,
            outlineVariant = AndroidOutlineVariant,
            error = Color(0xFFF2B8B5),
            onError = Color(0xFF601410)
        )

        AppThemeMode.MINIMAL_PASTEL -> lightColorScheme(
            primary = PastelTextPrimary,
            onPrimary = Color.White,
            primaryContainer = PastelLavender,
            onPrimaryContainer = PastelTextPrimary,
            secondary = PastelAccentTeal,
            onSecondary = Color.White,
            secondaryContainer = PastelSky,
            onSecondaryContainer = PastelTextPrimary,
            tertiary = PastelAccentCoral,
            onTertiary = Color.White,
            background = tokens.background,
            onBackground = tokens.textPrimary,
            surface = Color.White,
            onSurface = tokens.textPrimary,
            surfaceVariant = PastelLavender,
            onSurfaceVariant = tokens.textSecondary,
            surfaceContainerLowest = Color.White,
            surfaceContainerLow = PastelBackground,
            surfaceContainer = Color.White,
            surfaceContainerHigh = PastelLavender,
            surfaceContainerHighest = PastelSky,
            outline = PastelOutlineStrong,
            outlineVariant = PastelOutline,
            error = PastelAccentCoral,
            onError = Color.White
        )
    }

    val shapes = if (themeMode == AppThemeMode.ANDROID_UI) AndroidUiShapes else AfterglowShapes

    CompositionLocalProvider(
        LocalAppThemeMode provides themeMode,
        LocalZenixTokens provides tokens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typographyForTheme(themeMode),
            shapes = shapes,
            content = content
        )
    }
}
