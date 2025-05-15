package org.citruscircuits.standstrategist.data.profiles.operations

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import org.citruscircuits.standstrategist.LocalAppSettings
import org.citruscircuits.standstrategist.data.AppSettings
import org.citruscircuits.standstrategist.data.FilePaths
import org.citruscircuits.standstrategist.data.profiles.Profile
import org.citruscircuits.standstrategist.data.profiles.io.exportFiles
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme
import java.io.File

/**
 * A [ProfileOperationOutput] for output to a new profile.
 *
 * @param getAppSettings Getter for the app's settings.
 * @see exportFiles
 */
class OutputToNewProfile(private val getAppSettings: () -> AppSettings) : ProfileOperationOutput() {
    /**
     * State of the new profile's name.
     */
    private val profileNameState = MutableStateFlow("")

    @Composable
    override fun ConfigUi() {
        // get the profile name state
        val profileName by profileNameState.collectAsStateWithLifecycle()
        // get the app settings
        val settings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.width(IntrinsicSize.Min)) {
            // title
            Text("Write to new profile", style = MaterialTheme.typography.titleMedium)
            // text field to edit profile name
            OutlinedTextField(
                value = profileName,
                onValueChange = { profileNameState.value = it },
                label = { Text("Profile name") }
            )
            // show warning if the profile name already exists
            if (profileName in settings.profiles) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // info icon
                    Icon(Icons.Default.Info, contentDescription = null)
                    // warning text
                    Text("Profile with this name already exists", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    override fun exportProfile(profile: Profile, context: Context) {
        val appSettings = getAppSettings()
        // check if a profile name was entered
        val profileName = profileNameState.value.takeIf { it.isNotEmpty() } ?: error("Profile name can't be empty")
        // check if the profile exists
        if (profileName in appSettings.settings.value.profiles) error("Profile already exists")
        // try making the folder for the profile
        val createdDir = File(FilePaths.profilesFolder, profileName).mkdirs()
        // check if making the folder worked, since folder names can be invalid
        if (!createdDir) error("Failed creating profile, try changing the name")
        // get the exported file contents as strings and iterate over the files
        profile.exportFiles().forEach { (fileName, data) ->
            // write to the file
            File(File(FilePaths.profilesFolder, profileName), fileName).writeText(data)
        }
        // update the profile list in the settings
        appSettings.updateSettings(
            appSettings.settings.value.copy(profiles = appSettings.settings.value.profiles + profileName)
        )
    }
}

@Preview
@Composable
private fun ConfigUiPreview() {
    val appSettings =
        AppSettings(
            AppSettings.AppSettings(currentProfile = "profile1", profiles = listOf("profile1", "profile2", "profile3"))
        )
    StandStrategistTheme {
        CompositionLocalProvider(LocalAppSettings provides appSettings) {
            Surface {
                Card(modifier = Modifier.padding(16.dp)) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        OutputToNewProfile { appSettings }.ConfigUi()
                    }
                }
            }
        }
    }
}
