package org.citruscircuits.standstrategist.data.profiles.io

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import org.citruscircuits.standstrategist.data.Grosbeak
import org.citruscircuits.standstrategist.data.profiles.Profile
import java.io.IOException

suspend fun importGrosbeak(username: String, onUpdate: () -> Unit = {}): Profile {
    if (!Grosbeak.urlProvided) {
        throw IOException("Grosbeak URL not provided, put URL in secrets.properties and rebuild the app")
    }
    if (!Grosbeak.authProvided) {
        throw IOException("Grosbeak auth key not provided, put key in secrets.properties and rebuild the app")
    }
    val client = HttpClient(CIO) { install(ContentNegotiation) { json() } }
    val data: Grosbeak.Data =
        client.get(Grosbeak.ENDPOINT) {
            header("Authorization", Grosbeak.AUTH)
            parameter("username", username)
        }.body()
    return Profile(teamData = data.teamData, timData = data.timData, onUpdate = onUpdate)
}
