package org.citruscircuits.standstrategist.data.datapoints

/**
 * Stores data points for the team
 * @param readableName - name of the datapoint
 * @param type - type that the datapoint is stored as
 * @param valueIn - the value that the datapoint is going to be set to
 * @param setValueIn - sets the datapoint in TeamDataEntry to the value passed in for valueIn
 */
data class TeamDataPoint<T>(
    val readableName: String,
    val type: DataType<T>,
    val valueIn: (DataPoints.Team.TeamDataEntry) -> T,
    val setValueIn: (DataPoints.Team.TeamDataEntry, T) -> DataPoints.Team.TeamDataEntry
)

/**
 * Stores data point for a team in a match
 * @param readableName - name of the datapoint
 * @param type - type that the datapoint is stored as
 * @param valueIn - the value that the datapoint is going to be set to
 * @param setValueIn - sets the datapoint in TimDataEntry to the value passed in for valueIn
 */
data class TimDataPoint<T>(
    val readableName: String,
    val type: DataType<T>,
    val valueIn: (DataPoints.Tim.TimDataEntry) -> T,
    val setValueIn: (DataPoints.Tim.TimDataEntry, T) -> DataPoints.Tim.TimDataEntry
)
