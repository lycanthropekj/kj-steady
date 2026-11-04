package com.kj.steady.logic

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kj.steady.data.dao.BloodPressureDao
import com.kj.steady.data.dao.DailyEntryDao
import com.kj.steady.data.model.BloodPressureReading
import com.kj.steady.data.model.DailyEntry

object V1MigrationHelper {

    suspend fun checkAndMigrateLegacySharedPreferences(
        context: Context,
        dailyEntryDao: DailyEntryDao,
        bloodPressureDao: BloodPressureDao
    ): Boolean {
        return try {
            val prefs = context.getSharedPreferences("tracker", Context.MODE_PRIVATE)
            val legacyJson = prefs.getString("data", null)
            if (!legacyJson.isNullOrBlank()) {
                val migrated = parseAndImportV1Json(legacyJson, dailyEntryDao, bloodPressureDao)
                if (migrated > 0) {
                    // Mark as migrated
                    prefs.edit().putBoolean("v1_migrated_to_room", true).apply()
                    true
                } else false
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun parseAndImportV1Json(
        jsonString: String,
        dailyEntryDao: DailyEntryDao,
        bloodPressureDao: BloodPressureDao
    ): Int {
        val gson = Gson()
        val root = gson.fromJson(jsonString, JsonObject::class.java) ?: return 0
        val entriesArray = root.getAsJsonArray("entries") ?: return 0

        val entriesToInsert = mutableListOf<DailyEntry>()
        val bpReadingsToInsert = mutableListOf<BloodPressureReading>()

        for (i in 0 until entriesArray.size()) {
            val obj = entriesArray.get(i).asJsonObject
            val date = obj.get("date")?.asString ?: continue

            val weight = if (obj.has("weight") && !obj.get("weight").isJsonNull) obj.get("weight").asDouble else null
            val steps = if (obj.has("steps") && !obj.get("steps").isJsonNull) obj.get("steps").asInt else null
            val sleep = if (obj.has("sleep") && !obj.get("sleep").isJsonNull) obj.get("sleep").asDouble else null
            val cigarettes = if (obj.has("cigarettes") && !obj.get("cigarettes").isJsonNull) obj.get("cigarettes").asInt else null
            val alcohol = if (obj.has("alcohol") && !obj.get("alcohol").isJsonNull) obj.get("alcohol").asBoolean else false

            val sys = if (obj.has("sys") && !obj.get("sys").isJsonNull) obj.get("sys").asInt else null
            val dia = if (obj.has("dia") && !obj.get("dia").isJsonNull) obj.get("dia").asInt else null

            val breakfast = if (obj.has("breakfast") && !obj.get("breakfast").isJsonNull) obj.get("breakfast").asString else null
            val snacks = if (obj.has("snacks") && !obj.get("snacks").isJsonNull) obj.get("snacks").asString else null
            val dinner = if (obj.has("dinner") && !obj.get("dinner").isJsonNull) obj.get("dinner").asString else null
            val notes = if (obj.has("notes") && !obj.get("notes").isJsonNull) obj.get("notes").asString else null

            entriesToInsert.add(
                DailyEntry(
                    date = date,
                    morningWeight = weight,
                    totalSteps = steps,
                    sleepHours = sleep,
                    cigarettes = cigarettes,
                    hadAlcohol = alcohol,
                    breakfastText = breakfast,
                    lunchSnacksText = snacks,
                    dinnerText = dinner,
                    notes = notes,
                    sourceAttribution = "Migrated Prototype v1"
                )
            )

            if (sys != null && dia != null) {
                bpReadingsToInsert.add(
                    BloodPressureReading(
                        date = date,
                        systolic = sys,
                        diastolic = dia,
                        pulse = null,
                        arm = "Left",
                        posture = "Sitting",
                        notes = "Imported from prototype v1"
                    )
                )
            }
        }

        if (entriesToInsert.isNotEmpty()) {
            dailyEntryDao.upsertEntries(entriesToInsert)
        }
        for (bp in bpReadingsToInsert) {
            bloodPressureDao.insertReading(bp)
        }

        return entriesToInsert.size
    }
}
