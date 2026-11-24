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
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.ui.theme.GoldRating
import com.example.ui.theme.MutedGreyText
import com.example.ui.theme.NeonButtonDarkText
import com.example.ui.theme.NeonButtonDeep
import com.example.ui.theme.NeonButtonLight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonFuchsia
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OffWhiteText
import com.example.ui.theme.RadialGlowCyan
import com.example.ui.theme.RadialGlowMagenta
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.VoidBackground
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
 * Global #07050F backdrop with soft radial glows:
 * - Magenta rgba(217,70,239,.45) at top-right
 * - Cyan rgba(34,211,238,.28) at left-middle
 */
@Composable
fun AfterglowBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxDim = max(size.width, size.height).coerceAtLeast(1f)
            // Top-right Magenta glow
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

            // Left-middle Cyan glow
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

            // Subtle bottom purple aura behind floating bar
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
        content()
    }
}

/**
 * Component #1: Screen Header
 * Menu icon, small gradient logo (#A855F7 to #EC4899 with glow), "AFTERGLOW" title with letter spacing, glass "+" button.
 */
@Composable
fun AfterglowTopBar(
    onOpenMenu: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                glowColor = NeonPurple,
                modifier = Modifier.testTag("open_drawer_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Open navigation menu",
                    tint = OffWhiteText,
                    modifier = Modifier.size(21.dp)
                )
            }

            // Small gradient logo (#A855F7 -> #EC4899) with glow
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .neonGlow(color = NeonPink, cornerRadius = 10.dp, alpha = 0.45f, spread = 6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NeonPurple, NeonPink)
                        )
                    )
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

            Text(
                text = "AFTERGLOW",
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 2.6.sp,
                color = OffWhiteText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        GlassIconButton(
            onClick = onAddClick,
            glowColor = NeonFuchsia,
            modifier = Modifier.testTag("topbar_add_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add title",
                tint = OffWhiteText,
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
    val shape = RoundedCornerShape(15.dp)
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
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
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = OffWhiteText),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * Component #2: Hero / Top Card
 * 28dp rounded, translucent purple->pink->cyan gradient fill, 1dp white border at 35%,
 * outer purple glow, and glossy highlight (white 22% to transparent) on top half.
 */
@Composable
fun LiquidGlassHeroCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier = modifier
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
                // Glossy top-half specular highlight (white 22% to transparent)
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            content = content
        )
    }
}

/**
 * Component #3a: Primary Button
 * Gradient #F0ABFC to #C084FC, dark text, fuchsia glow, white inner top highlight.
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
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val alphaMult = if (enabled) 1f else 0.45f

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(
                if (enabled) Modifier.pressScale(interactionSource, 0.97f)
                else Modifier
            )
            .neonGlow(
                color = NeonFuchsia,
                cornerRadius = cornerRadius,
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
                // White inner top highlight
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.55f * alphaMult),
                        0.45f to Color.Transparent
                    ),
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.65f * alphaMult),
                    start = Offset(cornerRadius.toPx() * 0.6f, 1.dp.toPx()),
                    end = Offset(size.width - cornerRadius.toPx() * 0.6f, 1.dp.toPx()),
                    strokeWidth = 1.2.dp.toPx()
                )
            }
            .border(1.dp, Color.White.copy(alpha = 0.35f * alphaMult), shape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = NeonButtonDarkText),
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides NeonButtonDarkText.copy(alpha = if (enabled) 1f else 0.7f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content
            )
        }
    }
}

/**
 * Component #3b: Secondary Button
 * Glass fill (white 12%) with a thin white border.
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
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val alphaMult = if (enabled) 1f else 0.45f
    val borderCol = accentBorderColor?.copy(alpha = 0.60f * alphaMult) ?: Color.White.copy(alpha = 0.28f * alphaMult)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(
                if (enabled) Modifier.pressScale(interactionSource, 0.97f)
                else Modifier
            )
            .then(
                if (accentBorderColor != null && enabled) {
                    Modifier.neonGlow(accentBorderColor, cornerRadius, alpha = 0.22f, spread = 6.dp)
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
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = OffWhiteText),
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides OffWhiteText.copy(alpha = if (enabled) 1f else 0.6f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content
            )
        }
    }
}

/**
 * Component #4: Cards (metric cards, title cards, list items)
 * 22dp rounded, subtle white gradient fill (white 14% to 3%), 1dp neon border, soft glow of same color.
 */
