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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.ui.theme.AndroidBackground
import com.example.ui.theme.AndroidCardSurface
import com.example.ui.theme.AndroidOnPrimary
import com.example.ui.theme.AndroidOnSecondaryContainer
import com.example.ui.theme.AndroidOutline
import com.example.ui.theme.AndroidOutlineVariant
import com.example.ui.theme.AndroidPrimary
import com.example.ui.theme.AndroidSecondaryContainer
import com.example.ui.theme.AndroidSurface
import com.example.ui.theme.AndroidTextPrimary
import com.example.ui.theme.AndroidTextSecondary
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.GoldRating
import com.example.ui.theme.LocalAppThemeMode
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.NeonButtonDarkText
import com.example.ui.theme.NeonButtonDeep
import com.example.ui.theme.NeonButtonLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonFuchsia
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OffWhiteText
import com.example.ui.theme.PastelAccentCoral
import com.example.ui.theme.PastelAccentMint
import com.example.ui.theme.PastelAccentPlum
import com.example.ui.theme.PastelAccentRose
import com.example.ui.theme.PastelAccentTeal
import com.example.ui.theme.PastelBackground
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelOutline
import com.example.ui.theme.PastelOutlineStrong
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelSky
import com.example.ui.theme.PastelTextPrimary
import com.example.ui.theme.PastelTextSecondary
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
    MediaCategory.MOVIE -> NeonPurple
    MediaCategory.WEB_SERIES -> NeonCyan
    MediaCategory.ANIME -> NeonPink
    MediaCategory.K_DRAMA -> NeonFuchsia
}

fun neonPaletteColor(index: Int): Color = when (index % 5) {
    0 -> NeonPurple
    1 -> NeonCyan
    2 -> NeonPink
    3 -> NeonGreen
    else -> NeonFuchsia
}

@Composable
fun themedCategoryColor(category: MediaCategory): Color = when (LocalAppThemeMode.current) {
    AppThemeMode.NEON -> categoryNeonColor(category)
    AppThemeMode.ANDROID_UI -> when (category) {
        MediaCategory.MOVIE -> AndroidPrimary
        MediaCategory.WEB_SERIES -> Color(0xFF80D8FF)
        MediaCategory.ANIME -> Color(0xFFEFB8C8)
        MediaCategory.K_DRAMA -> Color(0xFFCE93D8)
    }
    AppThemeMode.MINIMAL_PASTEL -> when (category) {
        MediaCategory.MOVIE -> PastelAccentPlum
        MediaCategory.WEB_SERIES -> PastelAccentTeal
        MediaCategory.ANIME -> PastelAccentCoral
        MediaCategory.K_DRAMA -> PastelAccentRose
    }
}

@Composable
fun themedPaletteColor(index: Int): Color = when (LocalAppThemeMode.current) {
    AppThemeMode.NEON -> neonPaletteColor(index)
    AppThemeMode.ANDROID_UI -> when (index % 5) {
        0 -> AndroidPrimary
        1 -> Color(0xFF80D8FF)
        2 -> Color(0xFFEFB8C8)
        3 -> Color(0xFF81C784)
        else -> Color(0xFFCE93D8)
    }
    AppThemeMode.MINIMAL_PASTEL -> when (index % 5) {
        0 -> PastelAccentPlum
        1 -> PastelAccentTeal
        2 -> PastelAccentCoral
        3 -> PastelAccentMint
        else -> PastelAccentRose
    }
}

@Composable
fun themedPastelFill(index: Int): Color? =
    if (LocalAppThemeMode.current == AppThemeMode.MINIMAL_PASTEL) {
        when (index % 4) {
            0 -> PastelLavender
            1 -> PastelSky
            2 -> PastelPeach
            else -> PastelMint
        }
    } else null

/**
 * Draws a soft, multi-layered neon outer glow behind any rounded component
 * without clipping or requiring RenderEffect-only APIs.
 */
