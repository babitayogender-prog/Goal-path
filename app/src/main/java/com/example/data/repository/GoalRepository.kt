package com.example.data.repository

import com.example.data.db.GoalPathDao
import com.example.data.model.DailyTask
import com.example.data.model.DreamProfile
import com.example.data.model.MonthlyGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoalRepository(private val dao: GoalPathDao) {

    val dreamProfile: Flow<DreamProfile?> = dao.getDreamProfile()
    val allMonthlyGoals: Flow<List<MonthlyGoal>> = dao.getAllMonthlyGoals()
    val allDailyTasks: Flow<List<DailyTask>> = dao.getAllDailyTasks()

    // Real-time calculation of overall cumulative progress towards dream
    val cumulativeProgress: Flow<Int> = allMonthlyGoals.map { goals ->
        if (goals.isEmpty()) return@map 0
        var weightedSum = 0f
        var totalWeight = 0f
        goals.forEach { goal ->
            weightedSum += (goal.weightage * (goal.progressPercentage / 100f))
            totalWeight += goal.weightage
        }
        if (totalWeight <= 0f) 0
        else {
            val ratio = if (totalWeight > 100f) weightedSum / totalWeight else weightedSum / 100f
            (ratio * 100f).coerceIn(0f, 100f).toInt()
        }
    }

    suspend fun saveProfile(profile: DreamProfile) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun addMonthlyGoal(goal: MonthlyGoal): Long {
        return dao.insertMonthlyGoal(goal)
    }

    suspend fun updateMonthlyGoal(goal: MonthlyGoal) {
        dao.updateMonthlyGoal(goal)
    }

    suspend fun deleteMonthlyGoal(id: Long) {
        dao.deleteTasksByGoalId(id)
        dao.deleteMonthlyGoalById(id)
    }

    suspend fun toggleGoalCompletion(goal: MonthlyGoal) {
        val newIsCompleted = !goal.isCompleted
        val newProgress = if (newIsCompleted) 100f else 0f
        dao.updateMonthlyGoal(
            goal.copy(isCompleted = newIsCompleted, progressPercentage = newProgress)
        )
    }

    suspend fun addDailyTask(task: DailyTask) {
        dao.insertDailyTask(task)
    }

    suspend fun updateDailyTask(task: DailyTask) {
        dao.updateDailyTask(task)
    }

    suspend fun toggleTaskCompletion(task: DailyTask, allGoalTasks: List<DailyTask>, goal: MonthlyGoal) {
        val newStatus = !task.isCompleted
        val updatedTask = task.copy(isCompleted = newStatus)
        dao.updateDailyTask(updatedTask)

        // Automatically update parent goal's progress percentage based on task completion ratio
        val currentTasksList = allGoalTasks.map { if (it.id == task.id) updatedTask else it }
        if (currentTasksList.isNotEmpty()) {
            val completedCount = currentTasksList.count { it.isCompleted }
            val newProgress = (completedCount.toFloat() / currentTasksList.size.toFloat()) * 100f
            val isGoalFullyDone = completedCount == currentTasksList.size
            dao.updateMonthlyGoal(
                goal.copy(progressPercentage = newProgress, isCompleted = isGoalFullyDone)
            )
        }
    }

    suspend fun seedInitialDataIfEmpty() {
        // Seeds initial sample dream blueprint & monthly goals if database is empty
        val initialProfile = DreamProfile(
            id = 1,
            userName = "Arjun",
            targetCollege = "MIT",
            preferredCity = "San Francisco, CA",
            dreamCompany = "Google",
            targetCourse = "Computer Science & AI",
            targetSalary = "$160,000 / Level 4",
            keySkills = "Python, Data Structures, System Design, Web Development",
            targetCompletionDate = "May 2027",
            primaryMotivation = "Pioneer impactful AI tools and secure top engineering roles",
            currentSkillLevel = 6,
            dailyHoursAvailable = "4 hours/day",
            masterRoadmap = """
# My Master Dream Roadmap

## Phase 1: Foundation Building
- Build core programming skills & CS fundamentals
- Learn Python Basics & Object-Oriented Principles
- Improve Technical Communication & Problem Solving

## Phase 2: Core Data Structures & Web
- Master Data Structures & Algorithms (Trees, Graphs, DP)
- Build 3 full-stack portfolio web projects
- Active GitHub contributions & open source

## Phase 3: Advanced System Design & AI
- Study Distributed Systems & High-Scale Architecture
- Deep dive into Machine Learning & Gemini AI APIs
- Prepare for top tech company coding rounds

## Phase 4: Placement & Dream Hiring
- Resume Optimization & Portfolio Presentation
- Mock Technical Interviews & Behavioral prep
- Secure Google Software Engineering offer!
            """.trimIndent(),
            isOnboardingCompleted = true
        )
        dao.insertOrUpdateProfile(initialProfile)

        // Seed 5 sample monthly goals
        val g1 = dao.insertMonthlyGoal(
            MonthlyGoal(
                monthYear = "May 2026",
                title = "Learn Python Basics",
                weightage = 25f,
                progressPercentage = 100f,
                isCompleted = true,
                categoryIcon = "code"
            )
        )
        val g2 = dao.insertMonthlyGoal(
            MonthlyGoal(
                monthYear = "May 2026",
                title = "Data Structures & Algorithms",
                weightage = 25f,
                progressPercentage = 60f,
                isCompleted = false,
                categoryIcon = "database"
            )
        )
        val g3 = dao.insertMonthlyGoal(
            MonthlyGoal(
                monthYear = "May 2026",
                title = "Web Development & APIs",
                weightage = 20f,
                progressPercentage = 40f,
                isCompleted = false,
                categoryIcon = "web"
            )
        )
        val g4 = dao.insertMonthlyGoal(
            MonthlyGoal(
                monthYear = "May 2026",
                title = "Build 2 Showcase Projects",
                weightage = 15f,
                progressPercentage = 20f,
                isCompleted = false,
                categoryIcon = "folder"
            )
        )
        val g5 = dao.insertMonthlyGoal(
            MonthlyGoal(
                monthYear = "May 2026",
                title = "Soft Skills & Aptitude",
                weightage = 15f,
                progressPercentage = 0f,
                isCompleted = false,
                categoryIcon = "user"
            )
        )

        // Seed sample daily tasks
        dao.insertDailyTask(DailyTask(monthlyGoalId = g2, taskTitle = "Solve 2 Binary Tree problems on LeetCode", dayOfWeek = "Today", isCompleted = true))
        dao.insertDailyTask(DailyTask(monthlyGoalId = g2, taskTitle = "Review Graph Traversal (BFS & DFS)", dayOfWeek = "Today", isCompleted = false))
        dao.insertDailyTask(DailyTask(monthlyGoalId = g3, taskTitle = "Implement RESTful endpoint in Ktor/Retrofit", dayOfWeek = "Tomorrow", isCompleted = false))
    }
}
