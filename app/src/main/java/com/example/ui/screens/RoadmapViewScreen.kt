package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ExportCard
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleGlow
import com.example.ui.viewmodel.GoalPathViewModel

@Composable
fun RoadmapViewScreen(
    viewModel: GoalPathViewModel,
    onEditRoadmapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dreamProfile by viewModel.dreamProfile.collectAsState()
    val roadmapText by viewModel.roadmapText.collectAsState()

    val targetCollege = dreamProfile?.targetCollege?.ifBlank { "Target University" } ?: "Target University"
    val dreamCompany = dreamProfile?.dreamCompany?.ifBlank { "Dream Company" } ?: "Dream Company"

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
                        text = "Master Strategy",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Dream Roadmap",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                IconButton(onClick = onEditRoadmapClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Roadmap",
                        tint = PrimaryPurple
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("roadmap_view_screen")
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Target Badge Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Path to $targetCollege & $dreamCompany",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Target Completion: ${dreamProfile?.targetCompletionDate ?: "May 2027"}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Formatted Roadmap Markdown Lines
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        if (roadmapText.isBlank()) {
                            Text(
                                text = "No roadmap created yet. Click the edit button at the top right or generate one with AI!",
                                color = MaterialTheme.colorScheme.outlineVariant,
                                fontSize = 14.sp
                            )
                        } else {
                            val lines = roadmapText.split("\n")
                            lines.forEach { line ->
                                when {
                                    line.startsWith("# ") -> {
                                        Text(
                                            text = line.removePrefix("# "),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                                        )
                                    }
                                    line.startsWith("## ") -> {
                                        Text(
                                            text = line.removePrefix("## "),
                                            color = PrimaryPurple,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                                        )
                                    }
                                    line.trim().startsWith("- ") || line.trim().startsWith("* ") -> {
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "• ",
                                                color = PrimaryPurple,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = line.trim().removePrefix("- ").removePrefix("* "),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 14.sp,
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                    else -> {
                                        if (line.isNotBlank()) {
                                            Text(
                                                text = line,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 13.sp,
                                                lineHeight = 19.sp,
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Export Roadmap & Progress Card
            item {
                ExportCard(viewModel = viewModel)
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}