fun Modifier.neonGlow(
    color: Color,
    cornerRadius: Dp = 22.dp,
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
 * Global themed backdrop:
 * - NEON: #07050F with soft radial glows (magenta top-right, cyan left-middle, purple bottom)
 * - ANDROID_UI: flat #141218
 * - MINIMAL_PASTEL: warm light cream #FAF7F2
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
                val magentaCenter = Offset(size.width * 0.92f, size.height * 0.06f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to RadialGlowMagenta.copy(alpha = 0.45f),
                            0.45f to RadialGlowMagenta.copy(alpha = 0.16f),
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
                            0.0f to RadialGlowCyan.copy(alpha = 0.28f),
                            0.45f to RadialGlowCyan.copy(alpha = 0.10f),
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
                            0.0f to NeonPurple.copy(alpha = 0.20f),
                            0.6f to NeonPurple.copy(alpha = 0.06f),
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
 * Screen Header: menu icon, themed ZENIX logo, "ZENIX" title with letter spacing, "+" button.
 */
@Composable
fun AfterglowTopBar(
    onOpenMenu: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val mode = tokens.mode

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
                modifier = Modifier.testTag("open_drawer_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Open navigation menu",
                    tint = tokens.textPrimary,
                    modifier = Modifier.size(21.dp)
                )
            }

            when (mode) {
                AppThemeMode.NEON -> {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .neonGlow(color = NeonPink, cornerRadius = 10.dp, alpha = 0.45f, spread = 6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(colors = listOf(NeonPurple, NeonPink)))
                            .border(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.12f))
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                AppThemeMode.ANDROID_UI -> {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AndroidPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = AndroidOnPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                AppThemeMode.MINIMAL_PASTEL -> {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PastelLavender)
                            .border(1.dp, PastelOutlineStrong, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = PastelTextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = "ZENIX",
                fontFamily = tokens.fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 2.6.sp,
                color = tokens.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        GlassIconButton(
            onClick = onAddClick,
            glowColor = tokens.secondaryAccent,
            modifier = Modifier.testTag("topbar_add_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add title",
                tint = tokens.textPrimary,
                modifier = Modifier.size(22.dp)
            )
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
    val shape = RoundedCornerShape(15.dp)
    val interactionSource = remember { MutableInteractionSource() }

    val baseMod = when (tokens.mode) {
        AppThemeMode.NEON -> modifier
            .size(size)
            .pressScale(interactionSource, 0.94f)
            .neonGlow(color = glowColor, cornerRadius = 15.dp, alpha = 0.24f, spread = 5.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.34f),
                        Color.White.copy(alpha = 0.12f)
                    )
                ),
                shape = shape
            )

        AppThemeMode.ANDROID_UI -> modifier
            .size(size)
            .pressScale(interactionSource, 0.96f)
            .clip(shape)
            .background(AndroidCardSurface)
            .border(1.dp, AndroidOutlineVariant, shape)

        AppThemeMode.MINIMAL_PASTEL -> modifier
            .size(size)
            .pressScale(interactionSource, 0.96f)
            .shadow(elevation = 4.dp, shape = shape, ambientColor = PastelTextPrimary.copy(alpha = 0.08f), spotColor = PastelTextPrimary.copy(alpha = 0.08f))
            .clip(shape)
            .background(Color.White)
            .border(1.dp, PastelOutline, shape)
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
 * Hero / Top Card: adapts across Neon (glossy liquid glass + glow), Android UI (flat M3 tonal container), and Minimal Pastel (lavender/sky soft card with shadow).
 */
@Composable
fun LiquidGlassHeroCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val shape = RoundedCornerShape(tokens.heroRadius)

    val cardMod = when (tokens.mode) {
        AppThemeMode.NEON -> modifier
            .fillMaxWidth()
            .neonGlow(color = NeonPurple, cornerRadius = 28.dp, alpha = 0.38f, spread = 11.dp)
            .clip(shape)
            .background(Color(0xFF100A22).copy(alpha = 0.65f))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        NeonPurple.copy(alpha = 0.34f),
                        NeonPink.copy(alpha = 0.24f),
                        NeonCyan.copy(alpha = 0.20f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1200f, 700f)
                )
            )
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.22f),
                        0.52f to Color.Transparent,
                        startY = 0f,
                        endY = size.height.coerceAtLeast(1f)
                    )
                )
            }
            .border(1.dp, Color.White.copy(alpha = 0.35f), shape)

        AppThemeMode.ANDROID_UI -> modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF2B2738))
            .border(1.dp, AndroidOutlineVariant, shape)

        AppThemeMode.MINIMAL_PASTEL -> modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = PastelTextPrimary.copy(alpha = 0.10f),
                spotColor = PastelTextPrimary.copy(alpha = 0.10f)
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        PastelLavender,
                        PastelSky.copy(alpha = 0.85f),
                        Color.White
                    )
                )
            )
            .border(1.dp, PastelOutline, shape)
    }

    Box(modifier = cardMod) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            content = content
        )
    }
}

