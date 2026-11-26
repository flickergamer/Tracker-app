package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val AfterglowShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Immutable
data class ZenixThemeTokens(
    val mode: AppThemeMode,
    val accentOption: ZenixAccentColor,
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

val LocalAppThemeMode = staticCompositionLocalOf { AppThemeMode.NEON_GLOSSY }
val LocalZenixTokens = staticCompositionLocalOf {
    buildStaticTokens(AppThemeMode.NEON_GLOSSY, ZenixAccentColor.PURPLE)
}

private fun accentSecondaryPair(accent: ZenixAccentColor): Color = when (accent) {
    ZenixAccentColor.PURPLE -> NeonPink
    ZenixAccentColor.CYAN -> NeonPurple
    ZenixAccentColor.PINK -> NeonPurple
    ZenixAccentColor.GREEN -> NeonCyan
    ZenixAccentColor.AMBER -> NeonPink
}

private fun buildStaticTokens(
    mode: AppThemeMode,
    accentOption: ZenixAccentColor
): ZenixThemeTokens {
    val primary = accentOption.color
    val secondary = accentSecondaryPair(accentOption)
    val buttonTextOnAccent = when (accentOption) {
        ZenixAccentColor.CYAN, ZenixAccentColor.GREEN, ZenixAccentColor.AMBER -> Color(0xFF0A0812)
        ZenixAccentColor.PURPLE, ZenixAccentColor.PINK -> Color.White
    }

    return when (mode) {
        AppThemeMode.NEON_GLOSSY -> ZenixThemeTokens(
            mode = AppThemeMode.NEON_GLOSSY,
            accentOption = accentOption,
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
            warningAccent = NeonAmber,
            dangerAccent = NeonPink,
            ratingGold = NeonAmber,
            outline = Color.White.copy(alpha = 0.24f),
            outlineSubtle = Color.White.copy(alpha = 0.13f),
            primaryButtonBg = primary,
            primaryButtonText = buttonTextOnAccent,
            secondaryButtonBg = Color.White.copy(alpha = 0.10f),
            secondaryButtonText = OffWhiteText,
            chipUnselectedBg = Color(0xFF0E0A1C).copy(alpha = 0.58f),
            chipSelectedBg = primary,
            chipSelectedText = buttonTextOnAccent,
            chipRadius = 50.dp,
            cardRadius = 24.dp,
            heroRadius = 24.dp,
            cardElevation = 0.dp,
            fontFamily = InterFontFamily
        )

        AppThemeMode.MINIMAL_DARK -> ZenixThemeTokens(
            mode = AppThemeMode.MINIMAL_DARK,
            accentOption = accentOption,
            isLight = false,
            useGlow = false,
            useGloss = false,
            useBlur = false,
            background = MinimalDarkBackground,
            surface = MinimalDarkSurface,
            cardBackground = MinimalDarkCard,
            tonalContainer = MinimalDarkContainer,
            dialogBackground = MinimalDarkSurface,
            drawerBackground = MinimalDarkSurface,
            textPrimary = MinimalDarkTextPrimary,
            textSecondary = MinimalDarkTextSecondary,
            primaryAccent = primary,
            secondaryAccent = primary,
            tertiaryAccent = NeonCyan,
            positiveAccent = NeonGreen,
            warningAccent = NeonAmber,
            dangerAccent = NeonPink,
            ratingGold = NeonAmber,
            outline = MinimalDarkOutline,
            outlineSubtle = MinimalDarkOutline.copy(alpha = 0.72f),
            primaryButtonBg = primary,
            primaryButtonText = buttonTextOnAccent,
            secondaryButtonBg = MinimalDarkContainer,
            secondaryButtonText = MinimalDarkTextPrimary,
            chipUnselectedBg = MinimalDarkSurface,
            chipSelectedBg = primary.copy(alpha = 0.22f),
            chipSelectedText = MinimalDarkTextPrimary,
            chipRadius = 50.dp,
            cardRadius = 24.dp,
            heroRadius = 24.dp,
            cardElevation = 0.dp,
            fontFamily = InterFontFamily
        )

        AppThemeMode.AMOLED_BLACK -> ZenixThemeTokens(
            mode = AppThemeMode.AMOLED_BLACK,
            accentOption = accentOption,
            isLight = false,
            useGlow = false,
            useGloss = false,
            useBlur = false,
            background = AmoledBackground,
            surface = AmoledSurface,
            cardBackground = AmoledCard,
            tonalContainer = AmoledContainer,
            dialogBackground = AmoledSurface,
            drawerBackground = AmoledBackground,
            textPrimary = AmoledTextPrimary,
            textSecondary = AmoledTextSecondary,
            primaryAccent = primary,
            secondaryAccent = secondary,
            tertiaryAccent = NeonCyan,
            positiveAccent = NeonGreen,
            warningAccent = NeonAmber,
            dangerAccent = NeonPink,
            ratingGold = NeonAmber,
            outline = AmoledOutline,
            outlineSubtle = AmoledOutline.copy(alpha = 0.85f),
            primaryButtonBg = primary,
            primaryButtonText = buttonTextOnAccent,
            secondaryButtonBg = AmoledContainer,
            secondaryButtonText = AmoledTextPrimary,
            chipUnselectedBg = AmoledCard,
            chipSelectedBg = primary.copy(alpha = 0.24f),
            chipSelectedText = AmoledTextPrimary,
            chipRadius = 50.dp,
            cardRadius = 24.dp,
            heroRadius = 24.dp,
            cardElevation = 0.dp,
            fontFamily = InterFontFamily
        )
    }
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
    val primaryBtnBg by animateColorAsState(target.primaryButtonBg, animSpec, label = "theme_pbb")
    val primaryBtnText by animateColorAsState(target.primaryButtonText, animSpec, label = "theme_pbt")

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
        tertiaryAccent = tertiaryAccent,
        primaryButtonBg = primaryBtnBg,
        primaryButtonText = primaryBtnText
    )
}

