package org.citruscircuits.standstrategist.ui.screens

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.citruscircuits.standstrategist.ui.MainNavGraph
import org.citruscircuits.standstrategist.ui.collection.CollectionNavigation
import org.citruscircuits.standstrategist.ui.destinations.ProfileManagementScreenDestination
import org.citruscircuits.standstrategist.ui.destinations.TeamScreenDestination

/**
 * The data Collection Screen
 */
@MainNavGraph(start = true)
@Destination
@Composable
fun CollectionScreen(navigator: DestinationsNavigator) {
    CollectionNavigation(
        onTeamSelect = { navigator.navigate(TeamScreenDestination(teamNumber = it)) },
        onOpenProfileManagement = { navigator.navigate(ProfileManagementScreenDestination) }
    )
}
