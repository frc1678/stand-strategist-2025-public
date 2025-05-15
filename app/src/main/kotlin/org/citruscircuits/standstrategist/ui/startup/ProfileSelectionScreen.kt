package org.citruscircuits.standstrategist.ui.startup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import org.citruscircuits.standstrategist.LocalAppSettings
import org.citruscircuits.standstrategist.LocalWindowSizeClass
import org.citruscircuits.standstrategist.data.AppSettings
import org.citruscircuits.standstrategist.ui.dialogs.NewProfileDialog
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme

/**
 * Screen for selecting a profile.
 *
 * @param onSelectProfile Called when a profile is selected.
 * @param checkProfileName Checks a profile name to ensure it is valid.
 * @param onCreateNewProfile Called when a new profile is to be created.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSelectionScreenContent(onSelectProfile: (String) -> Unit, onCreateNewProfile: (String) -> Unit) {
    val appSettings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
    // whether the new profile dialog is shown
    var creatingProfile by rememberSaveable { mutableStateOf(false) }
    if (creatingProfile) {
        // new profile dialog
        AlertDialog(onDismissRequest = { creatingProfile = false }) {
            NewProfileDialog(onCreateProfile = onCreateNewProfile, onDismiss = { creatingProfile = false })
        }
    }
    Scaffold { padding ->
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
                Text("Choose a profile", style = MaterialTheme.typography.headlineLarge)
                // check window width
                if (LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Expanded) {
                    // wide layout
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        // profile list
                        if (appSettings.profiles.isNotEmpty()) {
                            Box(modifier = Modifier.width(IntrinsicSize.Max)) { ProfileList(onSelectProfile) }
                        }
                        // buttons
                        Buttons(onCreateProfile = { creatingProfile = true }, onSelectProfile = onSelectProfile)
                    }
                } else {
                    // normal layout
                    // profile list
                    if (appSettings.profiles.isNotEmpty()) ProfileList(onSelectProfile, modifier = Modifier.weight(1f))
                    // buttons
                    Buttons(onCreateProfile = { creatingProfile = true }, onSelectProfile = onSelectProfile)
                }
            }
        }
    }
}

/**
 * Card showing a scrollable list of profiles.
 *
 * @param onSelectProfile Called when a profile is selected.
 */
@Composable
private fun ProfileList(onSelectProfile: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        // title
        Text("Profiles", style = MaterialTheme.typography.labelLarge)
        // card
        OutlinedCard {
            // scrollable column
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                val appSettings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
                // iterate over profiles
                for (profile in appSettings.profiles) {
                    // show profile
                    ListItem(
                        headlineContent = { Text(profile) },
                        modifier = Modifier.clickable { onSelectProfile(profile) }
                    )
                    Divider()
                }
            }
        }
    }
}

/**
 * Buttons for creating a new profile or using the last profile.
 *
 * @param onCreateProfile Called when the new profile button is clicked.
 * @param onSelectProfile Called when the select last profile button is clicked.
 */
@Composable
private fun Buttons(onCreateProfile: () -> Unit, onSelectProfile: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // create new profile button
        OutlinedButton(onClick = onCreateProfile, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create new profile")
        }
        val appSettings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
        // use last profile button
        if (appSettings.currentProfile != null) {
            Button(
                onClick = { onSelectProfile(appSettings.currentProfile!!) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Login, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use last profile: ${appSettings.currentProfile}")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ProfileSelectionScreenPreview() {
    CompositionLocalProvider(LocalAppSettings provides AppSettings()) {
        StandStrategistTheme {
            Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                ProfileSelectionScreenContent(onSelectProfile = {}, onCreateNewProfile = {})
            }
        }
    }
}
