package org.citruscircuits.standstrategist.data.profiles.io

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import org.citruscircuits.standstrategist.data.Grosbeak
import org.citruscircuits.standstrategist.data.profiles.Profile
import java.io.IOException

suspend fun Profile.exportGrosbeak(username: String) {
    if (!Grosbeak.urlProvided) {
        throw IOException("Grosbeak URL not provided, put URL in secrets.properties and rebuild the app")
    }
    if (!Grosbeak.authProvided) {
        throw IOException("Grosbeak auth key not provided, put key in secrets.properties and rebuild the app")
    }
    val client =
        HttpClient(CIO) {
            install(ContentNegotiation) { json() }
        }
    client.put(Grosbeak.ENDPOINT) {
        header("Authorization", Grosbeak.AUTH)
        parameter("username", username)
        contentType(ContentType.Application.Json)
        setBody(Grosbeak.Data(teamData = teamData.data.value, timData = timData.data.value))
    }
}
