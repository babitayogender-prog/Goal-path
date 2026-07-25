package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_tasks")
data class DailyTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthlyGoalId: Long,
    val taskTitle: String,
    val dayOfWeek: String = "Today",
    val isCompleted: Boolean = false,
    val estimatedMinutes: Int = 30
)
