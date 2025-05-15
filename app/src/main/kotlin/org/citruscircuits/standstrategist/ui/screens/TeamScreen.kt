package org.citruscircuits.standstrategist.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.citruscircuits.standstrategist.LocalMatchSchedule
import org.citruscircuits.standstrategist.LocalWindowSizeClass
import org.citruscircuits.standstrategist.data.Alliance
import org.citruscircuits.standstrategist.ui.MainNavGraph
import org.citruscircuits.standstrategist.ui.components.TeamDataColumn
import org.citruscircuits.standstrategist.ui.components.TimDataColumn
import org.citruscircuits.standstrategist.ui.destinations.TeamScreenDestination

/**
 * Screen showing team data and every match's team-in-match data for a given team.
 *
 * @param navigator The app's [DestinationsNavigator].
 * @param teamNumber The team to show data for.
 * @see TeamScreenContent
 */
@Composable
@Destination
@MainNavGraph
fun TeamScreen(navigator: DestinationsNavigator, teamNumber: String) {
    TeamScreenContent(teamNumber = teamNumber, onNavigateUp = navigator::navigateUp, onNavigateTeam = {navigator.navigate(TeamScreenDestination(it))})
}


/**
 * Content for [TeamScreen].
 *
 * @param teamNumber The team to show data for.
 * @param onNavigateUp Callback to navigate to the previous screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamScreenContent(teamNumber: String, onNavigateUp: () -> Unit, onNavigateTeam: (String) -> Unit) {
    val matchSchedule by LocalMatchSchedule.current.schedule.collectAsStateWithLifecycle()
    // get matches this team is in
    val matches = mutableSetOf<String>()
    matchSchedule.forEach { (matchNumber, match) ->
        if (teamNumber in match.teams.map { it.number }) matches.add(matchNumber)
    }
    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            // show team number in top bar
            TopAppBar(
                title = { Text("Team $teamNumber") },
                navigationIcon = {
                    // back button
                    IconButton(onClick = onNavigateUp) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                }
            )
        },
        modifier = Modifier.onKeyEvent {
            if (it.type == KeyEventType.KeyDown && it.isAltPressed) {
                when (it.key) {
                    Key.A -> focusManager.moveFocus(FocusDirection.Left)
                    Key.D -> focusManager.moveFocus(FocusDirection.Right)
                    Key.W -> focusManager.moveFocus(FocusDirection.Up)
                    Key.S -> focusManager.moveFocus(FocusDirection.Down)
                }
                return@onKeyEvent true
            }
            false
        }
    ) { padding ->
        // number of matches to show per row
        val columnCount =
            when (LocalWindowSizeClass.current.widthSizeClass) {
                WindowWidthSizeClass.Compact -> 1
                WindowWidthSizeClass.Medium -> 2
                WindowWidthSizeClass.Expanded -> 3
                else -> 1
            }
        // grid layout
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            modifier =
            Modifier
                .padding(padding)
                .imePadding()
        ) {
            // team data
            item { TeamDataColumn(teamNumber = teamNumber, title = { Text("Overall Data") }) }
            // team-in-match data for each match
            items(matches.toList()) { matchNumber ->

                TimDataColumn(
                    matchNumber = matchNumber,
                    teamNumber = teamNumber,
                    title =  "Match $matchNumber",
                    allianceTeams = {
                        // Creates buttons that link to other teams in the alliance
                        var allianceColor: Alliance = Alliance.BLUE
                        for (team in matchSchedule[matchNumber]?.teams!!) {
                            if (team.number == teamNumber){
                                allianceColor = team.color
                            }
                        }
                        Row {
                            for (team in (matchSchedule[matchNumber]?.teams
                                ?.filter { it.color == allianceColor}?.map { it.number } ?: emptyList())) {
                                TextButton(
                                    onClick = {
                                        onNavigateTeam(team)
                                    }
                                ) {
                                    Text(
                                        text = team,
                                        color = if (allianceColor == Alliance.BLUE) Color.Blue else Color.Red
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                    }
                )
            }
        }
    }
}
