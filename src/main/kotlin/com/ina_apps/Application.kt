package com.ina_apps

import com.ina_apps.plugins.*
import com.ina_apps.utils.EmailService
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.zozuliak.security.hashing.SHA256HashingService
import com.zozuliak.security.token.JwtTokenService
import com.zozuliak.security.token.TokenConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.HtmlEmail
import org.apache.commons.mail.SimpleEmail
import java.io.File

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT").toInt(), module = Application::module)
        .start(wait = true)
}


fun Application.module() {

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = "http://0.0.0.0:8000",
        audience = "users",
        expiresIn = 365L * 100L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    // MongoDB
    val uri = System.getenv("CONNECTION_STRING_URI")
    val mongoDBClient = MongoClient.create(uri)
    val database = mongoDBClient.getDatabase("InA-Database")

    configureSecurity(tokenConfig)
    configureMonitoring()
    configureSerialization()
    configureSockets()
    configureRouting(
        database = database,
        client = client,
        hashingService = hashingService,
        tokenService = tokenService,
        tokenConfig = tokenConfig
    )

}
