package com.example.ui.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ui.theme.LocalReducedMotion
import com.example.ui.theme.LocalZenixRadii
import com.example.ui.theme.LocalZenixSpacing
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
import kotlin.math.roundToLong

val FloatingBottomBarClearance: Dp = 112.dp

@Composable
fun rememberFloatingBottomBarClearance(): Dp {
    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return 80.dp + navBarBottom + 16.dp
}

@Composable
fun HorizontalScrollFadeBox(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    fadeLeft: Boolean = true,
    fadeRight: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                val fadeWidth = 28.dp.toPx()
                if (fadeRight && scrollState.canScrollForward && size.width > fadeWidth) {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Black, Color.Transparent),
                            startX = size.width - fadeWidth,
                            endX = size.width
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
                if (fadeLeft && scrollState.canScrollBackward && size.width > fadeWidth) {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startX = 0f,
                            endX = fadeWidth
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
            }
    ) {
        content()
    }
}

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

fun Modifier.zenixHeading(): Modifier = semantics { heading() }

fun Modifier.zenixFocusRing(
    accentColor: Color,
    cornerRadius: Dp = 16.dp
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    this
        .onFocusChanged { isFocused = it.isFocused }
        .then(
            if (isFocused) {
                Modifier.border(2.dp, accentColor, RoundedCornerShape(cornerRadius))
            } else Modifier
        )
}

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

/**
 * Press micro-animation: scales to 0.96 over 150ms ease-out using graphicsLayer (GPU transform).
 * Automatically disabled when Reduced Motion is active.
 */
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.96f
): Modifier = composed {
    val reducedMotion = LocalReducedMotion.current
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (!reducedMotion && isPressed) pressedScale else 1f,
        animationSpec = if (reducedMotion) snap() else tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "press_scale"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Staggered list item reveal: GPU transform + opacity only, 50ms delay per index.
 * Instantly visible when Reduced Motion is active.
 */
@Composable
fun StaggeredFadeItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val reducedMotion = LocalReducedMotion.current
    var visible by remember { mutableStateOf(reducedMotion) }
    val slideOffsetPx = with(LocalDensity.current) { 8.dp.toPx() }

    LaunchedEffect(reducedMotion) {
        if (!reducedMotion) {
            visible = true
        }
    }

    val delay = if (reducedMotion) 0 else (index.coerceIn(0, 8) * 50)
    val alpha by animateFloatAsState(
        targetValue = if (visible || reducedMotion) 1f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(
            durationMillis = 220,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "stagger_alpha"
    )
    val translateY by animateFloatAsState(
        targetValue = if (visible || reducedMotion) 0f else slideOffsetPx,
        animationSpec = if (reducedMotion) snap() else tween(
            durationMillis = 220,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "stagger_ty"
    )

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            this.translationY = translateY
        }
    ) {
        content()
    }
}

/**
 * Count-up animation for integer stats on first load; instant when Reduced Motion is active.
 */
@Composable
fun rememberAnimatedIntCount(target: Int): Int {
    val reducedMotion = LocalReducedMotion.current
    var started by remember { mutableStateOf(reducedMotion) }
    LaunchedEffect(Unit) {
        started = true
    }
    val value by animateIntAsState(
        targetValue = if (started || reducedMotion) target else 0,
        animationSpec = if (reducedMotion) snap() else tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "count_up_int"
    )
    return if (reducedMotion) target else value
}

/**
 * Count-up animation for Long values (e.g. watch-time minutes) on first load.
 */
@Composable
fun rememberAnimatedLongCount(target: Long): Long {
    val reducedMotion = LocalReducedMotion.current
    var started by remember { mutableStateOf(reducedMotion) }
    LaunchedEffect(Unit) {
        started = true
    }
    val progress by animateFloatAsState(
        targetValue = if (started || reducedMotion) 1f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "count_up_long"
    )
    return if (reducedMotion) target else (target.toDouble() * progress.toDouble()).roundToLong()
}

