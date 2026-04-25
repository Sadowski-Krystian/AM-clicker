package com.example.am_clicker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserStatsEntity::class, UpgradeEntity::class, AchievementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {

    abstract val gameDao: GameDao

    // This companion object ensures we only create ONE instance of the database to prevent crashes.
    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "asteroid_clicker_db"
                )
                    // If you add a new table later, this wipes the old DB so the app doesn't crash
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}