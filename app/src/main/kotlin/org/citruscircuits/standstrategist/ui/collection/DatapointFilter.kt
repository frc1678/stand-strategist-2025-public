package org.citruscircuits.standstrategist.ui.collection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.citruscircuits.standstrategist.LocalCoroutineScope
import org.citruscircuits.standstrategist.LocalMatchSchedule
import org.citruscircuits.standstrategist.LocalProfileSettings
import org.citruscircuits.standstrategist.LocalTimData
import org.citruscircuits.standstrategist.ui.components.IntegerInput

/** Bottom sheet to filter matches by defense rating */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatapointFilter(events: Events) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = LocalCoroutineScope.current

    fun dismiss() =
        coroutineScope.launch {
            sheetState.hide()
            events.onHideDatapointFilter()
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
        var filterValue by rememberSaveable { mutableIntStateOf(0) }
        val timData by LocalTimData.current.data.collectAsStateWithLifecycle()
        val matchSchedule by LocalMatchSchedule.current.schedule.collectAsStateWithLifecycle()
        val profileSettings by LocalProfileSettings.current.profileSettings.collectAsStateWithLifecycle()

        LazyColumn(
            contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
            modifier = Modifier.fillMaxWidth()
        ) {
            // The rating to filter by
            item {
                IntegerInput(
                    range = Pair(0, 5),
                    value = filterValue,
                    onValueChange = { filterValue = it },
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    dataPointName = "Defense Rating Filter",
                    defenseChecked = true
                )
            }
            // Loop through each matchNumber: matchData
            for ((matchNumber, matchData) in timData) {
                // For each match, matchFiltered tracks if it qualifies to be filtered
                var matchFiltered = false
                // teamsFiltered tracks which teams have specifically qualified, as there can be multiple
                val teamsFiltered = mutableListOf<String>()
                // teamsAlliance tracks the teams in the match that are in the proper alliance
                val teamsAlliance = matchSchedule[matchNumber]!!.teams.filter { it.color == profileSettings.alliance }
                    .map { it.number }
                // Iterate through the match and only qualify it if a team's defense rating matches, has played defense, and is part of the proper alliance.
                for ((team, data) in matchData) {
                    if (data.defense_rating == filterValue && data.played_defense && team in teamsAlliance) {
                        matchFiltered = true
                        teamsFiltered.add(team)
                    }
                }
                // If the match is filtered show it. Underline teams that have qualified, and are in teamsFiltered
                if (matchFiltered) {
                    item {
                        ListItem(
                            headlineContent = { Text(matchNumber) },
                            supportingContent = {
                                Row {
                                    for (t in teamsAlliance) {
                                        Text(
                                            t,
                                            textDecoration = if (t in teamsFiltered) TextDecoration.Underline else TextDecoration.None
                                        )
                                        Spacer(modifier = Modifier.size(3.dp))
                                    }
                                }
                            },
                            modifier =
                            Modifier.clickable {
                                events.onMatchSelect(matchNumber)
                                dismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}



