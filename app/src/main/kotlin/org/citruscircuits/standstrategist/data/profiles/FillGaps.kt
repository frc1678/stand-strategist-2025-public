package org.citruscircuits.standstrategist.data.profiles

import org.citruscircuits.standstrategist.data.datapoints.DataPoints

/**
 * Looks at the match schedule and fills in any team or matches that aren't there
 */
fun Profile.fillGaps() {
    // team data
    val teamsList = getTeamsList(matchSchedule.schedule.value)
    val newTeamData = teamData.data.value.toMutableMap()
    // iterate over teams
    for (team in teamsList) {
        newTeamData.putIfAbsent(team, DataPoints.Team.TeamDataEntry())
    }
    teamData.updateAllData(newTeamData)

    // tim data
    val newTimData = timData.data.value.toMutableMap()
    // iterate over matches
    for ((match, matchObj) in matchSchedule.schedule.value) {
        val newTeamsMap = newTimData[match]?.toMutableMap() ?: mutableMapOf()
        // iterate over the teams in the match
        for (team in matchObj.teams.map { it.number }) {
            newTeamsMap.putIfAbsent(team, DataPoints.Tim.TimDataEntry())
        }
        newTimData[match] = newTeamsMap
    }
    timData.updateAllData(newTimData)
}
