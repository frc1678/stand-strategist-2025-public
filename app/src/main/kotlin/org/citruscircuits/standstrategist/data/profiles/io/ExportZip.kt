package org.citruscircuits.standstrategist.data.profiles.io

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.citruscircuits.standstrategist.data.FilePaths
import org.citruscircuits.standstrategist.data.profiles.Profile
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Exports a profile in the format of a Zip file
 */
fun Profile.exportZip(): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val stream = ZipOutputStream(byteArrayOutputStream)
    with(stream) {
        putNextEntry(ZipEntry(FilePaths.SETTINGS))
        write(Json.encodeToString(settings.profileSettings.value).toByteArray())
        closeEntry()
        putNextEntry(ZipEntry(FilePaths.MATCH_SCHEDULE))
        write(Json.encodeToString(matchSchedule.schedule.value).toByteArray())
        closeEntry()
        putNextEntry(ZipEntry(FilePaths.TEAM_DATA))
        write(Json.encodeToString(teamData.data.value).toByteArray())
        closeEntry()
        putNextEntry(ZipEntry(FilePaths.TIM_DATA))
        write(Json.encodeToString(timData.data.value).toByteArray())
        closeEntry()
        close()
    }
    return byteArrayOutputStream.toByteArray()
}
