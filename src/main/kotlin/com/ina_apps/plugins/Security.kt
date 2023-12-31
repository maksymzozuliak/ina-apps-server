package com.ina_apps.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ina_apps.plugins.session.MenuSession
import com.ina_apps.utils.RegistrationSession
import com.zozuliak.security.token.TokenConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.litote.kmongo.json
import java.lang.NullPointerException

fun Application.configureSecurity(config: TokenConfig) {

    authentication {
        jwt {
            realm = "Muscle Mate"
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

    intercept(Plugins) {
        val apiKey = call.request.headers["api-key"]
        if (apiKey != System.getenv("API_KEY")) {
            call.respond(HttpStatusCode.Forbidden)
            return@intercept
        }
        if(call.sessions.get<RegistrationSession>() == null) {
            val restaurantId = call.parameters["restaurantId"]
            call.sessions.set(
                RegistrationSession(
                    sessionId = generateSessionId(),
                    restaurantId = restaurantId?:""
                )
            )
        }
    }
}
