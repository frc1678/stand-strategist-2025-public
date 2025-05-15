package org.citruscircuits.standstrategist.data.profiles.operations

import android.content.Context
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import org.citruscircuits.standstrategist.data.profiles.Profile
import java.util.UUID

/**
 * A type of input to a profile operation.
 * Comes with a configuration UI for the user to configure the exact input source,
 * and a method to import the [Profile] from that input source.
 *
 * @see ProfileOperationOutput
 */
sealed class ProfileOperationInput {
    /**
     * Configuration UI for the user to configure exactly where the profile is imported from.
     * Suitable for display in a [Card].
     */
    @Composable
    abstract fun ConfigUi()

    /**
     * Method to import the [Profile] from the input source.
     *
     * @param context The app's [Context].
     * @return The imported [Profile].
     */
    abstract fun importProfile(context: Context): Profile

    /**
     * Unique ID for this [ProfileOperationInput] instance.
     */
    val id = UUID.randomUUID().toString()
}
