package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ExportCard
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleGlow
import com.example.ui.viewmodel.GoalPathViewModel

@Composable
fun ProfileScreen(
    viewModel: GoalPathViewModel,
    onEditDreamQuestionnaire: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dreamProfile by viewModel.dreamProfile.collectAsState()

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Dream Profile",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                IconButton(onClick = onEditDreamQuestionnaire) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Questionnaire",
                        tint = PrimaryPurple
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("profile_screen")
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // User Header Avatar Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(PrimaryPurple)
                        ) {
                            Text(
                                text = (dreamProfile?.userName?.take(1) ?: "A").uppercase(),
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = dreamProfile?.userName ?: "Arjun",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Targeting ${dreamProfile?.targetCollege ?: "College"} & ${dreamProfile?.dreamCompany ?: "Company"}",
                                color = PrimaryPurple,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Specs List
            item {
                Text(
                    text = "Blueprint Answers (10 Questions)",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            item {
                ProfileSpecRow(
                    label = "1. Target College",
                    value = dreamProfile?.targetCollege?.ifBlank { "Not set" } ?: "Not set",
                    icon = Icons.Default.AccountBalance
                )
            }

            item {
                ProfileSpecRow(
                    label = "2. Preferred Location",
                    value = dreamProfile?.preferredCity?.ifBlank { "Not set" } ?: "Not set",
                    icon = Icons.Default.LocationOn
                )
            }

            item {
                ProfileSpecRow(
                    label = "3. Dream Company / Role",
                    value = dreamProfile?.dreamCompany?.ifBlank { "Not set" } ?: "Not set",
                    icon = Icons.Default.Work
                )
            }

            item {
                ProfileSpecRow(
                    label = "4. Target Course / Major",
                    value = dreamProfile?.targetCourse?.ifBlank { "Not set" } ?: "Not set",
                    icon = Icons.Default.Book
                )
            }

            item {
                ProfileSpecRow(
                    label = "5. Starting Salary / Level",
                    value = dreamProfile?.targetSalary?.ifBlank { "Not set" } ?: "Not set",
                    icon = Icons.Default.AttachMoney
                )
            }

            item {
                ProfileSpecRow(
                    label = "6. Key Skills Needed",
                    value = dreamProfile?.keySkills?.ifBlank { "Not set" } ?: "Not set",
                    icon = Icons.Default.Star
                )
            }

            item {
                ProfileSpecRow(
                    label = "7. Target Completion Date",
                    value = dreamProfile?.targetCompletionDate?.ifBlank { "Not set" } ?: "Not set",
                    icon = Icons.Default.CalendarToday
                )
            }

            item {
                ProfileSpecRow(
                    label = "8. Primary Motivation",
                    value = dreamProfile?.primaryMotivation?.ifBlank { "Not set" } ?: "Not set",
                    icon = Icons.Default.Favorite
                )
            }

            item {
                ProfileSpecRow(
                    label = "9. Skill Rating",
                    value = "${dreamProfile?.currentSkillLevel ?: 5} / 10",
                    icon = Icons.Default.Equalizer
                )
            }

            item {
                ProfileSpecRow(
                    label = "10. Daily Hours Available",
                    value = dreamProfile?.dailyHoursAvailable?.ifBlank { "Not set" } ?: "Not set",
                    icon = Icons.Default.Schedule
                )
            }

            // Daily Motivational Push Notification Card
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PrimaryPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Daily Motivational Push",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sends a motivational push notification once per day based on your 'Why' motivation statement:",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "\"${dreamProfile?.primaryMotivation?.ifBlank { "Pioneer impactful AI tools and secure top engineering roles" } ?: "Pioneer impactful AI tools and secure top engineering roles"}\"",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.triggerTestNotification() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Test Push Notification Now",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Export Roadmap & Progress Data
            item {
                Spacer(modifier = Modifier.height(12.dp))
                ExportCard(viewModel = viewModel)
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onEditDreamQuestionnaire,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PrimaryPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Text("Re-take Dream Questionnaire", color = PrimaryPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun ProfileSpecRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
