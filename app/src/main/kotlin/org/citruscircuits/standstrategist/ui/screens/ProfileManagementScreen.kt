package org.citruscircuits.standstrategist.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCallsBuilder
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.citruscircuits.standstrategist.LocalAppSettings
import org.citruscircuits.standstrategist.LocalWindowSizeClass
import org.citruscircuits.standstrategist.MainActivityViewModel
import org.citruscircuits.standstrategist.data.profiles.operations.ProfileOperationPreset
import org.citruscircuits.standstrategist.ui.MainNavGraph
import org.citruscircuits.standstrategist.ui.destinations.DeleteProfileDialogDestination
import org.citruscircuits.standstrategist.ui.destinations.NewProfileDialogDestination
import org.citruscircuits.standstrategist.ui.destinations.ProfileManagementScreenDestination
import org.citruscircuits.standstrategist.ui.destinations.ProfileOperationScreenDestination
import org.citruscircuits.standstrategist.ui.destinations.RenameProfileDialogDestination

/**
 * Manual composable call for the [ProfileManagementScreen].
 *
 * @see ManualComposableCallsBuilder.composable
 */
fun ManualComposableCallsBuilder.profileManagementScreen(viewModel: MainActivityViewModel) {
    composable(ProfileManagementScreenDestination) {
        val settings by viewModel.appSettings.settings.collectAsStateWithLifecycle()
        ProfileManagementScreen(navigator = destinationsNavigator, onSwitchProfile = {
            // set the current profile in the settings
            viewModel.appSettings.updateSettings(settings.copy(currentProfile = it))
            // reload the app
            viewModel.loadApp(skipProfileSelection = true)
        })
    }
}

/**
 * Screen for viewing the list of profiles and taking actions on profiles.
 *
 * @param navigator The app's main [DestinationsNavigator].
 * @param onSwitchProfile A callback that makes the app load a different profile.
 * @see ProfileManagementScreenContent
 */
@MainNavGraph
@Destination
@Composable
fun ProfileManagementScreen(navigator: DestinationsNavigator, onSwitchProfile: (String) -> Unit) {
    // initialize screen-level actions
    val actions = ProfileManagementActions(createProfile = { navigator.navigate(NewProfileDialogDestination) },
        operation = { navigator.navigate(ProfileOperationScreenDestination()) },
        importZip = {
            navigator.navigate(ProfileOperationScreenDestination(preset = ProfileOperationPreset.ImportZip))
        },
        importFolder = {
            navigator.navigate(ProfileOperationScreenDestination(preset = ProfileOperationPreset.ImportFolder))
        },
        navigateUp = { navigator.navigateUp() })
    // initialize per-profile actions
    val profileActions = ProfileActions(switch = { onSwitchProfile(it) },
        rename = { navigator.navigate(RenameProfileDialogDestination(existingName = it)) },
        delete = { navigator.navigate(DeleteProfileDialogDestination(profile = it)) },
        mergeFrom = {
            navigator.navigate(
                ProfileOperationScreenDestination(preset = ProfileOperationPreset.MergeFrom(profile = it))
            )
        },
        mergeInto = {
            navigator.navigate(
                ProfileOperationScreenDestination(preset = ProfileOperationPreset.MergeInto(profile = it))
            )
        },
        duplicate = {
            navigator.navigate(
                ProfileOperationScreenDestination(preset = ProfileOperationPreset.Duplicate(profile = it))
            )
        },
        exportZip = {
            navigator.navigate(
                ProfileOperationScreenDestination(preset = ProfileOperationPreset.ExportZip(profile = it))
            )
        },
        exportFolder = {
            navigator.navigate(
                ProfileOperationScreenDestination(
                    preset = ProfileOperationPreset.ExportFolder(profile = it)
                )
            )
        },
        exportOds = {
            navigator.navigate(
                ProfileOperationScreenDestination(preset = ProfileOperationPreset.ExportOds(profile = it))
            )
        })
    // show content
    ProfileManagementScreenContent(actions, profileActions)
}

/**
 * Actions holder for the [ProfileManagementScreen].
 *
 * @param createProfile Action to create a new profile.
 * @param operation Action to open the profile operation screen.
 * @param importZip Action to open the profile operation screen with the `.zip` import preset.
 * @param importFolder Action to open the profile operation screen with the folder import preset.
 * @param navigateUp Action to navigate to the previous screen.
 */
data class ProfileManagementActions(
    val createProfile: () -> Unit,
    val operation: () -> Unit,
    val importZip: () -> Unit,
    val importFolder: () -> Unit,
    val navigateUp: () -> Unit
)

