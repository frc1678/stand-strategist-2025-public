package org.citruscircuits.standstrategist.ui.components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.scope.DestinationScope
import org.citruscircuits.standstrategist.LocalTimData
import org.citruscircuits.standstrategist.data.datapoints.DataPoints
import org.citruscircuits.standstrategist.data.datapoints.TimDataPoint
import org.citruscircuits.standstrategist.ui.screens.TeamScreen

/**
 * The team-in-match data fields for one team in one match.
 *
 * @param matchNumber The match to show data for.
 * @param teamNumber The team to show data for.
 * @param title Title content to show over the fields.
 * @param wideLayout Whether there is wide space available, and the wide layout should be used.
 * @param header Whether there's already a sticky header being displayed
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun TimDataColumn(
    matchNumber: String,
    teamNumber: String,
    title: String,
    modifier: Modifier = Modifier,
    wideLayout: Boolean = false,
    header: Boolean = false,
    allianceTeams: @Composable () -> Unit = {}
) {
    val timData = LocalTimData.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = modifier) {

        // Creates the titles for each section if the section doesn't already have a sticky header
        if (!header) {
            ProvideTextStyle(MaterialTheme.typography.headlineMedium) {
                Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                    Text(title)
                }
            }
        }

        // Creates buttons linking to the TeamScreens for the other teams in the alliance
        // only creates them in the TeamScreen
        allianceTeams()

        // iterates over data points and creates the given data collection field for each type
        DataPoints.Tim.forEachTyped(
            onString = { dataPoint ->
                TextInput(
                    value = timData[matchNumber, teamNumber, dataPoint],
                    onValueChange = { timData[matchNumber, teamNumber, dataPoint] = it },
                    dataPointName = dataPoint.readableName,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .imeNestedScroll(),
                    )
            },
            onInt = { dataPoint ->
                IntegerInput(
                    range = Pair(0, 5),
                    value = timData[matchNumber, teamNumber, dataPoint],
                    onValueChange = { timData[matchNumber, teamNumber, dataPoint] = it },
                    dataPointName = dataPoint.readableName,
                    wideLayout = wideLayout,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .imeNestedScroll(),
                    // Passes whether or not played defense has been checked for a specific team number in a match
                    defenseChecked = timData[matchNumber, teamNumber, DataPoints.Tim.dataPoints[1] as TimDataPoint<Boolean>]
                )
            },
            onBoolean = { dataPoint ->
                BooleanInput(
                    value = timData[matchNumber, teamNumber, dataPoint],
                    onValueChange = { timData[matchNumber, teamNumber, dataPoint] = it },
                    dataPointName = dataPoint.readableName,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .imeNestedScroll()
                )
            },
            onDropdown = {}
        )
    }
}
