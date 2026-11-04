package com.kj.steady.logic

data class BpCategory(
    val label: String,
    val colorHex: String,
    val description: String
)

object BloodPressureEvaluator {
    fun evaluate(systolic: Int, diastolic: Int): BpCategory {
        return when {
            systolic < 120 && diastolic < 80 -> BpCategory(
                label = "Normal",
                colorHex = "#2E7D32",
                description = "Systolic < 120 and Diastolic < 80 mmHg"
            )
            systolic in 120..129 && diastolic < 80 -> BpCategory(
                label = "Elevated",
                colorHex = "#F57F17",
                description = "Systolic 120–129 and Diastolic < 80 mmHg"
            )
            systolic in 130..139 || diastolic in 80..89 -> BpCategory(
                label = "Stage 1 HTN Range",
                colorHex = "#E65100",
                description = "Systolic 130–139 or Diastolic 80–89 mmHg"
            )
            systolic >= 140 || diastolic >= 90 -> BpCategory(
                label = "Stage 2 HTN Range",
                colorHex = "#C62828",
                description = "Systolic ≥ 140 or Diastolic ≥ 90 mmHg"
            )
            else -> BpCategory(
                label = "Logged Reading",
                colorHex = "#455A64",
                description = "Recorded for clinical review"
            )
        }
    }
}
