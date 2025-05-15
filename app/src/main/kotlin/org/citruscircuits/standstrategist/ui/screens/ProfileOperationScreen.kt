package org.citruscircuits.standstrategist.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.citruscircuits.standstrategist.LocalAppSettings
import org.citruscircuits.standstrategist.LocalWindowSizeClass
import org.citruscircuits.standstrategist.data.profiles.operations.InputFromFolder
import org.citruscircuits.standstrategist.data.profiles.operations.InputFromProfile
import org.citruscircuits.standstrategist.data.profiles.operations.InputFromZip
import org.citruscircuits.standstrategist.data.profiles.operations.OutputToFolder
import org.citruscircuits.standstrategist.data.profiles.operations.OutputToNewProfile
import org.citruscircuits.standstrategist.data.profiles.operations.OutputToOds
import org.citruscircuits.standstrategist.data.profiles.operations.OutputToProfile
import org.citruscircuits.standstrategist.data.profiles.operations.OutputToZip
import org.citruscircuits.standstrategist.data.profiles.operations.ProfileOperationInput
import org.citruscircuits.standstrategist.data.profiles.operations.ProfileOperationOutput
import org.citruscircuits.standstrategist.data.profiles.operations.ProfileOperationPreset
import org.citruscircuits.standstrategist.ui.MainNavGraph
import sh.calvin.reorderable.ReorderableColumn

/**
 * Current state of the profile operation screen.
 * Includes which inputs and outputs have been selected, and the running state of the operation.
 *
 * @param inputs The input instances that have been added.
 * @param outputs The output instances that have been added.
 * @param inputResults The results of the completed [inputs].
 * Keys are IDs of input instances, and values are results.
 * Currently running/pending inputs do not appear in this map.
 * @param outputResults The results of the completed [outputs].
 * Keys are IDs of output instances, and values are results.
 * Currently running/pending outputs do not appear in this map.
 * @param mergeState The result of the merge step (inputted profiles are merged).
 * `null` if the merge is currently running/pending.
 * @param status The overall state of the operation.
 */
data class ProfileOperationScreenState(
    val inputs: List<ProfileOperationInput> = listOf(),
    val outputs: List<ProfileOperationOutput> = listOf(),
    val inputResults: Map<String, ProfileOperationStepResult> = mapOf(),
    val outputResults: Map<String, ProfileOperationStepResult> = mapOf(),
    val mergeState: ProfileOperationStepResult? = null,
    val status: ProfileOperationStatus = ProfileOperationStatus.Idle
)

/**
 * The result of a single step in a profile operation.
 */
sealed interface ProfileOperationStepResult {
    /**
     * Indicates that this step in the profile operation succeeded.
     */
    data object Success : ProfileOperationStepResult

    /**
     * Indicates that this step in the profile operation failed with an exception.
     *
     * @param exception The exception thrown by the step while executing.
     */
    data class Error(val exception: Throwable) : ProfileOperationStepResult
}

/**
 * The overall state of a profile operation.
 */
sealed interface ProfileOperationStatus {
    /**
     * Indicates that the profile operation is not running and has not run yet.
     */
    data object Idle : ProfileOperationStatus

    /**
     * Indicates that the profile operation is actively running.
     */
    data object Running : ProfileOperationStatus

    /**
     * Indicates that the profile operation is not running but has previously run,
     * and there are results to show.
     */
    data object Completed : ProfileOperationStatus
}

/**
 * The screen for constructing and executing a profile operation.
 *
 * @param preset A [ProfileOperationPreset] to load initially, or `null` if there's no preset.
 * @param navigator The app's [DestinationsNavigator].
 * @param viewModel A [ProfileOperationViewModel] instance to store data in and use methods from.
 * @see ProfileOperationScreenContent
 */
