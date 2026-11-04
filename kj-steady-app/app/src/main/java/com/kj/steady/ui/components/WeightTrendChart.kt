package com.kj.steady.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kj.steady.data.model.DailyEntry
import com.kj.steady.logic.DateUtils
import com.kj.steady.ui.theme.*
import kotlin.math.max
import kotlin.math.min

enum class ChartRange(val label: String, val days: Int) {
    SEVEN_DAYS("7D", 7),
    THIRTY_DAYS("30D", 30),
    NINETY_DAYS("90D", 90),
    ALL("All", 365)
}

@Composable
fun WeightTrendChart(
    entries: List<DailyEntry>,
    goalWeight: Double = 85.0,
    modifier: Modifier = Modifier
) {
    var selectedRange by remember { mutableStateOf(ChartRange.THIRTY_DAYS) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    val today = DateUtils.today()
    val filteredEntries = remember(entries, selectedRange) {
        val valid = entries.filter { it.morningWeight != null && it.date <= today }.sortedBy { it.date }
        if (selectedRange == ChartRange.ALL) {
            valid
        } else {
            valid.filter {
                val diff = DateUtils.daysBetween(it.date, today)
                diff in 0 until selectedRange.days
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weight Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate900
                )

                // Range Selector Pills
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ChartRange.values().forEach { range ->
                        val isSelected = range == selectedRange
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) BrandBlue else Slate100,
                            modifier = Modifier.height(28.dp),
                            onClick = {
                                selectedRange = range
                                selectedPointIndex = null
                            }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = range.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White else Slate600
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your trend begins with your first morning weigh-in.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate500
                    )
                }
            } else {
                val weights = filteredEntries.mapNotNull { it.morningWeight }
                val minW = (weights.minOrNull() ?: 80.0) - 1.0
                val maxW = (weights.maxOrNull() ?: 105.0) + 1.0
                val rangeW = max(1.0, maxW - minW)

                // Selected point inspection display
                selectedPointIndex?.let { idx ->
                    if (idx in filteredEntries.indices) {
                        val point = filteredEntries[idx]
                        Text(
                            text = "${DateUtils.formatDisplay(point.date)}: ${point.morningWeight} kg",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandBlue,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .pointerInput(filteredEntries) {
                            detectTapGestures { offset ->
                                val width = size.width
                                val step = width / (filteredEntries.size.coerceAtLeast(2) - 1).toFloat()
                                val tappedIdx = (offset.x / step).toInt().coerceIn(0, filteredEntries.size - 1)
                                selectedPointIndex = tappedIdx
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val bottomPadding = 24.dp.toPx()
                    val plotHeight = height - bottomPadding

                    // Draw min and max reference lines
                    val lineStroke = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)))
                    drawLine(
                        color = Slate300.copy(alpha = 0.5f),
                        start = Offset(0f, 10f),
                        end = Offset(width, 10f),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Slate300.copy(alpha = 0.5f),
                        start = Offset(0f, plotHeight),
                        end = Offset(width, plotHeight),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw Goal Line (85 kg) if in range
                    if (goalWeight in minW..maxW) {
                        val goalY = plotHeight - ((goalWeight - minW) / rangeW * plotHeight).toFloat()
                        drawLine(
                            color = ProteinAmber,
                            start = Offset(0f, goalY),
                            end = Offset(width, goalY),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = lineStroke.pathEffect
                        )
                    }

                    // Calculate point offsets
                    val points = filteredEntries.mapIndexed { index, entry ->
                        val x = if (filteredEntries.size == 1) width / 2f
                        else index * (width / (filteredEntries.size - 1))
                        val weight = entry.morningWeight ?: minW
                        val y = plotHeight - ((weight - minW) / rangeW * plotHeight).toFloat()
                        Offset(x, y)
                    }

                    // Draw Trend Line
                    if (points.size > 1) {
                        val path = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }
                        drawPath(path = path, color = BrandBlue, style = Stroke(width = 3.dp.toPx()))
                    }

                    // Draw point dots
                    points.forEachIndexed { i, pt ->
                        val isSelected = i == selectedPointIndex
                        drawCircle(
                            color = if (isSelected) HeroCyan else BrandBlue,
                            radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                            center = pt
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = DateUtils.formatShort(filteredEntries.first().date),
                        fontSize = 11.sp,
                        color = Slate500
                    )
                    Text(
                        text = "Goal: ${goalWeight.toInt()} kg (dashed)",
                        fontSize = 11.sp,
                        color = ProteinAmber,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = DateUtils.formatShort(filteredEntries.last().date),
                        fontSize = 11.sp,
                        color = Slate500
                    )
                }
            }
        }
    }
}
