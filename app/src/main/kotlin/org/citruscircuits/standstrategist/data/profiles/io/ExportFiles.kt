package org.citruscircuits.standstrategist.data.profiles.io

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.citruscircuits.standstrategist.data.FilePaths
import org.citruscircuits.standstrategist.data.profiles.Profile

/**
 * Returns a map of FilePaths for SETTINGS, MATCH_SCHEDULE, TEAM_DATA, and TIM_DATA for a given Profile
 */
fun Profile.exportFiles() =
    mapOf(
        FilePaths.SETTINGS to Json.encodeToString(settings.profileSettings.value),
        FilePaths.MATCH_SCHEDULE to Json.encodeToString(matchSchedule.schedule.value),
        FilePaths.TEAM_DATA to Json.encodeToString(teamData.data.value),
        FilePaths.TIM_DATA to Json.encodeToString(timData.data.value)
    )