/**
 * Primary Button:
 * - NEON: gradient #F0ABFC -> #C084FC, dark text, fuchsia glow, white inner top highlight
 * - ANDROID_UI: filled M3 pill button (#D0BCFF fill, #381E72 text, flat)
 * - MINIMAL_PASTEL: dark #2D2A32 pill button with white text
 */
@Composable
fun NeonPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadius: Dp = 16.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val effectiveRadius = if (tokens.mode == AppThemeMode.NEON) cornerRadius else 50.dp
    val shape = RoundedCornerShape(effectiveRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val alphaMult = if (enabled) 1f else 0.45f

    val buttonMod = when (tokens.mode) {
        AppThemeMode.NEON -> modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
            .neonGlow(
                color = NeonFuchsia,
                cornerRadius = effectiveRadius,
                alpha = if (enabled) 0.40f else 0.10f,
                spread = 8.dp
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NeonButtonLight.copy(alpha = alphaMult),
                        NeonButtonDeep.copy(alpha = alphaMult)
                    )
                )
            )
            .drawBehind {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.55f * alphaMult),
                        0.45f to Color.Transparent
                    ),
                    cornerRadius = CornerRadius(effectiveRadius.toPx(), effectiveRadius.toPx())
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.65f * alphaMult),
                    start = Offset(effectiveRadius.toPx() * 0.6f, 1.dp.toPx()),
                    end = Offset(size.width - effectiveRadius.toPx() * 0.6f, 1.dp.toPx()),
                    strokeWidth = 1.2.dp.toPx()
                )
            }
            .border(1.dp, Color.White.copy(alpha = 0.35f * alphaMult), shape)

        AppThemeMode.ANDROID_UI -> modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
            .clip(shape)
            .background(AndroidPrimary.copy(alpha = alphaMult))

        AppThemeMode.MINIMAL_PASTEL -> modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
            .shadow(
                elevation = if (enabled) 4.dp else 0.dp,
                shape = shape,
                ambientColor = PastelTextPrimary.copy(alpha = 0.18f),
                spotColor = PastelTextPrimary.copy(alpha = 0.18f)
            )
            .clip(shape)
            .background(PastelTextPrimary.copy(alpha = alphaMult))
    }

    val contentColor = tokens.primaryButtonText.copy(alpha = if (enabled) 1f else 0.7f)

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
 * Secondary Button:
 * - NEON: glass fill (white 12%) with thin border
 * - ANDROID_UI: flat tonal/outlined pill
 * - MINIMAL_PASTEL: white pill with soft shadow & subtle border
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
    val effectiveRadius = if (tokens.mode == AppThemeMode.NEON) cornerRadius else 50.dp
    val shape = RoundedCornerShape(effectiveRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val alphaMult = if (enabled) 1f else 0.45f

    val buttonMod = when (tokens.mode) {
        AppThemeMode.NEON -> {
            val borderCol = accentBorderColor?.copy(alpha = 0.60f * alphaMult)
                ?: Color.White.copy(alpha = 0.28f * alphaMult)
            modifier
                .defaultMinSize(minHeight = 48.dp)
                .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
                .then(
                    if (accentBorderColor != null && enabled) {
                        Modifier.neonGlow(accentBorderColor, effectiveRadius, alpha = 0.22f, spread = 6.dp)
                    } else Modifier
                )
                .clip(shape)
                .background(Color.White.copy(alpha = 0.12f * alphaMult))
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.14f * alphaMult),
                            0.45f to Color.Transparent
                        )
                    )
                }
                .border(1.dp, borderCol, shape)
        }

        AppThemeMode.ANDROID_UI -> {
            val borderCol = accentBorderColor?.copy(alpha = 0.65f * alphaMult) ?: AndroidOutline.copy(alpha = alphaMult)
            modifier
                .defaultMinSize(minHeight = 48.dp)
                .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
                .clip(shape)
                .background(AndroidSecondaryContainer.copy(alpha = 0.72f * alphaMult))
                .border(1.dp, borderCol, shape)
        }

        AppThemeMode.MINIMAL_PASTEL -> {
            val borderCol = accentBorderColor?.copy(alpha = 0.55f * alphaMult) ?: PastelOutlineStrong.copy(alpha = alphaMult)
            modifier
                .defaultMinSize(minHeight = 48.dp)
                .then(if (enabled) Modifier.pressScale(interactionSource, 0.97f) else Modifier)
                .shadow(
                    elevation = if (enabled) 3.dp else 0.dp,
                    shape = shape,
                    ambientColor = PastelTextPrimary.copy(alpha = 0.08f),
                    spotColor = PastelTextPrimary.copy(alpha = 0.08f)
                )
                .clip(shape)
                .background(Color.White.copy(alpha = alphaMult))
                .border(1.dp, borderCol, shape)
        }
    }

    val contentColor = tokens.secondaryButtonText.copy(alpha = if (enabled) 1f else 0.6f)

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
 * Cards (metric cards, title cards, list items):
 * - NEON: 22dp rounded, white 14%->3% gradient, 1dp neon border, soft glow
 * - ANDROID_UI: flat #2B2930 M3 card with subtle outline, no glow
 * - MINIMAL_PASTEL: white (or optional pastelFill) 22dp card with soft shadow
 */
