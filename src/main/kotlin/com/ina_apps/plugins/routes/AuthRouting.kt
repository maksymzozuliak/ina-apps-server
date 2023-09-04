package com.ina_apps.plugins.routes

import com.ina_apps.model.AuthRequest
import com.ina_apps.model.database_classes.User
import com.ina_apps.model.services.UserService
import com.ina_apps.utils.EmailService
import com.zozuliak.security.hashing.HashingService
import com.zozuliak.security.hashing.SaltedHash
import com.zozuliak.security.token.TokenClaim
import com.zozuliak.security.token.TokenConfig
import com.zozuliak.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Route.authRouting(
    userService: UserService,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    emailService: EmailService
) {

    post("/signUp") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val isBlank = request.email.isBlank() && request.password.isBlank()
        if (isBlank) {
            call.respond(HttpStatusCode.BadRequest, "Data is blank")
            return@post
        }
        val existingUsers = userService.getUsersForRestaurant(request.restaurantId)
        if (existingUsers.any { it.email == request.email }) {
            call.respond(HttpStatusCode.Conflict, "This email is already used")
            return@post
        }

        call.respond(HttpStatusCode.OK)
        launch(Dispatchers.Default) {
            emailService.sendVerificationEmail(request.email)
        }
    }

    post("/verifyEmail") {

        val code = call.parameters["code"]
        val request = call.receiveNullable<AuthRequest>()
        if (request == null || code == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if (emailService.verifyCode(request.email, code)) {

            val saltedHash = hashingService.generateSaltedHash(request.password)
            val user = User(
                email = request.email,
                password = saltedHash.hash,
                salt = saltedHash.salt,
                restaurantId = request.restaurantId
            )
            val wasAcknowledged = userService.insertUser(user)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            } else {
                call.respond(HttpStatusCode.OK)
                return@post
            }
        } else {
            call.respond(HttpStatusCode.Forbidden)
            return@post
        }
    }

    post("/signIn") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val existingUsers = userService.getUsersForRestaurant(request.restaurantId)
        val user = existingUsers.find { it.email == request.email }
        if (user == null) {
            call.respond(HttpStatusCode.NotFound)
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Forbidden)
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id ?: "error_tag"
            ),
            TokenClaim(
                name = "restaurantId",
                value = user.restaurantId
            )
        )
        call.respond(HttpStatusCode.OK, token)

    }


    authenticate {
        get("/authenticate") {
            call.respond(HttpStatusCode.OK)
        }

//        get("/secret") {
//            val principal = call.principal<JWTPrincipal>()
//            val userId = principal?.getClaim("userId", String::class)
//            if (userId != null) {
//                call.respond(HttpStatusCode.OK, userId)
//            } else {
//                call.respond(HttpStatusCode.NotFound)
//            }
//        }
    }
}