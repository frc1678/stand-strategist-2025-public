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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
 * A [ProfileOperationOutput] for output to an existing profile. This will overwrite the existing profile.
 *
 * @param name A profile name to initially select.
 * @see exportFiles
 */
class OutputToProfile(name: String? = null) : ProfileOperationOutput() {
    /**
     * State of the selected profile name. `null` if no profile is selected.
     */
    private val selectedProfileState = MutableStateFlow(name)

    /**
     * State of the profile name loaded in the app. Used to ensure the current profile isn't being overwritten.
     */
    private val loadedProfileState = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ConfigUi() {
        // get the selected profile name state
        val selectedProfile by selectedProfileState.collectAsStateWithLifecycle()
        // dropdown menu state
        var expanded by rememberSaveable { mutableStateOf(false) }
        // get the app settings
        val settings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
        // run when UI is shown
        LaunchedEffect(settings.currentProfile) {
            // set which profile is loaded
            loadedProfileState.value = settings.currentProfile
        }
        // width is minimum intrinsic width so that warning text width matches dropdown width
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.width(IntrinsicSize.Min)) {
            // title
            Text("Overwrite existing profile", style = MaterialTheme.typography.titleMedium)
            // dropdown menu layout
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                // text field showing selected profile name
                OutlinedTextField(
                    readOnly = true,
                    value = selectedProfile ?: "Select profile",
                    onValueChange = {},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    // anchor to the ExposedDropdownMenu
                    modifier = Modifier.menuAnchor()
                )
                // dropdown menu listing profiles
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    // show item for each profile
                    settings.profiles.forEach { profile ->
                        DropdownMenuItem(
                            text = { Text(profile) },
                            onClick = {
                                selectedProfileState.value = profile
                                expanded = false
                            }
                        )
                    }
                }
            }
            // show warning if the selected profile is the currently loaded profile
            if (selectedProfile == settings.currentProfile) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // info icon
                    Icon(Icons.Default.Info, contentDescription = null)
                    // warning text
                    Text(
                        "Can't overwrite the current profile",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    override fun exportProfile(profile: Profile, context: Context) {
        // get selected profile
        val selectedProfile = selectedProfileState.value ?: error("Profile not selected")
        // ensure the selected profile isn't loaded
        if (selectedProfile == loadedProfileState.value) {
            error("Can't overwrite current profile")
        }
        // get the exported file contents as strings and iterate over the files
        profile.exportFiles().forEach { (fileName, data) ->
            // write to the file
            File(File(FilePaths.profilesFolder, selectedProfile), fileName).writeText(data)
        }
    }
}

@Preview
@Composable
private fun ConfigUiPreview() {
    StandStrategistTheme {
        CompositionLocalProvider(
            LocalAppSettings provides
                AppSettings(
                    AppSettings.AppSettings(
                        currentProfile = "profile1", profiles = listOf("profile1", "profile2", "profile3")
                    )
                )
        ) {
            Surface {
                Card(modifier = Modifier.padding(16.dp)) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        OutputToProfile().ConfigUi()
                    }
                }
            }
        }
    }
}
