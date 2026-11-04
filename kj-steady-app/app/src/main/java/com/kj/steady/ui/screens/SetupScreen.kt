package com.kj.steady.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kj.steady.data.model.UserPreferences
import com.kj.steady.ui.theme.ForestGreen
import com.kj.steady.ui.theme.RestrainedLime
import com.kj.steady.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun SetupScreen(
    viewModel: MainViewModel,
    onNavigateToHealth: () -> Unit = {},
    onNavigateToAdaptive: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userPrefs by viewModel.userPreferences.collectAsState()

    var startWeightText by remember(userPrefs) { mutableStateOf(userPrefs.startWeight.toString()) }
    var goalWeightText by remember(userPrefs) { mutableStateOf(userPrefs.goalWeight.toString()) }
    var proteinText by remember(userPrefs) { mutableStateOf(userPrefs.dailyProteinTargetGrams.toString()) }
    var hydrationText by remember(userPrefs) { mutableStateOf(userPrefs.dailyHydrationGoalMl.toString()) }
    var calorieText by remember(userPrefs) { mutableStateOf(userPrefs.dailyCalorieTarget.toString()) }

    var isAiEnabled by remember(userPrefs) { mutableStateOf(userPrefs.isAiEnabled) }

    // Import/Export dialogs
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    var showExportResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Setup & Parameters",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Personal goals, clinical targets, and complete data portability.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile & Goals Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "BODY & GOAL PARAMETERS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = startWeightText,
                        onValueChange = { startWeightText = it },
                        label = { Text("Starting Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = goalWeightText,
                        onValueChange = { goalWeightText = it },
                        label = { Text("Chosen Goal (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = proteinText,
                        onValueChange = { proteinText = it },
                        label = { Text("Protein (g/day)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = hydrationText,
                        onValueChange = { hydrationText = it },
                        label = { Text("Hydration (mL/day)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = calorieText,
                    onValueChange = { calorieText = it },
                    label = { Text("Daily Calorie Target (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val updated = userPrefs.copy(
                            startWeight = startWeightText.toDoubleOrNull() ?: userPrefs.startWeight,
                            goalWeight = goalWeightText.toDoubleOrNull() ?: userPrefs.goalWeight,
                            dailyProteinTargetGrams = proteinText.toDoubleOrNull() ?: userPrefs.dailyProteinTargetGrams,
                            dailyHydrationGoalMl = hydrationText.toIntOrNull() ?: userPrefs.dailyHydrationGoalMl,
                            dailyCalorieTarget = calorieText.toIntOrNull() ?: userPrefs.dailyCalorieTarget
                        )
                        viewModel.savePreferences(updated)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Parameters", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Personal Profile Context Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "PERSONALIZED ONBOARDING CONTEXT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("• Equipment: ${userPrefs.homeEquipment}", fontSize = 12.sp, lineHeight = 17.sp)
                Text("• Dietary Focus: ${userPrefs.dietaryFocus}", fontSize = 12.sp, lineHeight = 17.sp)
                Text("• Medications: ${userPrefs.medicationsNote}", fontSize = 12.sp, lineHeight = 17.sp)
                Text("• Blood Pressure: ${userPrefs.bpStatusNote}", fontSize = 12.sp, lineHeight = 17.sp)
                Text("• Kidney Health: ${userPrefs.kidneyStoneStatusNote}", fontSize = 12.sp, lineHeight = 17.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Data Backup & Portability Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "BACKUP & DATA PORTABILITY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "All entries reside exclusively in private SQLite database storage on this device. Backups contain health information; store them privately.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val json = viewModel.exportBackupJson()
                                showExportResult = json
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Export JSON", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val csv = viewModel.exportCsv()
                                showExportResult = csv
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Export CSV", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showImportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import Backup (Supports Prototype v1 & v2)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Optional AI Configuration Card
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OPTIONAL ASSISTANT (AI)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Feature-flagged and disabled by default.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    Switch(
                        checked = isAiEnabled,
                        onCheckedChange = {
                            isAiEnabled = it
                            viewModel.savePreferences(userPrefs.copy(isAiEnabled = it))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Core Offline Guarantee: Every tracking, routine, and calculation feature works 100% offline without AI.\n" +
                            "• Bounded Context: If enabled, only anonymous meal text or weekly aggregated metrics leave the device.\n" +
                            "• Spending Cap: Default hard cap of $1.00/month (typical Gemini Flash cost is < $0.02/month).",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Health Connect & Integrations Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "HEALTH CONNECT & SENSOR SYNC",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Connect Samsung Health, Fitbit Air (Google Health), and Google Fit. Configure preferred steps sources and sleep de-overlap rules.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onNavigateToHealth,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Configure Health Connect Sync →", fontWeight = FontWeight.SemiBold, color = ForestGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // In-App Self-Updater Card
        val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()
        val availableUpdate by viewModel.availableUpdate.collectAsState()
        var updateUrlText by remember(userPrefs) { mutableStateOf(userPrefs.updateCheckUrl) }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "APP UPDATES & RELEASES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Current Installed Version: v2.0.5 (Build 7)\n" +
                            "Direct 1-tap updates without Google Play delays, developer fees, or public review.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.checkForAppUpdate(silent = false) },
                    enabled = !isCheckingUpdate,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(if (isCheckingUpdate) "Checking..." else "Check for Updates Now", fontWeight = FontWeight.Bold)
                }

                availableUpdate?.let { update ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = RestrainedLime.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("New Version Available: v${update.versionName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreen)
                            Text(update.releaseNotes, fontSize = 11.sp, color = ForestGreen)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.downloadAndInstallUpdate(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Download & Install v${update.versionName}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = updateUrlText,
                    onValueChange = { newUrl ->
                        updateUrlText = newUrl
                        viewModel.savePreferences(userPrefs.copy(updateCheckUrl = newUrl))
                    },
                    label = { Text("Update Manifest URL") },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Export Result Dialog
    showExportResult?.let { content ->
        AlertDialog(
            onDismissRequest = { showExportResult = null },
            title = { Text("Export Generated") },
            text = {
                Column {
                    Text("Content (${content.length} characters):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = content.take(1500) + if (content.length > 1500) "\n... [truncated for display]" else "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("KJ Steady Export", content))
                        showExportResult = null
                    }
                ) { Text("Copy to Clipboard") }
            },
            dismissButton = {
                TextButton(onClick = { showExportResult = null }) { Text("Close") }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import JSON Backup") },
            text = {
                Column {
                    Text("Paste prototype v1 JSON or v2 backup JSON:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        placeholder = { Text("{\"version\": 1, \"entries\": [...]}") },
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val count = viewModel.importBackupJson(importJsonText)
                                showImportDialog = false
                                importJsonText = ""
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                ) { Text("Import & Merge") }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) { Text("Cancel") }
            }
        )
    }
}
