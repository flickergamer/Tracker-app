package com.example.ui.components

import android.os.Build
import android.view.View
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MediaCategory
import com.example.ui.theme.AmoledCard
import com.example.ui.theme.AmoledOutline
import com.example.ui.theme.AmoledSurface
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.MinimalDarkCard
import com.example.ui.theme.MinimalDarkOutline
import com.example.ui.theme.MinimalDarkSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OffWhiteText
import com.example.ui.theme.RadialGlowCyan
import com.example.ui.theme.RadialGlowMagenta
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlin.math.max

val FloatingBottomBarClearance: Dp = 112.dp

fun categoryNeonColor(category: MediaCategory): Color = when (category) {
    MediaCategory.MOVIE -> NeonCyan
    MediaCategory.WEB_SERIES -> NeonPink
    MediaCategory.ANIME -> NeonGreen
    MediaCategory.K_DRAMA -> NeonPurple
}

fun neonPaletteColor(index: Int): Color = when (index % 5) {
    0 -> NeonPurple
    1 -> NeonCyan
    2 -> NeonPink
    3 -> NeonGreen
    else -> NeonAmber
}

@Composable
fun themedCategoryColor(category: MediaCategory): Color {
    val tokens = LocalZenixTokens.current
    return if (tokens.mode == AppThemeMode.MINIMAL_DARK) {
        when (category) {
            MediaCategory.MOVIE -> tokens.primaryAccent
            MediaCategory.WEB_SERIES -> tokens.primaryAccent.copy(alpha = 0.88f)
            MediaCategory.ANIME -> NeonCyan
            MediaCategory.K_DRAMA -> NeonPink
        }
    } else {
        categoryNeonColor(category)
    }
}

@Composable
fun themedPaletteColor(index: Int): Color {
    val tokens = LocalZenixTokens.current
    return if (tokens.mode == AppThemeMode.MINIMAL_DARK && index % 5 == 0) {
        tokens.primaryAccent
    } else {
        neonPaletteColor(index)
    }
}

@Composable
fun themedPastelFill(@Suppress("UNUSED_PARAMETER") index: Int): Color? = null

fun Modifier.neonGlow(
    color: Color,
    cornerRadius: Dp = 24.dp,
    alpha: Float = 0.28f,
    spread: Dp = 8.dp
): Modifier = drawBehind {
    if (alpha <= 0f || color == Color.Transparent) return@drawBehind
    val baseRadiusPx = cornerRadius.toPx()
    val spreadPx = spread.toPx()
    val layers = 4
    for (i in 1..layers) {
        val t = i.toFloat() / layers.toFloat()
        val inflate = spreadPx * t
        val layerAlpha = alpha * (1f - t * 0.72f) * 0.42f
        drawRoundRect(
            color = color.copy(alpha = layerAlpha.coerceIn(0f, 1f)),
            topLeft = Offset(-inflate, -inflate + (inflate * 0.18f)),
            size = Size(size.width + inflate * 2f, size.height + inflate * 2f),
            cornerRadius = CornerRadius(baseRadiusPx + inflate, baseRadiusPx + inflate)
        )
    }
}

fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.975f
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "press_scale"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * ZENIX "Z" Gradient Brand Logo
 */
@Composable
fun ZenixBrandLogo(
    size: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val corner = (size.value * 0.31f).dp
    val shape = RoundedCornerShape(corner)
    val startCol = tokens.primaryAccent
    val endCol = if (tokens.mode == AppThemeMode.MINIMAL_DARK) {
        lerp(tokens.primaryAccent, Color.White, 0.22f)
    } else {
        NeonPink
    }

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (tokens.useGlow) {
                    Modifier.neonGlow(color = startCol, cornerRadius = corner, alpha = 0.45f, spread = 6.dp)
                } else Modifier
            )
            .clip(shape)
            .background(Brush.linearGradient(colors = listOf(startCol, endCol)))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.14f))
                ),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Z",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.54f).sp,
            color = Color.White
        )
    }
}