/**
 * Global themed backdrop
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
 * Global App Header (applied on every screen):
 * Left: "ZENIX" capsule brand chip (40dp height, 20dp horizontal padding, 20dp radius, 0.5dp border)
 * Right: Circular Search button + Circular Settings gear button (40dp circles, 8dp visual gap, >=44dp tap targets)
 */
@Composable
fun AfterglowTopBar(
    onSearchClick: () -> Unit,
    onSettingsMenuClick: () -> Unit,
    settingsMenuExpanded: Boolean,
    showUpdateBadge: Boolean,
    settingsButtonFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current

    val pillShape = RoundedCornerShape(20.dp)
    val capsuleBg = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> tokens.surface.copy(alpha = 0.86f)
        AppThemeMode.MINIMAL_DARK -> MinimalDarkSurface
        AppThemeMode.AMOLED_BLACK -> AmoledSurface
    }
    val subtleBorderColor = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color.White.copy(alpha = 0.18f)
        AppThemeMode.MINIMAL_DARK -> MinimalDarkOutline
        AppThemeMode.AMOLED_BLACK -> AmoledOutline
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = spacing.space4, vertical = spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Brand label capsule (non-interactive)
        Box(
            modifier = Modifier
                .height(40.dp)
                .clip(pillShape)
                .background(capsuleBg)
                .border(0.5.dp, subtleBorderColor, pillShape)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ZENIX",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                letterSpacing = 3.sp,
                color = tokens.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.zenixHeading()
            )
        }

        // Right: Search circle + Settings gear circle (40dp visual, 8dp visual gap, >=44dp tap area)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            HeaderCircleIconButton(
                onClick = onSearchClick,
                active = false,
                surfaceBg = capsuleBg,
                subtleBorder = subtleBorderColor,
                modifier = Modifier.testTag("topbar_search_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search library",
                    tint = tokens.textPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }

            HeaderCircleIconButton(
                onClick = onSettingsMenuClick,
                active = settingsMenuExpanded,
                showBadgeDot = showUpdateBadge,
                badgeColor = NeonPink,
                surfaceBg = capsuleBg,
                subtleBorder = subtleBorderColor,
                modifier = Modifier
                    .focusRequester(settingsButtonFocusRequester)
                    .semantics {
                        contentDescription = "Settings and menu"
                        stateDescription = if (settingsMenuExpanded) "Expanded" else "Collapsed"
                    }
                    .testTag("topbar_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                    tint = if (settingsMenuExpanded) tokens.primaryAccent else tokens.textPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
private fun HeaderCircleIconButton(
    onClick: () -> Unit,
    active: Boolean,
    surfaceBg: Color,
    subtleBorder: Color,
    modifier: Modifier = Modifier,
    showBadgeDot: Boolean = false,
    badgeColor: Color = NeonPink,
    content: @Composable BoxScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val interactionSource = remember { MutableInteractionSource() }
    val borderWidth = if (active) 1.dp else 0.5.dp
    val borderColor = if (active) tokens.primaryAccent else subtleBorder
    val bgColor = if (active) tokens.primaryAccent.copy(alpha = 0.16f) else surfaceBg

    Box(
        modifier = modifier
            .size(width = 44.dp, height = 48.dp)
            .pressScale(interactionSource, 0.96f)
            .zenixFocusRing(tokens.primaryAccent, 22.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 22.dp, color = tokens.primaryAccent),
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(borderWidth, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            content()
            if (showBadgeDot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp)
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                        .border(1.dp, tokens.background, CircleShape)
                )
            }
        }
    }
}

/**
 * Unified Neutral Icon Button:
 * 48dp minimum touch target wrapping a 40dp visual box with 12dp corner radius,
 * neutral border and background across all screens.
 */
