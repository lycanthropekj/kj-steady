package com.kj.steady.logic

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("Asia/Kolkata")
    }

    fun today(): String {
        return isoFormat.format(Date())
    }

    fun formatDisplay(dateStr: String): String {
        return try {
            val date = isoFormat.parse(dateStr) ?: return dateStr
            val displayFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
            displayFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatShort(dateStr: String): String {
        return try {
            val date = isoFormat.parse(dateStr) ?: return dateStr
            val displayFormat = SimpleDateFormat("d MMM", Locale.getDefault())
            displayFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun daysBetween(startDateStr: String, endDateStr: String): Int {
        return try {
            val start = isoFormat.parse(startDateStr)?.time ?: return 0
            val end = isoFormat.parse(endDateStr)?.time ?: return 0
            ((end - start) / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun addDays(dateStr: String, days: Int): String {
        return try {
            val cal = Calendar.getInstance()
            cal.time = isoFormat.parse(dateStr) ?: Date()
            cal.add(Calendar.DAY_OF_YEAR, days)
            isoFormat.format(cal.time)
        } catch (e: Exception) {
            today()
        }
    }

    fun formatTime(millis: Long): String {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return timeFormat.format(Date(millis))
    }
}
