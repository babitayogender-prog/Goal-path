package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.data.model.DailyTask
import com.example.data.model.DreamProfile
import com.example.data.model.MonthlyGoal
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportHelper {

    fun generateMarkdownExport(
        profile: DreamProfile?,
        monthlyGoals: List<MonthlyGoal>,
        dailyTasks: List<DailyTask>,
        progressPercent: Int
    ): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        return buildString {
            appendLine("# GoalPath - Master Roadmap & Progress Export")
            appendLine("*Exported on: $currentDate*")
            appendLine()
            appendLine("## 🎯 Dream Profile & Blueprint")
            appendLine("- **User Name**: ${profile?.userName ?: "N/A"}")
            appendLine("- **Target College**: ${profile?.targetCollege ?: "N/A"}")
            appendLine("- **Dream Company**: ${profile?.dreamCompany ?: "N/A"}")
            appendLine("- **Target Course**: ${profile?.targetCourse ?: "N/A"}")
            appendLine("- **Target Salary**: ${profile?.targetSalary ?: "N/A"}")
            appendLine("- **Key Skills**: ${profile?.keySkills ?: "N/A"}")
            appendLine("- **Target Completion Date**: ${profile?.targetCompletionDate ?: "N/A"}")
            appendLine("- **Skill Level**: ${profile?.currentSkillLevel ?: 1} / 10")
            appendLine("- **Daily Hours Available**: ${profile?.dailyHoursAvailable ?: "N/A"}")
            appendLine("- **Primary Motivation ('Why')**: ${profile?.primaryMotivation ?: "N/A"}")
            appendLine("- **Cumulative Progress**: $progressPercent%")
            appendLine()
            appendLine("## 🗺️ Master Roadmap")
            if (!profile?.masterRoadmap.isNullOrEmpty()) {
                appendLine(profile?.masterRoadmap)
            } else {
                appendLine("*No master roadmap generated yet.*")
            }
            appendLine()
            appendLine("## 📅 Monthly Goals & Action Plan")
            if (monthlyGoals.isNotEmpty()) {
                monthlyGoals.forEach { goal ->
                    val status = if (goal.isCompleted) "[x]" else "[ ]"
                    appendLine("### $status ${goal.title} (${goal.monthYear})")
                    appendLine("- **Weightage**: ${goal.weightage}%")
                    appendLine("- **Progress**: ${goal.progressPercentage.toInt()}%")
                    if (goal.notes.isNotBlank()) {
                        appendLine("- **Notes**: ${goal.notes}")
                    }
                    appendLine()
                }
            } else {
                appendLine("*No monthly goals defined.*")
            }
            appendLine()
            appendLine("## 📝 Daily Tasks")
            if (dailyTasks.isNotEmpty()) {
                dailyTasks.forEach { task ->
                    val status = if (task.isCompleted) "[x]" else "[ ]"
                    appendLine("- $status **${task.taskTitle}** (${task.estimatedMinutes} mins, ${task.dayOfWeek})")
                }
            } else {
                appendLine("*No daily tasks scheduled.*")
            }
        }
    }

    fun generateJsonExport(
        profile: DreamProfile?,
        monthlyGoals: List<MonthlyGoal>,
        dailyTasks: List<DailyTask>,
        progressPercent: Int
    ): String {
        val rootJson = JSONObject()
        val profileJson = JSONObject().apply {
            put("userName", profile?.userName ?: "")
            put("targetCollege", profile?.targetCollege ?: "")
            put("dreamCompany", profile?.dreamCompany ?: "")
            put("targetCourse", profile?.targetCourse ?: "")
            put("targetSalary", profile?.targetSalary ?: "")
            put("keySkills", profile?.keySkills ?: "")
            put("targetCompletionDate", profile?.targetCompletionDate ?: "")
            put("primaryMotivation", profile?.primaryMotivation ?: "")
            put("currentSkillLevel", profile?.currentSkillLevel ?: 1)
            put("dailyHoursAvailable", profile?.dailyHoursAvailable ?: "")
        }

        val goalsArray = JSONArray()
        monthlyGoals.forEach { goal ->
            val goalObj = JSONObject().apply {
                put("id", goal.id)
                put("title", goal.title)
                put("monthYear", goal.monthYear)
                put("weightage", goal.weightage)
                put("progressPercentage", goal.progressPercentage)
                put("isCompleted", goal.isCompleted)
                put("categoryIcon", goal.categoryIcon)
                put("notes", goal.notes)
            }
            goalsArray.put(goalObj)
        }

        val tasksArray = JSONArray()
        dailyTasks.forEach { task ->
            val taskObj = JSONObject().apply {
                put("id", task.id)
                put("monthlyGoalId", task.monthlyGoalId)
                put("taskTitle", task.taskTitle)
                put("dayOfWeek", task.dayOfWeek)
                put("isCompleted", task.isCompleted)
                put("estimatedMinutes", task.estimatedMinutes)
            }
            tasksArray.put(taskObj)
        }

        rootJson.put("exportDate", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()))
        rootJson.put("cumulativeProgressPercent", progressPercent)
        rootJson.put("profile", profileJson)
        rootJson.put("masterRoadmap", profile?.masterRoadmap ?: "")
        rootJson.put("monthlyGoals", goalsArray)
        rootJson.put("dailyTasks", tasksArray)

        return rootJson.toString(2)
    }

    fun saveExportFile(context: Context, content: String, fileName: String): File? {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { stream ->
                stream.write(content.toByteArray())
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to external files dir or internal files dir
            try {
                val fallbackDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
                val fallbackFile = File(fallbackDir, fileName)
                FileOutputStream(fallbackFile).use { stream ->
                    stream.write(content.toByteArray())
                }
                fallbackFile
            } catch (ex: Exception) {
                ex.printStackTrace()
                null
            }
        }
    }

    fun shareExportContent(context: Context, content: String, title: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooser = Intent.createChooser(shareIntent, "Export Roadmap & Progress Data")
        chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooser)
    }
}
