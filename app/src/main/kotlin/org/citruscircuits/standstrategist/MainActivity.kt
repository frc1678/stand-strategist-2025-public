package org.citruscircuits.standstrategist

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.citruscircuits.standstrategist.data.FilePaths
import org.citruscircuits.standstrategist.data.profiles.Profile
import org.citruscircuits.standstrategist.ui.NavGraphs
import org.citruscircuits.standstrategist.ui.dialogs.deleteProfileDialog
import org.citruscircuits.standstrategist.ui.dialogs.newProfileDialog
import org.citruscircuits.standstrategist.ui.dialogs.renameProfileDialog
import org.citruscircuits.standstrategist.ui.screens.profileManagementScreen
import org.citruscircuits.standstrategist.ui.startup.LoadingScreenContent
import org.citruscircuits.standstrategist.ui.startup.MatchScheduleSelectionScreen
import org.citruscircuits.standstrategist.ui.startup.ProfileSelectionScreenContent
import org.citruscircuits.standstrategist.ui.startup.StoragePermissionScreenContent
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    // Stores the state of the app
    sealed interface UiState {
        data object Loading : UiState

        data object StoragePermissions : UiState

        data object SelectingProfile : UiState

        data object SelectingMatchSchedule : UiState

        data object Collection : UiState
    }

    /**
     * Sets up the app at the very beginning
     */

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (!viewModel.appLoaded) viewModel.loadApp()
        setContent {
            val coroutineScope = rememberCoroutineScope()
            StandStrategistTheme {
                val profile by viewModel.profile.collectAsStateWithLifecycle()
                // Makes the different profile components accessible throughout all files in the app
                CompositionLocalProvider(
                    LocalMatchSchedule provides profile.matchSchedule,
                    LocalTeamData provides profile.teamData,
                    LocalTimData provides profile.timData,
                    LocalProfileSettings provides profile.settings,
                    LocalAppSettings provides viewModel.appSettings,
                    LocalWindowSizeClass provides calculateWindowSizeClass(this),
                    LocalCoroutineScope provides coroutineScope
                ) {
                    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                        val settings = viewModel.appSettings.settings.collectAsStateWithLifecycle().value
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                        var matchScheduleError by rememberSaveable { mutableStateOf(false) }
                        when (uiState) {
                            UiState.Loading -> LoadingScreenContent()
                            // Checks to make sure that the app has storage permissions enabled
                            UiState.StoragePermissions -> {
                                val lifecycleState by
                                    LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
                                LaunchedEffect(lifecycleState) {
                                    if (lifecycleState == Lifecycle.State.RESUMED) {
                                        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
                                            return@LaunchedEffect
                                        }
                                        viewModel.loadApp(skipProfileSelection = true)
                                    }
                                }
                                StoragePermissionScreenContent(onRequestStoragePermission = {
                                    val intent =
                                        Intent().apply {
                                            if (Build.VERSION.SDK_INT >= 30) {
                                                action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                                            }
                                        }
                                    startActivity(intent)
                                })
                            }
                            // Lets the user select which profile they'd like to use or if they'd like to create a new one
                            UiState.SelectingProfile ->
                                ProfileSelectionScreenContent(
                                    onSelectProfile = {
                                        viewModel.appSettings.updateSettings(settings.copy(currentProfile = it))
                                        viewModel.loadApp(skipProfileSelection = true)
                                    },
                                    onCreateNewProfile = {
                                        File(FilePaths.profilesFolder, it).mkdirs()
                                        viewModel.appSettings.updateSettings(
                                            settings.copy(currentProfile = it, profiles = settings.profiles + it)
                                        )
                                        viewModel.loadApp(skipProfileSelection = true)
                                    }
                                )

                            // Lets the user select a match schedule
                            UiState.SelectingMatchSchedule -> {
                                val launcher =
                                    rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.OpenDocument()
                                    ) { uri ->
                                        uri?.let {
                                            contentResolver.openInputStream(it)?.run {
                                                try {
                                                    val matchSchedule: Map<String, Profile.MatchSchedule.Match> =
                                                        Json.decodeFromString(reader().readText())
                                                    profile.matchSchedule.update(
                                                        matchSchedule.toList().sortedBy {
                                                                entry ->
                                                            entry.first.toIntOrNull()
                                                        }.toMap())
                                                    viewModel.forceSave()
                                                    viewModel.loadApp(skipProfileSelection = true)
                                                } catch (_: IOException) {
                                                    matchScheduleError = true
                                                } catch (_: RuntimeException) {
                                                    matchScheduleError = true
                                                }
                                                close()
                                            }
                                        }
                                    }
                                MatchScheduleSelectionScreen(
                                    onOpenFilePicker = { launcher.launch(arrayOf("*/*")) },
                                    onSwitchProfile = { viewModel.loadApp() }
                                )
                            }
                            // Shows the current screen
                            UiState.Collection ->
                                DestinationsNavHost(navGraph = NavGraphs.main) {
                                    newProfileDialog(viewModel)
                                    renameProfileDialog(viewModel)
                                    deleteProfileDialog(viewModel)
                                    profileManagementScreen(viewModel)
                                }
                        }

                        // Popup for if the selected match schedule is invalid, only shows if they select an invalid match schedule
                        if (matchScheduleError) {
                            // Closes the popup if the user taps outside of the popup
                            Dialog(onDismissRequest = { matchScheduleError = false }) {
                                Card(
                                    modifier = Modifier.wrapContentSize(),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(5.dp, Color.Black)
                                ) {
                                    Column(
                                        modifier = Modifier.wrapContentSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Invalid Match Schedule File",
                                            style = TextStyle(
                                                fontSize = 40.sp,
                                                textAlign = TextAlign.Center
                                            ),
                                            modifier = Modifier.padding(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
