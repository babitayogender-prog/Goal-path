package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import com.example.ui.components.QuestionCard
import com.example.ui.theme.DarkPurpleText
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleGlow
import com.example.ui.theme.SecondaryViolet
import com.example.ui.viewmodel.GoalPathViewModel

@Composable
fun OnboardingScreen(
    viewModel: GoalPathViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsState()
    val targetCollege by viewModel.targetCollege.collectAsState()
    val preferredCity by viewModel.preferredCity.collectAsState()
    val dreamCompany by viewModel.dreamCompany.collectAsState()
    val targetCourse by viewModel.targetCourse.collectAsState()
    val targetSalary by viewModel.targetSalary.collectAsState()
    val keySkills by viewModel.keySkills.collectAsState()
    val targetCompletionDate by viewModel.targetCompletionDate.collectAsState()
    val primaryMotivation by viewModel.primaryMotivation.collectAsState()
    val currentSkillLevel by viewModel.currentSkillLevel.collectAsState()
    val dailyHoursAvailable by viewModel.dailyHoursAvailable.collectAsState()

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Your Dream Blueprint",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.size(48.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("onboarding_screen")
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Answer a few questions to map your dream",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Step Progress Indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(PrimaryPurple, DarkPurpleText)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LazyColumn for 10 Questionnaire Steps
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // User Name (Optional personalizer)
                item {
                    QuestionCard(
                        stepNumber = 0,
                        title = "Your Name / Alias",
                        subtitle = "How should GoalPath address you?",
                        icon = Icons.Default.Person,
                        value = userName,
                        onValueChange = { viewModel.userName.value = it },
                        placeholder = "e.g., Arjun"
                    )
                }

                // Question 1: Target College / University
                item {
                    QuestionCard(
                        stepNumber = 1,
                        title = "Target College / University",
                        subtitle = "Where do you see yourself studying?",
                        icon = Icons.Default.AccountBalance,
                        value = targetCollege,
                        onValueChange = { viewModel.targetCollege.value = it },
                        placeholder = "e.g., MIT / Stanford / IIT"
                    )
                }

                // Question 2: Preferred City / Location
                item {
                    QuestionCard(
                        stepNumber = 2,
                        title = "Preferred City / Location",
                        subtitle = "Your ideal living or working location",
                        icon = Icons.Default.LocationOn,
                        value = preferredCity,
                        onValueChange = { viewModel.preferredCity.value = it },
                        placeholder = "e.g., San Francisco, CA / London"
                    )
                }

                // Question 3: Dream Company / Role
                item {
                    QuestionCard(
                        stepNumber = 3,
                        title = "Dream Company / Role",
                        subtitle = "Where do you want to work?",
                        icon = Icons.Default.Work,
                        value = dreamCompany,
                        onValueChange = { viewModel.dreamCompany.value = it },
                        placeholder = "e.g., Google / OpenAI / Senior AI Engineer"
                    )
                }

                // Question 4: Target Course / Major
                item {
                    QuestionCard(
                        stepNumber = 4,
                        title = "Target Course / Major",
                        subtitle = "What do you want to study or specialize in?",
                        icon = Icons.Default.Book,
                        value = targetCourse,
                        onValueChange = { viewModel.targetCourse.value = it },
                        placeholder = "e.g., Computer Science & Artificial Intelligence"
                    )
                }

                // Question 5: Desired Starting Salary / Level
                item {
                    QuestionCard(
                        stepNumber = 5,
                        title = "Desired Starting Salary / Level",
                        subtitle = "Your expected financial starting point",
                        icon = Icons.Default.AttachMoney,
                        value = targetSalary,
                        onValueChange = { viewModel.targetSalary.value = it },
                        placeholder = "e.g., $150,000 / Level 4 Engineer"
                    )
                }

                // Question 6: Key Skills Needed
                item {
                    QuestionCard(
                        stepNumber = 6,
                        title = "Key Skills Needed",
                        subtitle = "Skills you need to master to reach this goal",
                        icon = Icons.Default.Star,
                        value = keySkills,
                        onValueChange = { viewModel.keySkills.value = it },
                        placeholder = "e.g., Python, Data Structures, System Design"
                    )
                }

                // Question 7: Target Completion Date
                item {
                    QuestionCard(
                        stepNumber = 7,
                        title = "Target Completion Date",
                        subtitle = "By when do you want to achieve this?",
                        icon = Icons.Default.CalendarToday,
                        value = targetCompletionDate,
                        onValueChange = { viewModel.targetCompletionDate.value = it },
                        placeholder = "e.g., May 2027"
                    )
                }

                // Question 8: Primary Motivation / "Why"
                item {
                    QuestionCard(
                        stepNumber = 8,
                        title = "Primary Motivation / \"Why\"",
                        subtitle = "What deeply drives you towards this dream?",
                        icon = Icons.Default.Favorite,
                        value = primaryMotivation,
                        onValueChange = { viewModel.primaryMotivation.value = it },
                        placeholder = "e.g., Build technology that impacts millions"
                    )
                }

                // Question 9: Current Skill Level (1-10)
                item {
                    QuestionCard(
                        stepNumber = 9,
                        title = "Current Skill Level (1-10)",
                        subtitle = "How would you rate your current preparation?",
                        icon = Icons.Default.Equalizer,
                        value = "$currentSkillLevel",
                        onValueChange = {},
                        isSlider = true,
                        sliderValue = currentSkillLevel.toFloat(),
                        onSliderChange = { viewModel.currentSkillLevel.value = it.toInt() }
                    )
                }

                // Question 10: Daily Hours Available
                item {
                    QuestionCard(
                        stepNumber = 10,
                        title = "Daily Hours Available",
                        subtitle = "How much dedicated time can you give each day?",
                        icon = Icons.Default.Schedule,
                        value = dailyHoursAvailable,
                        onValueChange = { viewModel.dailyHoursAvailable.value = it },
                        placeholder = "e.g., 4 hours/day"
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Bottom Continue Button
            Button(
                onClick = {
                    viewModel.saveOnboardingProfile {
                        onNavigateNext()
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(vertical = 4.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(PrimaryPurple, DarkPurpleText)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .testTag("onboarding_continue_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continue to Roadmap",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