@MainNavGraph
@Destination
@Composable
fun ProfileOperationScreen(
    preset: ProfileOperationPreset? = null,
    navigator: DestinationsNavigator,
    viewModel: ProfileOperationViewModel = hiltViewModel()
) {
    // get app context
    val context = LocalContext.current
    // get app settings
    val appSettings = LocalAppSettings.current
    // run when the screen is shown
    LaunchedEffect(true) {
        // load the preset if one is given
        preset?.let { viewModel.loadPreset(it, getAppSettings = { appSettings }) }
    }
    // show content
    ProfileOperationScreenContent(
        state = viewModel.state.collectAsStateWithLifecycle().value,
        onUpdateInputs = viewModel::updateInputs,
        onUpdateOutputs = viewModel::updateOutputs,
        onRun = { viewModel.run(context) },
        onNavigateUp = navigator::navigateUp
    )
}

/**
 * Content for the [ProfileOperationScreen].
 *
 * @param state The current state of the screen.
 * @param onUpdateInputs Callback to update the input instances in the screen state.
 * @param onUpdateOutputs Callback to update the output instances in the screen state.
 * @param onRun Callback to run the profile operation.
 * @param onNavigateUp Called when the user presses the back button in the top bar.
 * @see ExpandedContent
 * @see NormalContent
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileOperationScreenContent(
    state: ProfileOperationScreenState,
    onUpdateInputs: (newInputs: List<ProfileOperationInput>) -> Unit,
    onUpdateOutputs: (newOutputs: List<ProfileOperationOutput>) -> Unit,
    onRun: () -> Unit,
    onNavigateUp: () -> Unit
) {
    // scaffold for top bar
    Scaffold(
        topBar = {
            // top bar with title and back button
            TopAppBar(
                title = { Text("Profile Operation") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        // box to use padding from scaffold
        Box(modifier = Modifier.padding(padding)) {
            // check whether the screen is wide
            if (LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Expanded) {
                // show wide content
                ExpandedContent(state, onUpdateInputs, onUpdateOutputs, onRun)
            } else {
                // show normal width content
                NormalContent(state, onUpdateInputs, onUpdateOutputs, onRun)
            }
        }
    }
}

/**
 * Content for [ProfileOperationScreen] when the current [WindowWidthSizeClass] is [WindowWidthSizeClass.Expanded].
 *
 * @param state The current state of the screen.
 * @param onUpdateInputs Callback to update the input instances in the screen state.
 * @param onUpdateOutputs Callback to update the output instances in the screen state.
 * @param onRun Callback to run the profile operation.
 */
@Composable
private fun ExpandedContent(
    state: ProfileOperationScreenState,
    onUpdateInputs: (newInputs: List<ProfileOperationInput>) -> Unit,
    onUpdateOutputs: (newOutputs: List<ProfileOperationOutput>) -> Unit,
    onRun: () -> Unit
) {
    // stack run button vertically with the rest of the layout
    // and make everything scrollable
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
    ) {
        // button to run the operation
        RunButton(state, onRun)
        // main layout
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            // make width enough to fit content
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            // inputs card
            OutlinedCard(
                modifier =
                    Modifier
                        // animate card size
                        .animateContentSize()
                        // weight the same as card for outputs
                        .weight(1f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(24.dp)) {
                    // state for whether the Add input dropdown is expanded
                    var dropdownShown by rememberSaveable { mutableStateOf(false) }
                    // title
                    Text("Inputs", style = MaterialTheme.typography.titleMedium)
                    // button for adding an input
                    Box {
                        // button
                        OutlinedButton(onClick = { dropdownShown = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add input...")
                        }
                        // dropdown
                        AddInputMenu(
                            expanded = dropdownShown,
                            onDismissRequest = { dropdownShown = false },
                            onInputSelect = { onUpdateInputs(state.inputs + it) }
                        )
                    }
                    // check if there are inputs
                    if (state.inputs.isNotEmpty()) {
                        // show inputs
                        InputsList(state, onUpdateInputs)
                    } else {
                        // placeholder
                        Text("No inputs added yet.")
                    }
                }
            }
            // right arrow icon
            Icon(Icons.Default.ChevronRight, contentDescription = null)
            // merge state card
            // animate card size
            OutlinedCard(modifier = Modifier.animateContentSize()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    // title
                    Text("Merge inputs")
                    // icon
                    StatusIcon(overallStatus = state.status, stepResult = state.mergeState)
                }
            }
            // right arrow icon
            Icon(Icons.Default.ChevronRight, contentDescription = null)
            // outputs card
            OutlinedCard(
                modifier =
                    Modifier
                        // animate card size
                        .animateContentSize()
                        // weight the same as card for inputs
                        .weight(1f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(24.dp)) {
                    // state for whether the Add output dropdown is expanded
                    var dropdownShown by rememberSaveable { mutableStateOf(false) }
                    // title
                    Text("Outputs", style = MaterialTheme.typography.titleMedium)
                    // button for adding an output
                    Box {
                        // button
                        OutlinedButton(onClick = { dropdownShown = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add output...")
                        }
                        // dropdown
                        AddOutputMenu(
                            expanded = dropdownShown,
                            onDismissRequest = { dropdownShown = false },
                            onOutputSelect = { onUpdateOutputs(state.outputs + it) }
                        )
                    }
                    // check if there are outputs
                    if (state.outputs.isNotEmpty()) {
                        // show outputs
                        OutputsList(state, onUpdateOutputs)
                    } else {
                        // placeholder
                        Text("No outputs added yet.")
                    }
                }
            }
        }
    }
}

