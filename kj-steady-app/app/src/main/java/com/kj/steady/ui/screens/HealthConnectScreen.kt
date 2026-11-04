package com.kj.steady.ui.screens

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
import com.kj.steady.health.HealthConnectStatus
import com.kj.steady.ui.theme.ForestGreen
import com.kj.steady.ui.theme.RestrainedLime
import com.kj.steady.ui.viewmodel.MainViewModel

@Composable
fun HealthConnectScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userPrefs by viewModel.userPreferences.collectAsState()
    var selectedSource by remember(userPrefs) { mutableStateOf(userPrefs.preferredStepsSource) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Health Integrations",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Centralized on-device sync via Android Health Connect.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ecosystem Status Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "CONNECTED ECOSYSTEM STATUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                IntegrationStatusRow("Android Health Connect", "Active Hub", "Built into Android OS (Samsung Galaxy S24 Ultra)")
                IntegrationStatusRow("Samsung Health", "Native HC Sync", "Steps, Sleep, Weight routed via Health Connect")
                IntegrationStatusRow("Fitbit Air (Google Health)", "Native HC Sync", "Synced into Health Connect via Fitbit App")
                IntegrationStatusRow("Google Fit", "Deprecated Standard", "Legacy APIs decommissioned 2024–2026; use HC")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Preferred Source Selection Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "PREFERRED STEPS SOURCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Prevents double-counting when both your S24 Ultra phone and Fitbit tracker log steps during the same walk.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                listOf("Samsung Health", "Fitbit", "Manual Verification First").forEach { source ->
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
                        Text(source, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sync Action Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "On-Demand Sensor Sync",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Sync pulls validated step totals, sleep session intervals, and smart scale weigh-ins without running background battery loops.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.syncHealthConnect() },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = androidx.compose.ui.graphics.Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Sync Today's Metrics Now", fontWeight = FontWeight.Bold)
                }
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
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(note, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = ForestGreen.copy(alpha = 0.1f)
        ) {
            Text(
                text = statusBadge,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
}
