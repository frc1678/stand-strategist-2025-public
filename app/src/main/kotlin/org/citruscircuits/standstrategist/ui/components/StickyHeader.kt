package org.citruscircuits.standstrategist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Creates team number headers that stay at the top of the screen
 */
@Composable
fun StickyTeamHeader(teamNumber : String, modifier : Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = modifier) {
        // adds a bigger style to the text
        ProvideTextStyle(MaterialTheme.typography.headlineMedium) {
            Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                Text(teamNumber)
            }
        }
    }
}