@Composable
fun NeonGlassCard(
    modifier: Modifier = Modifier,
    accentColor: Color = NeonPurple,
    cornerRadius: Dp = 22.dp,
    glowAlpha: Float = 0.25f,
    pastelFill: Color? = null,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val effectiveRadius = if (tokens.mode == AppThemeMode.ANDROID_UI) cornerRadius.coerceAtMost(20.dp) else cornerRadius
    val shape = RoundedCornerShape(effectiveRadius)
    val interactionSource = remember { MutableInteractionSource() }

    val cardMod = when (tokens.mode) {
        AppThemeMode.NEON -> modifier
            .then(if (onClick != null) Modifier.pressScale(interactionSource, 0.985f) else Modifier)
            .neonGlow(color = accentColor, cornerRadius = effectiveRadius, alpha = glowAlpha, spread = 8.dp)
            .clip(shape)
            .background(Color(0xFF0D0919).copy(alpha = 0.62f))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.14f),
                        Color.White.copy(alpha = 0.03f)
                    )
                )
            )
            .drawBehind {
                drawLine(
                    color = Color.White.copy(alpha = 0.22f),
                    start = Offset(effectiveRadius.toPx() * 0.7f, 1.dp.toPx()),
                    end = Offset(size.width - effectiveRadius.toPx() * 0.7f, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.80f),
                        accentColor.copy(alpha = 0.42f),
                        Color.White.copy(alpha = 0.25f)
                    )
                ),
                shape = shape
            )

        AppThemeMode.ANDROID_UI -> modifier
            .then(if (onClick != null) Modifier.pressScale(interactionSource, 0.99f) else Modifier)
            .clip(shape)
            .background(AndroidCardSurface)
            .border(1.dp, AndroidOutlineVariant, shape)

        AppThemeMode.MINIMAL_PASTEL -> {
            val fill = pastelFill ?: Color.White
            modifier
                .then(if (onClick != null) Modifier.pressScale(interactionSource, 0.988f) else Modifier)
                .shadow(
                    elevation = 6.dp,
                    shape = shape,
                    ambientColor = PastelTextPrimary.copy(alpha = 0.08f),
                    spotColor = PastelTextPrimary.copy(alpha = 0.08f)
                )
                .clip(shape)
                .background(fill)
                .border(1.dp, PastelOutline.copy(alpha = if (pastelFill != null) 0.55f else 1f), shape)
        }
    }

    Box(
        modifier = cardMod.then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = if (tokens.isLight) PastelTextPrimary else accentColor),
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
 * Chips and filters:
 * - NEON: pill, glass fill, 1dp neon border with glow; selected = neon gradient
 * - ANDROID_UI: 8dp outlined chips, #4A4458 selected container
 * - MINIMAL_PASTEL: white pill with border; selected = lavender #E4DDF7 pill
 */
