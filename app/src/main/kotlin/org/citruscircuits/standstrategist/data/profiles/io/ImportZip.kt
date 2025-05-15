package org.citruscircuits.standstrategist.data.profiles.io

import java.util.zip.ZipInputStream

/**
 * Imports a profile that is of Zip file format
 * @param zipData - the zip file being imported
 *
 * @see importFiles
 */
fun importZip(zipData: ByteArray) =
    ZipInputStream(zipData.inputStream()).use { stream ->
        val map = mutableMapOf<String, ByteArray>()
        var entry = stream.nextEntry
        while (entry != null) {
            map[entry.name] = stream.readBytes()
            entry = stream.nextEntry
        }
        importFiles(map)
    }