/**
 * Global themed backdrop:
 * - NEON_GLOSSY: #07050F with radial neon glows
 * - MINIMAL_DARK: clean flat dark #101016
 * - AMOLED_BLACK: true #000000 black
 */
@Composable
fun AfterglowBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.background)
    ) {
        if (tokens.useGlow) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxDim = max(size.width, size.height).coerceAtLeast(1f)
                val primaryGlow = tokens.primaryAccent
                val magentaCenter = Offset(size.width * 0.90f, size.height * 0.06f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to lerp(RadialGlowMagenta, primaryGlow, 0.5f).copy(alpha = 0.38f),
                            0.45f to RadialGlowMagenta.copy(alpha = 0.14f),
                            1.0f to Color.Transparent
                        ),
                        center = magentaCenter,
                        radius = maxDim * 0.62f
                    ),
                    radius = maxDim * 0.62f,
                    center = magentaCenter
                )

                val cyanCenter = Offset(size.width * 0.04f, size.height * 0.48f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to RadialGlowCyan.copy(alpha = 0.24f),
                            0.45f to RadialGlowCyan.copy(alpha = 0.08f),
                            1.0f to Color.Transparent
                        ),
                        center = cyanCenter,
                        radius = maxDim * 0.56f
                    ),
                    radius = maxDim * 0.56f,
                    center = cyanCenter
                )

                val bottomCenter = Offset(size.width * 0.62f, size.height * 0.96f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to primaryGlow.copy(alpha = 0.18f),
                            0.6f to primaryGlow.copy(alpha = 0.05f),
                            1.0f to Color.Transparent
                        ),
                        center = bottomCenter,
                        radius = maxDim * 0.42f
                    ),
                    radius = maxDim * 0.42f,
                    center = bottomCenter
                )
            }
        }
        content()
    }
}

/**
 * Screen Header:
 * Left: Menu icon button + "Z" gradient logo + "ZENIX"
 * Right: Search + "+" glass icon buttons
 */
@Composable
fun AfterglowTopBar(
    onOpenMenu: () -> Unit,
    onSearchClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GlassIconButton(
                onClick = onOpenMenu,
                glowColor = tokens.primaryAccent,
                size = 44.dp,
                modifier = Modifier.testTag("open_drawer_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Open navigation menu",
                    tint = tokens.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            ZenixBrandLogo(size = 32.dp)

            Text(
                text = "ZENIX",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 2.4.sp,
                color = tokens.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassIconButton(
                onClick = onSearchClick,
                glowColor = NeonCyan,
                size = 44.dp,
                modifier = Modifier.testTag("topbar_search_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search library",
                    tint = tokens.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            GlassIconButton(
                onClick = onAddClick,
                glowColor = tokens.primaryAccent,
                size = 44.dp,
                modifier = Modifier.testTag("topbar_add_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add title",
                    tint = tokens.textPrimary,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    glowColor: Color = NeonPurple,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val shape = RoundedCornerShape(14.dp)
    val interactionSource = remember { MutableInteractionSource() }

    val baseMod = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> modifier
            .size(size)
            .pressScale(interactionSource, 0.94f)
            .neonGlow(color = glowColor, cornerRadius = 14.dp, alpha = 0.24f, spread = 5.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.14f),
                        Color.White.copy(alpha = 0.04f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.30f),
                        Color.White.copy(alpha = 0.10f)
                    )
                ),
                shape = shape
            )

        AppThemeMode.MINIMAL_DARK -> modifier
            .size(size)
            .pressScale(interactionSource, 0.96f)
            .clip(shape)
            .background(MinimalDarkCard)
            .border(1.dp, MinimalDarkOutline, shape)

        AppThemeMode.AMOLED_BLACK -> modifier
            .size(size)
            .pressScale(interactionSource, 0.96f)
            .clip(shape)
            .background(AmoledCard)
            .border(1.dp, AmoledOutline, shape)
    }

    Box(
        modifier = baseMod.clickable(
            interactionSource = interactionSource,
            indication = ripple(color = tokens.textPrimary),
            onClick = onClick
        ),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * Hero / Highlight 24dp Card
 */
@Composable
fun LiquidGlassHeroCard(
    modifier: Modifier = Modifier,
    accentColor: Color = LocalZenixTokens.current.primaryAccent,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val shape = RoundedCornerShape(24.dp)

    val cardMod = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> modifier
            .fillMaxWidth()
            .neonGlow(color = accentColor, cornerRadius = 24.dp, alpha = 0.34f, spread = 10.dp)
            .clip(shape)
            .background(Color(0xFF100A22).copy(alpha = 0.72f))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.28f),
                        NeonPink.copy(alpha = 0.16f),
                        NeonCyan.copy(alpha = 0.16f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1200f, 700f)
                )
            )
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.18f),
                        0.50f to Color.Transparent,
                        startY = 0f,
                        endY = size.height.coerceAtLeast(1f)
                    )
                )
            }
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        accentColor.copy(alpha = 0.72f),
                        Color.White.copy(alpha = 0.26f),
                        NeonCyan.copy(alpha = 0.45f)
                    )
                ),
                shape = shape
            )

        AppThemeMode.MINIMAL_DARK -> modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MinimalDarkCard)
            .border(1.dp, MinimalDarkOutline, shape)

        AppThemeMode.AMOLED_BLACK -> modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AmoledCard)
            .border(1.dp, accentColor.copy(alpha = 0.36f), shape)
    }

    Box(modifier = cardMod) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Primary Button (Purple/Theme-Accent filled with gloss + glow in Neon Glossy)
 */
