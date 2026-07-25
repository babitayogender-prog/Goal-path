package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_goals")
data class MonthlyGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthYear: String = "May 2026",
    val title: String,
    val weightage: Float = 20.0f, // Target completion percentage weightage e.g. 25%
    val progressPercentage: Float = 0.0f, // 0 to 100
    val isCompleted: Boolean = false,
    val categoryIcon: String = "code",
    val notes: String = ""
)
