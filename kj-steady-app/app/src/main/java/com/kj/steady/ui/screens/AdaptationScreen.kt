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
import com.kj.steady.ui.theme.*
import com.kj.steady.ui.viewmodel.MainViewModel

@Composable
fun AdaptationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val suggestions by viewModel.adaptiveSuggestions.collectAsState()

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
                    text = "Adaptive Learning",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Transparent local habit heuristics with zero silent changes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Safeguards Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BrandBlueLight),
            border = BorderStroke(1.dp, BrandBlueBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Rule Transparency & Safety Guarantee",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandBlueDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• No Silent Changes: Calories, water, or workouts are never modified without your tap.\n" +
                            "• Fluctuations Respected: Daily water retention spikes are ignored; only 7-day rolling trends trigger guidance.\n" +
                            "• Local & Reversible: All heuristics run purely on-device and can be reset anytime.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Active Heuristic Observations (${suggestions.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (suggestions.isEmpty()) {
            Text(
                text = "No routine adjustments observed yet. Continue logging for 3+ days to surface patterns.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            suggestions.forEach { suggestion ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = suggestion.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandBlueLight,
                                border = BorderStroke(1.dp, BrandBlueBorder)
                            ) {
                                Text(
                                    text = suggestion.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Observation: ${suggestion.rationale}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Suggested Adaptation: ${suggestion.proposedAction}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandBlueDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (suggestion.status == "PENDING") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.approveAdaptiveSuggestion(suggestion.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Adopt Suggestion", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { viewModel.dismissAdaptiveSuggestion(suggestion.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Dismiss", fontSize = 12.sp)
                                }
                            }
                        } else {
                            Text(
                                text = "Status: ${suggestion.status}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (suggestion.status == "APPROVED") SuccessGreen else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { viewModel.resetAdaptiveSuggestions() },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset All Learned Heuristics", fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
