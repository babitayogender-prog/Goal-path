package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AIProgressInsightsCard
import com.example.ui.components.FocusTimerCard
import com.example.ui.components.GoalCard
import com.example.ui.components.ProgressRing
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleGlow
import com.example.ui.theme.SecondaryViolet
import com.example.ui.viewmodel.GoalPathViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: GoalPathViewModel,
    onNavigateToRoadmap: () -> Unit,
    onNavigateToMentorChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dreamProfile by viewModel.dreamProfile.collectAsState()
    val monthlyGoals by viewModel.allMonthlyGoals.collectAsState()
    val dailyTasks by viewModel.allDailyTasks.collectAsState()
    val cumulativeProgress by viewModel.cumulativeProgress.collectAsState()
    val isGeneratingGoals by viewModel.isGeneratingMonthlyGoals.collectAsState()

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var newGoalTitle by remember { mutableStateOf("") }
    var newGoalWeightage by remember { mutableStateOf("25") }
    var selectedCategoryIcon by remember { mutableStateOf("code") }

    val userName = dreamProfile?.userName ?: "Achiever"
    val targetCollege = dreamProfile?.targetCollege?.ifBlank { "MIT" } ?: "MIT"
    val dreamCompany = dreamProfile?.dreamCompany?.ifBlank { "Google" } ?: "Google"

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Column {
                    Text(
                        text = "Welcome back, $userName! 👋",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Dashboard",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // AI Mentor Quick Chat Button
                    IconButton(
                        onClick = onNavigateToMentorChat,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Mentor",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("dashboard_screen")
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Personalized Motivational Hero Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .testTag("motivational_hero_card")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Let's get you into",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "[$targetCollege] ",
                                    color = PrimaryPurple,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "and hired at",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "[$dreamCompany]!",
                                color = PrimaryPurple,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Circular Progress Ring for Cumulative Dream Progress
                        ProgressRing(
                            progressPercentage = cumulativeProgress,
                            size = 100.dp,
                            strokeWidth = 9.dp
                        )
                    }
                }
            }

            // Monthly Progress Header Summary
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Monthly Progress Plan",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "$cumulativeProgress% Completed",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            // Month Tag
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "May 2026",
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Horizontal Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = (cumulativeProgress / 100f).coerceIn(0f, 1f))
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(PrimaryPurple, PrimaryPurpleGlow)
                                        )
                                    )
                            )
                        }
                    }
                }
            }

            // Pomodoro Focus Timer Card
            item {
                FocusTimerCard(
                    viewModel = viewModel,
                    monthlyGoals = monthlyGoals,
                    dailyHoursAvailable = dreamProfile?.dailyHoursAvailable ?: "4 hours/day"
                )
            }

            // AI Progress Insights & Suggestions Card
            item {
                Spacer(modifier = Modifier.height(12.dp))
                AIProgressInsightsCard(viewModel = viewModel)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // "This Month's Goals" Section Header & AI Generation Button
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "This Month's Goals",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Row {
                        // AI Timetable Generator Button
                        TextButton(
                            onClick = { viewModel.generateMonthlyTimetableWithAI() },
                            enabled = !isGeneratingGoals
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AI Plan", color = PrimaryPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        // Manual Add Goal Button
                        TextButton(
                            onClick = { showAddGoalDialog = true },
                            modifier = Modifier.testTag("add_goal_button")
                        ) {
                            Text("+ Add Goal", color = PrimaryPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Empty State or Goals List
            if (monthlyGoals.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Monthly Goals Yet",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Click 'AI Plan' or '+ Add Goal' to assign weighted milestone goals to your timetable!",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            } else {
                items(monthlyGoals, key = { it.id }) { goal ->
                    val goalTasks = dailyTasks.filter { it.monthlyGoalId == goal.id }
                    GoalCard(
                        goal = goal,
                        tasks = goalTasks,
                        onToggleGoalCompletion = { viewModel.toggleGoalCompletion(goal) },
                        onToggleTaskCompletion = { task -> viewModel.toggleTaskCompletion(task, goal) },
                        onAddTask = { taskTitle -> viewModel.addDailyTask(goal.id, taskTitle) },
                        onDeleteGoal = { viewModel.deleteGoal(goal.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }

    // Add Goal Dialog
    if (showAddGoalDialog) {
        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            title = { Text("Create Monthly Goal", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newGoalTitle,
                        onValueChange = { newGoalTitle = it },
                        label = { Text("Goal Title", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        placeholder = { Text("e.g., Learn System Design Basics", color = MaterialTheme.colorScheme.outlineVariant) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newGoalWeightage,
                        onValueChange = { newGoalWeightage = it },
                        label = { Text("Progress Weightage (%)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        placeholder = { Text("e.g., 25", color = MaterialTheme.colorScheme.outlineVariant) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Category Icon:", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("code", "database", "web", "folder", "user", "star").forEach { iconKey ->
                            IconButton(
                                onClick = { selectedCategoryIcon = iconKey },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selectedCategoryIcon == iconKey) PrimaryPurple else MaterialTheme.colorScheme.surfaceVariant
                                    )
                            ) {
                                Icon(
                                    imageVector = when (iconKey) {
                                        "code" -> Icons.Default.Code
                                        "database" -> Icons.Default.Storage
                                        "web" -> Icons.Default.Language
                                        "folder" -> Icons.Default.Folder
                                        "user" -> Icons.Default.Person
                                        else -> Icons.Default.Star
                                    },
                                    contentDescription = iconKey,
                                    tint = if (selectedCategoryIcon == iconKey) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val weightFloat = newGoalWeightage.toFloatOrNull() ?: 20f
                        if (newGoalTitle.isNotBlank()) {
                            viewModel.addMonthlyGoal(newGoalTitle, weightFloat, selectedCategoryIcon)
                            newGoalTitle = ""
                            showAddGoalDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text("Save Goal", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
