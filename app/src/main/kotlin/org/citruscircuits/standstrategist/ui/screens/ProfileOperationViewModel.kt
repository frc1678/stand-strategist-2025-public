package org.citruscircuits.standstrategist.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.citruscircuits.standstrategist.data.AppSettings
import org.citruscircuits.standstrategist.data.profiles.Profile
import org.citruscircuits.standstrategist.data.profiles.mergeProfiles
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
import javax.inject.Inject

/**
 * [ViewModel] for [ProfileOperationScreen].
 */
@HiltViewModel
class ProfileOperationViewModel
@Inject
constructor() : ViewModel() {
    /**
     * Screen state for [ProfileOperationScreen].
     *
     * @see ProfileOperationScreenState
     */
    val state = MutableStateFlow(ProfileOperationScreenState())

    /**
     * Updates the inputs in the current [ProfileOperationScreenState] with the [newInputs].
     */
    fun updateInputs(newInputs: List<ProfileOperationInput>) {
        state.value = state.value.copy(inputs = newInputs)
    }

    /**
     * Updates the outputs in the current [ProfileOperationScreenState] with the [newOutputs].
     */
    fun updateOutputs(newOutputs: List<ProfileOperationOutput>) {
        state.value = state.value.copy(outputs = newOutputs)
    }

    /**
     * Loads the given [preset] into the screen [state].
     *
     * @param preset The [ProfileOperationPreset] to load.
     * @param getAppSettings Getter for the app's settings.
     */
    fun loadPreset(preset: ProfileOperationPreset, getAppSettings: () -> AppSettings) {
        state.value =
            when (preset) {
                is ProfileOperationPreset.MergeFrom ->
                    ProfileOperationScreenState(
                        inputs = listOf(InputFromProfile(), InputFromProfile(preset.profile)),
                        outputs = listOf(OutputToProfile())
                    )

                is ProfileOperationPreset.MergeInto ->
                    ProfileOperationScreenState(
                        inputs = listOf(InputFromProfile(preset.profile), InputFromProfile()),
                        outputs = listOf(OutputToProfile(preset.profile))
                    )

                is ProfileOperationPreset.Duplicate ->
                    ProfileOperationScreenState(
                        inputs = listOf(InputFromProfile(preset.profile)),
                        outputs = listOf(OutputToNewProfile(getAppSettings))
                    )

                is ProfileOperationPreset.ImportZip ->
                    ProfileOperationScreenState(
                        inputs = listOf(InputFromZip()),
                        outputs = listOf(OutputToNewProfile(getAppSettings))
                    )

                is ProfileOperationPreset.ImportFolder ->
                    ProfileOperationScreenState(
                        inputs = listOf(InputFromFolder()),
                        outputs = listOf(OutputToNewProfile(getAppSettings))
                    )

                is ProfileOperationPreset.ExportZip ->
                    ProfileOperationScreenState(
                        inputs = listOf(InputFromProfile(preset.profile)),
                        outputs = listOf(OutputToZip())
                    )

                is ProfileOperationPreset.ExportFolder ->
                    ProfileOperationScreenState(
                        inputs = listOf(InputFromProfile(preset.profile)),
                        outputs = listOf(OutputToFolder())
                    )

                is ProfileOperationPreset.ExportOds ->
                    ProfileOperationScreenState(
                        inputs = listOf(InputFromProfile(preset.profile)),
                        outputs = listOf(OutputToOds())
                    )
            }
    }

    /**
     * Runs this profile operation.
     * Updates [state] regularly with the status of the operation.
     *
     * @param context The app's context, used when importing/exporting [Profile]s.
     */
    fun run(context: Context) {
        // run outside the UI thread
        viewModelScope.launch(Dispatchers.IO) {
            // reset the state
            state.value =
                state.value.copy(
                    inputResults = mapOf(),
                    outputResults = mapOf(),
                    mergeState = null,
                    status = ProfileOperationStatus.Running
                )
            // create the list of imported profiles to be merged
            val importedProfiles = mutableListOf<Profile>()
            // iterate over inputs
            for (input in state.value.inputs) {
                @Suppress("TooGenericExceptionCaught")
                val result =
                    try {
                        // import the profile
                        importedProfiles.add(input.importProfile(context))
                        // success
                        ProfileOperationStepResult.Success
                    } catch (e: Throwable) {
                        // something went wrong
                        ProfileOperationStepResult.Error(e)
                    }
                // show the result for this input
                state.value =
                    state.value.copy(
                        inputResults = state.value.inputResults.toMutableMap().apply { set(input.id, result) }
                    )
                // if any inputs fail, abort immediately
                if (result is ProfileOperationStepResult.Error) return@launch
            }
            // merge the imported profiles
            val mergedProfile = mergeProfiles(importedProfiles)
            // update the merge state once merging is done
            state.value = state.value.copy(mergeState = ProfileOperationStepResult.Success)
            // iterate over outputs
            for (output in state.value.outputs) {
                @Suppress("TooGenericExceptionCaught")
                val result =
                    try {
                        // export the profile
                        output.exportProfile(mergedProfile, context)
                        // success
                        ProfileOperationStepResult.Success
                    } catch (e: Throwable) {
                        // something went wrong
                        ProfileOperationStepResult.Error(e)
                    }
                // show the result for this output
                state.value =
                    state.value.copy(
                        outputResults = state.value.outputResults.toMutableMap().apply { set(output.id, result) }
                    )
            }
        }.invokeOnCompletion {
            // operation is completed
            state.value = state.value.copy(status = ProfileOperationStatus.Completed)
        }
    }
}
