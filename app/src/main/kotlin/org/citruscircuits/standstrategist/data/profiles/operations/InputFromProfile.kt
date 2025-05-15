package org.citruscircuits.standstrategist.data.profiles.operations

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import org.citruscircuits.standstrategist.LocalAppSettings
import org.citruscircuits.standstrategist.data.AppSettings
import org.citruscircuits.standstrategist.data.FilePaths
import org.citruscircuits.standstrategist.data.profiles.Profile
import org.citruscircuits.standstrategist.data.profiles.io.importFiles
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme
import java.io.File

/**
 * A [ProfileOperationInput] for input from an existing [Profile].
 *
 * @param name A profile name to initially select.
 * @see importFiles
 */
class InputFromProfile(name: String? = null) : ProfileOperationInput() {
    /**
     * State of the currently selected [Profile] name. `null` if no [Profile] is selected.
     */
    private val selectedProfileState = MutableStateFlow(name)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ConfigUi() {
        // get the selected profile name state
        val selectedProfile by selectedProfileState.collectAsStateWithLifecycle()
        // dropdown state
        var expanded by rememberSaveable { mutableStateOf(false) }
        // get the app settings
        val settings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // title
            Text("From existing profile", style = MaterialTheme.typography.titleMedium)
            // dropdown menu layout
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                // text field displaying the current selection
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
        }
    }

    override fun importProfile(context: Context) =
        importFiles(
            // get folder for the selected profile
            File(FilePaths.profilesFolder, selectedProfileState.value ?: error("Profile not selected"))
                // create a map of file name to file contents
                .listFiles()!!.associate { file -> file.name to file.readBytes() }
        )
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
                        InputFromProfile().ConfigUi()
                    }
                }
            }
        }
    }
}
