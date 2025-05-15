package org.citruscircuits.standstrategist.data.datapoints

/**
 * A supported data type for team and team-in-match data.
 */
sealed interface DataType<T> {
    /**
     * [DataType] representing [String]s.
     */
    data object Str : DataType<String>

    /**
     * [DataType] representing [Int]s.
     */
    data object Integer : DataType<Int>

    /**
     * [DataType] representing [Boolean]s.
     */
    data object Bool : DataType<Boolean>

    /**
     * [DataType] representing [Dropdown]s.
     */
    data object Dropdown : DataType<String>
}