/**
 * Content for [ProfileManagementScreen].
 *
 * If the current [WindowWidthSizeClass] is [WindowWidthSizeClass.Expanded],
 * this shows the profile list on the left and the profile details on the right.
 * Otherwise, this shows the profile list in fullscreen,
 * and the user can select a profile to view its details in fullscreen.
 *
 * @param actions Screen-level actions.
 * @param profileActions Per-profile actions.
 * @see ExpandedContent
 * @see NormalContent
 */
@Composable
private fun ProfileManagementScreenContent(actions: ProfileManagementActions, profileActions: ProfileActions) {
    // get app settings
    val appSettings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
    // remember which profile is being shown
    // this is separate from the currently loaded profile in the app
    var shownProfile by rememberSaveable(appSettings) { mutableStateOf<String?>(null) }
    // check whether the window width is expanded or not
    if (LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Expanded) {
        ExpandedContent(
            shownProfile = shownProfile,
            setShownProfile = { shownProfile = it },
            actions = actions,
            profileActions = profileActions
        )
    } else {
        NormalContent(
            shownProfile = shownProfile,
            setShownProfile = { shownProfile = it },
            actions = actions,
            profileActions = profileActions
        )
    }
}

/**
 * Content for [ProfileManagementScreen] when the current [WindowWidthSizeClass] is [WindowWidthSizeClass.Expanded].
 *
 * This shows a button at the top left to create a new profile,
 * and the rest of the left side has a [ProfileList] in an [OutlinedCard].
 * On the right side are the [ProfileDetails] for the profile selected on the left.
 *
 * @param shownProfile The currently shown profile (not the currently loaded profile in the app).
 * If `null`, no profile details will be shown, and no profile will show as selected.
 * @param setShownProfile A callback to set [shownProfile].
 * @param actions Screen-level actions.
 * @param profileActions Per-profile actions.
 * @see ProfileList
 * @see ProfileDetails
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandedContent(
    shownProfile: String?,
    setShownProfile: (String?) -> Unit,
    actions: ProfileManagementActions,
    profileActions: ProfileActions
) {
    // use scaffold to position the top bar
    Scaffold(topBar = {
        TopAppBar(title = { Text("Profile Management") },
            // back button
            navigationIcon = {
                IconButton(onClick = actions.navigateUp) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
            })
    }) { padding ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
        ) {
            // left side
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier
                    .fillMaxHeight()
                    // make the column wide enough for all the content to fit
                    // but let children use fillMaxWidth() to match parent size
                    .width(IntrinsicSize.Max)
            ) {
                // new profile button
                // extended FAB gives emphasis
                ExtendedFloatingActionButton(text = { Text("Create new profile") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    onClick = actions.createProfile
                )
                // import button with a dropdown menu
                Box {
                    // whether the menu is expanded
                    var importMenuShown by rememberSaveable { mutableStateOf(false) }
                    OutlinedButton(onClick = { importMenuShown = true }) {
                        Icon(Icons.Default.FileUpload, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Import...")
                    }
                    // menu
                    ImportMenu(
                        expanded = importMenuShown,
                        onDismissRequest = { importMenuShown = false },
                        onImportZip = actions.importZip,
                        onImportFolder = actions.importFolder
                    )
                }
                // run operation button
                OutlinedButton(onClick = actions.operation) {
                    Icon(Icons.Default.Terminal, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Run operation")
                }
                // wrap profile list in outline
                OutlinedCard(modifier = Modifier.weight(1f)) {
                    ProfileList(shownProfile, setShownProfile, expanded = true)
                }
            }
            // right side
            OutlinedCard(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                // animate when changing which profile is shown
                AnimatedContent(targetState = shownProfile, label = "") { targetShownProfile ->
                    // check if a profile is selected
                    if (targetShownProfile != null) {
                        ProfileDetails(shownProfile = targetShownProfile, actions = profileActions)
                    } else {
                        // placeholder text
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("Select a profile to show details")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Content for [ProfileManagementScreen] when the current [WindowWidthSizeClass] is **not**
 * [WindowWidthSizeClass.Expanded].
 *
 * If [shownProfile] is `null`, only the [ProfileList] is shown, in fullscreen.
 * Otherwise, the [ProfileDetails] for the [shownProfile] are shown in fullscreen.
 *
 * @param shownProfile The currently shown profile (not the currently loaded profile in the app).
 * @param setShownProfile A callback to set [shownProfile].
 * @param actions Screen-level actions.
 * @param profileActions Per-profile actions.
 * @see ProfileList
 * @see ProfileDetails
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NormalContent(
    shownProfile: String?,
    setShownProfile: (String?) -> Unit,
    actions: ProfileManagementActions,
    profileActions: ProfileActions
) {
    // animate the profile details to look like a page that shows over the profile list
    AnimatedContent(
        targetState = shownProfile, transitionSpec = {
            if (targetState != null) {
                // profile details page slides in from the right
                slideInHorizontally { fullWidth -> fullWidth } + fadeIn() togetherWith fadeOut()
            } else {
                // profile details page slides out to the right
                fadeIn() togetherWith slideOutHorizontally { fullWidth -> fullWidth } + fadeOut()
            }
        }, label = ""
    ) { targetShownProfile ->
        if (targetShownProfile == null) {
            // profile list
            // use scaffold to position the top bar and extended FAB
            Scaffold(topBar = {
                TopAppBar(title = { Text("Profile Management") },
                    // back button
                    navigationIcon = {
                        IconButton(onClick = actions.navigateUp) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    })
            }, bottomBar = {
                BottomAppBar(actions = {
                    // run operation button
                    IconButton(onClick = actions.operation) {
                        Icon(Icons.Default.Terminal, contentDescription = null)
                    }
                    // import button with a dropdown menu
                    Box {
                        // whether the menu is expanded
                        var importMenuShown by rememberSaveable { mutableStateOf(false) }
                        // import button
                        IconButton(onClick = { importMenuShown = true }) {
                            Icon(Icons.Default.FileUpload, contentDescription = null)
                        }
                        // menu
                        ImportMenu(
                            expanded = importMenuShown,
                            onDismissRequest = { importMenuShown = false },
                            onImportZip = actions.importZip,
                            onImportFolder = actions.importFolder
                        )
                    }
                }, floatingActionButton = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.End
                    ) {
                        // new profile button
                        ExtendedFloatingActionButton(text = { Text("Create new profile") },
                            icon = { Icon(Icons.Default.Add, contentDescription = null) },
                            onClick = actions.createProfile
                        )
                    }
                })
            }) { padding ->
                // box to use padding from the scaffold
                Box(modifier = Modifier.padding(padding)) {
                    ProfileList(shownProfile = null, setShownProfile = setShownProfile, expanded = false)
                }
            }
        } else {
            // profile details
            // use scaffold to position the top bar
            Scaffold(topBar = {
                TopAppBar(title = { Text("Profile Details") },
                    // back button
                    navigationIcon = {
                        // setting the shown profile to null goes back to the profile list
                        IconButton(onClick = { setShownProfile(null) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    })
            }) { padding ->
                // box to use padding from the scaffold
                Box(modifier = Modifier.padding(padding)) {
                    // check if a profile is selected
                    if (shownProfile != null) {
                        ProfileDetails(shownProfile = shownProfile, actions = profileActions)
                    } else {
                        // placeholder text
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("Select a profile to show details")
                        }
                    }
                }
            }
            // make the system back button go to the profile list instead of leaving the profile management screen
            BackHandler { setShownProfile(null) }
        }
    }
}

@Composable
private fun ImportMenu(
    expanded: Boolean, onDismissRequest: () -> Unit, onImportZip: () -> Unit, onImportFolder: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        // zip
        DropdownMenuItem(text = { Text("From .zip file on device") }, onClick = {
            onImportZip()
            onDismissRequest()
        }, leadingIcon = { Icon(Icons.Default.FolderZip, contentDescription = null) })
        // folder
        DropdownMenuItem(text = { Text("From folder on device") }, onClick = {
            onImportFolder()
            onDismissRequest()
        }, leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) })
    }
}

/**
 * Scrollable list of profiles. Each profile is shown on a [Card].
 *
 * @param shownProfile The currently shown profile (not the currently loaded profile in the app).
 * This profile's [Card] gets highlighted if [expanded] is `true`.
 * @param setShownProfile A callback to set [shownProfile].
 * @param expanded Whether the expanded layout is being used.
 * If `true`, the [shownProfile]'s [Card] gets highlighted to indicate that it is selected.
 * If `false`, each profile's [Card] will show a right arrow icon
 * to indicate that selecting it will open the details page.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileList(shownProfile: String?, setShownProfile: (String?) -> Unit, expanded: Boolean) {
    // get the settings for the whole app
    val appSettings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
    // scrollable column used instead of LazyColumn
    // so that all children's widths can be measured immediately to calculate parent width
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        // top padding inside the scrollable area
        Spacer(modifier = Modifier.height(12.dp))
        // show a card for each profile
        for (profile in appSettings.profiles) {
            Card(
                onClick = { setShownProfile(profile) }, colors =
                // highlight profile if it's selected and expanded layout is being used
                if (expanded && shownProfile == profile) {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                } else if (appSettings.currentProfile == profile) {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error)
                } else {
                    CardDefaults.cardColors()
                }
            ) {
                // show profile name on the left, right arrow icon on the right
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // profile name
                        Text(
                            profile,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (appSettings.currentProfile == profile) Color.Black else Color.White
                        )
                    }
                    // show right arrow icon if not using the expanded layout
                    if (!expanded) Icon(Icons.Default.ArrowRight, contentDescription = null)
                }
            }
        }
        // bottom padding inside the scrollable area
        Spacer(modifier = Modifier.height(12.dp))
    }
}
