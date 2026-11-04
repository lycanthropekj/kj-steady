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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kj.steady.logic.DateUtils
import com.kj.steady.ui.theme.*
import com.kj.steady.ui.viewmodel.MainViewModel

@Composable
fun RoutineScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userPrefs by viewModel.userPreferences.collectAsState()
    val notificationLogs by viewModel.notificationLogs.collectAsState()

    var notifyWeighIn by remember(userPrefs) { mutableStateOf(userPrefs.notifyWeighIn) }
    var notifyHydration by remember(userPrefs) { mutableStateOf(userPrefs.notifyHydration) }
    var notifyDinnerPrep by remember(userPrefs) { mutableStateOf(userPrefs.notifyDinnerPrep) }
    var notifyEveningLog by remember(userPrefs) { mutableStateOf(userPrefs.notifyEveningLog) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Routine & Schedule",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Intentional reminders that respect your day and protect your sleep.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Schedule Timeline Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "YOUR DAILY TIMELINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                RoutineTimeRow("5:45 AM", "Wake-up & Morning Weigh-in", "Fasting scale reading")
                RoutineTimeRow("6:30 AM", "Morning Walk / Intervals", "Targeting 8,000 morning steps")
                RoutineTimeRow("8:15 AM", "Breakfast & Protein Shake", "Muesli + Carbamide Forte (~38g protein)")
                RoutineTimeRow("9:30 AM – 6:30 PM", "Office Hours", "Hydration pauses; lunch skipped")
                RoutineTimeRow("7:15 PM", "Dinner Strategy", "15m Egg Bhurji or Paneer to reduce ordering")
                RoutineTimeRow("9:00 PM", "Evening Reflection", "BP, dinner, cigarettes, sleep rating")
                RoutineTimeRow("10:15 PM", "Wind-down & Bedtime", "Quiet hours activated")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quiet Hours Notice
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SleepVioletLight),
            border = BorderStroke(1.dp, SleepVioletBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Quiet Hours: 10:30 PM – 5:45 AM",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SleepVioletDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Strict non-disruption guarantee. No reminders or alerts will sound during your sleep window, even if a schedule shift occurred.",
                    fontSize = 12.sp,
                    color = Slate600
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Home Strength & Muscle Preservation Card (Customized for KJ)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HOME STRENGTH & MUSCLE PRESERVATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StepsOrangeLight,
                        border = BorderStroke(1.dp, StepsOrangeBorder)
                    ) {
                        Text(
                            text = "2–3x / WEEK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StepsOrangeDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tailored for your equipment: Yoga Mat, 10kg Resistance Band, 2 x 3kg Dumbbells. Protects lean muscle during your calorie deficit without needing a commercial gym:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                RoutineTimeRow("3 sets × 12–15", "Mat Box Squats", "Bodyweight to chair; joint-friendly quad/glute work")
                RoutineTimeRow("3 sets × 12–15", "10kg Band Standing Rows", "Upper back, lats & posture; loops under feet")
                RoutineTimeRow("3 sets × 8–12", "Incline Push-ups", "Hands on table/bed or knees on mat; chest & triceps")
                RoutineTimeRow("3 sets × 12", "10kg Band Romanian Deadlifts", "Hips back, slight knee bend; hamstrings & glutes")
                RoutineTimeRow("3 sets × 15", "3kg Dumbbell Lateral & Overhead", "Controlled deltoid raises & overhead shoulder presses")
                RoutineTimeRow("3 sets × 15", "Mat Glute Bridges", "Lower back protection & hip strength")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Notification Toggles Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "REMINDER CATEGORIES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                NotificationToggleRow("Morning Weigh-in Prompt", notifyWeighIn) {
                    notifyWeighIn = it
                    viewModel.savePreferences(userPrefs.copy(notifyWeighIn = it))
                }
                NotificationToggleRow("Daytime Hydration Check-in", notifyHydration) {
                    notifyHydration = it
                    viewModel.savePreferences(userPrefs.copy(notifyHydration = it))
                }
                NotificationToggleRow("Dinner Preparation Prompt", notifyDinnerPrep) {
                    notifyDinnerPrep = it
                    viewModel.savePreferences(userPrefs.copy(notifyDinnerPrep = it))
                }
                NotificationToggleRow("Evening Reflection Prompt", notifyEveningLog) {
                    notifyEveningLog = it
                    viewModel.savePreferences(userPrefs.copy(notifyEveningLog = it))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Samsung Galaxy S24 Ultra Battery Guide Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "SAMSUNG ONE UI BATTERY RESILIENCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "On Samsung Galaxy S24 Ultra, One UI may place apps into 'Deep Sleep' after 3 days of non-interaction. If reminders are ever delayed:\n\n" +
                            "1. Go to Galaxy Settings → Apps → KJ Steady.\n" +
                            "2. Tap Battery → Select 'Unrestricted'.\n" +
                            "3. Settings → Battery → Background usage limits → ensure KJ Steady is not in 'Deep sleeping apps'.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Recent Notification History
        Text(
            text = "Recent Reminder History (${notificationLogs.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (notificationLogs.isEmpty()) {
            Text(
                text = "No notification events recorded yet.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 6.dp)
            )
        } else {
            notificationLogs.take(10).forEach { log ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(log.title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("${DateUtils.formatTime(log.timestampMillis)} • Action: ${log.actionTaken}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RoutineTimeRow(time: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Text(time, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
    }
    HorizontalDivider(color = Slate200)
}

@Composable
private fun NotificationToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
