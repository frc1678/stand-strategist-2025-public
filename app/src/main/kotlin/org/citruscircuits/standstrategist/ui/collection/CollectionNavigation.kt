package org.citruscircuits.standstrategist.ui.collection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.citruscircuits.standstrategist.LocalCoroutineScope
import org.citruscircuits.standstrategist.LocalMatchSchedule
import org.citruscircuits.standstrategist.LocalProfileSettings
import org.citruscircuits.standstrategist.data.Alliance
import kotlin.math.absoluteValue

const val CollectionPageCount = 3

/**
 * The actions that a user can do
 */
data class Events(
    val onShowMatchSelection: () -> Unit = {},
    val onHideMatchSelection: () -> Unit = {},
    val onMatchSelect: (String) -> Unit = {},
    val onShowTeamsList: () -> Unit = {},
    val onHideTeamsList: () -> Unit = {},
    val onShowDatapointFilter: () -> Unit = {},
    val onHideDatapointFilter: () -> Unit = {},
    val onTeamSelect: (String) -> Unit = {},
    val onSwitchAlliance: () -> Unit = {},
    val onShowEditMatchSchedule: () -> Unit = {},
    val onHideEditMatchSchedule: () -> Unit = {},
    val onOpenProfileManagement: () -> Unit = {}
)

/**
 * Interface to allow the user to navigate to different screens
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionNavigation(onTeamSelect: (String) -> Unit, onOpenProfileManagement: () -> Unit) {
    val coroutineScope = LocalCoroutineScope.current
    val matchSchedule by LocalMatchSchedule.current.schedule.collectAsStateWithLifecycle()
    val settings = LocalProfileSettings.current
    val profileSettings by settings.profileSettings.collectAsStateWithLifecycle()
    val pagerState =
        rememberPagerState(
            initialPage =
                matchSchedule.toList()
                    .indexOfFirst { it.first == profileSettings.matchNumber } *
                    CollectionPageCount + profileSettings.page,
            pageCount = { matchSchedule.size * CollectionPageCount }
        )
    val currentMatchNumber =
        remember(matchSchedule, pagerState.currentPage) {
            matchSchedule.toList()[pagerState.currentPage / CollectionPageCount].first
        }

    fun pageOf(match: String) = matchSchedule.toList().indexOfFirst { it.first == match } * CollectionPageCount
    val currentPageInMatch = remember(pagerState.currentPage) { pagerState.currentPage % CollectionPageCount }
    LaunchedEffect(currentMatchNumber, currentPageInMatch) {
        settings.update(profileSettings.copy(matchNumber = currentMatchNumber, page = currentPageInMatch))
    }
    val focusManager = LocalFocusManager.current
    val dummyFocusRequester = FocusRequester()
    var showMatchSelectionSheet by rememberSaveable { mutableStateOf(false) }
    var showTeamsListSheet by rememberSaveable { mutableStateOf(false) }
    var showDatapointFilter by rememberSaveable { mutableStateOf(false) }
    var showEditMatchSchedule by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val events =
        Events(
            onShowMatchSelection = { showMatchSelectionSheet = true },
            onHideMatchSelection = { showMatchSelectionSheet = false },
            onMatchSelect = { match -> coroutineScope.launch { pagerState.scrollToPage(pageOf(match)) } },
            onShowTeamsList = { showTeamsListSheet = true },
            onHideTeamsList = { showTeamsListSheet = false },
            onShowDatapointFilter = { showDatapointFilter = true },
            onHideDatapointFilter = { showDatapointFilter = false },
            onTeamSelect = onTeamSelect,
            onSwitchAlliance = {
                settings.update(
                    profileSettings.copy(
                        alliance =
                            when (profileSettings.alliance) {
                                Alliance.BLUE -> Alliance.RED
                                Alliance.RED -> Alliance.BLUE
                                else -> Alliance.BLUE
                            }
                    )
                )
            },
            onShowEditMatchSchedule = { showEditMatchSchedule = true },
            onHideEditMatchSchedule = { showEditMatchSchedule = false },
            onOpenProfileManagement = onOpenProfileManagement
        )
    Scaffold(
        topBar = {
            CollectionHeader(
                matchNumber = currentMatchNumber,
                alliance = profileSettings.alliance,
                pageIndex = currentPageInMatch,
                offsetFraction = remember { derivedStateOf { pagerState.currentPageOffsetFraction } }.value,
                events = events
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.onKeyEvent {
            if (it.type == KeyEventType.KeyDown && it.isAltPressed) {
                when (it.key) {
                    Key.A -> focusManager.moveFocus(FocusDirection.Left)
                    Key.D -> focusManager.moveFocus(FocusDirection.Right)
                    Key.W -> focusManager.moveFocus(FocusDirection.Up)
                    Key.S -> focusManager.moveFocus(FocusDirection.Down)
                    Key.Q -> {
                        dummyFocusRequester.requestFocus()
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    }
                    Key.E -> {
                        dummyFocusRequester.requestFocus()
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                }
                return@onKeyEvent true
            }
            false
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // dummy to reset focus on when switching pages using keyboard shortcut
            Box(
                modifier = Modifier
                    .focusRequester(dummyFocusRequester)
                    .focusable()
            )
            Pager(pagerState, events)
        }
        if (showMatchSelectionSheet) MatchSelectionSheet(events)
        if (showDatapointFilter) DatapointFilter(events)
        if (showTeamsListSheet) TeamsListSheet(events)
        if (showEditMatchSchedule) { EditMatchScheduleDialog { showEditMatchSchedule = !showEditMatchSchedule } }
    }
    BackHandler { coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }
}

/**
 * Has the different data collection pages
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Pager(pagerState: PagerState, events: Events, modifier: Modifier = Modifier) {
    val matchSchedule by LocalMatchSchedule.current.schedule.collectAsStateWithLifecycle()
    HorizontalPager(state = pagerState, beyondBoundsPageCount = 1, modifier = modifier.fillMaxSize()) { page ->
        val matchNumber = matchSchedule.toList()[page / CollectionPageCount].first
        Row {
            // divider
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(DividerDefaults.Thickness * pagerState.currentPageOffsetFraction.absoluteValue * 2)
                    .background(DividerDefaults.color)
            )
            when (page % CollectionPageCount) {
                0 ->
                    MatchInfoPage(
                        matchNumber = matchNumber,
                        onShowTeamsList = events.onShowTeamsList,
                        onShowMatchSelection = events.onShowMatchSelection,
                        onTeamClick = events.onTeamSelect,
                        onShowEditMatchSchedule = events.onShowEditMatchSchedule
                    )
                1 -> TimDataPage(matchNumber = matchNumber)
                2 -> TeamDataPage(matchNumber = matchNumber)
            }
        }
    }
}
