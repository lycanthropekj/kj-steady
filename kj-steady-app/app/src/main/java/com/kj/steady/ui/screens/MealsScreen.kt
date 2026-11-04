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
import com.kj.steady.ui.theme.*
import com.kj.steady.ui.viewmodel.MainViewModel

@Composable
fun MealsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val mealTemplates by viewModel.mealTemplates.collectAsState()
    val weeklyPlan by viewModel.weeklyMealPlan.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Templates, 1: Dinner Planner, 2: Grocery List

    val groceryItems = remember {
        mutableStateListOf(
            "Eggs (2–3 dozen)" to false,
            "Fresh Curd / Greek Yogurt (1 kg)" to false,
            "Low-oil Fresh Paneer (500g)" to false,
            "Firm Tofu (400g)" to false,
            "Carbamide Forte Plant Protein Isolate" to false,
            "Muesli & Cow's Milk" to false,
            "Max Protein Bars (box of 6)" to false,
            "Yellow Moong Dal & Whole Wheat Atta" to false,
            "Onions, Tomatoes, Green Chillies & Coriander" to false,
            "Bell Peppers, Spinach & Cucumbers" to false
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Meals & Planning",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "High-protein vegetarian & egg recipes with zero non-veg cooking at home.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs
        TabRow(selectedTabIndex = activeTab) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("High-Protein Presets") })
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Weekly Dinner Plan") })
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text("Grocery List") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeTab) {
            0 -> {
                // High Protein Presets
                mealTemplates.forEach { template ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Slate200),
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
                                    text = template.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ProteinAmberLight,
                                    border = BorderStroke(1.dp, ProteinAmberBorder)
                                ) {
                                    Text(
                                        text = "${template.proteinGrams.toInt()}g Protein",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ProteinAmber,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = template.description,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Category: ${template.category.replace('_', ' ')} • Approx ${template.calories} kcal",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            1 -> {
                // Weekly Dinner Planner
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NutritionEmeraldLight),
                    border = BorderStroke(1.dp, NutritionEmeraldBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Friction Reduction Strategy",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NutritionEmeraldDark
                        )
                        Text(
                            text = "Deciding what to eat at 7:30 PM after office is when ordering happens. Having pre-stocked ingredients eliminates decision fatigue.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Slate600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                weeklyPlan.forEach { day ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Slate200),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = day.dayName.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandBlue,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "~${day.proteinTargetGrams.toInt()}g Protein Target",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Slate500
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = day.dinnerTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = day.dinnerDescription,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            2 -> {
                // Grocery Shopping List
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "WEEKLY PANTRY ESSENTIALS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        groceryItems.forEachIndexed { idx, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.second,
                                    onCheckedChange = { checked ->
                                        groceryItems[idx] = item.first to checked
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.first,
                                    fontSize = 14.sp,
                                    fontWeight = if (item.second) FontWeight.Normal else FontWeight.Medium,
                                    color = if (item.second) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