@Composable
fun NeonGlassChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = NeonPurple,
    leadingIcon: ImageVector? = null
) {
    val tokens = LocalZenixTokens.current
    val shape = RoundedCornerShape(tokens.chipRadius)
    val interactionSource = remember { MutableInteractionSource() }

    val glowAlpha by animateFloatAsState(
        targetValue = if (tokens.useGlow) (if (selected) 0.38f else 0.16f) else 0f,
        animationSpec = tween(220),
        label = "chip_glow"
    )
    val targetTextColor = when (tokens.mode) {
        AppThemeMode.NEON -> if (selected) Color.White else OffWhiteText
        AppThemeMode.ANDROID_UI -> if (selected) AndroidOnSecondaryContainer else AndroidTextPrimary
        AppThemeMode.MINIMAL_PASTEL -> PastelTextPrimary
    }
    val textColor by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = tween(200),
        label = "chip_text"
    )

    val chipSurfaceMod = when (tokens.mode) {
        AppThemeMode.NEON -> {
            if (selected) {
                Modifier
                    .neonGlow(color = accentColor, cornerRadius = 20.dp, alpha = glowAlpha, spread = 6.dp)
                    .clip(shape)
                    .background(Brush.horizontalGradient(colors = listOf(NeonFuchsia, NeonPurple)))
                    .drawBehind {
                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to Color.White.copy(alpha = 0.28f),
                                0.55f to Color.Transparent
                            )
                        )
                    }
                    .border(1.dp, Color.White.copy(alpha = 0.45f), shape)
            } else {
                Modifier
                    .neonGlow(color = accentColor, cornerRadius = 20.dp, alpha = glowAlpha, spread = 6.dp)
                    .clip(shape)
                    .background(Color(0xFF0E0A1C).copy(alpha = 0.55f))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.11f),
                                Color.White.copy(alpha = 0.04f)
                            )
                        )
                    )
                    .border(1.dp, accentColor.copy(alpha = 0.55f), shape)
            }
        }

        AppThemeMode.ANDROID_UI -> {
            if (selected) {
                Modifier
                    .clip(shape)
                    .background(AndroidSecondaryContainer)
                    .border(1.dp, AndroidPrimary.copy(alpha = 0.65f), shape)
            } else {
                Modifier
                    .clip(shape)
                    .background(Color.Transparent)
                    .border(1.dp, AndroidOutline, shape)
            }
        }

        AppThemeMode.MINIMAL_PASTEL -> {
            if (selected) {
                Modifier
                    .clip(shape)
                    .background(PastelLavender)
                    .border(1.dp, PastelAccentPlum.copy(alpha = 0.45f), shape)
            } else {
                Modifier
                    .shadow(2.dp, shape, ambientColor = PastelTextPrimary.copy(alpha = 0.06f), spotColor = PastelTextPrimary.copy(alpha = 0.06f))
                    .clip(shape)
                    .background(Color.White)
                    .border(1.dp, PastelOutlineStrong, shape)
            }
        }
    }

    val iconTint = when (tokens.mode) {
        AppThemeMode.NEON -> if (selected) Color.White else accentColor
        AppThemeMode.ANDROID_UI -> if (selected) AndroidPrimary else AndroidTextSecondary
        AppThemeMode.MINIMAL_PASTEL -> if (selected) PastelAccentPlum else accentColor
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp)
            .pressScale(interactionSource, 0.96f)
            .then(chipSurfaceMod)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = if (tokens.isLight) PastelTextPrimary else accentColor),
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
 * Text fields adapted across Neon, Android UI, and Minimal Pastel.
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
    focusNeonColor: Color = NeonFuchsia
) {
    val tokens = LocalZenixTokens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val corner = if (tokens.mode == AppThemeMode.ANDROID_UI) 12.dp else 16.dp
    val shape = RoundedCornerShape(corner)

    val focusedColor = when (tokens.mode) {
        AppThemeMode.NEON -> focusNeonColor
        AppThemeMode.ANDROID_UI -> AndroidPrimary
        AppThemeMode.MINIMAL_PASTEL -> PastelTextPrimary
    }
    val unfocusedBorder = when (tokens.mode) {
        AppThemeMode.NEON -> Color.White.copy(alpha = 0.22f)
        AppThemeMode.ANDROID_UI -> AndroidOutline
        AppThemeMode.MINIMAL_PASTEL -> PastelOutlineStrong
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
                    AppThemeMode.NEON -> Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = if (minLines > 1) 76.dp else 48.dp)
                        .neonGlow(color = focusNeonColor, cornerRadius = corner, alpha = glowAlpha, spread = 6.dp)
                        .clip(shape)
                        .background(Color(0xFF0B0717).copy(alpha = 0.78f))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.08f),
                                    Color.White.copy(alpha = 0.02f)
                                )
                            )
                        )
                        .drawBehind {
                            drawLine(
                                color = Color.White.copy(alpha = 0.26f),
                                start = Offset(14.dp.toPx(), 1.dp.toPx()),
                                end = Offset(size.width - 14.dp.toPx(), 1.dp.toPx()),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .border(1.dp, borderColor, shape)

                    AppThemeMode.ANDROID_UI -> Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = if (minLines > 1) 76.dp else 48.dp)
                        .clip(shape)
                        .background(AndroidCardSurface)
                        .border(if (isFocused) 1.5.dp else 1.dp, borderColor, shape)

                    AppThemeMode.MINIMAL_PASTEL -> Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = if (minLines > 1) 76.dp else 48.dp)
                        .shadow(2.dp, shape, ambientColor = PastelTextPrimary.copy(alpha = 0.06f), spotColor = PastelTextPrimary.copy(alpha = 0.06f))
                        .clip(shape)
                        .background(Color.White)
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
 * Themed Dialog adapted across Neon, Android UI, and Minimal Pastel.
 */
