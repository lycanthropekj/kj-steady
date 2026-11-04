package com.kj.steady.logic

import com.kj.steady.data.model.DailyEntry

data class WeightTrendSummary(
    val latestWeight: Double?,
    val latestWeightDate: String?,
    val sevenDayAverage: Double?,
    val sevenDayMeasurementCount: Int,
    val changeFromBaseline: Double?,
    val progressPercent: Double,
    val daysRemainingToDeadline: Int,
    val isDeadlinePassed: Boolean,
    val safePacingAdvice: String
)

object TrendCalculator {
    fun calculateTrend(
        entries: List<DailyEntry>,
        baselineWeight: Double = 104.0,
        goalWeight: Double = 85.0,
        deadlineDate: String = "2027-02-01",
        todayDate: String = DateUtils.today()
    ): WeightTrendSummary {
        val validWeightEntries = entries
            .filter { it.morningWeight != null && it.date <= todayDate }
            .sortedBy { it.date }

        val latestEntry = validWeightEntries.lastOrNull()
        val latestWeight = latestEntry?.morningWeight
        val latestDate = latestEntry?.date

        // 7-day trailing window: entries where days difference is >= 0 and < 7
        val last7DaysEntries = validWeightEntries.filter {
            val diff = DateUtils.daysBetween(it.date, todayDate)
            diff in 0..6
        }

        val sevenDayAverage = if (last7DaysEntries.isNotEmpty()) {
            last7DaysEntries.mapNotNull { it.morningWeight }.average()
        } else null

        val changeFromBaseline = latestWeight?.let { it - baselineWeight }

        val progressPercent = if (latestWeight != null && baselineWeight > goalWeight) {
            val lost = baselineWeight - latestWeight
            val totalNeeded = baselineWeight - goalWeight
            ((lost / totalNeeded) * 100.0).coerceIn(0.0, 100.0)
        } else 0.0

        val daysRemaining = DateUtils.daysBetween(todayDate, deadlineDate)
        val isPassed = daysRemaining <= 0

        // Safe clinical pacing context
        val pacingAdvice = if (daysRemaining > 0 && latestWeight != null && latestWeight > goalWeight) {
            val kgToLose = latestWeight - goalWeight
            val weeksRemaining = daysRemaining / 7.0
            val neededKgPerWeek = kgToLose / weeksRemaining
            if (neededKgPerWeek > 1.0) {
                "Pace: %.1f kg/wk needed for Feb 1. (Clinical safe guideline: 0.5–1.0 kg/wk. Prioritize sustainability over date.)".format(neededKgPerWeek)
            } else {
                "Pace: %.1f kg/wk needed for Feb 1 (within healthy 0.5–1.0 kg/wk rate).".format(neededKgPerWeek)
            }
        } else {
            "Focus on daily routine consistency; review weekly averages."
        }

        return WeightTrendSummary(
            latestWeight = latestWeight,
            latestWeightDate = latestDate,
            sevenDayAverage = sevenDayAverage,
            sevenDayMeasurementCount = last7DaysEntries.size,
            changeFromBaseline = changeFromBaseline,
            progressPercent = progressPercent,
            daysRemainingToDeadline = daysRemaining.coerceAtLeast(0),
            isDeadlinePassed = isPassed,
            safePacingAdvice = pacingAdvice
        )
    }
}
