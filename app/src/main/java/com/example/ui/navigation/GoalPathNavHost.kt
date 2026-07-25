package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleGlow

enum class BottomTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("dashboard_tab", "Dashboard", Icons.Filled.Home, Icons.Outlined.Home),
    ROADMAP("roadmap_tab", "Roadmap", Icons.Filled.Map, Icons.Outlined.Map),
    CHAT("chat_tab", "AI Mentor", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    PROFILE("profile_tab", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun MainTabScaffold(
    viewModel: com.example.ui.viewmodel.GoalPathViewModel,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToRoadmapInput: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(BottomTab.DASHBOARD) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .testTag("bottom_navigation_bar")
            ) {
                BottomTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                tint = if (isSelected) PrimaryPurple else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                color = if (isSelected) PrimaryPurple else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val modifier = Modifier.padding(paddingValues)
        when (selectedTab) {
            BottomTab.DASHBOARD -> DashboardScreen(
                viewModel = viewModel,
                onNavigateToRoadmap = { selectedTab = BottomTab.ROADMAP },
                onNavigateToMentorChat = { selectedTab = BottomTab.CHAT },
                modifier = modifier
            )
            BottomTab.ROADMAP -> RoadmapViewScreen(
                viewModel = viewModel,
                onEditRoadmapClick = onNavigateToRoadmapInput,
                modifier = modifier
            )
            BottomTab.CHAT -> ChatMentorScreen(
                viewModel = viewModel,
                modifier = modifier
            )
            BottomTab.PROFILE -> ProfileScreen(
                viewModel = viewModel,
                onEditDreamQuestionnaire = onNavigateToOnboarding,
                modifier = modifier
            )
        }
    }
}

@Composable
fun GoalPathNavHost(
    viewModel: com.example.ui.viewmodel.GoalPathViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val dreamProfile by viewModel.dreamProfile.collectAsState()

    val startDestination = if (dreamProfile?.isOnboardingCompleted == true) "main" else "welcome"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("welcome") {
            WelcomeScreen(
                onStartJourney = {
                    navController.navigate("onboarding")
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateNext = { navController.navigate("roadmap_input") }
            )
        }

        composable("roadmap_input") {
            RoadmapInputScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onStartJourneyComplete = {
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainTabScaffold(
                viewModel = viewModel,
                onNavigateToOnboarding = { navController.navigate("onboarding") },
                onNavigateToRoadmapInput = { navController.navigate("roadmap_input") }
            )
        }
    }
}
