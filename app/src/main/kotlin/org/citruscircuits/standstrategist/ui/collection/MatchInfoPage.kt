package org.citruscircuits.standstrategist.ui.collection

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.citruscircuits.standstrategist.LocalMatchSchedule
import org.citruscircuits.standstrategist.LocalProfileSettings
import org.citruscircuits.standstrategist.LocalWindowSizeClass

/**
 * Page displaying info about the next match in data collection.
 *
 * @param matchNumber The match to show info for.
 * @param onShowTeamsList Callback to open the team list sheet.
 * @param onShowMatchSelection Callback to open the match list sheet.
 * @param onTeamClick Called when the user clicks on a team number.
 */
@Composable
fun MatchInfoPage(
    matchNumber: String,
    onShowTeamsList: () -> Unit,
    onShowMatchSelection: () -> Unit,
    onTeamClick: (String) -> Unit,
    onShowEditMatchSchedule: () -> Unit,
) {
    // align items in the center
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .fillMaxSize()
                // allow scrolling
                .verticalScroll(rememberScrollState())
    ) {
        // check window width
        if (LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Expanded) {
            // wide layout
            Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                MatchInfo(matchNumber, onShowMatchSelection)
                TeamsInfo(matchNumber, onTeamClick, onShowTeamsList)
            }
        } else {
            // normal layout
            Column(verticalArrangement = Arrangement.spacedBy(48.dp)) {
                MatchInfo(matchNumber, onShowMatchSelection)
                TeamsInfo(matchNumber, onTeamClick, onShowTeamsList)
            }
        }
    }
}

/**
 * Match number title and Switch match button.
 *
 * @param matchNumber The match number to show in the title.
 * @param onShowMatchSelection Called when the Switch match button is clicked.
 */
@Composable
private fun MatchInfo(matchNumber: String, onShowMatchSelection: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.width(IntrinsicSize.Max)) {
        // title
        Text("Match $matchNumber", style = MaterialTheme.typography.headlineMedium)
        // switch button
        OutlinedButton(onClick = onShowMatchSelection, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.SwapHoriz, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Switch match")
        }
    }
}

/**
 * List of teams and a button to open the team list.
 *
 * @param matchNumber Match to show teams for.
 * @param onTeamClick Called when the user clicks a team number.
 * @param onShowTeamsList Called when the user clicks the team list button.
 */
@Composable
private fun TeamsInfo(matchNumber: String, onTeamClick: (String) -> Unit, onShowTeamsList: () -> Unit) {
    val matchSchedule by LocalMatchSchedule.current.schedule.collectAsStateWithLifecycle()
    val profileSettings by LocalProfileSettings.current.profileSettings.collectAsStateWithLifecycle()
    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.width(IntrinsicSize.Max)) {
        // title
        Text("Teams", style = MaterialTheme.typography.titleLarge)
        // teams row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            matchSchedule[matchNumber]?.teams?.filter { it.color == profileSettings.alliance }?.map { it.number }
                ?.forEach { team ->
                    TextButton(onClick = { onTeamClick(team) }) {
                        Text(team, style = MaterialTheme.typography.bodyLarge)
                    }
                }
        }
        // team list button
        OutlinedButton(onClick = onShowTeamsList, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.List, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open teams list")
        }
    }
}