@Composable
fun NeonPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentColor: Color = LocalZenixTokens.current.primaryAccent,
    cornerRadius: Dp = 16.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val alphaMult = if (enabled) 1f else 0.45f

    val deepColor = lerp(accentColor, Color(0xFF340B6E), 0.26f)
    val lightColor = lerp(accentColor, Color.White, 0.18f)

    val buttonMod = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
            .neonGlow(
                color = accentColor,
                cornerRadius = cornerRadius,
                alpha = if (enabled) 0.40f else 0.10f,
                spread = 8.dp
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        lightColor.copy(alpha = alphaMult),
                        deepColor.copy(alpha = alphaMult)
                    )
                )
            )
            .drawBehind {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.35f * alphaMult),
                        0.45f to Color.Transparent
                    ),
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                )
            }
            .border(1.dp, Color.White.copy(alpha = 0.30f * alphaMult), shape)

        AppThemeMode.MINIMAL_DARK, AppThemeMode.AMOLED_BLACK -> modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
            .clip(shape)
            .background(accentColor.copy(alpha = alphaMult))
    }

    val contentColor = if (accentColor == NeonCyan || accentColor == NeonGreen || accentColor == NeonAmber) {
        Color(0xFF0A0812).copy(alpha = if (enabled) 1f else 0.7f)
    } else {
        Color.White.copy(alpha = if (enabled) 1f else 0.7f)
    }

    Box(
        modifier = buttonMod
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = contentColor),
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content
            )
        }
    }
}

/**
 * Secondary Button (Glass / Outlined)
 */
