package com.example.am_clicker.data



 // Dodaj to na samej górze pliku, przed package, lub nad funkcją ProfileScreen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextField
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@OptIn(ExperimentalMaterial3Api::class)

@Database(
    entities = [UserStatsEntity::class, UpgradeEntity::class, AchievementEntity::class],
    version = 2,
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
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}