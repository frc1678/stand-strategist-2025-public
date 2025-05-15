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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCallsBuilder
import com.ramcosta.composedestinations.manualcomposablecalls.dialogComposable
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.citruscircuits.standstrategist.MainActivityViewModel
import org.citruscircuits.standstrategist.data.FilePaths
import org.citruscircuits.standstrategist.data.profiles.io.exportZip
import org.citruscircuits.standstrategist.data.profiles.io.importFiles
import org.citruscircuits.standstrategist.ui.MainNavGraph
import org.citruscircuits.standstrategist.ui.destinations.DeleteProfileDialogDestination
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme
import java.io.File

/**
 * Manual composable call for the [DeleteProfileDialog].
 *
 * @param viewModel A [MainActivityViewModel] instance.
 */
fun ManualComposableCallsBuilder.deleteProfileDialog(viewModel: MainActivityViewModel) {
    dialogComposable(DeleteProfileDialogDestination) {
        // call the composable
        DeleteProfileDialog(
            // get nav arg
            profile = navArgs.profile,
            onConfirm = {
                // run in IO thread
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    // import profile to be deleted
                    val profile =
                        File(FilePaths.profilesFolder, navArgs.profile).listFiles()
                            ?.let { importFiles(it.associate { file -> file.name to file.readBytes() }) }
                    // export profile as .zip in trash folder
                    profile?.let {
                        File(FilePaths.trash.apply { mkdirs() }, "${Clock.System.now().toEpochMilliseconds()}.zip")
                            .writeBytes(it.exportZip())
                    }
                    // delete profile folder
                    File(FilePaths.profilesFolder, navArgs.profile).deleteRecursively()
                }
                // get app settings
                val appSettings = viewModel.appSettings.settings.value
                // remove profile from the profile list
                viewModel.appSettings.updateSettings(
                    newSettings = appSettings.copy(profiles = appSettings.profiles.filterNot { it == navArgs.profile })
                )
            },
            // exit dialog
            onDismiss = destinationsNavigator::navigateUp
        )
    }
}

/**
 * Dialog confirming that the [profile] should be deleted.
 * If the profile is deleted, it is backed up to the trash folder.
 *
 * @param profile The profile to be deleted.
 * @param onConfirm Callback to delete the profile.
 * @param onDismiss Callback to dismiss the dialog.
 */
@MainNavGraph
@Destination(style = DestinationStyle.Dialog::class)
@Composable
fun DeleteProfileDialog(profile: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
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
            Text("Delete profile $profile", style = MaterialTheme.typography.headlineMedium)
            // warning text
            Text("Are you sure you want to delete this profile? It will be backed up.")
            // cancel/confirm buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                modifier = Modifier.fillMaxWidth()
            ) {
                // cancel button
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                // confirm button
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text("Yes, delete profile $profile")
                }
            }
        }
    }
}

@Preview
@Composable
private fun DeleteProfileDialogPreview() {
    StandStrategistTheme {
        DeleteProfileDialog(profile = "profile1", onConfirm = {}, onDismiss = {})
    }
}