@Composable
fun GlassSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentBorderColor: Color? = null,
    cornerRadius: Dp = 16.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val alphaMult = if (enabled) 1f else 0.45f

    val buttonMod = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> {
            val borderCol = accentBorderColor?.copy(alpha = 0.75f * alphaMult)
                ?: Color.White.copy(alpha = 0.26f * alphaMult)
            modifier
                .defaultMinSize(minHeight = 48.dp)
                .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
                .then(
                    if (accentBorderColor != null && enabled) {
                        Modifier.neonGlow(accentBorderColor, cornerRadius, alpha = 0.24f, spread = 6.dp)
                    } else Modifier
                )
                .clip(shape)
                .background((accentBorderColor ?: Color.White).copy(alpha = 0.10f * alphaMult))
                .border(1.dp, borderCol, shape)
        }

        AppThemeMode.MINIMAL_DARK -> {
            val borderCol = accentBorderColor?.copy(alpha = 0.65f * alphaMult) ?: MinimalDarkOutline.copy(alpha = alphaMult)
            modifier
                .defaultMinSize(minHeight = 48.dp)
                .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
                .clip(shape)
                .background(MinimalDarkCard.copy(alpha = alphaMult))
                .border(1.dp, borderCol, shape)
        }

        AppThemeMode.AMOLED_BLACK -> {
            val borderCol = accentBorderColor?.copy(alpha = 0.70f * alphaMult) ?: AmoledOutline.copy(alpha = alphaMult)
            modifier
                .defaultMinSize(minHeight = 48.dp)
                .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
                .clip(shape)
                .background(AmoledCard.copy(alpha = alphaMult))
                .border(1.dp, borderCol, shape)
        }
    }

    val contentColor = (accentBorderColor ?: tokens.secondaryButtonText).copy(alpha = if (enabled) 1f else 0.6f)

    Box(
        modifier = buttonMod
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = contentColor),
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content
            )
        }
    }
}

/**
 * Rounded 24dp Glass Card with Soft Neon Glow Borders
 */
@Composable
fun NeonGlassCard(
    modifier: Modifier = Modifier,
    accentColor: Color = LocalZenixTokens.current.primaryAccent,
    cornerRadius: Dp = 24.dp,
    glowAlpha: Float = 0.24f,
    @Suppress("UNUSED_PARAMETER") pastelFill: Color? = null,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }

    val cardMod = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> modifier
            .then(if (onClick != null) Modifier.pressScale(interactionSource, 0.985f) else Modifier)
            .neonGlow(color = accentColor, cornerRadius = cornerRadius, alpha = glowAlpha, spread = 8.dp)
            .clip(shape)
            .background(Color(0xFF0E0A1B).copy(alpha = 0.74f))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.03f)
                    )
                )
            )
            .drawBehind {
                drawLine(
                    color = Color.White.copy(alpha = 0.20f),
                    start = Offset(cornerRadius.toPx() * 0.65f, 1.dp.toPx()),
                    end = Offset(size.width - cornerRadius.toPx() * 0.65f, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.72f),
                        accentColor.copy(alpha = 0.34f),
                        Color.White.copy(alpha = 0.20f)
                    )
                ),
                shape = shape
            )

        AppThemeMode.MINIMAL_DARK -> modifier
            .then(if (onClick != null) Modifier.pressScale(interactionSource, 0.99f) else Modifier)
            .clip(shape)
            .background(MinimalDarkCard)
            .border(1.dp, MinimalDarkOutline, shape)

        AppThemeMode.AMOLED_BLACK -> modifier
            .then(if (onClick != null) Modifier.pressScale(interactionSource, 0.99f) else Modifier)
            .clip(shape)
            .background(AmoledCard)
            .border(1.dp, AmoledOutline, shape)
    }

    Box(
        modifier = cardMod.then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = accentColor),
                    onClick = onClick
                )
            } else Modifier
        )
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

/**
 * Chips and Pills
 */