@Composable
fun NeonGlassDialog(
    onDismissRequest: () -> Unit,
    title: String,
    accentColor: Color = NeonPurple,
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
        val shape = RoundedCornerShape(28.dp)
        val dialogMod = when (tokens.mode) {
            AppThemeMode.NEON -> Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .neonGlow(color = accentColor, cornerRadius = 28.dp, alpha = 0.36f, spread = 10.dp)
                .clip(shape)
                .background(Color(0xFF100A21).copy(alpha = 0.94f))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    )
                )
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            0f to Color.White.copy(alpha = 0.16f),
                            0.35f to Color.Transparent
                        )
                    )
                }
                .border(1.dp, accentColor.copy(alpha = 0.72f), shape)

            AppThemeMode.ANDROID_UI -> Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(shape)
                .background(AndroidCardSurface)
                .border(1.dp, AndroidOutlineVariant, shape)

            AppThemeMode.MINIMAL_PASTEL -> Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .shadow(16.dp, shape, ambientColor = PastelTextPrimary.copy(alpha = 0.14f), spotColor = PastelTextPrimary.copy(alpha = 0.14f))
                .clip(shape)
                .background(Color.White)
                .border(1.dp, PastelOutline, shape)
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
                                accentBorderColor = tokens.dangerAccent,
                                cornerRadius = 14.dp,
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(confirmText, style = MaterialTheme.typography.labelLarge, color = tokens.dangerAccent, fontWeight = FontWeight.Bold)
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
 * Multi-theme Bottom Navigation Bar:
 * - NEON: Floating liquid glass pill with Haze blur (safe fallback below Android 12) + fuchsia->purple active pill
 * - ANDROID_UI: Standard full-width Material 3 NavigationBar (80dp, selected icon in 64x32 #4A4458 pill)
 * - MINIMAL_PASTEL: Floating white pill with soft shadow + lavender (#E4DDF7) active pill
 */
@Composable
fun ZenixBottomBar(
    tabs: List<LiquidNavTab>,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
    hazeState: HazeState?,
    modifier: Modifier = Modifier
) {
    when (LocalAppThemeMode.current) {
        AppThemeMode.NEON -> LiquidGlassBottomBar(
            tabs = tabs,
            selectedIndex = selectedIndex,
            onSelectTab = onSelectTab,
            hazeState = hazeState,
            modifier = modifier
        )

        AppThemeMode.ANDROID_UI -> AndroidMaterialBottomBar(
            tabs = tabs,
            selectedIndex = selectedIndex,
            onSelectTab = onSelectTab,
            modifier = modifier
        )

        AppThemeMode.MINIMAL_PASTEL -> PastelFloatingBottomBar(
            tabs = tabs,
            selectedIndex = selectedIndex,
            onSelectTab = onSelectTab,
            modifier = modifier
        )
    }
}

@Composable
private fun AndroidMaterialBottomBar(
    tabs: List<LiquidNavTab>,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = AndroidSurface,
        contentColor = AndroidTextPrimary,
        tonalElevation = 0.dp
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = index == selectedIndex
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(index) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AndroidOnSecondaryContainer,
                    selectedTextColor = AndroidTextPrimary,
                    indicatorColor = AndroidSecondaryContainer,
                    unselectedIconColor = AndroidTextSecondary,
                    unselectedTextColor = AndroidTextSecondary
                ),
                modifier = Modifier.testTag("bottom_tab_${tab.id}")
            )
        }
    }
}

