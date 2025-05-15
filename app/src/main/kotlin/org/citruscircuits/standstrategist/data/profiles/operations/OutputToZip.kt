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
import kotlinx.datetime.Clock
import org.citruscircuits.standstrategist.data.profiles.Profile
import org.citruscircuits.standstrategist.data.profiles.io.exportZip
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme

/**
 * A [ProfileOperationOutput] for output to a `.zip` file.
 *
 * @see exportZip
 */
class OutputToZip : ProfileOperationOutput() {
    /**
     * State of the selected folder to export the `.zip` file in. `null` if no folder has been selected.
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
                // convert the URI to a DocumentFile
                it?.let { uri -> folderState.value = DocumentFile.fromTreeUri(context, uri) }
            }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // title
            Text("To .zip file on device", style = MaterialTheme.typography.titleMedium)
            if (folder != null) {
                // a folder has been selected
                Text("Location selected: ${folder?.name}")
                // button to open the system file picker
                OutlinedButton(onClick = { launcher.launch(null) }) {
                    Text("Change")
                }
            } else {
                // no folder has been selected
                // button to open the system file picker
                Button(onClick = { launcher.launch(null) }) {
                    Text("Select location")
                }
            }
        }
    }

    override fun exportProfile(profile: Profile, context: Context) {
        // create file inside selected folder
        folderState.value?.createFile(
            "application/zip",
            // file name with timestamp
            "${Clock.System.now().toEpochMilliseconds()}.stand-strategist.zip"
        )?.let { file ->
            // write to the file
            context.contentResolver.openOutputStream(file.uri)?.use { stream -> stream.write(profile.exportZip()) }
        } ?: error("Folder not selected")
    }
}

@Preview
@Composable
private fun ConfigUiPreview() {
    StandStrategistTheme {
        Surface {
            Card(modifier = Modifier.padding(16.dp)) {
                Box(modifier = Modifier.padding(16.dp)) {
                    OutputToZip().ConfigUi()
                }
            }
        }
    }
}
