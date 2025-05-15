package org.citruscircuits.standstrategist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.citruscircuits.standstrategist.LocalTeamData
import org.citruscircuits.standstrategist.data.datapoints.DataPoints
import org.citruscircuits.standstrategist.data.datapoints.shootingLocations

/**
 * The team data fields for one team.
 *
 * @param teamNumber The team to show fields for.
 * @param title Title content to show over the fields.
 * @param modifier Determines different formatting aspects
 * @param wideLayout Whether there is wide space available, and the wide layout should be used.
 * @param stickyHeader Whether there is already a header that's displayed
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TeamDataColumn(
    teamNumber: String,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    wideLayout: Boolean = false,
    stickyHeader: Boolean = false
) {
    val teamData = LocalTeamData.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = modifier) {

        // if there isn't already a header displayed, creates a header with the given title
        if (!stickyHeader) {
            // use big text style
            ProvideTextStyle(MaterialTheme.typography.headlineMedium) {
                Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                    title()
                }
            }
        }

        // iterates over data points and creates the given data collection field for each type
        DataPoints.Team.forEachTyped(
            onString = { dataPoint ->
                TextInput(
                    value = teamData[teamNumber, dataPoint],
                    onValueChange = { teamData[teamNumber, dataPoint] = it },
                    dataPointName = dataPoint.readableName,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .imeNestedScroll()
                )
            },
            onInt = { dataPoint ->
                IntegerInput(
                    range = Pair(0, 5),
                    value = teamData[teamNumber, dataPoint],
                    onValueChange = { teamData[teamNumber, dataPoint] = it },
                    dataPointName = dataPoint.readableName,
                    wideLayout = wideLayout,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .imeNestedScroll()
                )
            },
            onBoolean = { dataPoint ->
                BooleanInput(
                    value = teamData[teamNumber, dataPoint],
                    onValueChange = { teamData[teamNumber, dataPoint] = it },
                    dataPointName = dataPoint.readableName,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .imeNestedScroll()
                )
            },
            onDropdown = { dataPoint ->
                var expanded by remember { mutableStateOf(false) }
                DropdownInput(
                    expanded = expanded,
                    value = teamData[teamNumber, dataPoint],
                    onValueChange = { teamData[teamNumber, dataPoint] = it },
                    dataPointName = dataPoint.readableName,
                    onDismissRequest = { expanded = !expanded },
                    valuesList = if (dataPoint.readableName == "Can Only Shoot From Specific Area") shootingLocations else listOf(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .imeNestedScroll()
                )
            }
        )
    }
}
