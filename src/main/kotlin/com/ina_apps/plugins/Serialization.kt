package com.ina_apps.plugins

import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            // Enable binary content type (e.g., application/octet-stream)
            register(ContentType.Application.OctetStream, JacksonConverter())
        }
    }
}

