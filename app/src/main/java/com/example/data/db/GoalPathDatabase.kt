package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.DailyTask
import com.example.data.model.DreamProfile
import com.example.data.model.MonthlyGoal

@Database(
    entities = [DreamProfile::class, MonthlyGoal::class, DailyTask::class],
    version = 1,
    exportSchema = false
)
abstract class GoalPathDatabase : RoomDatabase() {
    abstract fun dao(): GoalPathDao

    companion object {
        @Volatile
        private var INSTANCE: GoalPathDatabase? = null

        fun getInstance(context: Context): GoalPathDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoalPathDatabase::class.java,
                    "goalpath_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