@Composable
fun AfterglowTheme(
    themeMode: AppThemeMode = AppThemeMode.NEON_GLOSSY,
    accentColor: ZenixAccentColor = ZenixAccentColor.PURPLE,
    content: @Composable () -> Unit
) {
    val tokens = rememberAnimatedTokens(buildStaticTokens(themeMode, accentColor))

    val colorScheme = darkColorScheme(
        primary = tokens.primaryAccent,
        onPrimary = tokens.primaryButtonText,
        primaryContainer = tokens.primaryAccent.copy(alpha = 0.24f),
        onPrimaryContainer = tokens.textPrimary,
        secondary = tokens.secondaryAccent,
        onSecondary = tokens.primaryButtonText,
        secondaryContainer = tokens.tonalContainer,
        onSecondaryContainer = tokens.textPrimary,
        tertiary = tokens.tertiaryAccent,
        onTertiary = Color(0xFF06131A),
        background = tokens.background,
        onBackground = tokens.textPrimary,
        surface = tokens.cardBackground,
        onSurface = tokens.textPrimary,
        surfaceVariant = tokens.tonalContainer,
        onSurfaceVariant = tokens.textSecondary,
        surfaceContainerLowest = tokens.background,
        surfaceContainerLow = tokens.surface,
        surfaceContainer = tokens.cardBackground,
        surfaceContainerHigh = tokens.tonalContainer,
        surfaceContainerHighest = tokens.tonalContainer,
        outline = tokens.outline,
        outlineVariant = tokens.outlineSubtle,
        error = NeonPink,
        onError = Color(0xFF280618)
    )

    CompositionLocalProvider(
        LocalAppThemeMode provides themeMode,
        LocalZenixTokens provides tokens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typographyForTheme(themeMode),
            shapes = AfterglowShapes,
            content = content
        )
    }
}