@Composable
fun NeutralIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBadgeDot: Boolean = false,
    badgeColor: Color = LocalZenixTokens.current.primaryAccent,
    content: @Composable BoxScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val radii = LocalZenixRadii.current
    val shape = RoundedCornerShape(radii.sm)
    val interactionSource = remember { MutableInteractionSource() }

    val visualBoxMod = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Modifier
            .size(40.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), shape)

        AppThemeMode.MINIMAL_DARK -> Modifier
            .size(40.dp)
            .clip(shape)
            .background(MinimalDarkCard)
            .border(1.dp, MinimalDarkOutline, shape)

        AppThemeMode.AMOLED_BLACK -> Modifier
            .size(40.dp)
            .clip(shape)
            .background(AmoledCard)
            .border(1.dp, AmoledOutline, shape)
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .pressScale(interactionSource, 0.96f)
            .zenixFocusRing(tokens.primaryAccent, radii.sm)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 24.dp, color = tokens.textPrimary),
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = visualBoxMod,
            contentAlignment = Alignment.Center
        ) {
            content()
            if (showBadgeDot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                        .border(1.dp, tokens.background, CircleShape)
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
    val radii = LocalZenixRadii.current
    val shape = RoundedCornerShape(radii.sm)
    val interactionSource = remember { MutableInteractionSource() }
    val resolvedSize = if (size < 48.dp) 48.dp else size

    val baseMod = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> modifier
            .size(resolvedSize)
            .pressScale(interactionSource, 0.96f)
            .neonGlow(color = glowColor, cornerRadius = radii.sm, alpha = 0.24f, spread = 5.dp)
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
            .size(resolvedSize)
            .pressScale(interactionSource, 0.96f)
            .clip(shape)
            .background(MinimalDarkCard)
            .border(1.dp, MinimalDarkOutline, shape)

        AppThemeMode.AMOLED_BLACK -> modifier
            .size(resolvedSize)
            .pressScale(interactionSource, 0.96f)
            .clip(shape)
            .background(AmoledCard)
            .border(1.dp, AmoledOutline, shape)
    }

    Box(
        modifier = baseMod
            .zenixFocusRing(glowColor, radii.sm)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = tokens.textPrimary),
                role = Role.Button,
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
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val radii = LocalZenixRadii.current
    val shape = RoundedCornerShape(radii.lg)

    val cardMod = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> modifier
            .fillMaxWidth()
            .neonGlow(color = accentColor, cornerRadius = radii.lg, alpha = 0.34f, spread = 10.dp)
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
 * Primary Button (Purple/Theme-Accent filled with 0.96 press scale + keyboard focus ring)
 */
@Composable
fun NeonPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentColor: Color = LocalZenixTokens.current.primaryAccent,
    cornerRadius: Dp = LocalZenixRadii.current.md,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
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
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .then(if (enabled) Modifier.pressScale(interactionSource, 0.96f) else Modifier)
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
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .then(if (enabled) Modifier.pressScale(interactionSource, 0.96f) else Modifier)
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
            .zenixFocusRing(Color.White, cornerRadius)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = contentColor),
                role = Role.Button,
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
    cornerRadius: Dp = LocalZenixRadii.current.md,
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
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .then(if (enabled) Modifier.pressScale(interactionSource, 0.96f) else Modifier)
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
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .then(if (enabled) Modifier.pressScale(interactionSource, 0.96f) else Modifier)
                .clip(shape)
                .background(MinimalDarkCard.copy(alpha = alphaMult))
                .border(1.dp, borderCol, shape)
        }

        AppThemeMode.AMOLED_BLACK -> {
            val borderCol = accentBorderColor?.copy(alpha = 0.70f * alphaMult) ?: AmoledOutline.copy(alpha = alphaMult)
            modifier
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .then(if (enabled) Modifier.pressScale(interactionSource, 0.96f) else Modifier)
                .clip(shape)
                .background(AmoledCard.copy(alpha = alphaMult))
                .border(1.dp, borderCol, shape)
        }
    }

    val contentColor = (accentBorderColor ?: tokens.secondaryButtonText).copy(alpha = if (enabled) 1f else 0.75f)

    Box(
        modifier = buttonMod
            .zenixFocusRing(accentBorderColor ?: tokens.primaryAccent, cornerRadius)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = contentColor),
                role = Role.Button,
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
 * Rounded 24dp Glass Card with Soft Neon Glow Borders and 0.96 press scale when clickable
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NeonGlassCard(
    modifier: Modifier = Modifier,
    accentColor: Color = LocalZenixTokens.current.primaryAccent,
    cornerRadius: Dp = LocalZenixRadii.current.lg,
    glowAlpha: Float = 0.24f,
    @Suppress("UNUSED_PARAMETER") pastelFill: Color? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalZenixTokens.current
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val isInteractive = onClick != null || onLongClick != null

    val cardMod = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> modifier
            .then(if (isInteractive) Modifier.pressScale(interactionSource, 0.96f) else Modifier)
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
            .then(if (isInteractive) Modifier.pressScale(interactionSource, 0.96f) else Modifier)
            .clip(shape)
            .background(MinimalDarkCard)
            .border(1.dp, MinimalDarkOutline, shape)

        AppThemeMode.AMOLED_BLACK -> modifier
            .then(if (isInteractive) Modifier.pressScale(interactionSource, 0.96f) else Modifier)
            .clip(shape)
            .background(AmoledCard)
            .border(1.dp, AmoledOutline, shape)
    }

    Box(
        modifier = cardMod.then(
            if (isInteractive) {
                Modifier
                    .zenixFocusRing(accentColor, cornerRadius)
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = ripple(color = accentColor),
                        role = Role.Button,
                        onLongClick = onLongClick,
                        onClick = { onClick?.invoke() }
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
 * Chips and Pills (>= 44dp min height for comfortable touch target)
 */
@Composable
fun NeonGlassChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalZenixTokens.current.primaryAccent,
    leadingIcon: ImageVector? = null,
    neutralUnselected: Boolean = false
) {
    val tokens = LocalZenixTokens.current
    val reducedMotion = LocalReducedMotion.current
    val shape = RoundedCornerShape(tokens.chipRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val colorAnimSpec = if (reducedMotion) snap<Color>() else tween<Color>(200)
    val floatAnimSpec = if (reducedMotion) snap<Float>() else tween<Float>(220)

    val glowAlpha by animateFloatAsState(
        targetValue = if (tokens.useGlow) {
            if (selected) 0.36f else if (neutralUnselected) 0f else 0.14f
        } else 0f,
        animationSpec = floatAnimSpec,
        label = "chip_glow"
    )
    val targetTextColor = if (selected) {
        if (tokens.mode == AppThemeMode.NEON_GLOSSY && (accentColor == NeonCyan || accentColor == NeonGreen || accentColor == NeonAmber)) {
            Color(0xFF0A0812)
        } else {
            Color.White
        }
    } else {
        if (neutralUnselected) tokens.textSecondary else tokens.textPrimary
    }
    val textColor by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = colorAnimSpec,
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
            } else if (neutralUnselected) {
                Modifier
                    .clip(shape)
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.16f), shape)
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
            .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
            .pressScale(interactionSource, 0.96f)
            .then(chipSurfaceMod)
            .zenixFocusRing(accentColor, 24.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accentColor),
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
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
 * Accessible Toggle Switch with smooth sliding knob and Role.Switch semantics
 */
@Composable
fun ZenixToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalZenixTokens.current.primaryAccent,
    contentDescriptionLabel: String = "Toggle option"
) {
    val tokens = LocalZenixTokens.current
    val reducedMotion = LocalReducedMotion.current
    val interactionSource = remember { MutableInteractionSource() }

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 4.dp,
        animationSpec = if (reducedMotion) snap() else tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "switch_thumb_offset"
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) accentColor else tokens.outlineSubtle,
        animationSpec = if (reducedMotion) snap() else tween(durationMillis = 200),
        label = "switch_track"
    )

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 52.dp, minHeight = 48.dp)
            .zenixFocusRing(accentColor, 24.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 24.dp, color = accentColor),
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) }
            )
            .semantics {
                stateDescription = if (checked) "$contentDescriptionLabel On" else "$contentDescriptionLabel Off"
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 50.dp, height = 28.dp)
                .then(
                    if (checked && tokens.useGlow) {
                        Modifier.neonGlow(accentColor, cornerRadius = 14.dp, alpha = 0.34f, spread = 4.dp)
                    } else Modifier
                )
                .clip(CircleShape)
                .background(trackColor)
                .border(1.dp, if (checked) Color.White.copy(alpha = 0.35f) else tokens.outline, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = thumbOffset)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White)
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
    val radii = LocalZenixRadii.current
    val reducedMotion = LocalReducedMotion.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val corner = radii.md
    val shape = RoundedCornerShape(corner)

    val focusedColor = if (tokens.mode == AppThemeMode.MINIMAL_DARK) tokens.primaryAccent else focusNeonColor
    val unfocusedBorder = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color.White.copy(alpha = 0.22f)
        AppThemeMode.MINIMAL_DARK -> MinimalDarkOutline
        AppThemeMode.AMOLED_BLACK -> AmoledOutline
    }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) focusedColor else unfocusedBorder,
        animationSpec = if (reducedMotion) snap() else tween(180),
        label = "textfield_border"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (tokens.useGlow && isFocused) 0.34f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(200),
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
                        .border(if (isFocused) 2.dp else 1.dp, borderColor, shape)

                    AppThemeMode.MINIMAL_DARK -> Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = if (minLines > 1) 76.dp else 48.dp)
                        .clip(shape)
                        .background(MinimalDarkSurface)
                        .border(if (isFocused) 2.dp else 1.dp, borderColor, shape)

                    AppThemeMode.AMOLED_BLACK -> Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = if (minLines > 1) 76.dp else 48.dp)
                        .clip(shape)
                        .background(AmoledSurface)
                        .border(if (isFocused) 2.dp else 1.dp, borderColor, shape)
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
                                color = tokens.textSecondary.copy(alpha = 0.82f),
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
    val radii = LocalZenixRadii.current
    val spacing = LocalZenixSpacing.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val shape = RoundedCornerShape(radii.lg)
        val dialogMod = when (tokens.mode) {
            AppThemeMode.NEON_GLOSSY -> Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space4)
                .neonGlow(color = accentColor, cornerRadius = radii.lg, alpha = 0.34f, spread = 10.dp)
                .clip(shape)
                .background(Color(0xFF100A21).copy(alpha = 0.95f))
                .border(1.dp, accentColor.copy(alpha = 0.72f), shape)

            AppThemeMode.MINIMAL_DARK -> Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space4)
                .clip(shape)
                .background(MinimalDarkSurface)
                .border(1.dp, MinimalDarkOutline, shape)

            AppThemeMode.AMOLED_BLACK -> Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space4)
                .clip(shape)
                .background(AmoledSurface)
                .border(1.dp, AmoledOutline, shape)
        }

        Box(modifier = dialogMod) {
            Column(
                modifier = Modifier.padding(spacing.space5),
                verticalArrangement = Arrangement.spacedBy(spacing.space3)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = tokens.textPrimary,
                    modifier = Modifier.zenixHeading()
                )
                content()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (dismissText != null) {
                        GlassSecondaryButton(
                            onClick = onDismissRequest,
                            cornerRadius = radii.sm,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(dismissText, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    if (secondaryText != null && onSecondary != null) {
                        GlassSecondaryButton(
                            onClick = onSecondary,
                            accentBorderColor = tokens.tertiaryAccent,
                            cornerRadius = radii.sm,
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
                                cornerRadius = radii.sm,
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(confirmText, style = MaterialTheme.typography.labelLarge, color = NeonPink, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            NeonPrimaryButton(
                                onClick = onConfirm,
                                enabled = confirmEnabled,
                                cornerRadius = radii.sm,
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
    val spacing = LocalZenixSpacing.current
    val reducedMotion = LocalReducedMotion.current
    val pillShape = RoundedCornerShape(36.dp)
    val hasActiveTab = selectedIndex in tabs.indices
    val safeIndex = if (hasActiveTab) selectedIndex else 0
    val pillAlpha by animateFloatAsState(
        targetValue = if (hasActiveTab) 1f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(220),
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
            .background(
                Brush.verticalGradient(
                    0.0f to Color.Transparent,
                    0.32f to tokens.background.copy(alpha = 0.72f),
                    0.75f to tokens.background.copy(alpha = 0.94f),
                    1.0f to tokens.background
                )
            )
            .navigationBarsPadding()
            .padding(start = spacing.space4, end = spacing.space4, top = spacing.space3, bottom = spacing.space2)
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
                    animationSpec = if (reducedMotion) snap() else spring(
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
                            animationSpec = if (reducedMotion) snap() else tween(200),
                            label = "tab_tint_$index"
                        )
                        val interactionSource = remember { MutableInteractionSource() }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(28.dp))
                                .zenixFocusRing(activeStart, 28.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = ripple(color = OffWhiteText),
                                    role = Role.Tab,
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
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = tint,
                                maxLines = 1,
                                softWrap = false,
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
 * Themed Progress Bar: animates from 0 to target over 600ms ease-out on first load
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
    val reducedMotion = LocalReducedMotion.current
    val clamped = progress.coerceIn(0f, 1f)
    var hasLoaded by remember { mutableStateOf(reducedMotion) }

    LaunchedEffect(Unit) {
        hasLoaded = true
    }

    val targetFraction = if (hasLoaded || reducedMotion) clamped else 0f
    val animated by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = if (reducedMotion) snap() else tween(durationMillis = 600, easing = FastOutSlowInEasing),
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
    val stars = (ratingOutOfTen.coerceIn(0.0, 10.0) / 2.0).toFloat()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val fillFraction = (stars - (i - 1)).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (fillFraction > 0.05f && tokens.useGlow) {
                            Modifier.neonGlow(
                                tokens.ratingGold,
                                cornerRadius = starSize / 2,
                                alpha = 0.24f * fillFraction,
                                spread = 3.dp
                            )
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = tokens.textSecondary.copy(alpha = 0.55f),
                    modifier = Modifier.fillMaxSize()
                )
                if (fillFraction > 0f) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = tokens.ratingGold,
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                clipRect(right = size.width * fillFraction) {
                                    this@drawWithContent.drawContent()
                                }
                            }
                    )
                }
            }
        }
    }
}

/**
 * Consistent Watchlist-style Empty State across all screens
 */
@Composable
fun ZenixEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color = NeonPurple,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.space4, vertical = spacing.space5),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.space3)
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .then(
                        if (tokens.useGlow) {
                            Modifier.neonGlow(
                                color = accentColor,
                                cornerRadius = 42.dp,
                                alpha = 0.45f,
                                spread = 14.dp
                            )
                        } else Modifier
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.28f),
                                NeonCyan.copy(alpha = 0.18f)
                            )
                        )
                    )
                    .border(1.2.dp, accentColor.copy(alpha = 0.65f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(spacing.space1))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = tokens.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.zenixHeading()
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary,
                textAlign = TextAlign.Center
            )

            if (primaryActionLabel != null && onPrimaryAction != null) {
                Spacer(modifier = Modifier.height(spacing.space1))
                NeonPrimaryButton(
                    onClick = onPrimaryAction,
                    accentColor = accentColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(primaryActionLabel, fontWeight = FontWeight.Bold)
                }
            }
            if (secondaryActionLabel != null && onSecondaryAction != null) {
                GlassSecondaryButton(
                    onClick = onSecondaryAction,
                    accentBorderColor = NeonCyan,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(secondaryActionLabel, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/**
 * Shimmer Skeleton Loader for Cards & Lists
 */
@Composable
fun ZenixHomeSkeleton(modifier: Modifier = Modifier) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val reducedMotion = LocalReducedMotion.current

    val shimmerAlpha = if (reducedMotion) {
        0.12f
    } else {
        val transition = rememberInfiniteTransition(label = "skeleton_shimmer")
        val a by transition.animateFloat(
            initialValue = 0.08f,
            targetValue = 0.20f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shimmer_alpha"
        )
        a
    }

    val shimmerColor = tokens.textPrimary.copy(alpha = shimmerAlpha)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.space4, vertical = spacing.space2),
        verticalArrangement = Arrangement.spacedBy(spacing.space4)
    ) {
        // Greeting skeleton
        Column(verticalArrangement = Arrangement.spacedBy(spacing.space2)) {
            Box(
                modifier = Modifier
                    .size(width = 110.dp, height = 14.dp)
                    .clip(RoundedCornerShape(radii.sm))
                    .background(shimmerColor)
            )
            Box(
                modifier = Modifier
                    .size(width = 190.dp, height = 28.dp)
                    .clip(RoundedCornerShape(radii.sm))
                    .background(shimmerColor)
            )
        }

        // Top pick card skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
                .clip(RoundedCornerShape(radii.lg))
                .background(tokens.cardBackground)
                .border(1.dp, tokens.outlineSubtle, RoundedCornerShape(radii.lg))
                .padding(spacing.space4)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.space3)) {
                Box(
                    modifier = Modifier
                        .size(width = 84.dp, height = 116.dp)
                        .clip(RoundedCornerShape(radii.md))
                        .background(shimmerColor)
                )
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space2), modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(80.dp, 12.dp).clip(RoundedCornerShape(6.dp)).background(shimmerColor))
                    Box(modifier = Modifier.fillMaxWidth(0.85f).height(20.dp).clip(RoundedCornerShape(6.dp)).background(shimmerColor))
                    Box(modifier = Modifier.fillMaxWidth(0.6f).height(14.dp).clip(RoundedCornerShape(6.dp)).background(shimmerColor))
                    Spacer(modifier = Modifier.height(spacing.space1))
                    Box(modifier = Modifier.size(120.dp, 40.dp).clip(RoundedCornerShape(radii.md)).background(shimmerColor))
                }
            }
        }

        // Watch time card skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .clip(RoundedCornerShape(radii.lg))
                .background(tokens.cardBackground)
                .border(1.dp, tokens.outlineSubtle, RoundedCornerShape(radii.lg))
                .padding(spacing.space4)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                Box(modifier = Modifier.size(120.dp, 12.dp).clip(RoundedCornerShape(6.dp)).background(shimmerColor))
                Box(modifier = Modifier.fillMaxWidth(0.7f).height(22.dp).clip(RoundedCornerShape(6.dp)).background(shimmerColor))
                Box(modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(shimmerColor))
            }
        }

        // 4 stats row skeleton
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp)
                        .clip(RoundedCornerShape(radii.md))
                        .background(tokens.cardBackground)
                        .border(1.dp, tokens.outlineSubtle, RoundedCornerShape(radii.md))
                )
            }
        }

        // List rows skeleton
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(74.dp)
                    .clip(RoundedCornerShape(radii.lg))
                    .background(tokens.cardBackground)
                    .border(1.dp, tokens.outlineSubtle, RoundedCornerShape(radii.lg))
            )
        }
    }
}

