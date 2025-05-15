package org.citruscircuits.standstrategist.data

import android.os.Environment
import java.io.File

/**
 * Paths to files used by the app.
 */
object FilePaths {
    /**
     * The path to the main folder that the app stores files in.
     */
    private val mainFolder =
        File(Environment.getExternalStorageDirectory().absolutePath + "/Documents/StandStrategist")

    /**
     * The path to the folder containing all profiles defined in the global settings.
     */
    val profilesFolder = File(mainFolder, "profiles")

    /**
     * The name of the match schedule file.
     */
    const val MATCH_SCHEDULE = "match_schedule.json"

    /**
     * The name of the team data file.
     */
    const val TEAM_DATA = "team_data.json"

    /**
     * The name of the TIM data file.
     */
    const val TIM_DATA = "tim_data.json"

    /**
     * The name of the settings file.
     */
    const val SETTINGS = "settings.json"

    /**
     * The path to the global settings file.
     */
    val globalSettings = File(mainFolder, SETTINGS)

    /**
     * @return the path to the match schedule file for this [profile].
     */
    fun matchSchedule(profile: String) = File(profilesFolder, "$profile/match_schedule.json")

    /**
     * @return the path to the editable match schedule file for this [profile].
     */
    fun profileMatchSchedule(profile: String) = File(profilesFolder, "$profile/editable_match_schedule.json")

    /**
     * @return the path to the team-in-match data file for this [profile].
     */
    fun timData(profile: String) = File(profilesFolder, "$profile/tim_data.json")

    /**
     * @return the path to the team data file for this [profile].
     */
    fun teamData(profile: String) = File(profilesFolder, "$profile/team_data.json")

    /**
     * @return the path to the settings file for this [profile].
     */
    fun settings(profile: String) = File(profilesFolder, "$profile/settings.json")

    /**
     * The path to the trash folder.
     */
    val trash = File(mainFolder, "trash")
}
