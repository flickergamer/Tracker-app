package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Surface
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
    modifier: Modifier = Modifier
) {
    val total = slices.sumOf { it.value.toDouble() }.toFloat()
    val sweepScale by animateFloatAsState(
        targetValue = if (total > 0f) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "donut_sweep"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (total <= 0f) {
                Text(
                    text = "Add titles to visualize distribution.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box(
                        modifier = Modifier.size(136.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val trackColor = MaterialTheme.colorScheme.surfaceVariant
                        Canvas(modifier = Modifier.size(136.dp)) {
                            val strokeWidth = 22.dp.toPx()
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
                                drawArc(
                                    color = slice.color,
                                    startAngle = startAngle,
                                    sweepAngle = (sweep - 2f).coerceAtLeast(1f),
                                    useCenter = false,
                                    topLeft = Offset(inset, inset),
                                    size = arcSize,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                )
                                startAngle += sweep
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = centerLabel,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = centerSubLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                            .clip(CircleShape)
                                            .background(slice.color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = slice.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "${slice.value.toInt()} ($pct%)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
    val maxVal = (items.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))
            if (items.isEmpty()) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items.forEach { (label, count) ->
                        val ratio by animateFloatAsState(
                            targetValue = (count.toFloat() / maxVal.toFloat()).coerceIn(0.05f, 1f),
                            animationSpec = tween(600),
                            label = "bar_$label"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(ratio)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(barColor, MaterialTheme.colorScheme.secondary)
                                            )
                                        )
                                )
                            }
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
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))
            if (points.isEmpty()) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val maxVal = (points.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
                val gridColor = MaterialTheme.colorScheme.outline
                val secondaryColor = MaterialTheme.colorScheme.secondary

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    // Subtle horizontal grid lines
                    for (i in 0..3) {
                        val y = h * (i / 3f)
                        drawLine(
                            color = gridColor.copy(alpha = 0.45f),
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
                                colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent)
                            )
                        )
                        for (i in 0 until coords.size - 1) {
                            drawLine(
                                color = lineColor,
                                start = coords[i],
                                end = coords[i + 1],
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    coords.forEach { pt ->
                        drawCircle(color = lineColor, radius = 5.dp.toPx(), center = pt)
                        drawCircle(color = secondaryColor, radius = 2.5.dp.toPx(), center = pt)
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
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
    val progress = if (target <= 0) 0f else (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val animatedSweep by animateFloatAsState(
        targetValue = progress * 360f,
        animationSpec = tween(700),
        label = "goal_$label"
    )
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
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
                    drawArc(
                        color = accentColor,
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
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$current / $target",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
