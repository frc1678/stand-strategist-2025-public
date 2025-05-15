package org.citruscircuits.standstrategist.data.datapoints

import kotlinx.serialization.Serializable

/**
 * Represents a holder for a data points list.
 */
sealed interface DataPoints {
    /**
     * The team data points
     * Value dataPoints is a list of TeamDataPoints
     * Creates a instance of TeamDataPoint given the passed in arguments for each different collected datapoint
     */
    data object Team : DataPoints {
        val dataPoints: List<TeamDataPoint<*>> =
            listOf(
                TeamDataPoint(
                    readableName = "Can Intake Ground",
                    type = DataType.Bool,
                    valueIn = { entry -> entry.can_intake_ground },
                    setValueIn = { entry, value -> entry.copy( can_intake_ground = value) }
                ),
                TeamDataPoint(
                    readableName = "Strengths",
                    type = DataType.Str,
                    valueIn = { entry -> entry.strengths },
                    setValueIn = { entry, value -> entry.copy(strengths = value) }
                ),
                TeamDataPoint(
                    readableName = "Weaknesses",
                    type = DataType.Str,
                    valueIn = { entry -> entry.weaknesses },
                    setValueIn = { entry, value -> entry.copy(weaknesses = value) }
                ),
                TeamDataPoint(
                    readableName = "Team Notes",
                    type = DataType.Str,
                    valueIn = { entry -> entry.team_notes },
                    setValueIn = { entry, value -> entry.copy(team_notes = value) }
                ),
            )

        /**
         * Stores the value for each of the datapoints in the data object Team
         */
        @Serializable
        data class TeamDataEntry(
            val can_intake_ground: Boolean = false,
            val strengths: String = "",
            val weaknesses: String = "",
            val team_notes: String = "",
        )

        /**
         * Iterates over the data points and runs one of the given callbacks on each data point depending on its type.
         */
        inline fun forEachTyped(
            onString: (TeamDataPoint<String>) -> Unit,
            onInt: (TeamDataPoint<Int>) -> Unit,
            onBoolean: (TeamDataPoint<Boolean>) -> Unit,
            onDropdown: (TeamDataPoint<String>) -> Unit
        ) {
            for (dataPoint in dataPoints) {
                @Suppress("UNCHECKED_CAST")
                when (dataPoint.type) {
                    DataType.Str -> onString(dataPoint as TeamDataPoint<String>)
                    DataType.Integer -> onInt(dataPoint as TeamDataPoint<Int>)
                    DataType.Bool -> onBoolean(dataPoint as TeamDataPoint<Boolean>)
                    DataType.Dropdown -> onDropdown(dataPoint as TeamDataPoint<String>)
                }
            }
        }
    }

    /**
     * The team-in-match data points
     * Value dataPoints is a list of TimDataPoints
     * Creates a instance of TimDataPoint given the passed in arguments for each different collected datapoint
     */
    data object Tim : DataPoints {
        val dataPoints: List<TimDataPoint<*>> =
            listOf(
                TimDataPoint(
                    readableName = "Auto Strategies",
                    type = DataType.Str,
                    valueIn = { entry -> entry.tim_auto_strategies },
                    setValueIn = { entry, value -> entry.copy(tim_auto_strategies = value) }
                ),
                TimDataPoint(
                    readableName = "Played Defense",
                    type = DataType.Bool,
                    valueIn = { entry -> entry.played_defense },
                    setValueIn = { entry, value -> entry.copy(played_defense = value) }
                ),
                TimDataPoint(
                    readableName = "Defense Rating",
                    type = DataType.Integer,
                    valueIn = { entry -> entry.defense_rating },
                    setValueIn = { entry, value -> entry.copy(defense_rating = value) }
                ),
                TimDataPoint(
                    readableName = "Defense Timestamp",
                    type = DataType.Str,
                    valueIn = { entry -> entry.defense_timestamp },
                    setValueIn = { entry, value -> entry.copy(defense_timestamp = value) }
                ),
                TimDataPoint(
                    readableName = "Played Against Defense",
                    type = DataType.Bool,
                    valueIn = { entry -> entry.played_against_defense },
                    setValueIn = { entry, value -> entry.copy(played_against_defense = value) }
                ),
                TimDataPoint(
                    readableName = "Broken Mechanism",
                    type = DataType.Str,
                    valueIn = { entry -> entry.broken_mechanism },
                    setValueIn = { entry, value -> entry.copy(broken_mechanism = value) }
                ),
                TimDataPoint(
                    readableName = "Match Notes",
                    type = DataType.Str,
                    valueIn = { entry -> entry.tim_notes },
                    setValueIn = { entry, value -> entry.copy(tim_notes = value) }
                ),
            )

        /**
         * Stores the value for each of the datapoints in the data object Tim
         */
        @Serializable
        data class TimDataEntry(
            val played_defense: Boolean = false,
            val defense_rating: Int = 0,
            val defense_timestamp: String = "",
            val played_against_defense: Boolean = false,
            val tim_notes: String = "",
            val broken_mechanism: String = "",
            val tim_auto_strategies: String = ""
        )

        /**
         * Iterates over the data points and runs one of the given callbacks on each data point depending on its type.
         */
        inline fun forEachTyped(
            onString: (TimDataPoint<String>) -> Unit,
            onInt: (TimDataPoint<Int>) -> Unit,
            onBoolean: (TimDataPoint<Boolean>) -> Unit,
            onDropdown: (TimDataPoint<String>) -> Unit
        ) {
            for (dataPoint in dataPoints) {
                @Suppress("UNCHECKED_CAST")
                when (dataPoint.type) {
                    DataType.Str -> onString(dataPoint as TimDataPoint<String>)
                    DataType.Integer -> onInt(dataPoint as TimDataPoint<Int>)
                    DataType.Bool -> onBoolean(dataPoint as TimDataPoint<Boolean>)
                    DataType.Dropdown -> onDropdown(dataPoint as TimDataPoint<String>)
                }
            }
        }
    }
}
