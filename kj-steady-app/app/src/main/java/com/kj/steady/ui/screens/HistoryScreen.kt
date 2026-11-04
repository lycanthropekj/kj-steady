package com.kj.steady.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kj.steady.data.model.DailyEntry
import com.kj.steady.logic.DateUtils
import com.kj.steady.ui.theme.ForestGreen
import com.kj.steady.ui.theme.RestrainedLime
import com.kj.steady.ui.viewmodel.MainViewModel

@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    onNavigateToEdit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.allEntries.collectAsState()
    var entryToDelete by remember { mutableStateOf<DailyEntry?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text(
                text = "History & Timeline",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your record. Tap Edit to update any day.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Weekly Aggregates Card
        if (entries.isNotEmpty()) {
            item {
                val last7 = entries.take(7)
                val avgWeight = last7.mapNotNull { it.morningWeight }.average().takeIf { !it.isNaN() }
                val avgSteps = last7.mapNotNull { it.totalSteps }.average().takeIf { !it.isNaN() }
                val avgSleep = last7.mapNotNull { it.sleepHours }.average().takeIf { !it.isNaN() }
                val alcoholDays = last7.count { it.hadAlcohol }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ForestGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "LAST 7 LOGGED DAYS AGGREGATE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RestrainedLime,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("AVG WEIGHT", fontSize = 10.sp, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f))
                                Text(
                                    text = avgWeight?.let { "%.1f kg".format(it) } ?: "—",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                            }
                            Column {
                                Text("AVG STEPS", fontSize = 10.sp, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f))
                                Text(
                                    text = avgSteps?.let { "%,.0f".format(it) } ?: "—",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                            }
                            Column {
                                Text("AVG SLEEP", fontSize = 10.sp, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f))
                                Text(
                                    text = avgSleep?.let { "%.1f h".format(it) } ?: "—",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                            }
                            Column {
                                Text("ALCOHOL", fontSize = 10.sp, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f))
                                Text(
                                    text = "$alcoholDays d",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (alcoholDays > 0) RestrainedLime else androidx.compose.ui.graphics.Color.White
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (entries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nothing logged yet.\nYour first entry will appear here.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        lineHeight = 22.sp
                    )
                }
            }
        } else {
            items(entries, key = { it.date }) { entry ->
                Card(
                    shape = RoundedCornerShape(16.dp),
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
                                text = DateUtils.formatDisplay(entry.date),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row {
                                IconButton(
                                    onClick = { onNavigateToEdit(entry.date) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = ForestGreen, modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = { entryToDelete = entry },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        // Summary metric chips
                        val metricParts = mutableListOf<String>()
                        entry.morningWeight?.let { metricParts.add("%.1f kg".format(it)) }
                        entry.totalSteps?.let { metricParts.add("%,d steps".format(it)) }
                        entry.sleepHours?.let { metricParts.add("%.1f h sleep".format(it)) }
                        entry.cigarettes?.let { if (it > 0) metricParts.add("$it cigs") }
                        if (entry.hadAlcohol) metricParts.add("Alcohol logged")

                        if (metricParts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = metricParts.joinToString(" • "),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = ForestGreen
                            )
                        }

                        // Meals and notes
                        entry.breakfastText?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Breakfast: $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                        entry.lunchSnacksText?.let {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Lunch/Snacks: $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                        entry.dinnerText?.let {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Dinner: $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                        entry.notes?.let {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Notes: $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Source: ${entry.sourceAttribution}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }

    entryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Delete Entry") },
            text = { Text("Delete the record for ${DateUtils.formatDisplay(entry.date)}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDailyEntry(entry.date)
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) { Text("Cancel") }
            }
        )
    }
}
