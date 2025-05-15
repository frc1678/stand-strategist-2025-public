package org.citruscircuits.standstrategist.data.profiles

/**
 * Gets a team list based on the given match schedule
 */
fun getTeamsList(matchSchedule: Map<String, Profile.MatchSchedule.Match>): List<String> {
    val teamsSet = mutableSetOf<String>()
    // go through each match and collect all teams
    for (match in matchSchedule.values) {
        teamsSet.addAll(match.teams.map { it.number })
    }
    return teamsSet.toList()
}
