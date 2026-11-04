package com.kj.steady.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kj.steady.logic.DateUtils
import com.kj.steady.ui.components.ClinicalNoticeCard
import com.kj.steady.ui.theme.ForestGreen
import com.kj.steady.ui.theme.RestrainedLime
import com.kj.steady.ui.viewmodel.MainViewModel

@Composable
fun HydrationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val hydrationStatus by viewModel.currentHydrationStatus.collectAsState()
    val hydrationLogs by viewModel.currentHydrationLogs.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()

    var selectedBeverage by remember { mutableStateOf("WATER") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hydration",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Daily dilution pacing for kidney stone prevention",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            IconButton(onClick = { viewModel.undoHydration() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Undo drink", tint = ForestGreen)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Meter Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TODAY'S INTAKE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "%,d".format(hydrationStatus.totalAmountMl),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1.5).sp,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "/ %,d mL".format(userPrefs.dailyHydrationGoalMl),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { (hydrationStatus.progressPercent / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ForestGreen,
                    trackColor = ForestGreen.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${hydrationStatus.progressPercent.toInt()}% of daily hydration target",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Kidney Stone & Timing Guidance Notice
        hydrationStatus.kidneyStonePacingNotice?.let { notice ->
            ClinicalNoticeCard(notice = notice)
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Quick One-Tap Logging Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "ONE-TAP LOGGING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedBeverage == "WATER",
                        onClick = { selectedBeverage = "WATER" },
                        label = { Text("Plain Water") }
                    )
                    FilterChip(
                        selected = selectedBeverage == "TEA_COFFEE",
                        onClick = { selectedBeverage = "TEA_COFFEE" },
                        label = { Text("Americano / Tea") }
                    )
                    FilterChip(
                        selected = selectedBeverage == "ELECTROLYTES",
                        onClick = { selectedBeverage = "ELECTROLYTES" },
                        label = { Text("Electrolytes") }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                val vesselButtons = when (selectedBeverage) {
                    "TEA_COFFEE" -> listOf(
                        Triple(80, "Americano Cup", "80 mL"),
                        Triple(120, "Medium Cup", "120 mL"),
                        Triple(160, "Double Shot / 2x", "160 mL"),
                        Triple(200, "Office Mug", "200 mL")
                    )
                    "ELECTROLYTES" -> listOf(
                        Triple(250, "Small Glass", "250 mL"),
                        Triple(350, "Tall Glass", "350 mL"),
                        Triple(500, "Sipper Bottle", "500 mL"),
                        Triple(750, "Sports Flask", "750 mL")
                    )
                    else -> listOf(
                        Triple(250, "Glass", "250 mL"),
                        Triple(350, "Mug", "350 mL"),
                        Triple(500, "Bottle", "500 mL"),
                        Triple(750, "Gym Sipper", "750 mL")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.addHydration(vesselButtons[0].first, selectedBeverage, "${vesselButtons[0].second} ${vesselButtons[0].third}") },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(62.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+${vesselButtons[0].third}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(vesselButtons[0].second, fontSize = 10.sp, color = RestrainedLime)
                        }
                    }

                    Button(
                        onClick = { viewModel.addHydration(vesselButtons[1].first, selectedBeverage, "${vesselButtons[1].second} ${vesselButtons[1].third}") },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(62.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+${vesselButtons[1].third}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(vesselButtons[1].second, fontSize = 10.sp, color = RestrainedLime)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.addHydration(vesselButtons[2].first, selectedBeverage, "${vesselButtons[2].second} ${vesselButtons[2].third}") },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(62.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+${vesselButtons[2].third}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(vesselButtons[2].second, fontSize = 10.sp, color = RestrainedLime)
                        }
                    }

                    Button(
                        onClick = { viewModel.addHydration(vesselButtons[3].first, selectedBeverage, "${vesselButtons[3].second} ${vesselButtons[3].third}") },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(62.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+${vesselButtons[3].third}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(vesselButtons[3].second, fontSize = 10.sp, color = RestrainedLime)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Clinical Context Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Kidney Stone Clinical Protocol",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Target: Maintain urine volume ≥ 2.5 L/day via 2.8–3.2 L total fluids.\n" +
                            "• Pace: Sip 250–350 mL evenly every 90 minutes during waking hours.\n" +
                            "• Sleep Hygiene: Avoid large volume catch-up drinking after 8:30 PM.\n" +
                            "• Dietary Calcium: Curd or milk at meals binds dietary oxalates in the gut.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Today's Drink Timeline
        Text(
            text = "Today's Drink Log (${hydrationLogs.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (hydrationLogs.isEmpty()) {
            Text(
                text = "No drinks logged yet today.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            hydrationLogs.forEach { log ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${log.amountMl} mL • ${log.vesselLabel}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${DateUtils.formatTime(log.timestampMillis)} • ${log.beverageType}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
