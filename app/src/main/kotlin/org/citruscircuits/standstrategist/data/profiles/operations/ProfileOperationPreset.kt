package org.citruscircuits.standstrategist.data.profiles.operations

import kotlinx.serialization.Serializable

/**
 * A preset for a profile operation.
 */
@Serializable
sealed interface ProfileOperationPreset {
    /**
     * A profile operation preset to merge the given [profile] into another profile.
     * This [profile] is placed lower in the input list.
     *
     * @param profile The name of the profile to merge from.
     */
    @Serializable
    data class MergeFrom(val profile: String) : ProfileOperationPreset

    /**
     * A profile operation preset to merge another profile into the [profile] given.
     * This [profile] is placed higher in the input list.
     *
     * @param profile The name of the profile to merge to.
     */
    @Serializable
    data class MergeInto(val profile: String) : ProfileOperationPreset

    /**
     * A profile operation preset to duplicate the [profile] into a new profile.
     *
     * @param profile The name of the profile to duplicate.
     */
    @Serializable
    data class Duplicate(val profile: String) : ProfileOperationPreset

    /**
     * A profile operation preset to import a `.zip` file and create a new profile from it.
     */
    @Serializable
    data object ImportZip : ProfileOperationPreset

    /**
     * A profile operation preset to import a folder and create a new profile from it.
     */
    @Serializable
    data object ImportFolder : ProfileOperationPreset

    /**
     * A profile operation preset to export the [profile] to a `.zip` file.
     *
     * @param profile The name of the profile to export.
     */
    @Serializable
    data class ExportZip(val profile: String) : ProfileOperationPreset

    /**
     * A profile operation preset to export the [profile] to a folder.
     *
     * @param profile The name of the profile to export.
     */
    @Serializable
    data class ExportFolder(val profile: String) : ProfileOperationPreset

    /**
     * A profile operation preset to export the [profile] to a `.ods` spreadsheet.
     *
     * @param profile The name of the profile to export.
     */
    @Serializable
    data class ExportOds(val profile: String) : ProfileOperationPreset
}