@Composable
private fun PastelFloatingBottomBar(
    tabs: List<LiquidNavTab>,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pillShape = RoundedCornerShape(36.dp)
    val hasActiveTab = selectedIndex in tabs.indices
    val safeIndex = if (hasActiveTab) selectedIndex else 0
    val pillAlpha by animateFloatAsState(
        targetValue = if (hasActiveTab) 1f else 0f,
        animationSpec = tween(220),
        label = "pastel_bar_pill_alpha"
    )
    val tokens = LocalZenixTokens.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 14.dp, end = 14.dp, bottom = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = pillShape,
                    ambientColor = PastelTextPrimary.copy(alpha = 0.12f),
                    spotColor = PastelTextPrimary.copy(alpha = 0.12f)
                )
                .clip(pillShape)
                .background(Color.White)
                .border(1.dp, PastelOutline, pillShape)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            ) {
                val slotWidth = maxWidth / tabs.size.coerceAtLeast(1)
                val indicatorOffset by animateDpAsState(
                    targetValue = slotWidth * safeIndex,
                    animationSpec = spring(
                        dampingRatio = 0.80f,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "pastel_bar_pill_offset"
                )

                // Animated Lavender Pill
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(slotWidth)
                        .fillMaxHeight()
                        .padding(2.dp)
                        .graphicsLayer { alpha = pillAlpha }
                        .clip(RoundedCornerShape(28.dp))
                        .background(PastelLavender)
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = index == selectedIndex
                        val tint by animateColorAsState(
                            targetValue = if (isSelected) PastelTextPrimary else PastelTextSecondary,
                            animationSpec = tween(200),
                            label = "pastel_tab_tint_$index"
                        )
                        val interactionSource = remember { MutableInteractionSource() }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(28.dp))
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = ripple(color = PastelTextPrimary),
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
 * Floating Liquid Glass Bottom Bar (Neon Theme)
 */
@Composable
fun LiquidGlassBottomBar(
    tabs: List<LiquidNavTab>,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
    hazeState: HazeState?,
    modifier: Modifier = Modifier
) {
    val pillShape = RoundedCornerShape(36.dp)
    val hasActiveTab = selectedIndex in tabs.indices
    val safeIndex = if (hasActiveTab) selectedIndex else 0
    val pillAlpha by animateFloatAsState(
        targetValue = if (hasActiveTab) 1f else 0f,
        animationSpec = tween(220),
        label = "bottom_bar_pill_alpha"
    )
    val canBlur = hazeState != null && rememberSafeBlurEnabled()
    val fallbackGlassColor = Color(0xFF130D24).copy(alpha = 0.88f)
    val tokens = LocalZenixTokens.current

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
                .neonGlow(color = NeonPurple, cornerRadius = 36.dp, alpha = 0.38f, spread = 10.dp)
                .clip(pillShape)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .then(blurLayerModifier)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.16f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .drawBehind {
                        drawLine(
                            color = Color.White.copy(alpha = 0.34f),
                            start = Offset(32.dp.toPx(), 1.dp.toPx()),
                            end = Offset(size.width - 32.dp.toPx(), 1.dp.toPx()),
                            strokeWidth = 1.2.dp.toPx()
                        )
                    }
                    .border(1.dp, Color.White.copy(alpha = 0.28f), pillShape)
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
                        .neonGlow(color = NeonFuchsia, cornerRadius = 28.dp, alpha = 0.42f * pillAlpha, spread = 6.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NeonPink, NeonPurple)
                            )
                        )
                        .drawBehind {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    0.0f to Color.White.copy(alpha = 0.34f),
                                    0.55f to Color.Transparent
                                )
                            )
                        }
                        .border(1.dp, Color.White.copy(alpha = 0.42f), RoundedCornerShape(28.dp))
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = index == selectedIndex
                        val tint by animateColorAsState(
                            targetValue = if (isSelected) Color.White else tokens.textSecondary,
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
        AppThemeMode.NEON -> Color.White.copy(alpha = 0.08f)
        AppThemeMode.ANDROID_UI -> AndroidSurface
        AppThemeMode.MINIMAL_PASTEL -> Color(0xFFECE6DB)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(trackBg)
            .then(
                if (tokens.mode == AppThemeMode.NEON) {
                    Modifier.border(0.5.dp, Color.White.copy(alpha = 0.16f), shape)
                } else Modifier
            )
    ) {
        if (animated > 0f) {
            val fillMod = when (tokens.mode) {
                AppThemeMode.NEON -> Modifier
                    .fillMaxWidth(animated.coerceAtLeast(0.03f))
                    .fillMaxHeight()
                    .neonGlow(color = endColor, cornerRadius = height / 2, alpha = 0.40f, spread = 4.dp)
                    .clip(shape)
                    .background(Brush.horizontalGradient(listOf(startColor, NeonFuchsia, endColor)))
                    .drawBehind {
                        drawLine(
                            color = Color.White.copy(alpha = 0.55f),
                            start = Offset(0f, 1.dp.toPx()),
                            end = Offset(size.width, 1.dp.toPx()),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                AppThemeMode.ANDROID_UI -> Modifier
                    .fillMaxWidth(animated.coerceAtLeast(0.03f))
                    .fillMaxHeight()
                    .clip(shape)
                    .background(startColor)

                AppThemeMode.MINIMAL_PASTEL -> Modifier
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
