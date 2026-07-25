package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiService
import com.example.data.db.GoalPathDatabase
import com.example.data.model.DailyTask
import com.example.data.model.DreamProfile
import com.example.data.model.MonthlyGoal
import com.example.data.repository.GoalRepository
import com.example.notification.MotivationalNotificationHelper
import com.example.util.ExportHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ExportFormat { MARKDOWN, JSON }

data class ChatMessage(
    val sender: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class GoalPathViewModel(application: Application) : AndroidViewModel(application) {

    private val db = GoalPathDatabase.getInstance(application)
    private val repository = GoalRepository(db.dao())

    val dreamProfile: StateFlow<DreamProfile?> = repository.dreamProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allMonthlyGoals: StateFlow<List<MonthlyGoal>> = repository.allMonthlyGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDailyTasks: StateFlow<List<DailyTask>> = repository.allDailyTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cumulativeProgress: StateFlow<Int> = repository.cumulativeProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Onboarding Questions State (10 specific specs)
    val userName = MutableStateFlow("Arjun")
    val targetCollege = MutableStateFlow("MIT")
    val preferredCity = MutableStateFlow("San Francisco, CA")
    val dreamCompany = MutableStateFlow("Google")
    val targetCourse = MutableStateFlow("Computer Science & AI")
    val targetSalary = MutableStateFlow("$160,000 / Level 4")
    val keySkills = MutableStateFlow("Python, Data Structures, System Design")
    val targetCompletionDate = MutableStateFlow("May 2027")
    val primaryMotivation = MutableStateFlow("Build game-changing technology & secure top tier offers")
    val currentSkillLevel = MutableStateFlow(6) // 1 to 10
    val dailyHoursAvailable = MutableStateFlow("4 hours/day")

    // Roadmap Text State
    val roadmapText = MutableStateFlow("")

    // AI Status States
    val isGeneratingRoadmap = MutableStateFlow(false)
    val isGeneratingMonthlyGoals = MutableStateFlow(false)
    val isAnalyzingProgress = MutableStateFlow(false)
    val progressAnalysisResult = MutableStateFlow<String?>(null)
    val aiMessage = MutableStateFlow<String?>(null)
    val exportSuccessMessage = MutableStateFlow<String?>(null)

    // Mentor Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    val isChatLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            dreamProfile.filterNotNull().first().let { profile ->
                userName.value = profile.userName
                targetCollege.value = profile.targetCollege
                preferredCity.value = profile.preferredCity
                dreamCompany.value = profile.dreamCompany
                targetCourse.value = profile.targetCourse
                targetSalary.value = profile.targetSalary
                keySkills.value = profile.keySkills
                targetCompletionDate.value = profile.targetCompletionDate
                primaryMotivation.value = profile.primaryMotivation
                currentSkillLevel.value = profile.currentSkillLevel
                dailyHoursAvailable.value = profile.dailyHoursAvailable
                roadmapText.value = profile.masterRoadmap
            }
            MotivationalNotificationHelper.scheduleDailyNotification(getApplication())
        }
    }

    fun triggerTestNotification() {
        val currentWhy = primaryMotivation.value
        MotivationalNotificationHelper.sendMotivationalNotification(getApplication(), currentWhy)
    }

    // Save Onboarding Questionnaire answers
    fun saveOnboardingProfile(onComplete: () -> Unit) {
        viewModelScope.launch {
            val updated = DreamProfile(
                id = 1,
                userName = userName.value,
                targetCollege = targetCollege.value,
                preferredCity = preferredCity.value,
                dreamCompany = dreamCompany.value,
                targetCourse = targetCourse.value,
                targetSalary = targetSalary.value,
                keySkills = keySkills.value,
                targetCompletionDate = targetCompletionDate.value,
                primaryMotivation = primaryMotivation.value,
                currentSkillLevel = currentSkillLevel.value,
                dailyHoursAvailable = dailyHoursAvailable.value,
                masterRoadmap = roadmapText.value,
                isOnboardingCompleted = true
            )
            repository.saveProfile(updated)
            MotivationalNotificationHelper.scheduleDailyNotification(getApplication())
            onComplete()
        }
    }

    // Generate Roadmap using AI based on questionnaire
    fun generateAIRoadmap() {
        viewModelScope.launch {
            isGeneratingRoadmap.value = true
            val profile = buildCurrentProfileFromInputs()
            val result = GeminiService.generateMasterRoadmap(profile)
            roadmapText.value = result
            isGeneratingRoadmap.value = false
            aiMessage.value = "AI Master Roadmap successfully generated!"
        }
    }

    // Save Master Journey (Onboarding Data + Roadmap)
    fun startJourney(onComplete: () -> Unit) {
        viewModelScope.launch {
            val profile = buildCurrentProfileFromInputs().copy(isOnboardingCompleted = true)
            repository.saveProfile(profile)
            onComplete()
        }
    }

    // Auto Generate Monthly Timetable via AI
    fun generateMonthlyTimetableWithAI(monthYear: String = "May 2026") {
        viewModelScope.launch {
            isGeneratingMonthlyGoals.value = true
            val profile = buildCurrentProfileFromInputs()
            val newGoals = GeminiService.generateMonthlyTimetable(profile, monthYear)
            newGoals.forEach { goal ->
                repository.addMonthlyGoal(goal)
            }
            isGeneratingMonthlyGoals.value = false
            aiMessage.value = "Monthly action plan generated!"
        }
    }

    // Add manual Goal
    fun addMonthlyGoal(title: String, weightage: Float, categoryIcon: String, monthYear: String = "May 2026") {
        viewModelScope.launch {
            val newGoal = MonthlyGoal(
                monthYear = monthYear,
                title = title,
                weightage = weightage,
                progressPercentage = 0f,
                categoryIcon = categoryIcon
            )
            repository.addMonthlyGoal(newGoal)
        }
    }

    // Toggle Goal status
    fun toggleGoalCompletion(goal: MonthlyGoal) {
        viewModelScope.launch {
            repository.toggleGoalCompletion(goal)
        }
    }

    // Log Focus Time toward Goal
    fun logFocusTime(goal: MonthlyGoal, minutesLogged: Int) {
        viewModelScope.launch {
            val progressBoost = ((minutesLogged.toFloat() / 25f) * 10f).coerceAtLeast(5f)
            val newProgress = (goal.progressPercentage + progressBoost).coerceAtMost(100f)
            val isDone = newProgress >= 100f
            val updated = goal.copy(
                progressPercentage = newProgress,
                isCompleted = isDone,
                notes = if (goal.notes.isBlank()) "Logged $minutesLogged min Pomodoro focus" else "${goal.notes}\n• Logged $minutesLogged min Pomodoro focus"
            )
            repository.updateMonthlyGoal(updated)
            aiMessage.value = "🎯 Focus session complete! Logged $minutesLogged mins toward '${goal.title}' (+${progressBoost.toInt()}% Progress)"
        }
    }

    // Delete Goal
    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            repository.deleteMonthlyGoal(goalId)
        }
    }

    // Add Daily Task under Goal
    fun addDailyTask(goalId: Long, taskTitle: String, dayOfWeek: String = "Today") {
        viewModelScope.launch {
            val task = DailyTask(monthlyGoalId = goalId, taskTitle = taskTitle, dayOfWeek = dayOfWeek)
            repository.addDailyTask(task)
        }
    }

    // Toggle Daily Task completion
    fun toggleTaskCompletion(task: DailyTask, goal: MonthlyGoal) {
        viewModelScope.launch {
            val goalTasks = repository.allDailyTasks.first().filter { it.monthlyGoalId == goal.id }
            repository.toggleTaskCompletion(task, goalTasks, goal)
        }
    }

    // Delete Task
    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            db.dao().deleteDailyTaskById(taskId)
        }
    }

    // Chat with PathAI Mentor
    fun sendMessageToMentor(userText: String) {
        if (userText.isBlank()) return
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(ChatMessage("user", userText))
        _chatMessages.value = currentList
        isChatLoading.value = true

        viewModelScope.launch {
            val profile = buildCurrentProfileFromInputs()
            val history = currentList.map { Pair(it.sender, it.text) }
            val replyText = GeminiService.chatWithMentor(history, userText, profile)
            val updatedList = _chatMessages.value.toMutableList()
            updatedList.add(ChatMessage("model", replyText))
            _chatMessages.value = updatedList
            isChatLoading.value = false
        }
    }

    private fun buildCurrentProfileFromInputs(): DreamProfile {
        return DreamProfile(
            id = 1,
            userName = userName.value,
            targetCollege = targetCollege.value,
            preferredCity = preferredCity.value,
            dreamCompany = dreamCompany.value,
            targetCourse = targetCourse.value,
            targetSalary = targetSalary.value,
            keySkills = keySkills.value,
            targetCompletionDate = targetCompletionDate.value,
            primaryMotivation = primaryMotivation.value,
            currentSkillLevel = currentSkillLevel.value,
            dailyHoursAvailable = dailyHoursAvailable.value,
            masterRoadmap = roadmapText.value,
            isOnboardingCompleted = true
        )
    }

    fun clearAiMessage() {
        aiMessage.value = null
    }

    fun exportData(format: ExportFormat, onCompleted: (String) -> Unit) {
        viewModelScope.launch {
            val profile = dreamProfile.value ?: buildCurrentProfileFromInputs()
            val goals = allMonthlyGoals.value
            val tasks = allDailyTasks.value
            val progress = cumulativeProgress.value

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val extension = if (format == ExportFormat.MARKDOWN) "md" else "json"
            val fileName = "GoalPath_Export_$timeStamp.$extension"

            val content = if (format == ExportFormat.MARKDOWN) {
                ExportHelper.generateMarkdownExport(profile, goals, tasks, progress)
            } else {
                ExportHelper.generateJsonExport(profile, goals, tasks, progress)
            }

            val savedFile = ExportHelper.saveExportFile(getApplication(), content, fileName)
            val resultMsg = if (savedFile != null) {
                "Saved locally to ${savedFile.name} (${savedFile.parentFile?.name})"
            } else {
                "Saved export file locally."
            }
            exportSuccessMessage.value = resultMsg
            onCompleted(content)
        }
    }

    fun shareExportData(format: ExportFormat) {
        viewModelScope.launch {
            val profile = dreamProfile.value ?: buildCurrentProfileFromInputs()
            val goals = allMonthlyGoals.value
            val tasks = allDailyTasks.value
            val progress = cumulativeProgress.value

            val content = if (format == ExportFormat.MARKDOWN) {
                ExportHelper.generateMarkdownExport(profile, goals, tasks, progress)
            } else {
                ExportHelper.generateJsonExport(profile, goals, tasks, progress)
            }

            val title = if (format == ExportFormat.MARKDOWN) "GoalPath Roadmap Markdown Export" else "GoalPath Roadmap JSON Export"
            ExportHelper.shareExportContent(getApplication(), content, title)
        }
    }

    fun clearExportMessage() {
        exportSuccessMessage.value = null
    }

    fun analyzeProgressWithAI() {
        val goals = allMonthlyGoals.value
        val profile = dreamProfile.value ?: buildCurrentProfileFromInputs()
        isAnalyzingProgress.value = true
        viewModelScope.launch {
            try {
                val result = GeminiService.analyzeGoalProgress(profile, goals)
                progressAnalysisResult.value = result
            } catch (e: Exception) {
                progressAnalysisResult.value = "Failed to analyze progress: ${e.message}"
            } finally {
                isAnalyzingProgress.value = false
            }
        }
    }

    fun clearProgressAnalysis() {
        progressAnalysisResult.value = null
    }
}
