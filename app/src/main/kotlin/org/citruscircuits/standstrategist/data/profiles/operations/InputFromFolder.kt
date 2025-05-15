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
import org.citruscircuits.standstrategist.data.profiles.io.importFiles
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme

/**
 * A [ProfileOperationInput] for input from a folder on the user's device.
 *
 * @see importFiles
 */
class InputFromFolder : ProfileOperationInput() {
    /**
     * State of the currently selected folder as a [DocumentFile]. `null` if no folder is selected.
     */
    private val folderState = MutableStateFlow<DocumentFile?>(null)

    @Composable
    override fun ConfigUi() {
        // get the selected folder state
        val folder by folderState.collectAsStateWithLifecycle()
        // get the app context
        val context = LocalContext.current
        // create a launcher for the system file picker to select a folder
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) {
                // convert folder URI to a DocumentFile
                it?.let { uri -> folderState.value = DocumentFile.fromTreeUri(context, uri) }
            }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // title
            Text("From folder on device", style = MaterialTheme.typography.titleMedium)
            if (folder != null) {
                // a folder has been selected
                Text("Folder selected: ${folder?.name}")
                // button to open the file picker
                OutlinedButton(onClick = { launcher.launch(null) }) {
                    Text("Change")
                }
            } else {
                // no folder has been selected
                // button to open the file picker
                Button(onClick = { launcher.launch(null) }) {
                    Text("Select folder")
                }
            }
        }
    }

    override fun importProfile(context: Context) =
        importFiles(
            // get all files in the folder to create a map of file name to file contents
            folderState.value?.listFiles()?.associate { file ->
                file.name!! to
                    if (file.isFile) {
                        // read file contents
                        context.contentResolver.openInputStream(file.uri).use { it!!.readBytes() }
                    } else {
                        // empty byte array
                        byteArrayOf()
                    }
            } ?: error("Folder not selected")
        )
}

@Preview
@Composable
private fun ConfigUiPreview() {
    StandStrategistTheme {
        Surface {
            Card(modifier = Modifier.padding(16.dp)) {
                Box(modifier = Modifier.padding(16.dp)) {
                    InputFromFolder().ConfigUi()
                }
            }
        }
    }
}