/**
 * Error state with "Something went wrong" + "Try again" button
 */
@Composable
fun ZenixErrorState(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ZenixEmptyState(
        icon = Icons.Rounded.ErrorOutline,
        title = "Something went wrong",
        subtitle = message?.takeIf { it.isNotBlank() }
            ?: "We couldn't load your ZENIX library right now. Check your connection and try again.",
        accentColor = NeonPink,
        primaryActionLabel = "Try again",
        onPrimaryAction = onRetry,
        modifier = modifier
    )
}

/**
 * Offline connectivity observer + compact banner
 */
@Composable
fun rememberIsOnline(): Boolean {
    val context = LocalContext.current
    val cm = remember(context) {
        runCatching {
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        }.getOrNull()
    }

    fun checkCurrent(): Boolean {
        if (cm == null) return true
        return runCatching {
            val net = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(net) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }.getOrDefault(true)
    }

    var online by remember { mutableStateOf(checkCurrent()) }

    DisposableEffect(cm) {
        if (cm == null) return@DisposableEffect onDispose {}
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                online = true
            }

            override fun onLost(network: Network) {
                online = checkCurrent()
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                online = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            }
        }
        runCatching { cm.registerDefaultNetworkCallback(callback) }
        onDispose { runCatching { cm.unregisterNetworkCallback(callback) } }
    }
    return online
}

