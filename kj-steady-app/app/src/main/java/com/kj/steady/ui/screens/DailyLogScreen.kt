package com.kj.steady.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kj.steady.data.model.DailyEntry
import com.kj.steady.logic.BloodPressureEvaluator
import com.kj.steady.logic.DateUtils
import com.kj.steady.ui.theme.ForestGreen
import com.kj.steady.ui.theme.RestrainedLime
import com.kj.steady.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLogScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentEntry by viewModel.currentEntry.collectAsState()
    val bpReadings by viewModel.currentBpReadings.collectAsState()

    // Form states
    var weightText by remember(currentEntry) { mutableStateOf(currentEntry?.morningWeight?.toString() ?: "") }
    var stepsText by remember(currentEntry) { mutableStateOf(currentEntry?.totalSteps?.toString() ?: "") }
    var sleepText by remember(currentEntry) { mutableStateOf(currentEntry?.sleepHours?.toString() ?: "") }
    var cigarettesCount by remember(currentEntry) { mutableStateOf(currentEntry?.cigarettes ?: 0) }
    var hadAlcohol by remember(currentEntry) { mutableStateOf(currentEntry?.hadAlcohol ?: false) }
    var breakfastText by remember(currentEntry) { mutableStateOf(currentEntry?.breakfastText ?: "") }
    var snacksText by remember(currentEntry) { mutableStateOf(currentEntry?.lunchSnacksText ?: "") }
    var dinnerText by remember(currentEntry) { mutableStateOf(currentEntry?.dinnerText ?: "") }
    var notesText by remember(currentEntry) { mutableStateOf(currentEntry?.notes ?: "") }

    // BP Dialog State
    var showBpDialog by remember { mutableStateOf(false) }
    var bpSysText by remember { mutableStateOf("") }
    var bpDiaText by remember { mutableStateOf("") }
    var bpPulseText by remember { mutableStateOf("") }
    var bpArm by remember { mutableStateOf("Left") }
    var bpPosture by remember { mutableStateOf("Sitting") }

    // Confirm Delete Dialog
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val today = DateUtils.today()
    val yesterday = DateUtils.addDays(today, -1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Daily Log",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Blank fields mean unrecorded. You can edit any day later.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Date Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedDate == today,
                onClick = { viewModel.setSelectedDate(today) },
                label = { Text("Today") }
            )
            FilterChip(
                selected = selectedDate == yesterday,
                onClick = { viewModel.setSelectedDate(yesterday) },
                label = { Text("Yesterday") }
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp),
                onClick = { /* Could trigger DatePicker */ }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = DateUtils.formatDisplay(selectedDate),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 1: Core Physical Metrics
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "BODY & ACTIVITY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Morning Weight (kg)") },
                        placeholder = { Text("e.g. 103.5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = stepsText,
                        onValueChange = { stepsText = it },
                        label = { Text("Total Steps") },
                        placeholder = { Text("e.g. 11500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = sleepText,
                        onValueChange = { sleepText = it },
                        label = { Text("Sleep Duration (hours)") },
                        placeholder = { Text("e.g. 6.5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = { viewModel.syncHealthConnect() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = ForestGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(54.dp)
                    ) {
                        Text("Sync Health Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 2: Multiple Blood Pressure Readings
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BLOOD PRESSURE (mmHg)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Logged for clinician review; not an automated diagnosis",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }

                    IconButton(onClick = { showBpDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add BP Reading", tint = ForestGreen)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (bpReadings.isEmpty()) {
                    Text(
                        text = "No readings logged yet. When you visit a clinic/pharmacy or check at home, rest for 5m and tap (+) to log your seated reading.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                } else {
                    bpReadings.forEach { bp ->
                        val category = BloodPressureEvaluator.evaluate(bp.systolic, bp.diastolic)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${bp.systolic} / ${bp.diastolic} mmHg ${bp.pulse?.let { "• $it bpm" } ?: ""}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${DateUtils.formatTime(bp.timestampMillis)} • ${bp.arm} arm • ${bp.posture} • ${category.label}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            IconButton(onClick = { viewModel.deleteBpReading(bp) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 3: Meals & Fast Presets
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "MEALS & NUTRITION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = breakfastText,
                    onValueChange = { breakfastText = it },
                    label = { Text("Breakfast (amounts / protein)") },
                    placeholder = { Text("Muesli 50g, milk 200ml, Carbamide Forte 1 scoop") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        TextButton(onClick = { breakfastText = "Muesli (50g) + Cow Milk (200ml) + Carbamide Forte Shake (1 scoop, ~38g protein)" }) {
                            Text("Preset", fontSize = 11.sp)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = snacksText,
                    onValueChange = { snacksText = it },
                    label = { Text("Lunch / Snacks / Drinks") },
                    placeholder = { Text("Max Protein bar, 3 Americanos; lunch skipped") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        TextButton(onClick = { snacksText = "Max Protein Strawberry Bar (~20g protein) + 3 Plain Americanos (80 mL each, ~240 mL total)" }) {
                            Text("Preset", fontSize = 11.sp)
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {
                            val current = snacksText.trim()
                            snacksText = if (current.isEmpty()) "1 plain Americano (80 mL)"
                            else "$current, 1 plain Americano (80 mL)"
                            viewModel.addHydration(80, "TEA_COFFEE", "Americano Cup 80 mL")
                        },
                        label = { Text("+1 Americano (80 mL)", fontSize = 11.sp) }
                    )
                    AssistChip(
                        onClick = {
                            val current = snacksText.trim()
                            snacksText = if (current.isEmpty()) "Max Protein Strawberry Bar"
                            else "$current, Max Protein Strawberry Bar"
                        },
                        label = { Text("+Max Protein Bar", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dinnerText,
                    onValueChange = { dinnerText = it },
                    label = { Text("Dinner (simple veg / eggs)") },
                    placeholder = { Text("Egg Bhurji (3 eggs), 2 rotis, 100g curd") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        TextButton(onClick = { dinnerText = "Egg Bhurji (3 eggs) + 2 Phulkas + 100g Fresh Curd (~40g protein)" }) {
                            Text("Preset", fontSize = 11.sp)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 4: Tobacco, Alcohol & Notes
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "HABITS & RECOVERY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Cigarettes today", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("Non-judgmental count", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalButton(
                            onClick = { if (cigarettesCount > 0) cigarettesCount-- },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) { Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                        Text(
                            text = "$cigarettesCount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        FilledTonalButton(
                            onClick = { cigarettesCount++ },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) { Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hadAlcohol,
                        onCheckedChange = { hadAlcohol = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Had alcohol on this day", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Notes / exercise / energy / symptoms") },
                    placeholder = { Text("Morning walk 8k, energy level, nasal obstruction...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Save Button
        Button(
            onClick = {
                val weight = weightText.toDoubleOrNull()
                val steps = stepsText.toIntOrNull()
                val sleep = sleepText.toDoubleOrNull()

                val entry = DailyEntry(
                    date = selectedDate,
                    morningWeight = weight,
                    totalSteps = steps,
                    sleepHours = sleep,
                    cigarettes = cigarettesCount,
                    hadAlcohol = hadAlcohol,
                    breakfastText = breakfastText.trim().ifEmpty { null },
                    lunchSnacksText = snacksText.trim().ifEmpty { null },
                    dinnerText = dinnerText.trim().ifEmpty { null },
                    notes = notesText.trim().ifEmpty { null },
                    isVerifiedMorningWeight = weight != null
                )
                viewModel.saveDailyEntry(entry)
            },
            colors = ButtonDefaults.buttonColors(containerColor = RestrainedLime, contentColor = ForestGreen),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Save Daily Entry", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (currentEntry != null) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = { showDeleteConfirm = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete this day's entry")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Add BP Dialog
    if (showBpDialog) {
        AlertDialog(
            onDismissRequest = { showBpDialog = false },
            title = { Text("Log Blood Pressure") },
            text = {
                Column {
                    Text("Sit calmly for 5 min before measuring.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bpSysText,
                            onValueChange = { bpSysText = it },
                            label = { Text("Systolic") },
                            placeholder = { Text("120") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = bpDiaText,
                            onValueChange = { bpDiaText = it },
                            label = { Text("Diastolic") },
                            placeholder = { Text("80") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bpPulseText,
                        onValueChange = { bpPulseText = it },
                        label = { Text("Pulse (optional bpm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sys = bpSysText.toIntOrNull()
                        val dia = bpDiaText.toIntOrNull()
                        val pulse = bpPulseText.toIntOrNull()
                        if (sys != null && dia != null) {
                            viewModel.addBloodPressureReading(sys, dia, pulse, bpArm, bpPosture, null)
                            showBpDialog = false
                            bpSysText = ""
                            bpDiaText = ""
                            bpPulseText = ""
                        }
                    }
                ) { Text("Save Reading") }
            },
            dismissButton = {
                TextButton(onClick = { showBpDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Confirm Delete Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Entry") },
            text = { Text("Delete the entry for ${DateUtils.formatDisplay(selectedDate)}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDailyEntry(selectedDate)
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
