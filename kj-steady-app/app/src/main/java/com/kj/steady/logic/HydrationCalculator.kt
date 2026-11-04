package com.kj.steady.logic

import com.kj.steady.data.model.HydrationLog
import java.util.*

data class HydrationStatus(
    val totalAmountMl: Int,
    val goalMl: Int,
    val progressPercent: Double,
    val drinksCount: Int,
    val kidneyStonePacingNotice: String?
)

object HydrationCalculator {
    fun calculateStatus(
        logs: List<HydrationLog>,
        goalMl: Int = 2800
    ): HydrationStatus {
        val total = logs.sumOf { it.amountMl }
        val percent = ((total.toDouble() / goalMl.toDouble()) * 100.0).coerceIn(0.0, 100.0)

        // Kidney stone distribution analysis:
        // AUA guidelines urge spreading intake throughout waking hours and avoiding late boluses
        val lateEveningLogs = logs.filter {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestampMillis }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hour >= 21 // after 9:00 PM
        }
        val lateAmount = lateEveningLogs.sumOf { it.amountMl }

        val notice = when {
            lateAmount > 750 -> "High late-night intake (%d mL after 9 PM). Try shifting fluids earlier to protect sleep architecture and nocturia.".format(lateAmount)
            total in 1..999 -> "Starting hydration. Aim for steady 250–300 mL glasses spaced throughout daytime."
            total >= goalMl -> "Target met! Excellent dilution for stone recurrence prevention. Maintain comfortable daytime sips."
            else -> null
        }

        return HydrationStatus(
            totalAmountMl = total,
            goalMl = goalMl,
            progressPercent = percent,
            drinksCount = logs.size,
            kidneyStonePacingNotice = notice
        )
    }
}
