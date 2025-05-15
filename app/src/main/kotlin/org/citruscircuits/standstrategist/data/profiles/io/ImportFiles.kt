package org.citruscircuits.standstrategist.data.profiles.io

import kotlinx.serialization.json.Json
import org.citruscircuits.standstrategist.data.FilePaths
import org.citruscircuits.standstrategist.data.TeamDataMap
import org.citruscircuits.standstrategist.data.TimDataMap
import org.citruscircuits.standstrategist.data.profiles.Profile
import org.citruscircuits.standstrategist.data.profiles.fillGaps

/**
 * Imports a profile and sets the profile's SETTINGS, MATCH_SCHEDULE, TEAM_DATA, and TIM_DATA according to the imported file
 * @param files - the files being imported
 * @param onUpdate - determines what happens after the profile is finished importing
 */
fun importFiles(files: Map<String, ByteArray>, onUpdate: () -> Unit = {}): Profile {
    val profileSettings =
        files[FilePaths.SETTINGS]?.let { byteArray ->
            Json.decodeFromString(byteArray.decodeToString())
        } ?: Profile.Settings.ProfileSettings()
    val matchSchedule =
        files[FilePaths.MATCH_SCHEDULE]?.let { byteArray ->
            Json.decodeFromString(byteArray.decodeToString())
        } ?: emptyMap<String, Profile.MatchSchedule.Match>()
    val teamData: TeamDataMap =
        files[FilePaths.TEAM_DATA]?.let { byteArray ->
            Json.decodeFromString(byteArray.decodeToString())
        } ?: emptyMap()
    val timData: TimDataMap =
        files[FilePaths.TIM_DATA]?.let { byteArray ->
            Json.decodeFromString(byteArray.decodeToString())
        } ?: emptyMap()
    return Profile(profileSettings, matchSchedule, teamData, timData, onUpdate).apply { fillGaps() }
}