@Composable
fun ZenixOfflineBanner(modifier: Modifier = Modifier) {
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val tokens = LocalZenixTokens.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.space4, vertical = spacing.space1)
            .clip(RoundedCornerShape(radii.sm))
            .background(NeonAmber.copy(alpha = 0.16f))
            .border(1.dp, NeonAmber.copy(alpha = 0.65f), RoundedCornerShape(radii.sm))
            .padding(horizontal = spacing.space3, vertical = spacing.space2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.space2)
    ) {
        Icon(
            imageVector = Icons.Rounded.CloudOff,
            contentDescription = "Offline status",
            tint = NeonAmber,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "You're offline — cached library active; edits sync when reconnected",
            style = MaterialTheme.typography.labelMedium,
            color = tokens.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryBottomSheet(
    preselectedCategory: MediaCategory?,
    onDismiss: () -> Unit,
    onSelectCategory: (MediaCategory) -> Unit
) {
    val tokens = LocalZenixTokens.current
    val spacing = LocalZenixSpacing.current
    val radii = LocalZenixRadii.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember(preselectedCategory) { mutableStateOf(preselectedCategory) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = tokens.dialogBackground,
        contentColor = tokens.textPrimary,
        shape = RoundedCornerShape(topStart = radii.lg, topEnd = radii.lg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = spacing.space4, vertical = spacing.space2),
            verticalArrangement = Arrangement.spacedBy(spacing.space3)
        ) {
            Text(
                text = "Add to ZENIX",
                style = MaterialTheme.typography.headlineMedium,
                color = tokens.textPrimary,
                modifier = Modifier.zenixHeading()
            )
            Text(
                text = if (preselectedCategory != null) {
                    "Preselected ${preselectedCategory.label}. Tap another category or continue below."
                } else {
                    "Choose what kind of title you want to log."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary
            )

            Column(verticalArrangement = Arrangement.spacedBy(spacing.space2)) {
                MediaCategory.entries.forEach { category ->
                    val accent = themedCategoryColor(category)
                    val isSelected = selectedCategory == category
                    val icon = when (category) {
                        MediaCategory.MOVIE -> Icons.Rounded.Movie
                        MediaCategory.WEB_SERIES -> Icons.Rounded.Tv
                        MediaCategory.ANIME -> Icons.Rounded.AutoAwesome
                        MediaCategory.K_DRAMA -> Icons.Rounded.Favorite
                    }
                    val subtitle = when (category) {
                        MediaCategory.MOVIE -> "Feature films, documentaries & animated movies"
                        MediaCategory.WEB_SERIES -> "Multi-season shows with episode progress"
                        MediaCategory.ANIME -> "Seasonal anime series & cours"
                        MediaCategory.K_DRAMA -> "Korean dramas & limited miniseries"
                    }
                    val shape = RoundedCornerShape(radii.md)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 56.dp)
                            .clip(shape)
                            .background(
                                if (isSelected) accent.copy(alpha = 0.18f)
                                else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) accent else tokens.outlineSubtle,
                                shape = shape
                            )
                            .zenixFocusRing(accent, radii.md)
                            .clickable(role = Role.Button) {
                                if (preselectedCategory == null || selectedCategory == category) {
                                    onSelectCategory(category)
                                } else {
                                    selectedCategory = category
                                }
                            }
                            .padding(horizontal = spacing.space3, vertical = spacing.space3)
                            .testTag("add_sheet_category_${category.name.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.space3)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(radii.sm))
                                .background(accent.copy(alpha = 0.18f))
                                .border(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(radii.sm)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = category.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = tokens.textPrimary
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Selected",
                                tint = accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            val chosen = selectedCategory
            if (chosen != null) {
                NeonPrimaryButton(
                    onClick = { onSelectCategory(chosen) },
                    accentColor = themedCategoryColor(chosen),
                    cornerRadius = radii.md,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_sheet_continue_button")
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(spacing.space2))
                    Text("Continue as ${chosen.label}", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(spacing.space2))
        }
    }
}