@Composable
fun NeonGlassChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalZenixTokens.current.primaryAccent,
    leadingIcon: ImageVector? = null
) {
    val tokens = LocalZenixTokens.current
    val shape = RoundedCornerShape(tokens.chipRadius)
    val interactionSource = remember { MutableInteractionSource() }

    val glowAlpha by animateFloatAsState(
        targetValue = if (tokens.useGlow) (if (selected) 0.36f else 0.14f) else 0f,
        animationSpec = tween(220),
        label = "chip_glow"
    )
    val targetTextColor = if (selected) {
        if (tokens.mode == AppThemeMode.NEON_GLOSSY && (accentColor == NeonCyan || accentColor == NeonGreen || accentColor == NeonAmber)) {
            Color(0xFF0A0812)
        } else {
            Color.White
        }
    } else {
        tokens.textPrimary
    }
    val textColor by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = tween(200),
        label = "chip_text"
    )

    val chipSurfaceMod = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> {
            if (selected) {
                val gradEnd = lerp(accentColor, Color(0xFF5D21D1), 0.35f)
                Modifier
                    .neonGlow(color = accentColor, cornerRadius = 20.dp, alpha = glowAlpha, spread = 6.dp)
                    .clip(shape)
                    .background(Brush.horizontalGradient(colors = listOf(accentColor, gradEnd)))
                    .border(1.dp, Color.White.copy(alpha = 0.40f), shape)
            } else {
                Modifier
                    .neonGlow(color = accentColor, cornerRadius = 20.dp, alpha = glowAlpha, spread = 5.dp)
                    .clip(shape)
                    .background(Color(0xFF0E0A1C).copy(alpha = 0.65f))
                    .border(1.dp, accentColor.copy(alpha = 0.52f), shape)
            }
        }

        AppThemeMode.MINIMAL_DARK -> {
            if (selected) {
                Modifier
                    .clip(shape)
                    .background(tokens.primaryAccent.copy(alpha = 0.24f))
                    .border(1.dp, tokens.primaryAccent, shape)
            } else {
                Modifier
                    .clip(shape)
                    .background(MinimalDarkCard)
                    .border(1.dp, MinimalDarkOutline, shape)
            }
        }

        AppThemeMode.AMOLED_BLACK -> {
            if (selected) {
                Modifier
                    .clip(shape)
                    .background(accentColor.copy(alpha = 0.24f))
                    .border(1.dp, accentColor, shape)
            } else {
                Modifier
                    .clip(shape)
                    .background(AmoledCard)
                    .border(1.dp, AmoledOutline, shape)
            }
        }
    }

    val iconTint = if (selected && tokens.mode == AppThemeMode.NEON_GLOSSY) textColor else accentColor

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp)
            .pressScale(interactionSource, 0.96f)
            .then(chipSurfaceMod)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accentColor),
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Text Fields
 */
@Composable
fun NeonGlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 6,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    focusNeonColor: Color = LocalZenixTokens.current.primaryAccent
) {
    val tokens = LocalZenixTokens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val corner = 16.dp
    val shape = RoundedCornerShape(corner)

    val focusedColor = if (tokens.mode == AppThemeMode.MINIMAL_DARK) tokens.primaryAccent else focusNeonColor
    val unfocusedBorder = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color.White.copy(alpha = 0.22f)
        AppThemeMode.MINIMAL_DARK -> MinimalDarkOutline
        AppThemeMode.AMOLED_BLACK -> AmoledOutline
    }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) focusedColor else unfocusedBorder,
        animationSpec = tween(180),
        label = "textfield_border"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (tokens.useGlow && isFocused) 0.34f else 0f,
        animationSpec = tween(200),
        label = "textfield_glow"
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isFocused) focusedColor else tokens.textSecondary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(focusedColor),
            textStyle = TextStyle(
                fontFamily = tokens.fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = tokens.textPrimary
            ),
            decorationBox = { innerTextField ->
                val boxMod = when (tokens.mode) {
                    AppThemeMode.NEON_GLOSSY -> Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = if (minLines > 1) 76.dp else 48.dp)
                        .neonGlow(color = focusNeonColor, cornerRadius = corner, alpha = glowAlpha, spread = 6.dp)
                        .clip(shape)
                        .background(Color(0xFF0B0717).copy(alpha = 0.80f))
                        .border(1.dp, borderColor, shape)

                    AppThemeMode.MINIMAL_DARK -> Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = if (minLines > 1) 76.dp else 48.dp)
                        .clip(shape)
                        .background(MinimalDarkSurface)
                        .border(if (isFocused) 1.5.dp else 1.dp, borderColor, shape)

                    AppThemeMode.AMOLED_BLACK -> Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = if (minLines > 1) 76.dp else 48.dp)
                        .clip(shape)
                        .background(AmoledSurface)
                        .border(if (isFocused) 1.5.dp else 1.dp, borderColor, shape)
                }

                Row(
                    modifier = boxMod.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = if (minLines > 1) Alignment.Top else Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (leadingIcon != null) {
                        CompositionLocalProvider(LocalContentColor provides if (isFocused) focusedColor else tokens.textSecondary) {
                            leadingIcon()
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty() && !placeholder.isNullOrBlank()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.textSecondary.copy(alpha = 0.65f),
                                maxLines = if (singleLine) 1 else 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }
                    if (trailingIcon != null) {
                        CompositionLocalProvider(LocalContentColor provides tokens.textSecondary) {
                            trailingIcon()
                        }
                    }
                }
            }
        )
    }
}

