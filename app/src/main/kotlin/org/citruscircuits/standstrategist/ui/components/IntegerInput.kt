package org.citruscircuits.standstrategist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme

/**
 * Input component for integer input.
 *
 * @param value The current integer value.
 * @param onValueChange Setter for the integer value.
 * @param dataPointName Name of the data point.
 * @param wideLayout Whether there is wide space available, and the wide layout should be used.
 * @param defenseChecked If the Defense checkbox has been set to true for this team in this match
 */
@Composable
fun IntegerInput(
    range: Pair<Int, Int>,
    value: Int,
    onValueChange: (Int) -> Unit,
    dataPointName: String,
    modifier: Modifier = Modifier,
    wideLayout: Boolean = false,
    defenseChecked: Boolean = false
) {
    // separate state for input validation
    var input by rememberSaveable(value) { mutableStateOf(value.toString()) }

    // sets the defense rating equal to 0 if played defense is not checked
    LaunchedEffect(defenseChecked) {
        if (!defenseChecked) {
            input = "0"
            onValueChange(0)
        }
    }

    if (wideLayout) {
        // wide layout
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            // label
            Text(dataPointName, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(16.dp))
            // counter
            Box(modifier = Modifier.width(IntrinsicSize.Max)) {
                InputRow(
                    range = range,
                    value = input,
                    onValueChange = {
                        input = it
                        if (validateInput(it)) onValueChange(it.toInt())
                    },
                    defenseChecked = defenseChecked
                )
            }
        }
    } else {
        // normal layout
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier.width(intrinsicSize = IntrinsicSize.Min)
        ) {
            // label
            Text(dataPointName, style = MaterialTheme.typography.labelLarge)
            // counter
            InputRow(
                range = range,
                value = input,
                onValueChange = {
                    input = it
                    if (validateInput(it)) onValueChange(it.toInt())
                },
                defenseChecked = defenseChecked
            )
        }
    }
}

/**
 * The counter. Has a plus button and minus button, and the number is editable as text.
 *
 * @param value The current input value.
 * @param onValueChange Setter for the current input value.
 * @param defenseChecked If the Defense checkbox has been set to true for this team in this match
 */
@Composable
private fun InputRow(range: Pair<Int, Int>, value: String, onValueChange: (String) -> Unit, defenseChecked: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        // minus button
        OutlinedIconButton(
            onClick = { onValueChange((value.toIntOrNull()?.minus(1)?.coerceIn(0..5) ?: 0).toString()) },
            // enable button if value is an integer, decrementing it results in a valid value, and defense is checked
            enabled = value.toIntOrNull()?.let { validateInput("${it - 1}") } ?: false && defenseChecked
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease")
        }
        // number
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = defenseChecked,
            isError = !validateInput(value),
            supportingText =
                if (value.toIntOrNull() == null) {
                    { Text("Must be an integer") }
                } else if (value.toInt() !in range.first..range.second) {
                    { Text("Must be between 0 and 5") }
                } else {
                    null
                },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
        // plus button
        OutlinedIconButton(
            onClick = { onValueChange((value.toIntOrNull()?.plus(1)?.coerceIn(0..5) ?: 0).toString()) },
            // enable button if value is an integer, incrementing it results in a valid value, and defense is checked
            enabled = value.toIntOrNull()?.let { validateInput("${it + 1}") } ?: false && defenseChecked
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase")
        }
    }
}

private fun validateInput(input: String) = input.toIntOrNull()?.let { it in 0..5 } ?: false

@Preview
@Composable
private fun IntegerInputPreview() {
    StandStrategistTheme {
        Surface {
            var int by remember { mutableIntStateOf(0) }
            IntegerInput(
                range = Pair(0, 3),
                value = int,
                onValueChange = { int = it },
                dataPointName = "Data point name",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
            )
        }
    }
}
