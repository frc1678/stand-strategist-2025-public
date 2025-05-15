package org.citruscircuits.standstrategist.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DropdownInput(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    dataPointName: String,
    onDismissRequest: () -> Unit,
    valuesList: List<String>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .width(IntrinsicSize.Min)
            .onKeyEvent {
                if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                    onValueChange(valuesList[(valuesList.indexOf(value) + 1) % valuesList.size])
                    return@onKeyEvent true
                }
                false
            }
    ) {
        // label
        Text(dataPointName, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 16.dp))
        // get whether the text field is focused
        val interactionSource = remember { MutableInteractionSource() }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onDismissRequest() },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            TextField(
                modifier = modifier
                    .fillMaxSize()
                    .menuAnchor(),
                value = value,
                onValueChange = { onValueChange(it) },
                placeholder = { valuesList[0] },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                interactionSource = interactionSource,
            )
            /* Loop through drivetrainTypes, for each it will have text of the item be the key of the given drivetrainType
            *  On click, it will set the given team's datapoint value, and then set it
            *  selectedText keeps track of what the value of your datapoint is
            *  It also determines what is being displayed in the text field, which shows which item is selected.
            * */
            DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
                valuesList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        trailingIcon = {
                            if (value == item) {
                                Icon(Icons.Default.Check, "")
                            }
                        },
                        onClick = {
                            onValueChange(item)
                            onDismissRequest()
                        },
                    )
                }
            }
        }
    }
}
