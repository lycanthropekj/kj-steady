package com.kj.steady

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.kj.steady.ui.screens.*
import com.kj.steady.ui.theme.*
import com.kj.steady.ui.viewmodel.MainViewModel
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.flow.collectLatest

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Overview : Screen("overview", "Overview", Icons.Default.Home)
    object DailyLog : Screen("log", "Log", Icons.Default.EditNote)
    object Hydration : Screen("hydration", "Hydrate", Icons.Default.WaterDrop)
    object Meals : Screen("meals", "Meals", Icons.Default.Restaurant)
    object History : Screen("history", "History", Icons.Default.CalendarMonth)
    object Routine : Screen("routine", "Routine", Icons.Default.Schedule)
    object Health : Screen("health", "Health", Icons.Default.Favorite)
    object Adaptation : Screen("adaptive", "Learn", Icons.Default.Psychology)
    object Setup : Screen("setup", "Setup", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KJSteadyTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Collect toasts
                LaunchedEffect(Unit) {
                    viewModel.uiToast.collectLatest { msg ->
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                val primaryNavItems = listOf(
                    Screen.Overview,
                    Screen.DailyLog,
                    Screen.Hydration,
                    Screen.Meals,
                    Screen.History,
                    Screen.Routine,
                    Screen.Setup
                )

                Scaffold(
                    bottomBar = {
                        Surface(
                            shadowElevation = 8.dp,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                tonalElevation = 0.dp
                            ) {
                                primaryNavItems.forEach { screen ->
                                    val selected = currentRoute == screen.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            if (currentRoute != screen.route) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                                        label = {
                                            Text(
                                                screen.title,
                                                fontSize = 10.sp,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        alwaysShowLabel = false,
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = BrandBlue,
                                            selectedTextColor = BrandBlue,
                                            indicatorColor = BrandBlueLight,
                                            unselectedIconColor = Slate400,
                                            unselectedTextColor = Slate500
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Overview.route,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable(Screen.Overview.route) {
                            OverviewScreen(
                                viewModel = viewModel,
                                onNavigateToLog = { date ->
                                    viewModel.setSelectedDate(date)
                                    navController.navigate(Screen.DailyLog.route)
                                },
                                onNavigateToHealth = {
                                    navController.navigate(Screen.Health.route)
                                }
                            )
                        }
                        composable(Screen.DailyLog.route) {
                            DailyLogScreen(viewModel = viewModel)
                        }
                        composable(Screen.Hydration.route) {
                            HydrationScreen(viewModel = viewModel)
                        }
                        composable(Screen.Meals.route) {
                            MealsScreen(viewModel = viewModel)
                        }
                        composable(Screen.History.route) {
                            HistoryScreen(
                                viewModel = viewModel,
                                onNavigateToEdit = { date ->
                                    viewModel.setSelectedDate(date)
                                    navController.navigate(Screen.DailyLog.route)
                                }
                            )
                        }
                        composable(Screen.Routine.route) {
                            RoutineScreen(viewModel = viewModel)
                        }
                        composable(Screen.Health.route) {
                            HealthConnectScreen(viewModel = viewModel)
                        }
                        composable(Screen.Adaptation.route) {
                            AdaptationScreen(viewModel = viewModel)
                        }
                        composable(Screen.Setup.route) {
                            SetupScreen(
                                viewModel = viewModel,
                                onNavigateToHealth = {
                                    navController.navigate(Screen.Health.route)
                                },
                                onNavigateToAdaptive = {
                                    navController.navigate(Screen.Adaptation.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
