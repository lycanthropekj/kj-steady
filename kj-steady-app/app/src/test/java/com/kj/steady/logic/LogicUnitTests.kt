package com.kj.steady.logic

import com.google.gson.Gson
import com.kj.steady.data.model.DailyEntry
import com.kj.steady.data.model.HydrationLog
import org.junit.Assert.*
import org.junit.Test

class LogicUnitTests {

    @Test
    fun testDateMathAndFormatting() {
        assertEquals(89, DateUtils.daysBetween("2026-11-04", "2027-02-01"))
        assertEquals(0, DateUtils.daysBetween("2026-11-04", "2026-11-04"))
        assertEquals(-5, DateUtils.daysBetween("2026-11-09", "2026-11-04"))
        assertEquals("2026-11-14", DateUtils.addDays("2026-11-04", 10))
    }

    @Test
    fun testTrendCalculatorWithSparseAndMissingRecords() {
        // Empty entries
        val emptySummary = TrendCalculator.calculateTrend(
            entries = emptyList(),
            baselineWeight = 104.0,
            goalWeight = 85.0,
            deadlineDate = "2027-02-01",
            todayDate = "2026-11-04"
        )
        assertNull(emptySummary.latestWeight)
        assertNull(emptySummary.sevenDayAverage)
        assertEquals(0, emptySummary.sevenDayMeasurementCount)
        assertEquals(0.0, emptySummary.progressPercent, 0.01)

        // Entries with gaps and missing weights
        val entries = listOf(
            DailyEntry(date = "2026-10-25", morningWeight = 104.5),
            DailyEntry(date = "2026-11-01", morningWeight = 104.0),
            DailyEntry(date = "2026-11-02", morningWeight = null), // missing weigh-in
            DailyEntry(date = "2026-11-03", morningWeight = 103.4),
            DailyEntry(date = "2026-11-04", morningWeight = 103.0)
        )

        val summary = TrendCalculator.calculateTrend(
            entries = entries,
            baselineWeight = 104.0,
            goalWeight = 85.0,
            deadlineDate = "2027-02-01",
            todayDate = "2026-11-04"
        )

        assertEquals(103.0, summary.latestWeight!!, 0.01)
        assertEquals("2026-11-04", summary.latestWeightDate)
        assertEquals(-1.0, summary.changeFromBaseline!!, 0.01)

        // Trailing 7-day window relative to 2026-11-04 includes 2026-11-01 (104.0), 2026-11-03 (103.4), 2026-11-04 (103.0)
        // 2026-10-25 is 10 days prior, outside the 7-day window
        assertEquals(3, summary.sevenDayMeasurementCount)
        val expectedAvg = (104.0 + 103.4 + 103.0) / 3.0
        assertEquals(expectedAvg, summary.sevenDayAverage!!, 0.01)

        // Progress toward 85 kg from 104 kg: (104 - 103) / (104 - 85) = 1.0 / 19.0 = 5.26%
        assertEquals(5.26, summary.progressPercent, 0.05)
    }

    @Test
    fun testBloodPressureCategories() {
        val normal = BloodPressureEvaluator.evaluate(118, 76)
        assertEquals("Normal", normal.label)

        val elevated = BloodPressureEvaluator.evaluate(124, 78)
        assertEquals("Elevated", elevated.label)

        val stage1Sys = BloodPressureEvaluator.evaluate(132, 78)
        assertEquals("Stage 1 HTN Range", stage1Sys.label)

        val stage1Dia = BloodPressureEvaluator.evaluate(118, 84)
        assertEquals("Stage 1 HTN Range", stage1Dia.label)

        val stage2 = BloodPressureEvaluator.evaluate(145, 95)
        assertEquals("Stage 2 HTN Range", stage2.label)
    }

    @Test
    fun testHydrationCalculatorPacing() {
        val morningTime = 1793775600000L // 10:00 AM
        val logs = listOf(
            HydrationLog(date = "2026-11-04", amountMl = 250, timestampMillis = morningTime),
            HydrationLog(date = "2026-11-04", amountMl = 500, timestampMillis = morningTime + 3600000L)
        )
        val status = HydrationCalculator.calculateStatus(logs, 2800)
        assertEquals(750, status.totalAmountMl)
        assertEquals(2, status.drinksCount)
        assertEquals(26.78, status.progressPercent, 0.1)
    }

    @Test
    fun testPrototypeV1JsonParsing() {
        val v1Json = """
            {
              "version": 1,
              "settings": {
                "start": "2026-11-04",
                "deadline": "2027-02-01",
                "startWeight": 104,
                "goal": 85
              },
              "entries": [
                {
                  "date": "2026-11-04",
                  "weight": 103.5,
                  "steps": 11200,
                  "sleep": 7.2,
                  "sys": 122,
                  "dia": 78,
                  "cigarettes": 4,
                  "alcohol": false,
                  "breakfast": "Muesli + protein",
                  "snacks": "Americano",
                  "dinner": "Egg bhurji",
                  "notes": "Good energy"
                }
              ]
            }
        """.trimIndent()

        val gson = Gson()
        val root = gson.fromJson(v1Json, com.google.gson.JsonObject::class.java)
        assertEquals(1, root.get("version").asInt)
        val entries = root.getAsJsonArray("entries")
        assertEquals(1, entries.size())
        val first = entries.get(0).asJsonObject
        assertEquals("2026-11-04", first.get("date").asString)
        assertEquals(103.5, first.get("weight").asDouble, 0.01)
        assertEquals(11200, first.get("steps").asInt)
        assertEquals(122, first.get("sys").asInt)
        assertEquals(78, first.get("dia").asInt)
        assertEquals("Muesli + protein", first.get("breakfast").asString)
    }

    @Test
    fun testSleepIntervalUnionDeduplication() {
        val t0 = java.time.Instant.parse("2026-11-04T17:30:00Z") // 23:00 IST
        val t1 = java.time.Instant.parse("2026-11-04T18:00:00Z") // 23:30 IST
        val t2 = java.time.Instant.parse("2026-11-05T00:30:00Z") // 06:00 IST
        val t3 = java.time.Instant.parse("2026-11-05T01:00:00Z") // 06:30 IST
        val napStart = java.time.Instant.parse("2026-11-05T08:30:00Z") // 14:00 IST
        val napEnd = java.time.Instant.parse("2026-11-05T09:30:00Z") // 15:00 IST

        // Samsung: 23:00 to 06:00 (7 hrs)
        // Fitbit: 23:30 to 06:30 (7 hrs) - overlaps with Samsung
        // Nap: 14:00 to 15:00 (1 hr)
        val intervals = listOf(
            com.kj.steady.health.TimeInterval(t0, t2),
            com.kj.steady.health.TimeInterval(t1, t3),
            com.kj.steady.health.TimeInterval(napStart, napEnd)
        )

        val merged = com.kj.steady.health.HealthConnectManager.mergeIntervals(intervals)
        assertEquals(2, merged.size)
        // First merged interval spans t0 to t3 (23:00 to 06:30 -> 7.5 hours)
        assertEquals(t0, merged[0].start)
        assertEquals(t3, merged[0].end)
        // Second interval is the nap (1 hour)
        assertEquals(napStart, merged[1].start)
        assertEquals(napEnd, merged[1].end)

        val totalSeconds = merged.sumOf { java.time.Duration.between(it.start, it.end).seconds }
        val hours = totalSeconds / 3600.0
        assertEquals(8.5, hours, 0.01) // Not 7 + 7 + 1 = 15!
    }
}
