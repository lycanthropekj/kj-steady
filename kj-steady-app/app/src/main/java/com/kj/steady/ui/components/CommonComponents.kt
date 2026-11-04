package com.kj.steady.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kj.steady.logic.DateUtils
import com.kj.steady.logic.WeightTrendSummary
import com.kj.steady.ui.theme.*

@Composable
fun HeroWeightCard(
    summary: WeightTrendSummary,
    goalWeight: Double = 85.0,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ForestGreen)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = "LATEST RECORDED WEIGHT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = RestrainedLime.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = if (summary.latestWeight != null) "%.1f".format(summary.latestWeight) else "—",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-2).sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "kg",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Text(
                text = if (summary.latestWeightDate != null)
                    "Recorded ${DateUtils.formatDisplay(summary.latestWeightDate)}"
                else
                    "Your first morning weigh-in starts the trend.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((summary.progressPercent / 100.0).toFloat().coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(RestrainedLime)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "CHANGE FROM BASELINE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.65f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = summary.changeFromBaseline?.let {
                            (if (it > 0) "+" else "") + "%.1f kg".format(it)
                        } ?: "—",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "CHOSEN GOAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.65f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "%.0f kg".format(goalWeight),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = RestrainedLime
                    )
                }
            }
        }
    }
}

@Composable
fun StatMetricCard(
    label: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ClinicalNoticeCard(
    notice: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AmberWarning)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "ⓘ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AmberText,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = notice,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = AmberText
            )
        }
    }
}
