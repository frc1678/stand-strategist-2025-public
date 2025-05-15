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
import org.citruscircuits.standstrategist.ui.components.TimDataColumn

/**
 * Page for entering team-in-match data.
 *
 * @param matchNumber The match to enter data for.
 */
@Composable
fun TimDataPage(matchNumber: String) {
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
            // second row contains a column for each TIM data input
            Column() {
                Row() {
                    // creates a header for each team number that stays at the top of the screen
                    teams.forEach { teamNumber ->
                        StickyTeamHeader(
                            teamNumber = teamNumber,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier =
                    Modifier
                        .imePadding()
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // creates the TIM data input columns for each team
                    teams.forEach { teamNumber ->
                        TimDataColumn(
                            matchNumber = matchNumber,
                            teamNumber = teamNumber,
                            modifier = Modifier.weight(1f),
                            header = true,
                            title = teamNumber
                        )
                    }
                }
            }
        }

        else {
            // layout when app is vertical
            // stacks the team data vertically with each team on top of another
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier =
                Modifier
                    .imePadding()
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                ) {

                // creates the TIM data input for each team
                    teams.forEach { teamNumber ->
                        TimDataColumn(
                            matchNumber = matchNumber,
                            teamNumber = teamNumber,
                            title = teamNumber,
                            wideLayout = LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Medium
                        )
                    }
                }
            }
        }
    }