/**
 * Content for [ProfileOperationScreen] when the current [WindowWidthSizeClass] is **not**
 * [WindowWidthSizeClass.Expanded].
 *
 * @param state The current state of the screen.
 * @param onUpdateInputs Callback to update the input instances in the screen state.
 * @param onUpdateOutputs Callback to update the output instances in the screen state.
 * @param onRun Callback to run the profile operation.
 */
@Composable
private fun NormalContent(
    state: ProfileOperationScreenState,
    onUpdateInputs: (newInputs: List<ProfileOperationInput>) -> Unit,
    onUpdateOutputs: (newOutputs: List<ProfileOperationOutput>) -> Unit,
    onRun: () -> Unit
) {
    // scrollable column
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
    ) {
        // state for whether inputs are expanded (vs. minimized)
        var inputsShown by rememberSaveable { mutableStateOf(true) }
        // title
        // clickable to expand/minimize inputs
        ListItem(
            // title
            headlineContent = { Text("Inputs") },
            // dropdown icon
            trailingContent = {
                if (inputsShown) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                } else {
                    Icon(Icons.Default.ArrowLeft, contentDescription = null)
                }
            },
            // click handler
            modifier = Modifier.clickable { inputsShown = !inputsShown }
        )
        // check whether inputs should be expanded
        if (inputsShown) {
            // button for adding an input
            Box {
                // state for whether the Add input dropdown is shown
                var dropdownShown by rememberSaveable { mutableStateOf(false) }
                // button
                OutlinedButton(
                    onClick = { dropdownShown = true },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add input...")
                }
                // dropdown
                AddInputMenu(
                    expanded = dropdownShown,
                    onDismissRequest = { dropdownShown = false },
                    onInputSelect = { onUpdateInputs(state.inputs + it) }
                )
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
            ) {
                // show inputs
                InputsList(state, onUpdateInputs)
            }
        }
        // merge status
        ListItem(
            // title
            headlineContent = { Text("Merge inputs") },
            // icon
            trailingContent = { StatusIcon(overallStatus = state.status, stepResult = state.mergeState) }
        )
        // state for whether outputs are expanded (vs. minimized)
        var outputsShown by rememberSaveable { mutableStateOf(true) }
        // title
        // clickable to expand/minimize outputs
        ListItem(
            // title
            headlineContent = { Text("Outputs") },
            // dropdown icon
            trailingContent = {
                if (outputsShown) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                } else {
                    Icon(Icons.Default.ArrowLeft, contentDescription = null)
                }
            },
            // click handler
            modifier = Modifier.clickable { outputsShown = !outputsShown }
        )
        // check whether outputs should be expanded
        if (outputsShown) {
            // button for adding an output
            Box {
                // state for whether the Add output dropdown is shown
                var dropdownShown by rememberSaveable { mutableStateOf(false) }
                // button
                OutlinedButton(
                    onClick = { dropdownShown = true },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add output...")
                }
                // dropdown
                AddOutputMenu(
                    expanded = dropdownShown,
                    onDismissRequest = { dropdownShown = false },
                    onOutputSelect = { onUpdateOutputs(state.outputs + it) }
                )
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
            ) {
                // show outputs
                OutputsList(state, onUpdateOutputs)
            }
        }
        // button to run the operation
        RunButton(
            state = state,
            onRun = onRun,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
        )
    }
}

