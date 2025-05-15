package org.citruscircuits.standstrategist.data.profiles.operations

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import org.citruscircuits.standstrategist.data.profiles.Profile
import org.citruscircuits.standstrategist.data.profiles.io.importZip
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme

/**
 * A [ProfileOperationInput] for input from a `.zip` file on the user's device.
 *
 * @see importZip
 */
class InputFromZip : ProfileOperationInput() {
    /**
     * State of the currently selected `.zip` file location. `null` if no location is selected.
     */
    private val fileState = MutableStateFlow<DocumentFile?>(null)

    @Composable
    override fun ConfigUi() {
        // get the selected file location state
        val file by fileState.collectAsStateWithLifecycle()
        // get the app context
        val context = LocalContext.current
        // create a launcher for the system file picker to select a .zip file
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) {
                // convert the URI to a DocumentFile
                it?.let { uri -> fileState.value = DocumentFile.fromSingleUri(context, uri) }
            }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // title
            Text("From .zip file on device", style = MaterialTheme.typography.titleMedium)
            if (file != null) {
                // a file has been selected
                Text("File selected: ${file?.name}")
                // button to open the file picker
                OutlinedButton(onClick = { launcher.launch(arrayOf("application/zip")) }) {
                    Text("Change")
                }
            } else {
                // no file has been selected
                // button to open the file picker
                Button(onClick = { launcher.launch(arrayOf("application/zip")) }) {
                    Text("Select file")
                }
            }
        }
    }

    override fun importProfile(context: Context): Profile {
        // read from the .zip file
        val data =
            fileState.value?.let {
                context.contentResolver.openInputStream(it.uri)?.use { stream -> stream.readBytes() }
            } ?: error("Zip file not selected")
        // import from the .zip file's contents
        return importZip(data)
    }
}

@Preview
@Composable
private fun ConfigUiPreview() {
    StandStrategistTheme {
        Surface {
            Card(modifier = Modifier.padding(16.dp)) {
                Box(modifier = Modifier.padding(16.dp)) {
                    InputFromZip().ConfigUi()
                }
            }
        }
    }
}
