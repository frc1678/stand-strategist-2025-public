package org.citruscircuits.standstrategist.data.profiles.operations

import android.content.Context
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import org.citruscircuits.standstrategist.data.profiles.Profile
import java.util.UUID

/**
 * A type of output from a profile operation.
 * Comes with a configuration UI for the user to configure the exact output location,
 * and a method to export the [Profile] to that location.
 *
 * @see ProfileOperationInput
 */
sealed class ProfileOperationOutput {
    /**
     * Configuration UI for the user to configure exactly where the profile is exported to.
     * Suitable for display in a [Card].
     */
    @Composable
    abstract fun ConfigUi()

    /**
     * Method to export the [profile] to the output location.
     *
     * @param profile The [Profile] to export.
     * @param context The app's [Context].
     */
    abstract fun exportProfile(profile: Profile, context: Context)

    /**
     * Unique ID for this [ProfileOperationOutput] instance.
     */
    val id = UUID.randomUUID().toString()
}
