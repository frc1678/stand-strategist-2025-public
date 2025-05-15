package org.citruscircuits.standstrategist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.citruscircuits.standstrategist.ui.theme.StandStrategistTheme

/**
 * Input component for boolean input.
 *
 * @param value The current boolean value.
 * @param onValueChange Setter for the boolean value.
 * @param dataPointName Name of the data point.
 */
@Composable
fun BooleanInput(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    dataPointName: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .clickable { onValueChange(!value) }
                .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // name
        Text(dataPointName)
        Spacer(modifier = Modifier.width(16.dp))
        // checkbox
        Checkbox(checked = value, onCheckedChange = { onValueChange(it) })
    }
}

@Preview
@Composable
private fun BooleanInputPreview() {
    StandStrategistTheme {
        Surface {
            var boolean by remember { mutableStateOf(false) }
            BooleanInput(
                value = boolean,
                onValueChange = { boolean = it },
                dataPointName = "Data point name",
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}
