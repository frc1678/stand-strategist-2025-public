package org.citruscircuits.standstrategist.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.citruscircuits.standstrategist.LocalAppSettings

/**
 * Screen for details of a profile. Shown as part of the [ProfileManagementScreen].
 */
@Composable
fun ProfileDetails(shownProfile: String, actions: ProfileActions) {
    // get the settings for the whole app
    val appSettings by LocalAppSettings.current.settings.collectAsStateWithLifecycle()
    // scrollable
    Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.padding(36.dp)) {
            // title
            Text("Profile: $shownProfile", style = MaterialTheme.typography.titleLarge)
            // switch
            if (appSettings.currentProfile == shownProfile) {
                Button(onClick = {}, enabled = false) {
                    Icon(Icons.Default.SwitchAccount, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("You're using this profile")
                }
            } else {
                Button(onClick = { actions.switch(shownProfile) }) {
                    Icon(Icons.Default.SwitchAccount, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Switch to this profile")
                }
            }
            // export button with a dropdown menu
            Box {
                // whether the menu is expanded
                var exportDropdownShown by rememberSaveable { mutableStateOf(false) }
                OutlinedButton(onClick = { exportDropdownShown = true }) {
                    Icon(Icons.Default.Output, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export profile...")
                }
                // menu
                ExportMenu(
                    profile = shownProfile,
                    expanded = exportDropdownShown,
                    onDismissRequest = { exportDropdownShown = false },
                    actions = actions
                )
            }
            // rename
            OutlinedButton(onClick = { actions.rename(shownProfile) }) {
                Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rename profile")
            }
            // delete
            if (appSettings.currentProfile == shownProfile) {
                OutlinedButton(onClick = {}, enabled = false) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Can't delete the current profile")
                }
            } else {
                OutlinedButton(onClick = { actions.delete(shownProfile) }) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete profile")
                }
            }
            // title
            Text("Profile operations", style = MaterialTheme.typography.titleMedium)
            // merge from here
            OutlinedButton(onClick = { actions.mergeFrom(shownProfile) }) {
                Icon(Icons.Default.MergeType, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Merge this profile into another profile")
            }
            // merge to here
            if (appSettings.currentProfile == shownProfile) {
                OutlinedButton(onClick = {}, enabled = false) {
                    Icon(Icons.Default.MergeType, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Can't overwrite the current profile")
                }
            } else {
                OutlinedButton(onClick = { actions.mergeInto(shownProfile) }) {
                    Icon(Icons.Default.MergeType, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Merge another profile into this profile")
                }
            }
            // duplicate
            OutlinedButton(onClick = { actions.duplicate(shownProfile) }) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Duplicate this profile")
            }
        }
    }
}

/**
 * Actions holder for a specific profile displayed in the profile details screen.
 *
 * @param switch Action to switch to this profile.
 * @param rename Action to rename this profile.
 * @param delete Action to delete this profile.
 * @param mergeFrom Action to merge this profile into another profile.
 * @param mergeInto Action to merge another profile into this profile.
 * @param duplicate Action to duplicate this profile.
 * @param exportZip Action to export this profile as a `.zip` file.
 * @param exportFolder Action to export this profile as a folder.
 * @param exportOds Action to export this profile as a `.ods` spreadsheet.
 */
data class ProfileActions(
    val switch: (profile: String) -> Unit,
    val rename: (profile: String) -> Unit,
    val delete: (profile: String) -> Unit,
    val mergeFrom: (profile: String) -> Unit,
    val mergeInto: (profile: String) -> Unit,
    val duplicate: (profile: String) -> Unit,
    val exportZip: (profile: String) -> Unit,
    val exportFolder: (profile: String) -> Unit,
    val exportOds: (profile: String) -> Unit
)

@Composable
private fun ExportMenu(profile: String, expanded: Boolean, onDismissRequest: () -> Unit, actions: ProfileActions) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        // zip
        DropdownMenuItem(
            text = { Text("To .zip file on device") },
            onClick = { actions.exportZip(profile) },
            leadingIcon = { Icon(Icons.Default.FolderZip, contentDescription = null) }
        )
        // folder
        DropdownMenuItem(
            text = { Text("To folder on device") },
            onClick = { actions.exportFolder(profile) },
            leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) }
        )
        // ods
        DropdownMenuItem(
            text = { Text("To .ods spreadsheet on device") },
            onClick = { actions.exportOds(profile) },
            leadingIcon = { Icon(Icons.Default.TableView, contentDescription = null) }
        )
    }
}
