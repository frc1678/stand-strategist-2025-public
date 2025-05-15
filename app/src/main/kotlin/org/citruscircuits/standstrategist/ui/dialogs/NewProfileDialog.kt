package org.citruscircuits.standstrategist.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCallsBuilder
import com.ramcosta.composedestinations.manualcomposablecalls.dialogComposable
import com.ramcosta.composedestinations.spec.DestinationStyle
import org.citruscircuits.standstrategist.LocalAppSettings
import org.citruscircuits.standstrategist.MainActivityViewModel
import org.citruscircuits.standstrategist.data.AppSettings
import org.citruscircuits.standstrategist.data.FilePaths
import org.citruscircuits.standstrategist.ui.MainNavGraph
import org.citruscircuits.standstrategist.ui.destinations.NewProfileDialogDestination
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme
import org.citruscircuits.standstrategist.util.cleanFileName
import java.io.File

/**
 * Manual composable call for the [NewProfileDialog].
 *
 * @param viewModel A [MainActivityViewModel] instance.
 */
fun ManualComposableCallsBuilder.newProfileDialog(viewModel: MainActivityViewModel) {
    dialogComposable(NewProfileDialogDestination) {
        // get app settings
        val settings by viewModel.appSettings.settings.collectAsStateWithLifecycle()
        // call the composable
        NewProfileDialog(
            // the profile doesn't exist yet
            onCreateProfile = {
                // create the folder
                File(FilePaths.profilesFolder, it).mkdirs()
                // update app settings
                viewModel.appSettings.updateSettings(
                    settings.copy(currentProfile = it, profiles = settings.profiles + it)
                )
                // reload the app, automatically using the new profile
                viewModel.loadApp(skipProfileSelection = true)
            },
            // exit dialog
            onDismiss = { destinationsNavigator.popBackStack() })
    }
}

/**
 * Dialog for creating a new profile.
 *
 * @param onCreateProfile Callback to create the profile and reload the app with the new profile.
 * @param onDismiss Called when the dialog is dismissed.
 */
@MainNavGraph
@Destination(style = DestinationStyle.Dialog::class)
@Composable
fun NewProfileDialog(onCreateProfile: (String) -> Unit, onDismiss: () -> Unit) {
    // get app settings
    val appSettings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
    // name of the new profile
    var profileName by rememberSaveable { mutableStateOf("") }
    // check if the profile name exists already
    val profileNameValid = profileName !in appSettings.profiles
    // whether the profile is being created
    var creatingProfile by rememberSaveable { mutableStateOf(false) }
    // main card
    Card(
        // make shape look more like a dialog
        shape = MaterialTheme.shapes.large,
        // background color
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier
            // match content width
            .width(IntrinsicSize.Max)
            // keep content out of system bars
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // scrollable column
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // title
            Text("Create new profile", style = MaterialTheme.typography.headlineMedium)
            // profile name field
            OutlinedTextField(value = profileName,
                onValueChange = { profileName = cleanFileName(it) },
                enabled = !creatingProfile,
                label = { Text("Profile name") },
                supportingText = { Text(
                    if (profileName.length <= 64) ""
                    else if (profileNameValid) "Profile name must be under 64 characters."
                    else "Profile name must be unique."
                ) },
                isError = profileName.length > 64
            )
            // Cancel/Create buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End), modifier = Modifier.fillMaxWidth()
            ) {
                // Cancel button
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                // Create button
                Button(
                    onClick = {
                        // start creating
                        creatingProfile = true
                        // check and then create the profile or stop creation
                        if (profileNameValid) onCreateProfile(profileName) else creatingProfile = false
                    },
                    // if creating the profile already, disable the button
                    // also disable if the profile name is invalid
                    enabled = !creatingProfile && profileNameValid && profileName.length <= 64
                ) {
                    Text("Create")
                }
            }
        }
    }
}

@Preview
@Composable
private fun NewProfileDialogPreview() {
    StandStrategistTheme {
        val appSettings = AppSettings(
            AppSettings.AppSettings(
                currentProfile = "profile1", profiles = listOf("profile1", "profile2", "profile3")
            )
        )
        CompositionLocalProvider(LocalAppSettings provides appSettings) {
            NewProfileDialog(onCreateProfile = {}, onDismiss = {})
        }
    }
}
