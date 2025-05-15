package org.citruscircuits.standstrategist.ui.collection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.citruscircuits.standstrategist.LocalMatchSchedule
import org.citruscircuits.standstrategist.LocalProfileSettings
import org.citruscircuits.standstrategist.data.Alliance
import org.citruscircuits.standstrategist.data.profiles.Profile


/**
 * EditMatchScheduleDialog() creates a popup with an editable version of the match schedule.
 * @param onCancelRequest - the action that occurs when the the popup is closed
 **/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditMatchScheduleDialog(onCancelRequest: () -> Unit) {
    // Creates the dialog/popup that displays the editable match schedule
    Dialog(onDismissRequest = { onCancelRequest() }) {
        // Current match schedule
        val matchSchedule by LocalMatchSchedule.current.schedule.collectAsStateWithLifecycle()
        // Current profile's settings
        val profileSettings by LocalProfileSettings.current.profileSettings.collectAsStateWithLifecycle()
        // Whether or not the add match section is visible
        val addMatchIsVisible = remember { mutableStateOf(false) }
        // Whether or not the edit match section is visible
        val editMatchIsVisible = remember { mutableStateOf("") }
        var teamsInMatch = remember { listOf(String()) }
        var searchQuery by rememberSaveable { mutableStateOf("") }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(700.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            //Displays the match schedule
            LazyColumn(
                contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
                modifier = Modifier.fillMaxWidth()
            ) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                    ) {
                        Row (modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //Creates search bar for match #s
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search...") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier =
                                Modifier
                                    .background(MaterialTheme.colorScheme.surface)
                            )
                            //Add new match button
                            Button(
                                onClick = { addMatchIsVisible.value = true }
                            ) {
                                Text("Add New Match")
                            }
                        }
                    }
                    if (addMatchIsVisible.value) {
                        Row {
                            MatchEditEntry(true, "", teamsInMatch,
                                onClose = { addMatchIsVisible.value = false } // Reset visibility here
                            )
                        }
                    }
                }

                // displays the match schedule
                for ((matchNumber, matchObj) in matchSchedule) {
                    val teams =
                        matchObj.teams.filter { it.color == profileSettings.alliance }.joinToString { it.number }
                    // filters matches by inputted search query
                    if (searchQuery in matchNumber) {
                        item {
                            ListItem(
                                headlineContent = { Text(matchNumber) },
                                supportingContent = { Text(teams) },
                                modifier =
                                Modifier.clickable {
                                    editMatchIsVisible.value = matchNumber
                                    teamsInMatch = matchSchedule[matchNumber]?.teams?.map { it.number }!!
                                }
                            )

                        }
                        // if the match has been clicked, display the edit match section for that match
                        if (editMatchIsVisible.value == matchNumber) {
                            item {
                                MatchEditEntry(false, matchNumber, teamsInMatch,
                                    onClose = { editMatchIsVisible.value = "" }) // Reset visibility here
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section that allows the user to edit or add a match
 * @param newMatch - True if adding a new match, false if editing a pre-existing match
 * @param matchNumber - The match number that is currently selected
 * @param teamsInMatch - The teams in the match that is currently selected
 * @param onClose - What happens when the match edit section is closed
 */
@Composable
fun MatchEditEntry(newMatch: Boolean, matchNumber: String, teamsInMatch: List<String>, onClose: () -> Unit) {
    val matchSchedule = LocalMatchSchedule.current
    val schedule by matchSchedule.schedule.collectAsStateWithLifecycle()
    var newMatchNumber by rememberSaveable { mutableStateOf(matchNumber) }
    // hidden (true/false) determines whether or not the edit match info popup is hidden or not
    var visible by remember { mutableStateOf(true) }
    var editedTeamList = teamsInMatch.toMutableList()
    // If creating a new match, sets the default of the new team list to empty strings as placeholders
    if(newMatch) {
        editedTeamList = mutableListOf("", "", "", "", "", "")
    }
    var newTeamOne by rememberSaveable { mutableStateOf(editedTeamList[0]) }
    var newTeamTwo by rememberSaveable { mutableStateOf(editedTeamList[1]) }
    var newTeamThree by rememberSaveable { mutableStateOf(editedTeamList[2]) }
    var newTeamFour by rememberSaveable { mutableStateOf(editedTeamList[3]) }
    var newTeamFive by rememberSaveable { mutableStateOf(editedTeamList[4]) }
    var newTeamSix by rememberSaveable { mutableStateOf(editedTeamList[5]) }

    // Edit/add match section - only shows after a corresponding match #/add button has been clicked
    if (visible) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp))
                .border(BorderStroke(2.dp, SolidColor(Color.Gray)))
                .padding(5.dp)
        ) {
            Text("Match Number", fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    value = newMatchNumber,
                    onValueChange = { newMatchNumber = it },
                    placeholder = { Text("New Match Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier =
                    Modifier
                        .width(520.dp)
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
            Text("Blue Alliance Teams", fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    value = newTeamOne,
                    onValueChange = { if (it.length <= 6) newTeamOne = it },
                    placeholder = { Text("Team #") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier =
                    Modifier
                        .padding(8.dp)
                        .width(150.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
                OutlinedTextField(
                    value = newTeamTwo,
                    onValueChange = { if (it.length <= 6) newTeamTwo = it },
                    placeholder = { Text("Team #") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier =
                    Modifier
                        .padding(8.dp)
                        .width(150.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
                OutlinedTextField(
                    value = newTeamThree,
                    onValueChange = { if (it.length <= 6) newTeamThree = it },
                    placeholder = { Text("Team #") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier =
                    Modifier
                        .padding(8.dp)
                        .width(150.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
            Text("Red Alliance Teams", fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    value = newTeamFour,
                    onValueChange = { if (it.length <= 6) newTeamFour = it },
                    placeholder = { Text("Team #") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier =
                    Modifier
                        .padding(8.dp)
                        .width(150.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
                OutlinedTextField(
                    value = newTeamFive,
                    onValueChange = { if (it.length <= 6) newTeamFive = it },
                    placeholder = { Text("Team #") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier =
                    Modifier
                        .padding(8.dp)
                        .width(150.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
                OutlinedTextField(
                    value = newTeamSix,
                    onValueChange = { if (it.length <= 6) newTeamSix = it },
                    placeholder = { Text("Team #") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier =
                    Modifier
                        .padding(8.dp)
                        .width(150.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { visible = false; onClose() }
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = { visible = false; onClose();
                        // A duplicate of the current match schedule
                        val newMatchSchedule = schedule.toMutableMap()
                        // Stores the inputted data as an instance of Match for the edited/added match
                        val newMatchInfo: Profile.MatchSchedule.Match = Profile.MatchSchedule.Match(listOf(
                            Profile.MatchSchedule.Team(color = Alliance.BLUE, number = newTeamOne),
                            Profile.MatchSchedule.Team(color = Alliance.BLUE, number = newTeamTwo),
                            Profile.MatchSchedule.Team(color = Alliance.BLUE, number = newTeamThree),
                            Profile.MatchSchedule.Team(color = Alliance.RED, number = newTeamFour),
                            Profile.MatchSchedule.Team(color = Alliance.RED, number = newTeamFive),
                            Profile.MatchSchedule.Team(color = Alliance.RED, number = newTeamSix)
                        ))
                        // Replaces/adds the new/edited match in the new match schedule
                        newMatchSchedule[newMatchNumber] = newMatchInfo
                        // Updates the match schedule to be the new match schedule
                        matchSchedule.update(newMatchSchedule)
                    }
                ) {
                    Text("Confirm")
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}
