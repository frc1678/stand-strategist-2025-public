package org.citruscircuits.standstrategist.ui.startup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.citruscircuits.standstrategist.LocalAppSettings
import org.citruscircuits.standstrategist.data.AppSettings
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme

/**
 * Screen for selecting a match schedule for the currently selected profile.
 *
 * @param onOpenFilePicker Callback to open the system file picker dialog.
 * @param onSwitchProfile Callback to go back to the profile selection screen.
 */
@Composable
fun MatchScheduleSelectionScreen(onOpenFilePicker: () -> Unit, onSwitchProfile: () -> Unit) {
    Scaffold { padding ->
        // align items in the center
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier =
                    Modifier
                        .width(IntrinsicSize.Max)
                        .padding(padding)
                        .padding(24.dp)
            ) {
                // title
                Text("Select match schedule", style = MaterialTheme.typography.headlineLarge)
                // info text
                Text(
                    "Select a match schedule file from your device to continue.",
                    style = MaterialTheme.typography.bodyLarge
                )
                // current profile indicator
                val settings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
                Text("Current profile: ${settings.currentProfile}")
                // switch profile button
                OutlinedButton(onClick = onSwitchProfile) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Switch profile")
                }
                Spacer(modifier = Modifier.weight(1f))
                // file picker button
                Button(onClick = onOpenFilePicker, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.UploadFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open file picker")
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MatchScheduleSelectionScreenPreview() {
    CompositionLocalProvider(LocalAppSettings provides AppSettings()) {
        StandStrategistTheme {
            MatchScheduleSelectionScreen(onOpenFilePicker = {}, onSwitchProfile = {})
        }
    }
}
