package com.ina_apps.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ina_apps.utils.RegistrationSession
import com.zozuliak.security.token.TokenConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*

fun Application.configureSecurity(config: TokenConfig) {

    authentication {
        jwt {
            realm = "InA Apps"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(config.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }

    install(Sessions) {
        cookie<RegistrationSession>("SESSION")
    }

    install(CORS) {

        anyHost() // TODO()
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowHeadersPrefixed("api-key")
    }

    intercept(Plugins) {
        val uri = call.request.uri
        val apiKey = call.request.headers["api-key"]
        println("TAGG " + apiKey)
        if (apiKey != System.getenv("API_KEY") && !uri.contains("/poster")) {
            call.respond(HttpStatusCode.Forbidden)
            return@intercept
        }
        if (call.sessions.get<RegistrationSession>() == null) {
            val restaurantId = call.parameters["restaurantId"]
            call.sessions.set(
                RegistrationSession(
                    sessionId = generateSessionId(),
                    restaurantId = restaurantId ?: ""
                )
            )
        }
    }
}
