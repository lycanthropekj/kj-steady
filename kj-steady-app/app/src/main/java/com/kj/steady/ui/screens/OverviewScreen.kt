package com.kj.steady.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kj.steady.logic.DateUtils
import com.kj.steady.ui.components.*
import com.kj.steady.ui.theme.*
import com.kj.steady.ui.viewmodel.MainViewModel

@Composable
fun OverviewScreen(
    viewModel: MainViewModel,
    onNavigateToLog: (String) -> Unit,
    onNavigateToHealth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val trendSummary by viewModel.trendSummary.collectAsState()
    val allEntries by viewModel.allEntries.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()
    val today = DateUtils.today()
    val todayEntry = allEntries.find { it.date == today }
    val hydrationStatus by viewModel.currentHydrationStatus.collectAsState()
    val bpReadings by viewModel.currentBpReadings.collectAsState()

    val availableUpdate by viewModel.availableUpdate.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkForAppUpdate(silent = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        // App Update Banner if available
        availableUpdate?.let { update ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandBlueLight),
                border = BorderStroke(1.dp, BrandBlueBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Update v${update.versionName} Available!",
                            fontWeight = FontWeight.Bold,
                            color = BrandBlueDark,
                            fontSize = 14.sp
                        )
                        Text(
                            text = update.releaseNotes,
                            fontSize = 11.sp,
                            color = BrandBlueDark.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                        if (downloadProgress != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { downloadProgress ?: 0f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                color = BrandBlue,
                                trackColor = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { viewModel.downloadAndInstallUpdate(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (downloadProgress != null) "Downloading..." else "Update",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "OVERVIEW",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "One day at a time.",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = DateUtils.formatDisplay(today),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            }

            Surface(
                shape = MaterialTheme.shapes.small,
                color = BrandBlueLight,
                border = BorderStroke(1.dp, BrandBlueBorder)
            ) {
                Text(
                    text = "OFFLINE · PRIVATE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Latest Weight Card
        HeroWeightCard(
            summary = trendSummary,
            goalWeight = userPrefs.goalWeight
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 7-Day Average & Days to Deadline Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricCard(
                label = "7-Day Average",
                value = trendSummary.sevenDayAverage?.let { "%.1f kg".format(it) } ?: "—",
                subtitle = if (trendSummary.sevenDayMeasurementCount > 0)
                    "${trendSummary.sevenDayMeasurementCount} weigh-in${if (trendSummary.sevenDayMeasurementCount == 1) "" else "s"} this week"
                else "No recent weigh-ins",
                modifier = Modifier.weight(1f)
            )

            StatMetricCard(
                label = "Days to Goal",
                value = "${trendSummary.daysRemainingToDeadline}",
                subtitle = DateUtils.formatShort(userPrefs.deadlineDate),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Weight Trend Chart
        WeightTrendChart(
            entries = allEntries,
            goalWeight = userPrefs.goalWeight
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Clinical Safe Pacing Notice
        ClinicalNoticeCard(
            notice = "Personal Ambition: ${userPrefs.goalWeight.toInt()} kg by ${DateUtils.formatDisplay(userPrefs.deadlineDate)}. ${trendSummary.safePacingAdvice}"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Today's Activity Snapshot
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Slate900
            )
            TextButton(onClick = onNavigateToHealth) {
                Text("Health Sync ⚙", fontSize = 12.sp, color = BrandBlue, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Steps Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StepsOrangeLight,
                border = BorderStroke(1.dp, StepsOrangeBorder),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🏃 STEPS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StepsOrange,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = todayEntry?.totalSteps?.let { "%,d".format(it) } ?: "—",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StepsOrangeDark
                    )
                    Text(
                        text = "/ %,d".format(userPrefs.dailyStepGoal),
                        fontSize = 11.sp,
                        color = StepsOrange.copy(alpha = 0.75f)
                    )
                }
            }

            // Sleep Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SleepVioletLight,
                border = BorderStroke(1.dp, SleepVioletBorder),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🌙 SLEEP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleepViolet,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = todayEntry?.sleepHours?.let { "%.1f h".format(it) } ?: "—",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleepVioletDark
                    )
                    Text(
                        text = "Goal: ${userPrefs.dailySleepGoalHours} h",
                        fontSize = 11.sp,
                        color = SleepViolet.copy(alpha = 0.75f)
                    )
                }
            }

            // Fluids Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = WaterBlueLight,
                border = BorderStroke(1.dp, WaterBlueBorder),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "💧 FLUIDS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WaterBlue,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${hydrationStatus.totalAmountMl} mL",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WaterBlueDark
                    )
                    Text(
                        text = "/ ${userPrefs.dailyHydrationGoalMl} mL",
                        fontSize = 11.sp,
                        color = WaterBlue.copy(alpha = 0.75f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Log Today Button
        Button(
            onClick = { onNavigateToLog(today) },
            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Log Today →",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