/**
 * Button to run the profile operation.
 *
 * @param state The current state of the screen.
 * @param onRun Callback to run the profile operation.
 */
@Composable
private fun RunButton(state: ProfileOperationScreenState, onRun: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onRun, enabled = state.status != ProfileOperationStatus.Running, modifier = modifier) {
        // change depending on whether the operation is running
        when (state.status) {
            ProfileOperationStatus.Completed -> {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rerun operation")
            }
            ProfileOperationStatus.Idle -> {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Run operation")
            }
            ProfileOperationStatus.Running -> {
                Icon(Icons.Default.HourglassBottom, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Running")
            }
        }
    }
}

/**
 * Dropdown menu for adding an input.
 *
 * @param expanded Whether the dropdown menu should be shown.
 * @param onDismissRequest Called when the dropdown menu is dismissed.
 * @param onInputSelect Called when an input is selected to be added.
 */
@Composable
private fun AddInputMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onInputSelect: (ProfileOperationInput) -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(
            text = { Text("From existing profile") },
            onClick = {
                onDismissRequest()
                onInputSelect(InputFromProfile())
            },
            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) }
        )
        DropdownMenuItem(
            text = { Text("From folder on device") },
            onClick = {
                onDismissRequest()
                onInputSelect(InputFromFolder())
            },
            leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) }
        )
        DropdownMenuItem(
            text = { Text("From .zip file on device") },
            onClick = {
                onDismissRequest()
                onInputSelect(InputFromZip())
            },
            leadingIcon = { Icon(Icons.Default.FolderZip, contentDescription = null) }
        )
    }
}

/**
 * Dropdown menu for adding an output.
 *
 * @param expanded Whether the dropdown menu should be shown.
 * @param onDismissRequest Called when the dropdown menu is dismissed.
 * @param onOutputSelect Called when an output is selected to be added.
 */
@Composable
private fun AddOutputMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onOutputSelect: (ProfileOperationOutput) -> Unit
) {
    val appSettings = LocalAppSettings.current
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(
            text = { Text("Write to new profile") },
            onClick = {
                onDismissRequest()
                onOutputSelect(OutputToNewProfile(getAppSettings = { appSettings }))
            },
            leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null) }
        )
        DropdownMenuItem(
            text = { Text("Overwrite existing profile") },
            onClick = {
                onDismissRequest()
                onOutputSelect(OutputToProfile())
            },
            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) }
        )
        DropdownMenuItem(
            text = { Text("To folder on device") },
            onClick = {
                onDismissRequest()
                onOutputSelect(OutputToFolder())
            },
            leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) }
        )
        DropdownMenuItem(
            text = { Text("To .zip file on device") },
            onClick = {
                onDismissRequest()
                onOutputSelect(OutputToZip())
            },
            leadingIcon = { Icon(Icons.Default.FolderZip, contentDescription = null) }
        )
        DropdownMenuItem(
            text = { Text("To .ods spreadsheet on device") },
            onClick = {
                onDismissRequest()
                onOutputSelect(OutputToOds())
            },
            leadingIcon = { Icon(Icons.Default.TableView, contentDescription = null) }
        )
    }
}

