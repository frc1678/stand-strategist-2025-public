package org.citruscircuits.standstrategist.data

import io.ktor.http.Url
import kotlinx.serialization.Serializable
import org.citruscircuits.standstrategist.BuildConfig

/**
 * Sets up Grosbeak communication
 */
object Grosbeak {
    private const val URL = BuildConfig.GROSBEAK_URL
    val urlProvided get() = URL.isNotEmpty()
    const val AUTH = BuildConfig.GROSBEAK_AUTH
    val authProvided get() = AUTH.isNotEmpty()

    val ENDPOINT = Url("$URL/stand-strategist")

    /**
     * How data is given to Grosbeak
     */
    @Serializable
    data class Data(val teamData: TeamDataMap, val timData: TimDataMap)
}
