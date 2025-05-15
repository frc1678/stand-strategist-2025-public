package org.citruscircuits.standstrategist.util

/**
 * Removes illegal characters from the given [fileName].
 * See [this SO post](https://stackoverflow.com/a/2703882/) for details.
 *
 * @param fileName The file name to remove illegal characters from.
 * @return The cleaned file name.
 */
fun cleanFileName(fileName: String) = fileName.filterNot { it in "|\\?*<\":>+[]/'" }
