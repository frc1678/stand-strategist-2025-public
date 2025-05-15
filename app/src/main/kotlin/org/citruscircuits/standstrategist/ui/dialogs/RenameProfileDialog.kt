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
import org.citruscircuits.standstrategist.ui.destinations.RenameProfileDialogDestination
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme
import org.citruscircuits.standstrategist.util.cleanFileName
import java.io.File

/**
 * Manual composable call for the [RenameProfileDialog].
 *
 * @param viewModel A [MainActivityViewModel] instance.
 */
fun ManualComposableCallsBuilder.renameProfileDialog(viewModel: MainActivityViewModel) {
    dialogComposable(RenameProfileDialogDestination) {
        RenameProfileDialog(
            // get nav arg
            existingName = navArgs.existingName,
            onRenameProfile = { newName ->
                // rename folder
                File(FilePaths.profilesFolder, navArgs.existingName).renameTo(File(FilePaths.profilesFolder, newName))
                // get app settings
                val appSettings = viewModel.appSettings.settings.value
                // update app settings
                viewModel.appSettings.updateSettings(
                    newSettings =
                        appSettings.copy(
                            // rename current profile if necessary
                            currentProfile =
                                if (appSettings.currentProfile == navArgs.existingName) {
                                    newName
                                } else {
                                    appSettings.currentProfile
                                },
                            // rename profile in profile list matching name
                            profiles = appSettings.profiles.map { if (it == navArgs.existingName) newName else it }
                        )
                )
            },
            // exit dialog
            onDismiss = { destinationsNavigator.navigateUp() }
        )
    }
}

/**
 * Dialog for renaming an existing profile.
 *
 * @param existingName The name of the existing profile.
 * @param onRenameProfile Callback to rename the profile.
 * @param onDismiss Called when the dialog is dismissed.
 */
@MainNavGraph
@Destination(style = DestinationStyle.Dialog::class)
@Composable
fun RenameProfileDialog(existingName: String, onRenameProfile: (String) -> Unit, onDismiss: () -> Unit) {
    // get app settings
    val appSettings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
    // new name of the profile
    var newName by rememberSaveable { mutableStateOf(existingName) }
    // get whether the new name is valid
    val newNameValid = newName !in appSettings.profiles
    // whether the profile is being renamed
    var renamingProfile by rememberSaveable { mutableStateOf(false) }
    // main card
    Card(
        // make shape look more like a dialog
        shape = MaterialTheme.shapes.large,
        // background color
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier =
            Modifier
                // match content width
                .width(IntrinsicSize.Max)
                // keep content out of system bars
                .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // scrollable column
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
        ) {
            // title
            Text("Rename profile $existingName", style = MaterialTheme.typography.headlineMedium)
            // profile name field
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = cleanFileName(it) },
                enabled = !renamingProfile,
                label = { Text("New profile name") }
            )
            // Cancel/Create buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Cancel button
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                // Rename button
                Button(
                    onClick = {
                        // start renaming
                        renamingProfile = true
                        // check and then rename the profile and dismiss the dialog or stop renaming
                        if (newNameValid) {
                            onRenameProfile(newName)
                            onDismiss()
                        } else {
                            renamingProfile = false
                        }
                    },
                    // if renaming the profile already, disable the button
                    // also disable if the new name is invalid
                    enabled = !renamingProfile && newNameValid
                ) {
                    Text("Rename")
                }
            }
        }
    }
}

@Preview
@Composable
private fun RenameProfileDialogPreview() {
    StandStrategistTheme {
        val appSettings =
            AppSettings(
                AppSettings.AppSettings(
                    currentProfile = "profile1",
                    profiles = listOf("profile1", "profile2", "profile3")
                )
            )
        CompositionLocalProvider(LocalAppSettings provides appSettings) {
            RenameProfileDialog(existingName = "profile1", onRenameProfile = {}, onDismiss = {})
        }
    }
}
