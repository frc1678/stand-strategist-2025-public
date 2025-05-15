package org.citruscircuits.standstrategist.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Settings for the Stand Strategist app
 */
class AppSettings(initialSettings: AppSettings = AppSettings()) {
    @Serializable
    data class AppSettings(val currentProfile: String? = null, val profiles: List<String> = listOf())

    private val _settings = MutableStateFlow(initialSettings)
    val settings = _settings.asStateFlow()

    fun readSettings() {
        _settings.value =
            if (FilePaths.globalSettings.exists()) {
                Json.decodeFromString(FilePaths.globalSettings.readText())
            } else {
                AppSettings()
            }
    }

    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
        FilePaths.globalSettings.parentFile?.mkdirs()
        FilePaths.globalSettings.writeText(Json.encodeToString(_settings.value))
    }
}