/**
 * List of input instances.
 * The user can reorder these input instances using drag handles.
 *
 * @param state The current state of the screen.
 * @param onUpdateInputs Callback to update the input instances in the screen state.
 */
@Composable
private fun InputsList(
    state: ProfileOperationScreenState,
    onUpdateInputs: (newInputs: List<ProfileOperationInput>) -> Unit
) {
    // column in which the user can drag items around
    ReorderableColumn(
        list = state.inputs,
        // update inputs when a drag happens
        onSettle = { from, to -> onUpdateInputs(state.inputs.toMutableList().apply { add(to, removeAt(from)) }) },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) { index, input, _ ->
        // assign key to the card for reordering
        // ID is unique to each input instance
        key(input.id) {
            Card {
                // stack items horizontally
                // no padding because drag handle box has no padding
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            // use intrinsic height so that drag handle box can use fillMaxSize
                            .height(IntrinsicSize.Max)
                ) {
                    // config UI
                    // weighted so that other composables are measured and placed first
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(24.dp)
                    ) {
                        input.ConfigUi()
                    }
                    // row of icons + drag handle
                    Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                        // delete button
                        IconButton(
                            onClick = { onUpdateInputs(state.inputs.toMutableList().apply { removeAt(index) }) }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                        // status icon
                        StatusIcon(
                            overallStatus = state.status,
                            stepResult = state.inputResults[input.id],
                            modifier = Modifier.padding(24.dp)
                        )
                        // drag handle
                        // only show drag handle when not running
                        if (state.status != ProfileOperationStatus.Running) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier =
                                    Modifier
                                        .fillMaxHeight()
                                        // use the box as the drag target
                                        .draggableHandle()
                            ) {
                                // drag handle icon
                                Icon(
                                    Icons.Default.DragHandle,
                                    contentDescription = null,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * List of output instances.
 * The user can reorder these output instances using drag handles.
 *
 * @param state The current state of the screen.
 * @param onUpdateOutputs Callback to update the output instances in the screen state.
 */
@Composable
private fun OutputsList(
    state: ProfileOperationScreenState,
    onUpdateOutputs: (newOutputs: List<ProfileOperationOutput>) -> Unit
) {
    // no user reordering
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        state.outputs.forEachIndexed { index, output ->
            Card {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                ) {
                    // config UI
                    // weighted so that other composables are measured and placed first
                    Box(modifier = Modifier.weight(1f)) {
                        output.ConfigUi()
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    // row of icons
                    Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                        // delete button
                        IconButton(
                            onClick = { onUpdateOutputs(state.outputs.toMutableList().apply { removeAt(index) }) }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                        // status icon
                        StatusIcon(overallStatus = state.status, stepResult = state.outputResults[output.id])
                    }
                }
            }
        }
    }
}

/**
 * Step status icon shown on step cards while the profile operation is running or completed.
 *
 * @param overallStatus The current status of the overall operation.
 * @param stepResult The result from this step, or `null` if this step hasn't completed yet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusIcon(
    overallStatus: ProfileOperationStatus,
    stepResult: ProfileOperationStepResult?,
    modifier: Modifier = Modifier
) {
    // make sure the operation is running or has run
    if (overallStatus != ProfileOperationStatus.Idle) {
        // check if a result is available
        if (stepResult != null) {
            // check the result
            if (stepResult is ProfileOperationStepResult.Success) {
                // success
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = modifier)
            } else {
                // error
                // icon with tooltip
                RichTooltipBox(
                    // error message
                    text = { Text((stepResult as? ProfileOperationStepResult.Error)?.exception?.message ?: "") },
                    title = { Text("An error occurred") }
                ) {
                    // error icon
                    Icon(Icons.Default.Error, contentDescription = null, modifier = modifier.tooltipAnchor())
                }
            }
        } else if (overallStatus is ProfileOperationStatus.Running) {
            // result not available yet, but the step is running or pending
            CircularProgressIndicator(modifier = modifier)
        }
    }
}
