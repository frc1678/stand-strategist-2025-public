package org.citruscircuits.standstrategist

import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.citruscircuits.standstrategist.data.Alliance
import org.citruscircuits.standstrategist.data.AppSettings
import org.citruscircuits.standstrategist.data.AutoSaveManager
import org.citruscircuits.standstrategist.data.FilePaths
import org.citruscircuits.standstrategist.data.profiles.Profile
import org.citruscircuits.standstrategist.data.profiles.io.exportFiles
import org.citruscircuits.standstrategist.data.profiles.io.importFiles
import java.io.File

/**
 * Holds all of the app's data
 */
class MainActivityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<MainActivity.UiState>(MainActivity.UiState.Loading)
    val uiState = _uiState.asStateFlow()

    val appSettings = AppSettings()

    private val _profile = MutableStateFlow(Profile())
    val profile = _profile.asStateFlow()

    private var autoSaveManager: AutoSaveManager? = null

    fun forceSave() = autoSaveManager?.forceSave()

    var appLoaded = false

    /**
     * Loads the app if the profile has already been chosen
     */
    fun loadApp(skipProfileSelection: Boolean = false) =
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = MainActivity.UiState.Loading
            autoSaveManager?.stop()
            if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
                _uiState.value = MainActivity.UiState.StoragePermissions
                return@launch
            }
            appSettings.readSettings()
            if (appSettings.settings.value.profiles.isEmpty() || !skipProfileSelection) {
                _uiState.value = MainActivity.UiState.SelectingProfile
                return@launch
            }
            autoSaveManager =
                AutoSaveManager(
                    saveData = {
                        _profile.value.exportFiles().forEach { (fileName, data) ->
                            File(File(FilePaths.profilesFolder, appSettings.settings.value.currentProfile!!), fileName)
                                .writeText(data)
                        }
                    },
                    coroutineScope = viewModelScope
                )
            autoSaveManager!!.start()
            // Gets the profile that is currently selected
            _profile.value =
                importFiles(
                    files =
                        File(FilePaths.profilesFolder, appSettings.settings.value.currentProfile!!)
                            .listFiles()!!.associate { file -> file.name to file.readBytes() },
                    onUpdate = { viewModelScope.launch(Dispatchers.IO) { autoSaveManager!!.requestSave() } }
                )
            if (_profile.value.matchSchedule.schedule.value.isEmpty()) {
                _uiState.value = MainActivity.UiState.SelectingMatchSchedule
                return@launch
            }
            if (_profile.value.settings.profileSettings.value.alliance == null) {
                _profile.value.settings.update(
                    _profile.value.settings.profileSettings.value.copy(alliance = Alliance.BLUE)
                )
            }
            forceSave()
            _uiState.value = MainActivity.UiState.Collection
            appLoaded = true
        }
}
