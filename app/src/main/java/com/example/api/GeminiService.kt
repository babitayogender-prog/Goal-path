package com.example.api

import com.example.BuildConfig
import com.example.data.model.DreamProfile
import com.example.data.model.MonthlyGoal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun generateMasterRoadmap(profile: DreamProfile): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackRoadmap(profile)
        }

        val prompt = """
            You are a premier career and college roadmap strategist.
            Create a highly detailed, inspiring master long-term roadmap in clean Markdown format for the following user dream blueprint:
            - Target College: ${profile.targetCollege}
            - Preferred City: ${profile.preferredCity}
            - Dream Company / Role: ${profile.dreamCompany}
            - Target Course: ${profile.targetCourse}
            - Starting Salary Level: ${profile.targetSalary}
            - Key Skills Needed: ${profile.keySkills}
            - Target Completion Date: ${profile.targetCompletionDate}
            - Motivation: ${profile.primaryMotivation}
            - Current Skill Level: ${profile.currentSkillLevel}/10
            - Daily Available Hours: ${profile.dailyHoursAvailable}

            Format the output with Markdown section headers (## Phase 1: Foundation, ## Phase 2: Skill Building, ## Phase 3: Advanced, ## Phase 4: Final Placement) with bullet points and clear milestone action items.
        """.trimIndent()

        try {
            val responseText = callGeminiApi(apiKey, prompt)
            if (responseText.isNotBlank()) responseText else generateFallbackRoadmap(profile)
        } catch (e: Exception) {
            generateFallbackRoadmap(profile)
        }
    }

    suspend fun generateMonthlyTimetable(
        profile: DreamProfile,
        monthYear: String
    ): List<MonthlyGoal> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackMonthlyGoals(monthYear)
        }

        val prompt = """
            Create a monthly action plan for $monthYear to achieve the dream:
            Target College: ${profile.targetCollege} | Dream Role: ${profile.dreamCompany}
            Key Skills: ${profile.keySkills}
            Master Roadmap:
            ${profile.masterRoadmap.take(800)}

            Return a valid JSON array of 4 to 5 monthly goals.
            Each object MUST have:
            - "title": (String, concise goal name e.g. "Master Data Structures")
            - "weightage": (Number, integer percentage float weight e.g. 25.0, 20.0, sum of all items should equal 100)
            - "categoryIcon": (String, one of: "code", "database", "web", "folder", "user", "star", "book")

            ONLY return valid raw JSON array, without markdown backticks.
        """.trimIndent()

        try {
            val responseText = callGeminiApi(apiKey, prompt)
            val jsonArrayString = responseText.replace("```json", "").replace("```", "").trim()
            val jsonArray = JSONArray(jsonArrayString)
            val list = mutableListOf<MonthlyGoal>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val title = obj.optString("title", "Goal ${i + 1}")
                val weightage = obj.optDouble("weightage", 20.0).toFloat()
                val icon = obj.optString("categoryIcon", "code")
                list.add(
                    MonthlyGoal(
                        monthYear = monthYear,
                        title = title,
                        weightage = weightage,
                        progressPercentage = 0f,
                        isCompleted = false,
                        categoryIcon = icon
                    )
                )
            }
            if (list.isNotEmpty()) list else generateFallbackMonthlyGoals(monthYear)
        } catch (e: Exception) {
            generateFallbackMonthlyGoals(monthYear)
        }
    }

    suspend fun chatWithMentor(
        conversationHistory: List<Pair<String, String>>, // Pair(role "user"|"model", text)
        userMessage: String,
        profile: DreamProfile
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "I am your PathAI Mentor! To achieve your dream of getting into ${profile.targetCollege.ifBlank { "your target university" }} and hired at ${profile.dreamCompany.ifBlank { "your dream company" }}, focus on mastering ${profile.keySkills.ifBlank { "core fundamentals" }} daily for ${profile.dailyHoursAvailable}. (Tip: Add your Gemini API Key in AI Studio Secrets for live custom AI chat responses!)"
        }

        val systemPrompt = """
            You are PathAI Mentor, a world-class academic advisor and career strategist in the GoalPath app.
            You are helping ${profile.userName} reach their dream:
            - College: ${profile.targetCollege}
            - Role: ${profile.dreamCompany}
            - Course: ${profile.targetCourse}
            - Skills: ${profile.keySkills}
            - Target Completion: ${profile.targetCompletionDate}
            - Daily Hours: ${profile.dailyHoursAvailable}

            Be encouraging, highly specific, actionable, and structured. Use short bullet points when providing advice.
        """.trimIndent()

        try {
            val contentsArray = JSONArray()

            // System instruction or initial context
            val systemPart = JSONObject().put("text", systemPrompt)
            val systemContent = JSONObject().put("role", "user").put("parts", JSONArray().put(systemPart))
            contentsArray.put(systemContent)
            val systemRespPart = JSONObject().put("text", "Understood! I am ready to guide ${profile.userName} step-by-step toward ${profile.targetCollege} and ${profile.dreamCompany}.")
            val systemRespContent = JSONObject().put("role", "model").put("parts", JSONArray().put(systemRespPart))
            contentsArray.put(systemRespContent)

            // Past conversation
            conversationHistory.takeLast(6).forEach { (role, text) ->
                val partObj = JSONObject().put("text", text)
                val contentObj = JSONObject().put("role", role).put("parts", JSONArray().put(partObj))
                contentsArray.put(contentObj)
            }

            // Current message
            val currentPart = JSONObject().put("text", userMessage)
            val currentContent = JSONObject().put("role", "user").put("parts", JSONArray().put(currentPart))
            contentsArray.put(currentContent)

            val requestBodyJson = JSONObject().put("contents", contentsArray)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

            val url = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext "PathAI Mentor: Let's focus on your goal of ${profile.targetCollege} and ${profile.dreamCompany}! Keep practicing ${profile.keySkills}."
            }

            val jsonObject = JSONObject(responseBody)
            val candidates = jsonObject.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            text ?: "PathAI Mentor is here for you! Keep working toward ${profile.dreamCompany}!"
        } catch (e: Exception) {
            "PathAI Mentor: Focus on your core milestones today. You've got this!"
        }
    }

    private fun callGeminiApi(apiKey: String, prompt: String): String {
        val jsonPart = JSONObject().put("text", prompt)
        val jsonParts = JSONArray().put(jsonPart)
        val jsonContent = JSONObject().put("parts", jsonParts)
        val jsonContents = JSONArray().put(jsonContent)
        val requestBodyJson = JSONObject().put("contents", jsonContents)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

        val url = "$BASE_URL?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        if (!response.isSuccessful) return ""

        val jsonObject = JSONObject(responseBody)
        val candidates = jsonObject.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        return parts?.optJSONObject(0)?.optString("text") ?: ""
    }

    private fun generateFallbackRoadmap(profile: DreamProfile): String {
        val college = profile.targetCollege.ifBlank { "Target University" }
        val company = profile.dreamCompany.ifBlank { "Dream Company" }
        val skills = profile.keySkills.ifBlank { "Core Programming, Data Structures, System Design" }
        return """
# Master GoalPath Roadmap to $college & $company

## Phase 1: Foundation & Essentials
- Master core principles of $skills
- Complete daily practice (${profile.dailyHoursAvailable})
- Build clean habits & problem-solving frameworks

## Phase 2: Deep Skill Acquisition & Projects
- Build 3 full-stack portfolio applications relevant to $company
- Learn advanced algorithms, system architecture & database optimizations
- Collaborate on open source or college competitive events

## Phase 3: Targeted $company & $college Prep
- Practice mock interviews & technical whiteboard challenges
- Network with alumni from $college and engineers at $company
- Optimize resume and GitHub portfolio showcase

## Phase 4: Final Sprint & Offer
- Complete final evaluation rounds
- Apply and interview confidently
- Secure admission to $college & land offer at $company!
        """.trimIndent()
    }

    private fun generateFallbackMonthlyGoals(monthYear: String): List<MonthlyGoal> {
        return listOf(
            MonthlyGoal(
                monthYear = monthYear,
                title = "Learn Core Concepts & Syntax",
                weightage = 25f,
                progressPercentage = 0f,
                categoryIcon = "code"
            ),
            MonthlyGoal(
                monthYear = monthYear,
                title = "Data Structures & Algorithms",
                weightage = 25f,
                progressPercentage = 0f,
                categoryIcon = "database"
            ),
            MonthlyGoal(
                monthYear = monthYear,
                title = "Build Hands-on Project",
                weightage = 25f,
                progressPercentage = 0f,
                categoryIcon = "folder"
            ),
            MonthlyGoal(
                monthYear = monthYear,
                title = "Interview Prep & Portfolio",
                weightage = 25f,
                progressPercentage = 0f,
                categoryIcon = "star"
            )
        )
    }

    suspend fun analyzeGoalProgress(
        profile: DreamProfile,
        goals: List<MonthlyGoal>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackAnalysis(goals, profile)
        }

        val goalsInfo = goals.joinToString("\n") {
            "- ${it.title}: Progress: ${it.progressPercentage.toInt()}% | Weightage: ${it.weightage}% | Completed: ${it.isCompleted}"
        }

        val prompt = """
            You are a premier career and milestone strategist.
            Analyze the user's progress toward their monthly goals for their dream profile:
            - Name: ${profile.userName}
            - Target College: ${profile.targetCollege}
            - Dream Company/Role: ${profile.dreamCompany}
            - Current Skill Level: ${profile.currentSkillLevel}/10
            - Daily Available Hours: ${profile.dailyHoursAvailable}
            - Primary Motivation ('Why'): ${profile.primaryMotivation}
            
            Here are their current monthly goals and progress:
            $goalsInfo

            Please provide a friendly, professional analysis:
            1. **Overall Progress Status**: Summarize their cumulative effort and whether they are on track.
            2. **Lagging Goals Insights**: Identify goals with 0% or low progress (e.g., < 40%). Provide highly specific, actionable advice to help them jumpstart these lagging milestones.
            3. **A Motivational Spark**: Remind them of their 'Why' (${profile.primaryMotivation}) with an inspiring, energizing wrap-up statement.
            4. **Suggested Mini-Adjustments**: Recommend 1-2 small, bite-sized tasks they can schedule today to build momentum.

            Format the response in clean, beautiful Markdown with bullet points. Keep it punchy, encouraging, and easy to read.
        """.trimIndent()

        try {
            val responseText = callGeminiApi(apiKey, prompt)
            if (responseText.isNotBlank()) responseText else generateFallbackAnalysis(goals, profile)
        } catch (e: Exception) {
            generateFallbackAnalysis(goals, profile)
        }
    }

    private fun generateFallbackAnalysis(goals: List<MonthlyGoal>, profile: DreamProfile): String {
        val laggingGoals = goals.filter { !it.isCompleted && it.progressPercentage < 40f }
        val laggingText = if (laggingGoals.isNotEmpty()) {
            laggingGoals.joinToString("\n") { "- **${it.title}** (Currently at ${it.progressPercentage.toInt()}%): Focus on splitting this into 15-minute daily micro-tasks or use the Pomodoro timer to build daily consistency." }
        } else {
            "- All your goals are progressing nicely! Keep maintaining this momentum."
        }

        return """
## 🎯 GoalPath Progress Analysis

### 📊 Overall Status
You have established a solid foundation towards **${profile.targetCollege}** & **${profile.dreamCompany}**. Your current dedication demonstrates standard consistency, but there is always room to optimize and accelerate your learning path.

### ⚠️ Lagging Goals & Adjustments
$laggingText

### 💡 Suggested Mini-Adjustments for Today
1. **Micro-Learning**: Spend just 15 minutes practicing a basic concept under your active goals.
2. **Pomodoro Sprint**: Fire up the Dashboard's **Pomodoro Focus Timer** for one 25-minute uninterrupted session to build starting momentum.

### 🚀 A Motivational Spark
Remember your core 'WHY':
> *"${profile.primaryMotivation.ifBlank { "Build game-changing technology & secure top tier offers" }}"*

Every small milestone you complete today brings you closer to your ultimate destination. Stay focused, stay curious, and keep pushing!
        """.trimIndent()
    }
}
