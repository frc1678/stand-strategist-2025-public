package org.citruscircuits.standstrategist.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme

/**
 * Input component for text input.
 *
 * @param value The current string value.
 * @param onValueChange Setter for the current string value.
 * @param dataPointName Name of the data point.
 * @param modifier Determines layout attributes
 */
@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    dataPointName: String,
    modifier: Modifier = Modifier
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = modifier.width(IntrinsicSize.Min)) {
        // label
        Text(dataPointName, style = MaterialTheme.typography.labelLarge)
        // get whether the text field is focused
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()
        // text field
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Enter text") },
            trailingIcon =
                // show clear text button if focused and there's text entered
                if (isFocused && value.isNotEmpty()) {
                    {
                        IconButton(onClick = { onValueChange("") }) {
                            Icon(Icons.Default.Cancel, contentDescription = "Clear text")
                        }
                    }
                } else {
                    null
                },
            interactionSource = interactionSource,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun TextInputPreview() {
    StandStrategistTheme {
        Surface {
            var text by remember { mutableStateOf("") }
            TextInput(
                value = text,
                onValueChange = { text = it },
                dataPointName = "Data point name",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
            )
        }
    }
}
