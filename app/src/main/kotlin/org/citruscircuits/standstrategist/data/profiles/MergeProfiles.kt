package org.citruscircuits.standstrategist.data.profiles

import org.citruscircuits.standstrategist.data.TeamDataMap
import org.citruscircuits.standstrategist.data.TimDataMap
import org.citruscircuits.standstrategist.data.datapoints.DataPoints

/**
 * Merges the given [profiles] into a single [Profile].
 * [Profile]s that come earlier in [profiles] are prioritized.
 * - The profile settings are set to their default values, no matter what the settings are for the given profiles.
 * - The first non-empty match schedule is used.
 * - Both team data and TIM data are merged, with the following rules:
 *     - Every team that shows up in the team data of any given profile will show up in the final profile's team data.
 *     - Every match and every team in every match that shows up in the TIM data of any given profile will show up in
 *       the final profile's team data.
 *     - For strings, all non-default values are joined with newlines, ordered by the given profile order.
 *     - For integers and booleans, the first non-default value is used, or the default value if all values are default
 *       or missing.
 * @param profiles The [Profile]s to merge.
 * @param onUpdate An optional callback to be passed to the [Profile] constructor.
 * @return The merged [Profile].
 */
fun mergeProfiles(profiles: List<Profile>, onUpdate: () -> Unit = {}) =
    Profile(
        // use the first non-empty match schedule
        matchSchedule =
            profiles.firstOrNull { it.matchSchedule.schedule.value.isNotEmpty() }?.matchSchedule?.schedule?.value
                ?: emptyMap(),
        teamData = mergeTeamData(profiles),
        timData = mergeTimData(profiles),
        onUpdate = onUpdate
    )

/**
 * Merges the team data in the [profiles] into a single [TeamDataMap].
 *
 * @param profiles The [Profile]s to merge team data from.
 * @return The merged team data.
 */
private fun mergeTeamData(profiles: List<Profile>): TeamDataMap {
    // create a map to hold the final team data
    val teamDataMap = mutableMapOf<String, DataPoints.Team.TeamDataEntry>()
    // loop over teams
    for (team in profiles.flatMap { it.teamData.data.value.keys }.toSet()) {
        // create a new entry for this team
        var newEntry = DataPoints.Team.TeamDataEntry()
        // loop over team data points
        DataPoints.Team.forEachTyped(
            onString = { dataPoint ->
                // get the default value for this data point
                val default = dataPoint.valueIn(DataPoints.Team.TeamDataEntry())
                // get the value of this data point for this team in each profile's data
                // filter out nulls and default values
                val dataValues =
                    profiles.mapNotNull { profile ->
                        profile.teamData.data.value[team]?.let { dataPoint.valueIn(it) }
                    }.filterNot { it == default }
                // update the new entry with the values joined by newlines
                newEntry = dataPoint.setValueIn(newEntry, dataValues.joinToString("\n"))
            },
            onInt = { dataPoint ->
                // get the default value for this data point
                val default = dataPoint.valueIn(DataPoints.Team.TeamDataEntry())
                // get the value of this data point for this team in each profile's data
                val dataValues =
                    profiles.mapNotNull { profile ->
                        profile.teamData.data.value[team]?.let { dataPoint.valueIn(it) }
                    }.filterNot { it == default }
                // update the new entry with the first non-default value or with the default value
                newEntry = dataPoint.setValueIn(newEntry, dataValues.firstOrNull() ?: default)
            },
            onBoolean = { dataPoint ->
                // get the default value for this data point
                val default = dataPoint.valueIn(DataPoints.Team.TeamDataEntry())
                // get the value of this data point for this team in each profile's data
                val dataValues =
                    profiles.mapNotNull { profile ->
                        profile.teamData.data.value[team]?.let { dataPoint.valueIn(it) }
                    }.filterNot { it == default }
                // update the new entry with the first non-default value or with the default value
                newEntry = dataPoint.setValueIn(newEntry, dataValues.firstOrNull() ?: default)
            },
            onDropdown = { dataPoint ->
                // get the default value for this data point
                val default = dataPoint.valueIn(DataPoints.Team.TeamDataEntry())
                // get the value of this data point for this team in each profile's data
                val dataValues =
                    profiles.mapNotNull { profile ->
                        profile.teamData.data.value[team]?.let { dataPoint.valueIn(it) }
                    }.filterNot { it == default }
                // update the new entry with the first non-default value or with the default value
                newEntry = dataPoint.setValueIn(newEntry, dataValues.firstOrNull() ?: default)
            }
        )
        // put the new entry in the final map
        teamDataMap[team] = newEntry
    }
    return teamDataMap
}

