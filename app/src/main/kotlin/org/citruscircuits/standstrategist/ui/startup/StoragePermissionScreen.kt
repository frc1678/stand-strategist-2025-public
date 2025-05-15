package org.citruscircuits.standstrategist.ui.startup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.citruscircuits.standstrategist.R
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme
import org.citruscircuits.standstrategist.util.bold

/**
 * Screen for getting necessary storage permissions from the user.
 *
 * @param onRequestStoragePermission Callback to open the settings page for requesting the storage permission.
 */
@Composable
fun StoragePermissionScreenContent(onRequestStoragePermission: () -> Unit) {
    Scaffold { padding ->
        // align items in the center
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier =
                    Modifier
                        // limit width
                        .widthIn(max = 480.dp)
                        .padding(padding)
                        .padding(24.dp)
            ) {
                // title
                Text("Storage permissions", style = MaterialTheme.typography.headlineLarge)
                // info text
                Text(message(appName = stringResource(R.string.app_name)), style = MaterialTheme.typography.bodyLarge)
                // settings button
                Button(onClick = onRequestStoragePermission, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open settings")
                }
            }
        }
    }
}

/**
 * Constructs the info text.
 *
 * @param appName The name of this app.
 * @return The info text as an [AnnotatedString].
 */
@Composable
private fun message(appName: String) =
    buildAnnotatedString {
        append("To function properly, $appName requires a special permission called ")
        bold { append("All files access") }
        append(".\n\n")
        append("To enable this, tap the button below to open the system settings. Then, find ")
        bold { append(appName) }
        append(" and turn on ")
        bold { append("All files access") }
        append(".")
    }

@Preview(showSystemUi = true)
@Composable
private fun StoragePermissionScreenPreview() {
    StandStrategistTheme {
        StoragePermissionScreenContent {}
    }
}
