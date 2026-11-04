package com.kj.steady.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_templates")
data class MealTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // BREAKFAST, LUNCH_SNACK, DINNER
    val name: String,
    val description: String,
    val proteinGrams: Double = 0.0,
    val calories: Int = 0,
    val isPreset: Boolean = false,
    val isFavorite: Boolean = false
)

@Entity(tableName = "weekly_meal_plan")
data class WeeklyMealPlan(
    @PrimaryKey
    val dayOfWeek: Int, // 1 = Monday ... 7 = Sunday
    val dayName: String,
    val dinnerTitle: String,
    val dinnerDescription: String,
    val proteinTargetGrams: Double = 40.0,
    val shoppingIngredients: String = "" // comma separated
)
