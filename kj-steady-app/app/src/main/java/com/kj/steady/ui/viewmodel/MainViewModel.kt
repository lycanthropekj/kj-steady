package com.kj.steady.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kj.steady.KJSteadyApp
import com.kj.steady.data.model.*
import com.kj.steady.logic.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as KJSteadyApp
    private val repository = app.repository
    private val healthConnectManager = app.healthConnectManager

    // Selected Date for editing/viewing (YYYY-MM-DD)
    private val _selectedDate = MutableStateFlow(DateUtils.today())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // User preferences
    private val _userPreferences = MutableStateFlow(repository.getUserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    // All entries & Weight trend summary
    val allEntries = repository.getAllDailyEntriesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendSummary = combine(allEntries, userPreferences) { entries, prefs ->
        TrendCalculator.calculateTrend(
            entries = entries,
            baselineWeight = prefs.startWeight,
            goalWeight = prefs.goalWeight,
            deadlineDate = prefs.deadlineDate,
            todayDate = DateUtils.today()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TrendCalculator.calculateTrend(emptyList())
    )

    // Current Entry for selected date
    val currentEntry = _selectedDate.flatMapLatest { date ->
        repository.getDailyEntryByDateFlow(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current Date BP readings
    val currentBpReadings = _selectedDate.flatMapLatest { date ->
        repository.getBpReadingsForDateFlow(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Hydration for selected date
    val currentHydrationLogs = _selectedDate.flatMapLatest { date ->
        repository.getHydrationLogsForDateFlow(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentHydrationStatus = combine(currentHydrationLogs, userPreferences) { logs, prefs ->
        HydrationCalculator.calculateStatus(logs, prefs.dailyHydrationGoalMl)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HydrationStatus(0, 2800, 0.0, 0, null)
    )

    // Meal templates & weekly plan
    val mealTemplates = repository.getAllMealTemplatesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyMealPlan = repository.getWeeklyMealPlanFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notification logs & Adaptive suggestions
    val notificationLogs = repository.getRecentNotificationLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adaptiveSuggestions = repository.getAdaptiveSuggestionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback Messages
    private val _uiToast = MutableSharedFlow<String>()
    val uiToast: SharedFlow<String> = _uiToast.asSharedFlow()

    // In-App Update States
    private val _availableUpdate = MutableStateFlow<com.kj.steady.logic.UpdateInfo?>(null)
    val availableUpdate: StateFlow<com.kj.steady.logic.UpdateInfo?> = _availableUpdate.asStateFlow()

    private val _isCheckingUpdate = MutableStateFlow(false)
    val isCheckingUpdate: StateFlow<Boolean> = _isCheckingUpdate.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress.asStateFlow()

    fun checkForAppUpdate(silent: Boolean = false) {
        viewModelScope.launch {
            _isCheckingUpdate.value = true
            val url = _userPreferences.value.updateCheckUrl
            val update = com.kj.steady.logic.UpdateManager.checkForUpdate(url, currentVersionCode = 8)
            _isCheckingUpdate.value = false
            _availableUpdate.value = update
            if (update != null) {
                _uiToast.emit("Update available: v${update.versionName}!")
            } else if (!silent) {
                _uiToast.emit("KJ Steady is up to date (v2.0.6)")
            }
        }
    }

    fun downloadAndInstallUpdate(context: android.content.Context) {
        val update = _availableUpdate.value ?: return
        viewModelScope.launch {
            _downloadProgress.value = 0f
            _uiToast.emit("Downloading update...")
            val apk = com.kj.steady.logic.UpdateManager.downloadApk(context, update.downloadUrl) { progress ->
                _downloadProgress.value = progress
            }
            _downloadProgress.value = null
            if (apk != null) {
                com.kj.steady.logic.UpdateManager.promptInstallApk(context, apk)
            } else {
                _uiToast.emit("Download failed. Check your internet connection.")
            }
        }
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun saveDailyEntry(entry: DailyEntry) {
        viewModelScope.launch {
            repository.upsertDailyEntry(entry)
            _uiToast.emit("Saved entry for ${DateUtils.formatShort(entry.date)}")
        }
    }

    fun deleteDailyEntry(date: String) {
        viewModelScope.launch {
            repository.deleteDailyEntry(date)
            _uiToast.emit("Deleted entry for ${DateUtils.formatShort(date)}")
        }
    }

    fun addBloodPressureReading(
        systolic: Int,
        diastolic: Int,
        pulse: Int?,
        arm: String,
        posture: String,
        notes: String?
    ) {
        viewModelScope.launch {
            if (systolic <= diastolic) {
                _uiToast.emit("Systolic must be greater than diastolic")
                return@launch
            }
            repository.insertBpReading(
                BloodPressureReading(
                    date = _selectedDate.value,
                    systolic = systolic,
                    diastolic = diastolic,
                    pulse = pulse,
                    arm = arm,
                    posture = posture,
                    notes = notes
                )
            )
            _uiToast.emit("Logged BP reading ($systolic/$diastolic)")
        }
    }

    fun deleteBpReading(reading: BloodPressureReading) {
        viewModelScope.launch {
            repository.deleteBpReading(reading)
            _uiToast.emit("Deleted BP reading")
        }
    }

    fun addHydration(amountMl: Int, beverageType: String, vesselLabel: String) {
        viewModelScope.launch {
            repository.insertHydrationLog(
                HydrationLog(
                    date = _selectedDate.value,
                    amountMl = amountMl,
                    beverageType = beverageType,
                    vesselLabel = vesselLabel
                )
            )
            _uiToast.emit("Added $amountMl mL")
        }
    }

    fun undoHydration() {
        viewModelScope.launch {
            val undone = repository.undoLatestHydration(_selectedDate.value)
            if (undone) {
                _uiToast.emit("Undid last drink")
            } else {
                _uiToast.emit("Nothing to undo")
            }
        }
    }

    fun syncHealthConnect() {
        viewModelScope.launch {
            val date = _selectedDate.value
            val result = healthConnectManager.fetchDailyMetrics(date, _userPreferences.value)
            if (result != null) {
                val existing = repository.getDailyEntryByDate(date) ?: DailyEntry(date = date)
                val updated = existing.copy(
                    totalSteps = result.steps ?: existing.totalSteps,
                    sleepHours = result.sleepHours ?: existing.sleepHours,
                    morningWeight = if (existing.morningWeight == null) result.weightKg else existing.morningWeight,
                    sourceAttribution = result.sourceName,
                    updatedAt = System.currentTimeMillis()
                )
                repository.upsertDailyEntry(updated)
                _uiToast.emit("Synced: ${result.sourceName}")
            } else {
                _uiToast.emit("Health Connect unavailable or permissions needed.")
            }
        }
    }

    fun savePreferences(prefs: UserPreferences) {
        viewModelScope.launch {
            repository.saveUserPreferences(prefs)
            _userPreferences.value = prefs
            _uiToast.emit("Settings saved")
        }
    }

    fun approveAdaptiveSuggestion(id: String) {
        viewModelScope.launch {
            repository.updateAdaptiveStatus(id, "APPROVED")
            _uiToast.emit("Adopted suggestion")
        }
    }

    fun dismissAdaptiveSuggestion(id: String) {
        viewModelScope.launch {
            repository.updateAdaptiveStatus(id, "DISMISSED")
            _uiToast.emit("Dismissed suggestion")
        }
    }

    fun resetAdaptiveSuggestions() {
        viewModelScope.launch {
            repository.clearAdaptiveSuggestions()
            _uiToast.emit("Reset learned suggestions")
        }
    }

    suspend fun exportBackupJson(): String {
        return repository.exportJsonBackup()
    }

    suspend fun importBackupJson(rawJson: String): Int {
        return repository.importJsonBackup(rawJson)
    }

    suspend fun exportCsv(): String {
        return repository.exportCsvSummary()
    }
}
