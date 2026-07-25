package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MonthlyGoal
import com.example.ui.theme.AccentPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleGlow
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.GoalPathViewModel
import kotlinx.coroutines.delay

@Composable
fun FocusTimerCard(
    viewModel: GoalPathViewModel,
    monthlyGoals: List<MonthlyGoal>,
    dailyHoursAvailable: String,
    modifier: Modifier = Modifier
) {
    // Preset durations in minutes
    val presets = listOf(
        Pair("Focus 25m", 25),
        Pair("Deep Work 45m", 45),
        Pair("Quick 15m", 15),
        Pair("Break 5m", 5)
    )

    var selectedPresetMinutes by remember { mutableIntStateOf(25) }
    var totalSessionSeconds by remember { mutableIntStateOf(25 * 60) }
    var timeRemainingSeconds by remember { mutableIntStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var todayFocusMinutes by remember { mutableIntStateOf(0) }
    var selectedGoalId by remember { mutableStateOf<Long?>(null) }
    var showCompletionBanner by remember { mutableStateOf(false) }

    // Set default selected goal if available
    LaunchedEffect(monthlyGoals) {
        if (selectedGoalId == null && monthlyGoals.isNotEmpty()) {
            selectedGoalId = monthlyGoals.first().id
        }
    }

    val selectedGoal = monthlyGoals.find { it.id == selectedGoalId }

    // Timer countdown engine
    LaunchedEffect(isRunning, timeRemainingSeconds) {
        if (isRunning && timeRemainingSeconds > 0) {
            delay(1000L)
            timeRemainingSeconds -= 1
        } else if (isRunning && timeRemainingSeconds == 0) {
            isRunning = false
            val sessionMins = totalSessionSeconds / 60
            todayFocusMinutes += sessionMins
            showCompletionBanner = true

            // Log time to selected goal
            selectedGoal?.let { goal ->
                viewModel.logFocusTime(goal, sessionMins)
            }
        }
    }

    // Pulse animation when active
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRunning) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    fun resetTimer(minutes: Int = selectedPresetMinutes) {
        isRunning = false
        selectedPresetMinutes = minutes
        totalSessionSeconds = minutes * 60
        timeRemainingSeconds = minutes * 60
        showCompletionBanner = false
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, PrimaryPurple.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .testTag("pomodoro_focus_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header Row: Title & Daily Hours Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Pomodoro Timer",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pomodoro Focus Timer",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Target Daily: ${dailyHoursAvailable.ifBlank { "3 hours/day" }}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Today Logged Minutes Chip
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${todayFocusMinutes}m Today",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Goal Selection Selector
            Text(
                text = "Target Goal for Focus Session:",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (monthlyGoals.isEmpty()) {
                Text(
                    text = "No active monthly goals. Create a goal to track progress!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(monthlyGoals) { goal ->
                        val isSelected = goal.id == selectedGoalId
                        Surface(
                            color = if (isSelected) PrimaryPurple else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.clickable {
                                if (!isRunning) {
                                    selectedGoalId = goal.id
                                }
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.TaskAlt else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = goal.title,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preset Session Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                presets.forEach { (label, minutes) ->
                    val isSelected = selectedPresetMinutes == minutes
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            resetTimer(minutes)
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryPurple.copy(alpha = 0.2f),
                            selectedLabelColor = PrimaryPurple
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Digital Clock Box with Circular Arc
            val progressFraction = if (totalSessionSeconds > 0) {
                timeRemainingSeconds.toFloat() / totalSessionSeconds.toFloat()
            } else 0f

            val minutesRemaining = timeRemainingSeconds / 60
            val secondsRemaining = timeRemainingSeconds % 60
            val timeFormatted = String.format("%02d:%02d", minutesRemaining, secondsRemaining)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, PrimaryPurple.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(vertical = 20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(pulseScale)
                ) {
                    Text(
                        text = timeFormatted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isRunning) PrimaryPurpleGlow else MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when {
                            isRunning -> "🔥 Deep Focus Mode Active..."
                            timeRemainingSeconds == 0 -> "🎉 Session Completed!"
                            else -> "Ready to Focus"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRunning) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Linear progress indicator for session completion
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        color = PrimaryPurple,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .width(180.dp)
                            .height(6.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Controls (Start / Pause / Reset / Log)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Play / Pause Button
                Button(
                    onClick = {
                        if (timeRemainingSeconds == 0) {
                            resetTimer(selectedPresetMinutes)
                        } else {
                            isRunning = !isRunning
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) AccentPink else PrimaryPurple
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("timer_start_pause_button")
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start Focus",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRunning) "Pause" else if (timeRemainingSeconds == 0) "Restart" else "Start Focus",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Quick Log 15m Button
                OutlinedButton(
                    onClick = {
                        val minutesToLog = 15
                        todayFocusMinutes += minutesToLog
                        selectedGoal?.let { goal ->
                            viewModel.logFocusTime(goal, minutesToLog)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryPurple)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log 15m",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+15m Log", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Reset Button
                IconButton(
                    onClick = { resetTimer() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Completion Banner
            AnimatedVisibility(visible = showCompletionBanner) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = SuccessGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Awesome work! Logged focus time toward '${selectedGoal?.title ?: "Goal"}'.",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