/**
 * Merges the team-in-match data in the [profiles] into a single [TimDataMap].
 *
 * @param profiles The [Profile]s to merge team-in-match data from.
 * @return The merged team-in-match data.
 */
private fun mergeTimData(profiles: List<Profile>): TimDataMap {
    // get all matches
    val matches = profiles.flatMap { it.timData.data.value.keys }.toSet()
    // create a map to hold the final TIM data
    val timDataMap = mutableMapOf<String, Map<String, DataPoints.Tim.TimDataEntry>>()
    // loop over matches
    for (match in matches) {
        // create a new map for this match
        // team number to entry
        val matchMap = mutableMapOf<String, DataPoints.Tim.TimDataEntry>()
        // loop over teams
        for (team in profiles.mapNotNull { it.timData.data.value[match]?.keys }.flatten().toSet()) {
            // create a new entry for this team
            var newEntry = DataPoints.Tim.TimDataEntry()
            // loop over TIM data points
            DataPoints.Tim.forEachTyped(
                onString = { dataPoint ->
                    // get the default value for this data point
                    val default = dataPoint.valueIn(DataPoints.Tim.TimDataEntry())
                    // get the value of this data point for this team in each profile's data
                    // filter out nulls and default values
                    val dataValues =
                        profiles.mapNotNull { profile ->
                            profile.timData.data.value[match]?.get(team)?.let { dataPoint.valueIn(it) }
                        }.filterNot { it == default }
                    // update the new entry with the values joined by newlines
                    newEntry = dataPoint.setValueIn(newEntry, dataValues.joinToString("\n"))
                },
                onInt = { dataPoint ->
                    // get the default value for this data point
                    val default = dataPoint.valueIn(DataPoints.Tim.TimDataEntry())
                    // get the value of this data point for this team in each profile's data
                    val dataValues =
                        profiles.mapNotNull { profile ->
                            profile.timData.data.value[match]?.get(team)?.let { dataPoint.valueIn(it) }
                        }.filterNot { it == default }
                    // update the new entry with the first non-default value or with the default value
                    newEntry = dataPoint.setValueIn(newEntry, dataValues.firstOrNull() ?: default)
                },
                onBoolean = { dataPoint ->
                    // get the default value for this data point
                    val default = dataPoint.valueIn(DataPoints.Tim.TimDataEntry())
                    // get the value of this data point for this team in each profile's data
                    val dataValues =
                        profiles.mapNotNull { profile ->
                            profile.timData.data.value[match]?.get(team)?.let { dataPoint.valueIn(it) }
                        }.filterNot { it == default }
                    // update the new entry with the first non-default value or with the default value
                    newEntry = dataPoint.setValueIn(newEntry, dataValues.firstOrNull() ?: default)
                },
                onDropdown = { dataPoint ->
                    // get the default value for this data point
                    val default = dataPoint.valueIn(DataPoints.Tim.TimDataEntry())
                    // get the value of this data point for this team in each profile's data
                    val dataValues =
                        profiles.mapNotNull { profile ->
                            profile.timData.data.value[match]?.get(team)?.let { dataPoint.valueIn(it) }
                        }.filterNot { it == default }
                    // update the new entry with the first non-default value or with the default value
                    newEntry = dataPoint.setValueIn(newEntry, dataValues.firstOrNull() ?: default)
                }
            )
            // put the new entry in the final map
            matchMap[team] = newEntry
        }
        // put the new map in the final map
        timDataMap[match] = matchMap
    }
    return timDataMap
}
