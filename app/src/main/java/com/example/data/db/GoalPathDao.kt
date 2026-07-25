package com.example.data.db

import androidx.room.*
import com.example.data.model.DailyTask
import com.example.data.model.DreamProfile
import com.example.data.model.MonthlyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalPathDao {

    // --- Dream Profile Queries ---
    @Query("SELECT * FROM dream_profile WHERE id = 1 LIMIT 1")
    fun getDreamProfile(): Flow<DreamProfile?>

    @Query("SELECT * FROM dream_profile WHERE id = 1 LIMIT 1")
    suspend fun getDreamProfileDirect(): DreamProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: DreamProfile)

    // --- Monthly Goals Queries ---
    @Query("SELECT * FROM monthly_goals ORDER BY id ASC")
    fun getAllMonthlyGoals(): Flow<List<MonthlyGoal>>

    @Query("SELECT * FROM monthly_goals WHERE monthYear = :monthYear ORDER BY id ASC")
    fun getGoalsForMonth(monthYear: String): Flow<List<MonthlyGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyGoal(goal: MonthlyGoal): Long

    @Update
    suspend fun updateMonthlyGoal(goal: MonthlyGoal)

    @Query("DELETE FROM monthly_goals WHERE id = :id")
    suspend fun deleteMonthlyGoalById(id: Long)

    // --- Daily Tasks Queries ---
    @Query("SELECT * FROM daily_tasks WHERE monthlyGoalId = :goalId ORDER BY id ASC")
    fun getTasksForGoal(goalId: Long): Flow<List<DailyTask>>

    @Query("SELECT * FROM daily_tasks ORDER BY id ASC")
    fun getAllDailyTasks(): Flow<List<DailyTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyTask(task: DailyTask)

    @Update
    suspend fun updateDailyTask(task: DailyTask)

    @Query("DELETE FROM daily_tasks WHERE id = :id")
    suspend fun deleteDailyTaskById(id: Long)

    @Query("DELETE FROM daily_tasks WHERE monthlyGoalId = :goalId")
    suspend fun deleteTasksByGoalId(goalId: Long)
}
