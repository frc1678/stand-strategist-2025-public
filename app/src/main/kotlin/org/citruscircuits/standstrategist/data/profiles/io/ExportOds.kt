package org.citruscircuits.standstrategist.data.profiles.io

import android.util.Log
import com.github.miachm.sods.Sheet
import com.github.miachm.sods.SpreadSheet
import org.citruscircuits.standstrategist.data.datapoints.DataPoints
import org.citruscircuits.standstrategist.data.profiles.Profile
import java.io.ByteArrayOutputStream

/**
 * Exports a Profile in the format of a open document spreadsheet (.ods)
 */
fun Profile.exportOds(profileName: String?): ByteArray {
    val teamSheet =
        Sheet("Team Data" + (profileName?.let { "($it)" } ?: "")).apply {
            // new sheets are automatically sized 1x1, so we need to call append methods as we add data
            // sheet operations are 0-indexed
            // top left corner
            getRange(0, 0).value = "Team number"
            // put data point names in header row
            DataPoints.Team.dataPoints.forEach { dataPoint ->
                // get index of next column
                val colNum = maxColumns
                // create the next column
                appendColumn()
                // set the value in the cell
                getRange(0, colNum).value = dataPoint.readableName
                Log.e("CellValue", getRange(0, colNum).value.toString())
            }
            // one row per team
            teamData.data.value.toList().forEach { (teamNumber, teamDataEntry) ->
                // get index of next row
                val rowNum = maxRows
                // create the next row
                appendRow()
                // set 1st cell to team number
                getRange(rowNum, 0).value = teamNumber
                DataPoints.Team.dataPoints.forEachIndexed { index, dataPoint ->
                    // set the cell to the data value
                    // add 1 to index because the 1st cell is for team number
                    getRange(rowNum, index + 1).value = dataPoint.valueIn(teamDataEntry)
                }
            }
        }
    val timSheet =
        Sheet("TIM Data" + (profileName?.let { "($it)" } ?: "")).apply {
            // new sheets are automatically sized 1x1, so we need to call append methods as we add data
            // sheet operations are 0-indexed
            // top left corner
            getRange(0, 0).value = "Match number"
            // add column for team numbers
            appendColumn()
            // 2nd cell in the 1st row
            getRange(0, 1).value = "Team number"
            // put data point names in header row
            DataPoints.Tim.dataPoints.forEach { dataPoint ->
                // get index of next column
                val colNum = maxColumns
                // create the next column
                appendColumn()
                // set the value in the cell
                getRange(0, colNum).value = dataPoint.readableName
            }
            // one row per team/match combination
            timData.data.value.forEach { (matchNumber, matchMap) ->
                matchMap.forEach { (teamNumber, timDataEntry) ->
                    // get index of next row
                    val rowNum = maxRows
                    // create the next row
                    appendRow()
                    // set 1st cell to match numberF
                    getRange(rowNum, 0).value = matchNumber
                    // set 2nd cell to team number
                    getRange(rowNum, 1).value = teamNumber
                    DataPoints.Tim.dataPoints.forEachIndexed { index, dataPoint ->
                        // set the cell to the data value
                        // add 2 to index because the first 2 cells are for match and team numbers
                        getRange(rowNum, index + 2).value = dataPoint.valueIn(timDataEntry)
                    }
                }
            }
        }
    val spreadSheet =
        SpreadSheet().apply {
            appendSheet(teamSheet)
            appendSheet(timSheet)
        }
    val stream = ByteArrayOutputStream()
    spreadSheet.save(stream)
    return stream.toByteArray()
}
