package org.citruscircuits.standstrategist.ui.collection

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups3
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.citruscircuits.standstrategist.BuildConfig
import org.citruscircuits.standstrategist.LocalWindowSizeClass
import org.citruscircuits.standstrategist.R
import org.citruscircuits.standstrategist.data.Alliance
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme

/**
 * Creates the overall app header that includes the Match, Alliance, and Page names
 * @param matchNumber - the match number that it's on
 * @param alliance - the alliance that's chosen (red or blue)
 * @param pageIndex - which page it is currently on
 * @param offsetFraction - layout offset for the header
 * @param events - all of the actions that the user can do
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionHeader(matchNumber: String, alliance: Alliance?, pageIndex: Int, offsetFraction: Float, events: Events) {
    Surface(color =
    // Changes header color based on alliance color (the default tablet theme color is blue)
    if(alliance == Alliance.RED && isSystemInDarkTheme()) { Color(160,10,10)}
    else if (alliance == Alliance.RED && !isSystemInDarkTheme()) { Color(250,120,120)}
    else {MaterialTheme.colorScheme.primaryContainer}
    ) {
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleLarge) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                    .padding(16.dp)
            ) {
                // layout for horizontal
                if (LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Expanded) {
                    Row(modifier = Modifier.weight(1f)) {
                        MatchIndicator(
                            matchNumber = matchNumber,
                            onClick = events.onShowMatchSelection,
                            modifier = Modifier.weight(1f)
                        )
                        AllianceIndicator(
                            alliance = alliance,
                            onClick = events.onSwitchAlliance,
                            modifier = Modifier.weight(1f)
                        )
                        PageIndicator(
                            pageIndex = pageIndex,
                            offsetFraction = offsetFraction,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                // layout for vertical
                else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                        Row {
                            MatchIndicator(
                                matchNumber = matchNumber,
                                onClick = events.onShowMatchSelection,
                                modifier = Modifier.weight(1f)
                            )
                            AllianceIndicator(
                                alliance = alliance,
                                onClick = events.onSwitchAlliance,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        PageIndicator(
                            pageIndex = pageIndex,
                            offsetFraction = offsetFraction,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                // Triggers certain events when different menu buttons are clicked
                MenuButton(events)
            }
        }
    }
}

/**
 * Creates the indicator showing what the current match is
 */
@Composable
private fun MatchIndicator(matchNumber: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
        modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() }
            .padding(horizontal = 16.dp)
    ) {
        Icon(Icons.Default.SportsEsports, contentDescription = null)
        Text(matchNumber, maxLines = 1)
    }
}

/**
 * Creates the indicator showing what the current alliance is
 */
@Composable
private fun AllianceIndicator(alliance: Alliance?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
        modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() }
            .padding(horizontal = 16.dp)
    ) {
        Icon(Icons.Default.Groups3, contentDescription = null)
        Text(alliance?.readable ?: "Unknown", maxLines = 1)
    }
}

/**
 * Creates the indicator showing what the current page is
 */
@Composable
private fun PageIndicator(pageIndex: Int, offsetFraction: Float, modifier: Modifier = Modifier) {
    val pages = listOf("Match Info", "Team-in-Match Data", "Team Data")
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 18.dp)
    ) {
        Icon(Icons.Default.Description, contentDescription = null)
        BoxWithConstraints(
            modifier =
            Modifier
                .fillMaxWidth()
                .clipToBounds()
        ) {
            Text(
                pages.getOrElse(pageIndex) { "?" },
                modifier = Modifier.offset(x = maxWidth * -offsetFraction)
            )
            if (offsetFraction < 0) {
                Text(
                    pages.getOrElse((pageIndex - 1 + CollectionPageCount) % CollectionPageCount) { "?" },
                    modifier = Modifier.offset(x = maxWidth * -(1 + offsetFraction))
                )
            } else if (offsetFraction > 0) {
                Text(
                    pages.getOrElse((pageIndex + 1) % CollectionPageCount) { "?" },
                    modifier = Modifier.offset(x = maxWidth * (1 - offsetFraction))
                )
            }
        }
    }
}

/**
 * The main menu dropdown that'll display the options of Matches, Team list, Switch Alliance, Profile Management, ane the app name
 */
@Composable
private fun MenuButton(events: Events) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Matches") },
                onClick = {
                    events.onShowMatchSelection()
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text("Teams list") },
                onClick = {
                    events.onShowTeamsList()
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Default.List, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text("Datapoint Filter") },
                onClick = {
                    events.onShowDatapointFilter()
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Default.Analytics, contentDescription = null)}
            )
            DropdownMenuItem(
                text = { Text("Switch Alliance") },
                onClick = {
                    events.onSwitchAlliance()
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text("Edit Match Schedule") },
                onClick = {
                    events.onShowEditMatchSchedule()
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text("Profile management") },
                onClick = {
                    events.onOpenProfileManagement()
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Default.ManageAccounts, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text("${stringResource(R.string.app_name)} ${BuildConfig.VERSION_NAME}") },
                onClick = {},
                enabled = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun CollectionHeaderPreview() {
    if (LocalView.current.isInEditMode) {
        Text("please run this preview on a device/emulator", modifier = Modifier.background(Color.White))
        return
    }
    CompositionLocalProvider(
        LocalWindowSizeClass provides calculateWindowSizeClass(activity = LocalView.current.context as Activity)
    ) {
        StandStrategistTheme {
            Scaffold(topBar = {
                CollectionHeader(
                    matchNumber = "1",
                    alliance = Alliance.BLUE,
                    pageIndex = 0,
                    offsetFraction = 0.0f,
                    events = Events()
                )
            }) {
                Box(modifier = Modifier.padding(it))
            }
        }
    }
}
