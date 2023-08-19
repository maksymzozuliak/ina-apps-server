package com.ina_apps

import com.ina_apps.plugins.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    val uri = System.getenv("CONNECTION_STRING_URI")
    val client = MongoClient.create(uri)
    val database = client.getDatabase("InA-Database")

    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureSockets()
    configureRouting(database)

}