/**
 * Dialog
 */
@Composable
fun NeonGlassDialog(
    onDismissRequest: () -> Unit,
    title: String,
    accentColor: Color = LocalZenixTokens.current.primaryAccent,
    confirmText: String? = null,
    onConfirm: (() -> Unit)? = null,
    confirmEnabled: Boolean = true,
    confirmDangerous: Boolean = false,
    dismissText: String? = "Cancel",
    secondaryText: String? = null,
    onSecondary: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val shape = RoundedCornerShape(24.dp)
        val dialogMod = when (tokens.mode) {
            AppThemeMode.NEON_GLOSSY -> Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .neonGlow(color = accentColor, cornerRadius = 24.dp, alpha = 0.34f, spread = 10.dp)
                .clip(shape)
                .background(Color(0xFF100A21).copy(alpha = 0.95f))
                .border(1.dp, accentColor.copy(alpha = 0.72f), shape)

            AppThemeMode.MINIMAL_DARK -> Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(shape)
                .background(MinimalDarkSurface)
                .border(1.dp, MinimalDarkOutline, shape)

            AppThemeMode.AMOLED_BLACK -> Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(shape)
                .background(AmoledSurface)
                .border(1.dp, AmoledOutline, shape)
        }

        Box(modifier = dialogMod) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = tokens.textPrimary
                )
                content()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (dismissText != null) {
                        GlassSecondaryButton(
                            onClick = onDismissRequest,
                            cornerRadius = 14.dp,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(dismissText, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    if (secondaryText != null && onSecondary != null) {
                        GlassSecondaryButton(
                            onClick = onSecondary,
                            accentBorderColor = tokens.tertiaryAccent,
                            cornerRadius = 14.dp,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(secondaryText, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    if (confirmText != null && onConfirm != null) {
                        if (confirmDangerous) {
                            GlassSecondaryButton(
                                onClick = onConfirm,
                                enabled = confirmEnabled,
                                accentBorderColor = NeonPink,
                                cornerRadius = 14.dp,
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(confirmText, style = MaterialTheme.typography.labelLarge, color = NeonPink, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            NeonPrimaryButton(
                                onClick = onConfirm,
                                enabled = confirmEnabled,
                                cornerRadius = 14.dp,
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(confirmText, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class LiquidNavTab(
    val id: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun rememberSafeBlurEnabled(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false
    if (LocalInspectionMode.current) return false
    val view = LocalView.current
    var isHwAccelerated by remember(view) {
        mutableStateOf(view.isAttachedToWindow && view.isHardwareAccelerated)
    }
    DisposableEffect(view) {
        if (view.isAttachedToWindow) {
            isHwAccelerated = view.isHardwareAccelerated
        }
        val listener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                isHwAccelerated = v.isHardwareAccelerated
            }
            override fun onViewDetachedFromWindow(v: View) {}
        }
        view.addOnAttachStateChangeListener(listener)
        onDispose { view.removeOnAttachStateChangeListener(listener) }
    }
    if (!isHwAccelerated) return false
    return remember {
        runCatching { HazeDefaults.blurEnabled() }.getOrDefault(false)
    }
}

@Composable
fun rememberSafeHazeState(enabled: Boolean = true): HazeState? {
    val safeBlur = rememberSafeBlurEnabled()
    return remember(enabled, safeBlur) {
        if (!enabled || !safeBlur) null
        else runCatching { HazeState() }.getOrNull()
    }
}

fun Modifier.safeHazeSource(hazeState: HazeState?): Modifier {
    if (hazeState == null) return this
    return runCatching { this.hazeSource(state = hazeState) }.getOrDefault(this)
}

/**
 * Floating Glass Bottom Nav (Home, Movies, Series, Anime, K-Drama)
 * Active tab = purple/accent gradient pill across all 3 dark themes.
 */
@Composable
fun ZenixBottomBar(
    tabs: List<LiquidNavTab>,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
    hazeState: HazeState?,
    modifier: Modifier = Modifier
) {
    LiquidGlassBottomBar(
        tabs = tabs,
        selectedIndex = selectedIndex,
        onSelectTab = onSelectTab,
        hazeState = hazeState,
        modifier = modifier
    )
}

@Composable
fun LiquidGlassBottomBar(
    tabs: List<LiquidNavTab>,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
    hazeState: HazeState?,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val pillShape = RoundedCornerShape(36.dp)
    val hasActiveTab = selectedIndex in tabs.indices
    val safeIndex = if (hasActiveTab) selectedIndex else 0
    val pillAlpha by animateFloatAsState(
        targetValue = if (hasActiveTab) 1f else 0f,
        animationSpec = tween(220),
        label = "bottom_bar_pill_alpha"
    )
    val canBlur = tokens.useBlur && hazeState != null && rememberSafeBlurEnabled()
    val fallbackGlassColor = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color(0xFF130D24).copy(alpha = 0.88f)
        AppThemeMode.MINIMAL_DARK -> MinimalDarkSurface.copy(alpha = 0.96f)
        AppThemeMode.AMOLED_BLACK -> AmoledSurface.copy(alpha = 0.96f)
    }
    val borderColor = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color.White.copy(alpha = 0.24f)
        AppThemeMode.MINIMAL_DARK -> MinimalDarkOutline
        AppThemeMode.AMOLED_BLACK -> AmoledOutline
    }

    val activeStart = tokens.primaryAccent
    val activeEnd = lerp(tokens.primaryAccent, Color(0xFF531ED4), 0.42f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 14.dp, end = 14.dp, bottom = 10.dp)
    ) {
        val blurLayerModifier = if (canBlur && hazeState != null) {
            runCatching {
                Modifier.hazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        backgroundColor = Color(0xFF0E091C),
                        blurRadius = 20.dp,
                        tint = HazeTint(Color.White.copy(alpha = 0.08f)),
                        noiseFactor = 0f,
                        fallbackTint = HazeTint(fallbackGlassColor)
                    )
                )
            }.getOrElse {
                Modifier.background(fallbackGlassColor)
            }
        } else {
            Modifier.background(fallbackGlassColor)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .then(
                    if (tokens.useGlow) {
                        Modifier.neonGlow(color = tokens.primaryAccent, cornerRadius = 36.dp, alpha = 0.34f, spread = 10.dp)
                    } else Modifier
                )
                .clip(pillShape)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .then(blurLayerModifier)
                    .then(
                        if (tokens.useGloss) {
                            Modifier.background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.14f),
                                        Color.White.copy(alpha = 0.04f)
                                    )
                                )
                            )
                        } else Modifier
                    )
                    .border(1.dp, borderColor, pillShape)
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            ) {
                val slotWidth = maxWidth / tabs.size.coerceAtLeast(1)
                val indicatorOffset by animateDpAsState(
                    targetValue = slotWidth * safeIndex,
                    animationSpec = spring(
                        dampingRatio = 0.78f,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "bottom_bar_pill_offset"
                )

                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(slotWidth)
                        .fillMaxHeight()
                        .padding(2.dp)
                        .graphicsLayer { alpha = pillAlpha }
                        .then(
                            if (tokens.useGlow) {
                                Modifier.neonGlow(color = activeStart, cornerRadius = 28.dp, alpha = 0.40f * pillAlpha, spread = 6.dp)
                            } else Modifier
                        )
                        .clip(RoundedCornerShape(28.dp))
                        .background(Brush.linearGradient(colors = listOf(activeStart, activeEnd)))
                        .border(1.dp, Color.White.copy(alpha = 0.32f), RoundedCornerShape(28.dp))
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = index == selectedIndex
                        val tint by animateColorAsState(
                            targetValue = if (isSelected) tokens.primaryButtonText else tokens.textSecondary,
                            animationSpec = tween(200),
                            label = "tab_tint_$index"
                        )
                        val interactionSource = remember { MutableInteractionSource() }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(28.dp))
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = ripple(color = OffWhiteText),
                                    onClick = { onSelectTab(index) }
                                )
                                .testTag("bottom_tab_${tab.id}"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = tint,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.label,
                                fontFamily = tokens.fontFamily,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = tint,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Themed Progress Bar
 */
@Composable
fun NeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    startColor: Color = NeonPurple,
    endColor: Color = NeonCyan
) {
    val tokens = LocalZenixTokens.current
    val clamped = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = clamped,
        animationSpec = tween(durationMillis = 500),
        label = "neon_progress"
    )
    val shape = RoundedCornerShape(50)

    val trackBg = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color.White.copy(alpha = 0.08f)
        AppThemeMode.MINIMAL_DARK -> MinimalDarkSurface
        AppThemeMode.AMOLED_BLACK -> AmoledSurface
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(trackBg)
            .then(
                if (tokens.mode == AppThemeMode.NEON_GLOSSY) {
                    Modifier.border(0.5.dp, Color.White.copy(alpha = 0.16f), shape)
                } else Modifier
            )
    ) {
        if (animated > 0f) {
            val fillMod = when (tokens.mode) {
                AppThemeMode.NEON_GLOSSY -> Modifier
                    .fillMaxWidth(animated.coerceAtLeast(0.03f))
                    .fillMaxHeight()
                    .neonGlow(color = endColor, cornerRadius = height / 2, alpha = 0.38f, spread = 4.dp)
                    .clip(shape)
                    .background(Brush.horizontalGradient(listOf(startColor, endColor)))

                AppThemeMode.MINIMAL_DARK, AppThemeMode.AMOLED_BLACK -> Modifier
                    .fillMaxWidth(animated.coerceAtLeast(0.03f))
                    .fillMaxHeight()
                    .clip(shape)
                    .background(Brush.horizontalGradient(listOf(startColor, endColor)))
            }
            Box(modifier = fillMod)
        }
    }
}

@Composable
fun NeonRatingStars(
    ratingOutOfTen: Double,
    modifier: Modifier = Modifier,
    starSize: Dp = 15.dp
) {
    val tokens = LocalZenixTokens.current
    val stars = ((ratingOutOfTen / 2.0) * 2.0).toInt() / 2.0
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val filled = stars >= i - 0.25
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = if (filled) tokens.ratingGold else tokens.textSecondary.copy(alpha = 0.42f),
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (filled && tokens.useGlow) {
                            Modifier.neonGlow(tokens.ratingGold, cornerRadius = starSize / 2, alpha = 0.26f, spread = 3.dp)
                        } else Modifier
                    )
            )
        }
    }
}
