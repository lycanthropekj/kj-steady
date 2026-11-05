package com.kj.steady.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        // Page Title & Context
        Column {
            Text(
                text = "Health Integrations",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Centralized on-device sync via Android Health Connect.",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate600
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. DYNAMIC HEALTH CONNECT STATUS & PERMISSION CARD
        when (healthStatus) {
            HealthConnectStatus.AVAILABLE -> {
                val hasAny = grantedPermissions.isNotEmpty()
                val hasSteps = grantedPermissions.any { it.contains("STEPS", ignoreCase = true) }
                val hasSleep = grantedPermissions.any { it.contains("SLEEP", ignoreCase = true) }
                val hasWeight = grantedPermissions.any { it.contains("WEIGHT", ignoreCase = true) }
                val hasHeartRate = grantedPermissions.any { it.contains("HEART_RATE", ignoreCase = true) }

                if (hasAny) {
                    // Connected & Active State
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.dp, Slate200),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Status Pill
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7),
                                border = BorderStroke(1.dp, Color(0xFF86EFAC))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF166534),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "HEALTH CONNECT ACTIVE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF166534),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Sensors Connected & Ready",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Stratos has active permission to read verified health metrics from your Galaxy S24 Ultra and connected wearables.",
                                fontSize = 13.sp,
                                color = Slate700,
                                lineHeight = 19.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Semantic Multi-Colored Metric Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Steps: Vibrant Tangerine
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (hasSteps) StepsOrangeLight else Slate100,
                                    border = BorderStroke(1.dp, if (hasSteps) StepsOrangeBorder else Slate200),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (hasSteps) "✓ Steps" else "✗ Steps",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (hasSteps) StepsOrangeDark else Slate400
                                        )
                                        Text(
                                            text = if (hasSteps) "Active" else "Missing",
                                            fontSize = 10.sp,
                                            color = if (hasSteps) StepsOrangeDark else Slate500
                                        )
                                    }
                                }

                                // Sleep: Royal Violet
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (hasSleep) SleepVioletLight else Slate100,
                                    border = BorderStroke(1.dp, if (hasSleep) SleepVioletBorder else Slate200),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (hasSleep) "✓ Sleep" else "✗ Sleep",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (hasSleep) SleepVioletDark else Slate400
                                        )
                                        Text(
                                            text = if (hasSleep) "Active" else "Missing",
                                            fontSize = 10.sp,
                                            color = if (hasSleep) SleepVioletDark else Slate500
                                        )
                                    }
                                }

                                // Weight: Royal Blue
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (hasWeight) BrandBlueLight else Slate100,
                                    border = BorderStroke(1.dp, if (hasWeight) BrandBlueBorder else Slate200),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (hasWeight) "✓ Weight" else "✗ Weight",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (hasWeight) BrandBlueDark else Slate400
                                        )
                                        Text(
                                            text = if (hasWeight) "Active" else "Missing",
                                            fontSize = 10.sp,
                                            color = if (hasWeight) BrandBlueDark else Slate500
                                        )
                                    }
                                }

                                // Heart Rate: Crimson Rose
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (hasHeartRate) HeartRoseLight else Slate100,
                                    border = BorderStroke(1.dp, if (hasHeartRate) HeartRoseBorder else Slate200),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (hasHeartRate) "✓ Pulse" else "✗ Pulse",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (hasHeartRate) HeartRose else Slate400
                                        )
                                        Text(
                                            text = if (hasHeartRate) "Active" else "Optional",
                                            fontSize = 10.sp,
                                            color = if (hasHeartRate) HeartRose else Slate500
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.syncHealthConnect() },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1.3f).height(48.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(17.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Sync Today Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.healthConnectManager.openHealthConnectSettings(context) },
                                    border = BorderStroke(1.dp, Slate300),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp), tint = Slate800)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text("Settings", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Slate800)
                                }
                            }
                        }
                    }
                } else {
                    // Permissions Needed State (High contrast white card with amber indicator)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        border = BorderStroke(1.5.dp, Color(0xFFFCD34D)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEF3C7),
                                border = BorderStroke(1.dp, Color(0xFFFDE68A))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFB45309),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PERMISSIONS REQUIRED",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Grant Health Connect Access",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "To sync your daily steps, sleep intervals, and weigh-ins from Samsung Health without manual typing, tap the button below to grant permission.",
                                fontSize = 13.sp,
                                color = Slate700,
                                lineHeight = 19.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { permissionLauncher.launch(viewModel.healthConnectManager.requiredPermissions) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Grant Permissions Now", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { viewModel.healthConnectManager.openHealthConnectSettings(context) },
                                border = BorderStroke(1.dp, Slate300),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("Open Health Connect System Settings", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate800)
                            }
                        }
                    }
                }
            }
            HealthConnectStatus.UPDATE_REQUIRED -> {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "HEALTH CONNECT UPDATE REQUIRED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "The Health Connect service on this device needs to be updated from Google Play Store before sync can run.",
                            fontSize = 13.sp,
                            color = Slate700,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.healthConnectManager.openHealthConnectPlayStore(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Update from Google Play Store", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            HealthConnectStatus.UNAVAILABLE -> {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, ErrorBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "HEALTH CONNECT UNAVAILABLE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Health Connect framework is not responding. On Samsung Galaxy S24 Ultra, ensure it is enabled under Settings → Security and privacy → Health Connect.",
                            fontSize = 13.sp,
                            color = Slate700,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.healthConnectManager.openHealthConnectSettings(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate900, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Open Device Settings", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. SAMSUNG GALAXY S24 ULTRA SETUP GUIDE CARD (High Contrast)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, Slate200),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Samsung Galaxy S24 Ultra Setup Guide",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Slate900
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Follow these 3 quick steps to enable automatic sync from your phone and watch:",
                    fontSize = 12.sp,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Step 1
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(BrandBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1", color = BrandBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Grant Permissions in Stratos", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        Text("Tap 'Grant Permissions Now' above and switch ON Steps, Sleep, Weight.", fontSize = 12.sp, color = Slate700, lineHeight = 17.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Step 2
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(BrandBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("2", color = BrandBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Allow Samsung Health to Write", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        Text("Open Samsung Health → tap ⋮ (top-right menu) → Settings → Health Connect → turn 'Allow all' ON.", fontSize = 12.sp, color = Slate700, lineHeight = 17.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Step 3
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(BrandBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("3", color = BrandBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sync Anytime", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        Text("Tap 'Sync Today Now' or 'Connect & Sync' on your Daily Log to import verified metrics without typing.", fontSize = 12.sp, color = Slate700, lineHeight = 17.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. PREFERRED SOURCE SELECTION CARD (Interactive High-Contrast Tiles)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, Slate200),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "STEP COUNT SOURCE PREFERENCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate600,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prevents double-counting when both your S24 Ultra phone and a fitness wearable track steps on the same walk.",
                    fontSize = 12.sp,
                    color = Slate700,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                val stepSources = listOf(
                    Triple("Samsung Health", "Galaxy S24 Ultra sensors & Galaxy Watch", "Samsung Health"),
                    Triple("Google Fit / Fitbit", "Fitbit bands & Google Health tracker", "Fitbit"),
                    Triple("Auto (Highest Source)", "Picks the highest verified count without duplication", "Auto (Highest Source)")
                )

                stepSources.forEach { (title, subtitle, sourceValue) ->
                    val isSelected = selectedSource == sourceValue
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) BrandBlueLight else Slate50,
                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) BrandBlue else Slate200),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedSource = sourceValue
                                viewModel.savePreferences(userPrefs.copy(preferredStepsSource = sourceValue))
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedSource = sourceValue
                                    viewModel.savePreferences(userPrefs.copy(preferredStepsSource = sourceValue))
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = BrandBlue,
                                    unselectedColor = Slate400
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = Slate900
                                )
                                Text(
                                    text = subtitle,
                                    fontSize = 11.sp,
                                    color = if (isSelected) BrandBlueDark else Slate600
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "SLEEP TRACKING PREFERENCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate600,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Merges overlapping sleep intervals from multiple devices into a unified timeline without duplication.",
                    fontSize = 12.sp,
                    color = Slate700,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                val sleepSources = listOf(
                    Triple("Auto Merge (No Overlap)", "Combines sessions seamlessly; eliminates duplicate hours", "Auto Merge (No Overlap)"),
                    Triple("Samsung Health Only", "Galaxy Watch sleep tracking only", "Samsung Health"),
                    Triple("Fitbit Only", "Fitbit sleep staging only", "Fitbit")
                )

                sleepSources.forEach { (title, subtitle, sourceValue) ->
                    val isSelected = userPrefs.preferredSleepSource == sourceValue
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) BrandBlueLight else Slate50,
                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) BrandBlue else Slate200),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.savePreferences(userPrefs.copy(preferredSleepSource = sourceValue))
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.savePreferences(userPrefs.copy(preferredSleepSource = sourceValue))
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = BrandBlue,
                                    unselectedColor = Slate400
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = Slate900
                                )
                                Text(
                                    text = subtitle,
                                    fontSize = 11.sp,
                                    color = if (isSelected) BrandBlueDark else Slate600
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. ECOSYSTEM STATUS CARD (High Contrast)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, Slate200),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "CONNECTED ECOSYSTEM STATUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate600,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                IntegrationStatusRow(
                    title = "Android Health Connect",
                    statusBadge = "Active Hub",
                    badgeColor = Color(0xFFDCFCE7),
                    badgeBorder = Color(0xFF86EFAC),
                    badgeTextColor = Color(0xFF166534),
                    note = "System framework built into Android 14 (Galaxy S24 Ultra)"
                )
                IntegrationStatusRow(
                    title = "Samsung Health",
                    statusBadge = "Native Sync",
                    badgeColor = BrandBlueLight,
                    badgeBorder = BrandBlueBorder,
                    badgeTextColor = BrandBlueDark,
                    note = "Steps, Sleep, and Weight routed via Health Connect"
                )
                IntegrationStatusRow(
                    title = "Fitbit (Google Health)",
                    statusBadge = "Supported",
                    badgeColor = WaterBlueLight,
                    badgeBorder = WaterBlueBorder,
                    badgeTextColor = WaterBlueDark,
                    note = "Wearable sync via Health Connect in Fitbit App"
                )
                IntegrationStatusRow(
                    title = "Google Fit",
                    statusBadge = "Legacy API",
                    badgeColor = Slate100,
                    badgeBorder = Slate200,
                    badgeTextColor = Slate600,
                    note = "Google transitioned Fit to the Health Connect platform"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun IntegrationStatusRow(
    title: String,
    statusBadge: String,
    badgeColor: Color,
    badgeBorder: Color,
    badgeTextColor: Color,
    note: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
            Spacer(modifier = Modifier.height(2.dp))
            Text(note, fontSize = 12.sp, color = Slate700)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = badgeColor,
            border = BorderStroke(1.dp, badgeBorder)
        ) {
            Text(
                text = statusBadge,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = badgeTextColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
    HorizontalDivider(color = Slate200, thickness = 1.dp)
}
