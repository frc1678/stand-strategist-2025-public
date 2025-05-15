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
import org.citruscircuits.standstrategist.LocalProfileSettings

/**
 * Lets the user choose which match to collect data for
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MatchSelectionSheet(events: Events) {
    val coroutineScope = LocalCoroutineScope.current
    val sheetState = rememberModalBottomSheetState()

    fun dismiss() =
        coroutineScope.launch {
            sheetState.hide()
            events.onHideMatchSelection()
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
        val matchSchedule by LocalMatchSchedule.current.schedule.collectAsStateWithLifecycle()
        val profileSettings by LocalProfileSettings.current.profileSettings.collectAsStateWithLifecycle()
        var searchQuery by rememberSaveable { mutableStateOf("") }
        LazyColumn(
            contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Search for match from match schedule
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
            // Displays the match list so that the user can choose a match to collect data on
            for ((matchNumber, matchObj) in matchSchedule) {
                val teams =
                    matchObj.teams.filter { it.color == profileSettings.alliance }.joinToString { it.number }
                if (searchQuery in matchNumber) {
                    item {
                        ListItem(
                            headlineContent = { Text(matchNumber) },
                            supportingContent = { Text(teams) },
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
