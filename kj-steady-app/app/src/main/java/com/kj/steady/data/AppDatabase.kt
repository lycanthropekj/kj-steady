package com.kj.steady.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kj.steady.data.dao.*
import com.kj.steady.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DailyEntry::class,
        BloodPressureReading::class,
        HydrationLog::class,
        MealTemplate::class,
        WeeklyMealPlan::class,
        NotificationLog::class,
        AdaptiveSuggestion::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyEntryDao(): DailyEntryDao
    abstract fun bloodPressureDao(): BloodPressureDao
    abstract fun hydrationDao(): HydrationDao
    abstract fun mealDao(): MealDao
    abstract fun notificationDao(): NotificationDao
    abstract fun adaptiveDao(): AdaptiveDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE daily_entries ADD COLUMN isDinnerHomeCooked INTEGER DEFAULT NULL")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kj_steady.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.mealDao())
                }
            }
        }

        suspend fun populateInitialData(mealDao: MealDao) {
            val presets = listOf(
                MealTemplate(
                    category = "BREAKFAST",
                    name = "Muesli + Milk + Carbamide Forte Shake",
                    description = "50g muesli, 200ml cow milk, 1 scoop Carbamide Forte plant protein isolate",
                    proteinGrams = 38.0,
                    calories = 420,
                    isPreset = true,
                    isFavorite = true
                ),
                MealTemplate(
                    category = "BREAKFAST",
                    name = "Bananas & Plant Protein Shake",
                    description = "2 medium bananas, 1 scoop plant protein shaken with cold water",
                    proteinGrams = 26.0,
                    calories = 310,
                    isPreset = true,
                    isFavorite = false
                ),
                MealTemplate(
                    category = "LUNCH_SNACK",
                    name = "Max Protein Strawberry Bar",
                    description = "1 Max Protein bar with plain iced Americano",
                    proteinGrams = 20.0,
                    calories = 240,
                    isPreset = true,
                    isFavorite = true
                ),
                MealTemplate(
                    category = "DINNER",
                    name = "Egg Bhurji + 2 Rotis + Fresh Curd",
                    description = "3 whole eggs scrambled with green chillies & onions, 2 phulkas, 100g fresh curd",
                    proteinGrams = 39.0,
                    calories = 520,
                    isPreset = true,
                    isFavorite = true
                ),
                MealTemplate(
                    category = "DINNER",
                    name = "Paneer Bhurji + 2 Rotis + Cucumber Salad",
                    description = "150g low-oil paneer bhurji, 2 phulkas, sliced cucumbers & lemon",
                    proteinGrams = 35.0,
                    calories = 560,
                    isPreset = true,
                    isFavorite = true
                ),
                MealTemplate(
                    category = "DINNER",
                    name = "Boiled Eggs (4 whites + 2 yolks) + Dal + Rice",
                    description = "Boiled eggs, 1 small katori moong dal, 1 cup measured steamed rice",
                    proteinGrams = 34.0,
                    calories = 480,
                    isPreset = true,
                    isFavorite = false
                )
            )
            mealDao.insertTemplates(presets)

            val weeklyPlans = listOf(
                WeeklyMealPlan(1, "Monday", "Egg Bhurji & 2 Phulkas with Curd", "3 eggs, onions, tomatoes, 2 rotis, 100g curd", 39.0, "Eggs, Onions, Tomatoes, Whole wheat flour, Curd"),
                WeeklyMealPlan(2, "Tuesday", "Paneer Stir Fry with Mixed Bell Peppers & 2 Rotis", "150g paneer cubes, capsicum, jeera, 2 rotis", 35.0, "Paneer, Capsicum, Jeera, Whole wheat flour"),
                WeeklyMealPlan(3, "Wednesday", "4 Boiled Eggs, Moong Dal Tadka & Steamed Rice", "4 eggs (2 yolks), yellow dal, 1 cup rice, salad", 36.0, "Eggs, Yellow Moong Dal, Rice, Cucumbers"),
                WeeklyMealPlan(4, "Thursday", "Tofu Scramble with Spinach & 2 Toast/Rotis", "200g firm tofu, palak, turmeric, 2 rotis", 32.0, "Firm Tofu, Spinach, Whole wheat bread/flour"),
                WeeklyMealPlan(5, "Friday", "Omelette (3 eggs) with Veggie Sautée & Curd", "3-egg folded omelette, mushrooms/capsicum, 1 roti, curd", 37.0, "Eggs, Mushrooms, Capsicum, Curd"),
                WeeklyMealPlan(6, "Saturday", "Soya Chunks Curry & 2 Rotis with Curd", "50g dry soya chunks (boiled), tomato gravy, 2 rotis, curd", 42.0, "Soya Chunks, Tomatoes, Curd, Spices"),
                WeeklyMealPlan(7, "Sunday", "Chef's Choice: Egg Curry or Paneer Tikka", "Home-cooked protein feast; keep cooking oil measured (1-2 tsp)", 38.0, "Eggs or Paneer, Onions, Curd, Coriander")
            )
            mealDao.insertWeeklyPlan(weeklyPlans)
        }
    }
}
