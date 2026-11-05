package com.kj.steady.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import com.kj.steady.health.HealthConnectStatus
import com.kj.steady.ui.theme.*
import com.kj.steady.ui.viewmodel.MainViewModel

@Composable
fun HealthConnectScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userPrefs by viewModel.userPreferences.collectAsState()
    var selectedSource by remember(userPrefs) { mutableStateOf(userPrefs.preferredStepsSource) }

    val healthStatus by viewModel.healthConnectStatus.collectAsState()
    val grantedPermissions by viewModel.grantedHealthPermissions.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        viewModel.onHealthPermissionsResult(granted)
    }

    LaunchedEffect(Unit) {
        viewModel.refreshHealthConnectStatus()
    }

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
                    text = "Health Integrations",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = "Centralized on-device sync via Android Health Connect.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. DYNAMIC HEALTH CONNECT STATUS & PERMISSION CARD
        when (healthStatus) {
            HealthConnectStatus.AVAILABLE -> {
                val hasAny = grantedPermissions.isNotEmpty()
                val hasSteps = grantedPermissions.any { it.contains("STEPS", ignoreCase = true) }
                val hasSleep = grantedPermissions.any { it.contains("SLEEP", ignoreCase = true) }
                val hasWeight = grantedPermissions.any { it.contains("WEIGHT", ignoreCase = true) }

                if (hasAny) {
                    // Connected & Active
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SuccessBg),
                        border = BorderStroke(1.dp, SuccessBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Active", tint = SuccessGreen, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "HEALTH CONNECT: CONNECTED & READY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Permissions are granted to read sensor data from Samsung Health & Fitbit via Health Connect.",
                                fontSize = 12.sp,
                                color = Slate700,
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Permission Badges Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (hasSteps) Color(0xFFDCFCE7) else Slate100,
                                    border = BorderStroke(1.dp, if (hasSteps) Color(0xFF86EFAC) else Slate200)
                                ) {
                                    Text(
                                        text = if (hasSteps) "✓ Steps" else "✗ Steps",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasSteps) Color(0xFF15803D) else Slate400,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (hasSleep) Color(0xFFDCFCE7) else Slate100,
                                    border = BorderStroke(1.dp, if (hasSleep) Color(0xFF86EFAC) else Slate200)
                                ) {
                                    Text(
                                        text = if (hasSleep) "✓ Sleep" else "✗ Sleep",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasSleep) Color(0xFF15803D) else Slate400,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (hasWeight) Color(0xFFDCFCE7) else Slate100,
                                    border = BorderStroke(1.dp, if (hasWeight) Color(0xFF86EFAC) else Slate200)
                                ) {
                                    Text(
                                        text = if (hasWeight) "✓ Weight" else "✗ Weight",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasWeight) Color(0xFF15803D) else Slate400,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.syncHealthConnect() },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1.2f).height(46.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Sync Today Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.healthConnectManager.openHealthConnectSettings(context) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(46.dp)
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp), tint = Slate700)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Settings", fontSize = 12.sp, color = Slate700)
                                }
                            }
                        }
                    }
                } else {
                    // Permissions Needed
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = AmberWarning),
                        border = BorderStroke(1.dp, AmberBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = "Permission Needed", tint = AmberText, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "HEALTH CONNECT: PERMISSIONS NEEDED",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberText,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Android requires you to grant KJ Steady permission to read steps, sleep intervals, and weigh-ins from Health Connect.",
                                fontSize = 12.sp,
                                color = AmberText,
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { permissionLauncher.launch(viewModel.healthConnectManager.requiredPermissions) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Grant Permissions Now", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { viewModel.healthConnectManager.openHealthConnectSettings(context) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("Open Health Connect System Settings", fontSize = 12.sp, color = Slate700)
                            }
                        }
                    }
                }
            }
            HealthConnectStatus.UPDATE_REQUIRED -> {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberWarning),
                    border = BorderStroke(1.dp, AmberBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "HEALTH CONNECT UPDATE REQUIRED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "The Health Connect service on this device needs to be updated from the Google Play Store before sync can run.",
                            fontSize = 12.sp,
                            color = AmberText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.healthConnectManager.openHealthConnectPlayStore(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Update from Google Play Store", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            HealthConnectStatus.UNAVAILABLE -> {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ErrorBg),
                    border = BorderStroke(1.dp, ErrorBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "HEALTH CONNECT IS UNAVAILABLE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Health Connect framework is not responding. On Samsung Galaxy S24 Ultra, ensure it is enabled under Settings → Security and privacy → Health Connect.",
                            fontSize = 12.sp,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.healthConnectManager.openHealthConnectSettings(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Open Device Settings", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. SAMSUNG GALAXY S24 ULTRA SETUP GUIDE CARD
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100),
            border = BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Samsung Galaxy S24 Ultra Setup Guide",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandBlueDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Follow these 3 quick steps to enable automatic sync from your phone and watch:\n\n" +
                            "1. Tap 'Grant Permissions Now' above and switch ON Steps, Sleep, Weight.\n" +
                            "2. Open Samsung Health → tap ⋮ (top-right menu) → Settings → Health Connect → turn ON 'Allow all'.\n" +
                            "3. (Optional) In the Fitbit app, go to Settings → Health Connect → enable sync.\n\n" +
                            "Once enabled, tapping 'Sync Today Now' instantly pulls your verified steps and sleep intervals.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Slate600
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. PREFERRED SOURCE SELECTION CARD
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "PREFERRED STEPS SOURCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Prevents double-counting when both your S24 Ultra phone and Fitbit tracker log steps during the same walk.",
                    fontSize = 12.sp,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(12.dp))

                listOf("Samsung Health", "Fitbit", "Auto (Highest Source)").forEach { source ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSource == source,
                            onClick = {
                                selectedSource = source
                                viewModel.savePreferences(userPrefs.copy(preferredStepsSource = source))
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(source, fontSize = 14.sp, color = Slate900)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PREFERRED SLEEP SOURCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Merges overlapping sleep sessions from multiple wearables so identical hours are never added twice.",
                    fontSize = 12.sp,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(12.dp))

                listOf("Auto Merge (No Overlap)", "Samsung Health", "Fitbit").forEach { sleepSource ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = userPrefs.preferredSleepSource == sleepSource,
                            onClick = {
                                viewModel.savePreferences(userPrefs.copy(preferredSleepSource = sleepSource))
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(sleepSource, fontSize = 14.sp, color = Slate900)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. ECOSYSTEM STATUS CARD
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "CONNECTED ECOSYSTEM STATUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                IntegrationStatusRow("Android Health Connect", "Active Hub", "Built into Android OS (Samsung Galaxy S24 Ultra)")
                IntegrationStatusRow("Samsung Health", "Native HC Sync", "Steps, Sleep, Weight routed via Health Connect")
                IntegrationStatusRow("Fitbit Air (Google Health)", "Native HC Sync", "Synced into Health Connect via Fitbit App")
                IntegrationStatusRow("Google Fit", "Deprecated Standard", "Legacy APIs decommissioned 2024–2026; use HC")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun IntegrationStatusRow(title: String, statusBadge: String, note: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
            Text(note, fontSize = 11.sp, color = Slate500)
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = BrandBlueLight,
            border = BorderStroke(1.dp, BrandBlueBorder)
        ) {
            Text(
                text = statusBadge,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BrandBlue,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
    HorizontalDivider(color = Slate200)
}
