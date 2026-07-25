package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dream_profile")
data class DreamProfile(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Achiever",
    val targetCollege: String = "",
    val preferredCity: String = "",
    val dreamCompany: String = "",
    val targetCourse: String = "",
    val targetSalary: String = "",
    val keySkills: String = "",
    val targetCompletionDate: String = "",
    val primaryMotivation: String = "",
    val currentSkillLevel: Int = 5,
    val dailyHoursAvailable: String = "3 hours/day",
    val masterRoadmap: String = "",
    val isOnboardingCompleted: Boolean = false
)
