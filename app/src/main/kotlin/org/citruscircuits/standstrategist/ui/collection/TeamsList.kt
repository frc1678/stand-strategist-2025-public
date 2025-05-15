package org.citruscircuits.standstrategist.ui.collection

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.citruscircuits.standstrategist.LocalCoroutineScope
import org.citruscircuits.standstrategist.LocalMatchSchedule
import org.citruscircuits.standstrategist.data.profiles.getTeamsList

/**
 * Lets the user search for a team number
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TeamsListSheet(events: Events) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = LocalCoroutineScope.current

    fun dismiss() =
        coroutineScope.launch {
            sheetState.hide()
            events.onHideTeamsList()
        }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { dismiss() },
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                modifier =
                    Modifier
                        .statusBarsPadding()
                        .clickable { dismiss() }
            )
        },
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        var searchQuery by rememberSaveable { mutableStateOf("") }
        val matchSchedule by LocalMatchSchedule.current.schedule.collectAsStateWithLifecycle()
        val teamsList = getTeamsList(matchSchedule)
        LazyColumn(
            contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Search for a team number
            stickyHeader {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search...") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(MaterialTheme.colorScheme.surface)
                )
            }

            // Displays the list of teams
            for (team in sortTeamList(teamsList)) {
                if (searchQuery in team) {
                    item {
                        ListItem(
                            headlineContent = { Text(team) },
                            modifier =
                            Modifier
                                .clickable {
                                    events.onTeamSelect(team)
                                    dismiss()
                                }
                        )
                    }
                }
            }
        }
    }
}

/** Sorts team list in order, ignores characters so it works for MTTD as well **/
fun sortTeamList(list: List<String>): List<String> {
    val regex = "(?<=\\d)(?=\\D)".toRegex()
    var tempList = mutableListOf<List<String>>()
    for (teamNumber in list) {
        /*
        splits the team number between digits and letters into a list, then adds it to tempList,
        adds a blank string to the list in case the team number does not have letters
        */
        var temp = teamNumber.split(regex).toMutableList()
        temp.add("")
        if (temp[0] != "") tempList.add(temp)
    }
    //sorts tempList alphabetically by the 1st index of each list, then sorts it numerically by the 0th index of each list
    tempList.sortBy { it[1] }
    tempList.sortBy { it[0].toInt() }
    var newTeamList = mutableListOf<String>()
    for (team in tempList) {
        // recombines team into a team number, then adds it to the newTeamList
        newTeamList.add(team[0] + team[1])
    }
    return newTeamList
}