@Composable
fun NeonGlassCard(
    modifier: Modifier = Modifier,
    accentColor: Color = NeonPurple,
    cornerRadius: Dp = 22.dp,
    glowAlpha: Float = 0.25f,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .then(
                if (onClick != null) Modifier.pressScale(interactionSource, 0.985f)
                else Modifier
            )
            .neonGlow(color = accentColor, cornerRadius = cornerRadius, alpha = glowAlpha, spread = 8.dp)
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
                // Subtle glossy top specular rim
                drawLine(
                    color = Color.White.copy(alpha = 0.22f),
                    start = Offset(cornerRadius.toPx() * 0.7f, 1.dp.toPx()),
                    end = Offset(size.width - cornerRadius.toPx() * 0.7f, 1.dp.toPx()),
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
            .then(
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
 * Component #5: Chips and filters
 * Glass fill, 1dp neon border with glow; selected chip is filled with neon gradient.
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
    val shape = RoundedCornerShape(50)
    val interactionSource = remember { MutableInteractionSource() }
    val glowAlpha by animateFloatAsState(
        targetValue = if (selected) 0.38f else 0.16f,
        animationSpec = tween(220),
        label = "chip_glow"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Color.White else OffWhiteText,
        animationSpec = tween(200),
        label = "chip_text"
    )

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp)
            .pressScale(interactionSource, 0.96f)
            .neonGlow(color = accentColor, cornerRadius = 20.dp, alpha = glowAlpha, spread = 6.dp)
            .clip(shape)
            .then(
                if (selected) {
                    Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(NeonFuchsia, NeonPurple)
                            )
                        )
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
            )
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
                    tint = if (selected) Color.White else accentColor,
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
 * Component #6a: Text fields
 * Dark glass fill, neon border when focused, glossy top edge.
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
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(16.dp)

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) focusNeonColor else Color.White.copy(alpha = 0.22f),
        animationSpec = tween(180),
        label = "textfield_border"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.34f else 0f,
        animationSpec = tween(200),
        label = "textfield_glow"
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isFocused) focusNeonColor else MutedGreyText,
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
            cursorBrush = SolidColor(focusNeonColor),
            textStyle = TextStyle(
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = OffWhiteText
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = if (minLines > 1) 76.dp else 48.dp)
                        .neonGlow(color = focusNeonColor, cornerRadius = 16.dp, alpha = glowAlpha, spread = 6.dp)
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
                            // Glossy top edge
                            drawLine(
                                color = Color.White.copy(alpha = 0.26f),
                                start = Offset(14.dp.toPx(), 1.dp.toPx()),
                                end = Offset(size.width - 14.dp.toPx(), 1.dp.toPx()),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .border(1.dp, borderColor, shape)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = if (minLines > 1) Alignment.Top else Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (leadingIcon != null) {
                        CompositionLocalProvider(LocalContentColor provides if (isFocused) focusNeonColor else MutedGreyText) {
                            leadingIcon()
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty() && !placeholder.isNullOrBlank()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedGreyText.copy(alpha = 0.65f),
                                maxLines = if (singleLine) 1 else 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }
                    if (trailingIcon != null) {
                        CompositionLocalProvider(LocalContentColor provides MutedGreyText) {
                            trailingIcon()
                        }
                    }
                }
            }
        )
    }
}

/**
 * Component #6b: Dark Glass Dialog with neon border, glow, and glossy top edge.
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
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val shape = RoundedCornerShape(28.dp)
        Box(
            modifier = Modifier
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
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = OffWhiteText
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
                            accentBorderColor = NeonCyan,
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
fun rememberSafeHazeState(): HazeState? {
    val enabled = rememberSafeBlurEnabled()
    return remember(enabled) {
        if (!enabled) null
        else runCatching { HazeState() }.getOrNull()
    }
}

fun Modifier.safeHazeSource(hazeState: HazeState?): Modifier {
    if (hazeState == null) return this
    return runCatching { this.hazeSource(state = hazeState) }.getOrDefault(this)
}

/**
 * Component #7: Floating Liquid Glass Bottom Bar
 * - Inset 14dp from edges, 72dp high, fully rounded.
 * - Real backdrop blur via Haze on hardware-accelerated Android 12+, with safe frosted fallback.
 * - Frosted gradient fill (white 16% -> 5%), 1dp white border at 28%, purple glow, inner top highlight.
 * - Selected tab sits in an animated glossy gradient pill (fuchsia to purple) with glow.
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
            // Dedicated leaf blur/frosted backdrop layer (never placed directly on SubcomposeLayout/BoxWithConstraints)
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
                        // Inner top glossy highlight
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

            // Animated Glossy Selected Pill
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
                        targetValue = if (isSelected) Color.White else MutedGreyText,
                        animationSpec = tween(220),
                        label = "tab_tint_$index"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(28.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onSelectTab(index) }
                            )
                            .testTag("bottom_nav_${tab.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = tint,
                                modifier = Modifier.size(21.dp)
                            )
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall,
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
}

/**
 * Component #8: Neon Glowing Progress Bar & Rating Stars
 */
@Composable
fun NeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    startColor: Color = NeonPurple,
    endColor: Color = NeonCyan
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 550),
        label = "neon_progress"
    )
    val shape = RoundedCornerShape(50)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.10f))
            .border(0.5.dp, Color.White.copy(alpha = 0.18f), shape)
    ) {
        if (animatedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceAtLeast(0.03f))
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
            )
        }
    }
}

@Composable
fun NeonRatingStars(
    ratingOutOfTen: Double,
    modifier: Modifier = Modifier,
    starSize: Dp = 15.dp
) {
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
                tint = if (filled) GoldRating else MutedGreyText.copy(alpha = 0.45f),
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (filled) Modifier.neonGlow(GoldRating, cornerRadius = starSize / 2, alpha = 0.26f, spread = 3.dp)
                        else Modifier
                    )
            )
        }
    }
}
