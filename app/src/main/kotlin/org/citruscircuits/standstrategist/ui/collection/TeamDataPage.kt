package org.citruscircuits.standstrategist.ui.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.citruscircuits.standstrategist.LocalMatchSchedule
import org.citruscircuits.standstrategist.LocalProfileSettings
import org.citruscircuits.standstrategist.LocalWindowSizeClass
import org.citruscircuits.standstrategist.ui.components.StickyTeamHeader
import org.citruscircuits.standstrategist.ui.components.TeamDataColumn

/**
 * Page for entering team data.
 *
 * @param matchNumber Determines which teams are shown.
 */
@Composable
fun TeamDataPage(matchNumber: String) {
    Surface(color = MaterialTheme.colorScheme.background) {
        val matchSchedule by LocalMatchSchedule.current.schedule.collectAsStateWithLifecycle()
        val profileSettings by LocalProfileSettings.current.profileSettings.collectAsStateWithLifecycle()
        // get teams in this match in the selected alliance
        val teams =
            matchSchedule[matchNumber]?.teams
                ?.filter { it.color == profileSettings.alliance }?.map { it.number } ?: emptyList()
        // check window width
        if (LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Expanded) {

            // layout when tablet is horizontal
            // an overall column that contains two main rows
            // first row contains a column for each sticky team number header
            // second row contains a column for each Team data input
            Column() {
                Row() {
                    teams.forEach { teamNumber ->
                        StickyTeamHeader(
                            teamNumber = teamNumber,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier =
                    Modifier
                        .imePadding()
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // creates a data input column for each team
                    teams.forEach {
                        TeamDataColumn(
                            teamNumber = it,
                            title = { Text(it) },
                            modifier = Modifier.weight(1f),
                            stickyHeader = true
                        )
                    }
                }
            }
        }
        else {
            // layout when tablet is vertical
            // displays team input data stacked vertically
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier =
                Modifier
                    .imePadding()
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                teams.forEach {
                    TeamDataColumn(
                        teamNumber = it,
                        title = { Text(it) },
                        wideLayout = LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Medium
                    )
                }
            }
        }
    }
}
