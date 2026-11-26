package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmoledCard
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalZenixTokens
import com.example.ui.theme.MinimalDarkCard
import com.example.ui.theme.NeonPurple

data class ChartSlice(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun DonutChartCard(
    title: String,
    centerLabel: String,
    centerSubLabel: String,
    slices: List<ChartSlice>,
    accentColor: Color = NeonPurple,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val total = slices.sumOf { it.value.toDouble() }.toFloat()
    val sweepScale by animateFloatAsState(
        targetValue = if (total > 0f) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "donut_sweep"
    )
    val trackColor = if (tokens.isLight) Color(0xFFECE6DB) else Color.White.copy(alpha = 0.08f)

    NeonGlassCard(
        modifier = modifier.fillMaxWidth(),
        accentColor = accentColor,
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = tokens.textPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (total <= 0f) {
            Text(
                text = "Add titles to visualize distribution.",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(140.dp)) {
                        val strokeWidth = 20.dp.toPx()
                        val inset = strokeWidth / 2f
                        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                        drawArc(
                            color = trackColor,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = Offset(inset, inset),
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                        var startAngle = -90f
                        slices.filter { it.value > 0f }.forEach { slice ->
                            val sweep = (slice.value / total) * 360f * sweepScale
                            val activeSweep = (sweep - 3f).coerceAtLeast(1f)
                            if (tokens.useGlow) {
                                drawArc(
                                    color = slice.color.copy(alpha = 0.30f),
                                    startAngle = startAngle,
                                    sweepAngle = activeSweep,
                                    useCenter = false,
                                    topLeft = Offset(inset, inset),
                                    size = arcSize,
                                    style = Stroke(width = strokeWidth + 8.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            drawArc(
                                color = slice.color,
                                startAngle = startAngle,
                                sweepAngle = activeSweep,
                                useCenter = false,
                                topLeft = Offset(inset, inset),
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += sweep
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = centerLabel,
                            style = MaterialTheme.typography.headlineMedium,
                            color = tokens.textPrimary
                        )
                        Text(
                            text = centerSubLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.textSecondary
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    slices.filter { it.value > 0f }.forEach { slice ->
                        val pct = ((slice.value / total) * 100).toInt()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .then(
                                            if (tokens.useGlow) Modifier.neonGlow(slice.color, cornerRadius = 5.dp, alpha = 0.45f, spread = 3.dp)
                                            else Modifier
                                        )
                                        .clip(CircleShape)
                                        .background(slice.color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = slice.label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "${slice.value.toInt()} ($pct%)",
                                style = MaterialTheme.typography.labelMedium,
                                color = slice.color
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalBarChartCard(
    title: String,
    items: List<Pair<String, Int>>,
    barColor: Color,
    emptyMessage: String = "No records yet.",
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val maxVal = (items.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
    val trackBg = when (tokens.mode) {
        AppThemeMode.NEON_GLOSSY -> Color.White.copy(alpha = 0.08f)
        AppThemeMode.MINIMAL_DARK -> MinimalDarkCard
        AppThemeMode.AMOLED_BLACK -> AmoledCard
    }

    NeonGlassCard(
        modifier = modifier.fillMaxWidth(),
        accentColor = barColor,
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = tokens.textPrimary
        )
        Spacer(modifier = Modifier.height(14.dp))
        if (items.isEmpty()) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                items.forEachIndexed { idx, (label, count) ->
                    val ratio by animateFloatAsState(
                        targetValue = (count.toFloat() / maxVal.toFloat()).coerceIn(0.06f, 1f),
                        animationSpec = tween(600),
                        label = "bar_$label"
                    )
                    val endColor = themedPaletteColor(idx + 1)
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = endColor
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(9.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(trackBg)
                                .then(
                                    if (tokens.mode == AppThemeMode.NEON_GLOSSY) {
                                        Modifier.border(0.5.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(5.dp))
                                    } else Modifier
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(ratio)
                                    .height(9.dp)
                                    .then(
                                        if (tokens.useGlow) Modifier.neonGlow(endColor, cornerRadius = 5.dp, alpha = 0.36f, spread = 4.dp)
                                        else Modifier
                                    )
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        if (tokens.mode == AppThemeMode.MINIMAL_DARK) Brush.horizontalGradient(listOf(barColor, barColor))
                                        else Brush.horizontalGradient(colors = listOf(barColor, endColor))
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LineActivityChartCard(
    title: String,
    points: List<Pair<String, Int>>,
    lineColor: Color,
    emptyMessage: String = "Log titles with dates to unlock trend lines.",
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val gridColor = if (tokens.isLight) Color(0xFFDDD6C9) else Color.White.copy(alpha = 0.12f)
    val pointCenterColor = if (tokens.isLight) Color.White else Color.White

    NeonGlassCard(
        modifier = modifier.fillMaxWidth(),
        accentColor = lineColor,
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = tokens.textPrimary
        )
        Spacer(modifier = Modifier.height(14.dp))
        if (points.isEmpty()) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary
            )
        } else {
            val maxVal = (points.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                val w = size.width
                val h = size.height
                for (i in 0..3) {
                    val y = h * (i / 3f)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val stepX = if (points.size <= 1) 0f else w / (points.size - 1)
                val coords = points.mapIndexed { idx, (_, v) ->
                    val x = if (points.size == 1) w / 2f else idx * stepX
                    val y = h - (v.toFloat() / maxVal.toFloat()) * (h - 16.dp.toPx()) - 8.dp.toPx()
                    Offset(x, y)
                }

                if (coords.size > 1) {
                    val fillPath = Path().apply {
                        moveTo(coords.first().x, h)
                        coords.forEach { lineTo(it.x, it.y) }
                        lineTo(coords.last().x, h)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(lineColor.copy(alpha = if (tokens.isLight) 0.22f else 0.42f), Color.Transparent)
                        )
                    )
                    for (i in 0 until coords.size - 1) {
                        if (tokens.useGlow) {
                            drawLine(
                                color = lineColor.copy(alpha = 0.35f),
                                start = coords[i],
                                end = coords[i + 1],
                                strokeWidth = 8.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                        drawLine(
                            brush = Brush.horizontalGradient(listOf(lineColor, tokens.secondaryAccent)),
                            start = coords[i],
                            end = coords[i + 1],
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }

                coords.forEach { pt ->
                    if (tokens.useGlow) {
                        drawCircle(color = lineColor.copy(alpha = 0.35f), radius = 9.dp.toPx(), center = pt)
                    }
                    drawCircle(color = lineColor, radius = 5.dp.toPx(), center = pt)
                    drawCircle(color = pointCenterColor, radius = 2.2.dp.toPx(), center = pt)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                points.forEach { (label, value) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = lineColor
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalRingCard(
    label: String,
    current: Int,
    target: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val tokens = LocalZenixTokens.current
    val progress = if (target <= 0) 0f else (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val animatedSweep by animateFloatAsState(
        targetValue = progress * 360f,
        animationSpec = tween(700),
        label = "goal_$label"
    )
    val trackColor = if (tokens.isLight) Color(0xFFECE6DB) else Color.White.copy(alpha = 0.10f)

    NeonGlassCard(
        modifier = modifier,
        accentColor = accentColor,
        cornerRadius = 24.dp,
        contentPadding = PaddingValues(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(74.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(74.dp)) {
                    val stroke = 8.dp.toPx()
                    val inset = stroke / 2f
                    val arcSize = Size(size.width - stroke, size.height - stroke)
                    drawArc(
                        color = trackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = stroke)
                    )
                    if (tokens.useGlow) {
                        drawArc(
                            color = accentColor.copy(alpha = 0.30f),
                            startAngle = -90f,
                            sweepAngle = animatedSweep,
                            useCenter = false,
                            topLeft = Offset(inset, inset),
                            size = arcSize,
                            style = Stroke(width = stroke + 5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    drawArc(
                        brush = Brush.sweepGradient(listOf(accentColor, tokens.secondaryAccent, accentColor)),
                        startAngle = -90f,
                        sweepAngle = animatedSweep,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = tokens.textPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = tokens.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$current / $target",
                style = MaterialTheme.typography.labelSmall,
                color = tokens.textSecondary
            )
        }
    }
}
