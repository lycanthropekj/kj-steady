package com.kj.steady.data.dao

import androidx.room.*
import com.kj.steady.data.model.MealTemplate
import com.kj.steady.data.model.WeeklyMealPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meal_templates ORDER BY isFavorite DESC, category ASC")
    fun getAllTemplatesFlow(): Flow<List<MealTemplate>>

    @Query("SELECT * FROM meal_templates WHERE category = :category ORDER BY isFavorite DESC")
    suspend fun getTemplatesByCategory(category: String): List<MealTemplate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: MealTemplate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<MealTemplate>)

    @Delete
    suspend fun deleteTemplate(template: MealTemplate)

    @Query("SELECT * FROM weekly_meal_plan ORDER BY dayOfWeek ASC")
    fun getWeeklyPlanFlow(): Flow<List<WeeklyMealPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDayPlan(plan: WeeklyMealPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyPlan(plans: List<WeeklyMealPlan>)
}
