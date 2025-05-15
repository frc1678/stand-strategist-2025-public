package org.citruscircuits.standstrategist.data.profiles

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import org.citruscircuits.standstrategist.data.Alliance
import org.citruscircuits.standstrategist.data.TeamDataMap
import org.citruscircuits.standstrategist.data.TimDataMap
import org.citruscircuits.standstrategist.data.datapoints.DataPoints
import org.citruscircuits.standstrategist.data.datapoints.TeamDataPoint
import org.citruscircuits.standstrategist.data.datapoints.TimDataPoint

/**
 * User Profile class
 * @param profileSettings - the Profile's settings
 * @param matchSchedule - the Profile's selected match schedule
 * @param teamData - the Profile's recorded team data
 * @param timData - the Profile's recorded tim data
 * @param onUpdate - what happens when you change things in the Profile
 */
class Profile(
    profileSettings: Settings.ProfileSettings = Settings.ProfileSettings(),
    matchSchedule: Map<String, MatchSchedule.Match> = emptyMap(),
    teamData: TeamDataMap = emptyMap(),
    timData: TimDataMap = emptyMap(),
    onUpdate: () -> Unit = {}
) {
    // The Profile's settings, match schedule, team data, and tim data
    val settings = Settings(initialSettings = profileSettings, onUpdate = onUpdate)
    val matchSchedule = MatchSchedule(initialMatchSchedule = matchSchedule, onUpdate = onUpdate)
    val teamData = TeamData(initialTeamData = teamData, onUpdate = onUpdate)
    val timData = TimData(initialTimData = timData, onUpdate = onUpdate)

    /**
     * Settings class
     * @param initialSettings - Initial settings for the Profile
     * @param onUpdate - what happens when you update the settings
     */
    class Settings(initialSettings: ProfileSettings, private val onUpdate: () -> Unit) {
        /**
         * The Profile's current settings for matches
         * @param alliance - what alliance the Profile is currently scouting
         * @param matchNumber - what match number the Profile is currently scouting
         * @param page - which page the Profile is on
         */
        @Serializable
        data class ProfileSettings(
            val alliance: Alliance? = null,
            val matchNumber: String = "1",
            val page: Int = 0
        )

        // sets the Profile settings to the initial settings
        private val _profileSettings = MutableStateFlow(initialSettings)
        var profileSettings = _profileSettings.asStateFlow()

        /**
         * updates the Profile's settings
         */
        fun update(newSettings: ProfileSettings) {
            _profileSettings.value = newSettings
            onUpdate()
        }
    }

    /**
     * Match contains a list of instances of Team which are the teams in the match
     * Team contains the alliance color and number
     * The match schedule that the app uses is stored as a map, where the keys are the match numbers (Strings)
       and the values are instances of Match so that it has every match in the schedule
     * @param initialMatchSchedule - the original imported match schedule
     * @param onUpdate - updates the match schedule
     */
    class MatchSchedule(initialMatchSchedule: Map<String, Match>, private val onUpdate: () -> Unit) {
        @Serializable
        data class Match(val teams: List<Team>)

        @Serializable
        data class Team(
            val color: Alliance,
            val number: String
        )

        private val _schedule = MutableStateFlow(initialMatchSchedule)
        var schedule = _schedule.asStateFlow()

        fun update(newSchedule: Map<String, Match>) {
            _schedule.value = newSchedule
            onUpdate()
        }
    }

    /**
     * Team Data class
     * @param initialTeamData - Team data already collected
     * @param onUpdate - what happens when you update the TeamData
     */
    class TeamData(initialTeamData: TeamDataMap, private val onUpdate: () -> Unit) {
        // sets the Profile's data to the initial team data
        private val _data = MutableStateFlow(initialTeamData)
        var data = _data.asStateFlow()

        /**
         * updates the Profile's TeamData
         */
        fun updateAllData(newData: TeamDataMap) {
            _data.value = newData
            onUpdate()
        }

        /**
         * Gets the value of the specified data point for the specified team from the team data.
         */
        @Composable
        operator fun <T : Any> get(team: String, dataPoint: TeamDataPoint<T>) =
            dataPoint.valueIn(data.collectAsStateWithLifecycle().value[team] ?: DataPoints.Team.TeamDataEntry())

        /**
         * Sets the value of the specified data point for the specified team in the team data.
         */
        operator fun <T : Any> set(team: String, dataPoint: TeamDataPoint<T>, value: T) {
            val map = _data.value.toMutableMap()
            map[team] = map[team]?.let { dataPoint.setValueIn(it, value) }
                ?: dataPoint.setValueIn(DataPoints.Team.TeamDataEntry(), value)
            _data.value = map
            onUpdate()
        }
    }

    /**
     * Tim Data class
     * @param initialTimData - Tim data already collected
     * @param onUpdate - what happens when you update the TimData
     */
    class TimData(initialTimData: TimDataMap, private val onUpdate: () -> Unit) {
        // sets the Profile's data to the initial tim data
        private val _data = MutableStateFlow(initialTimData)
        var data = _data.asStateFlow()

        /**
         * updates the Profile's tim data
         */
        fun updateAllData(newData: TimDataMap) {
            _data.value = newData
            onUpdate()
        }

        /**
         * Gets the value of the specified data point for the specified match and team from the team-in-match data.
         */
        @Composable
        operator fun <T : Any> get(match: String, team: String, dataPoint: TimDataPoint<T>) =
            dataPoint.valueIn(
                data.collectAsStateWithLifecycle().value[match]?.get(team) ?: DataPoints.Tim.TimDataEntry()
            )

        /**
         * Sets the value of the specified data point for the specified match and team in the team-in-match data.
         */
        operator fun <T : Any> set(match: String, team: String, dataPoint: TimDataPoint<T>, value: T) {
            val map = _data.value.toMutableMap()
            map[match] = map[match]?.toMutableMap()?.apply {
                this[team] = this[team]?.let { dataPoint.setValueIn(it, value) }
                    ?: dataPoint.setValueIn(DataPoints.Tim.TimDataEntry(), value)
            } ?: mapOf(team to dataPoint.setValueIn(DataPoints.Tim.TimDataEntry(), value))
            _data.value = map
            onUpdate()
        }
    }
}
