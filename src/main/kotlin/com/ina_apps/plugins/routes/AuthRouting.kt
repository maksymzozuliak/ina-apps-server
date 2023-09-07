package com.ina_apps.plugins.routes

import com.ina_apps.model.AuthRequest
import com.ina_apps.model.RegistrationRequest
import com.ina_apps.model.database_classes.User
import com.ina_apps.model.database_classes.UserInformation
import com.ina_apps.model.services.UserService
import com.ina_apps.room.RegistrationRoomController
import com.ina_apps.utils.EmailService
import com.ina_apps.utils.RegistrationSession
import com.ina_apps.utils.security.SocketMessage
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
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDateTime

fun Route.authRouting(
    userService: UserService,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    emailService: EmailService,
    registrationRoomController: RegistrationRoomController
) {

    webSocket("/signUp") {
        val session = call.sessions.get<RegistrationSession>()
        var email: String? = null
        var password: String? = null
        if(session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
            return@webSocket
        }
        try {
            registrationRoomController.onJoin(
                sessionId = session.sessionId,
                socket = this
            )
            incoming.consumeEach { frame ->
                if(frame is Frame.Text) {
                    val message = frame.readText()
                    val jsonObject = Json.parseToJsonElement(message)
                    val socketMessage = Json.decodeFromJsonElement(SocketMessage.serializer(), jsonObject)
                    println("STEP0" + message)
                    when (socketMessage.type) {
                        "VERIFICATION_CODE" -> {
                            launch(Dispatchers.Default) {
                                println("STEP1" + email + socketMessage.message?.get("verification_code").toString())
                                registrationRoomController.sendBoolean(session.sessionId, emailService.verifyCode(email!!, socketMessage.message!!["verification_code"]!!))
                            }
                        }
                        "REGISTRATION_INFO" -> {
                            val existingUsers = userService.getUsersForRestaurant(session.restaurantId)
                            val doesExist = existingUsers.any { it.email == socketMessage.message!!["email"] }
                            launch(Dispatchers.Default) {
                                registrationRoomController.sendBoolean(session.sessionId, !doesExist)
                                if (!doesExist) {
                                    email = socketMessage.message!!["email"]
                                    println(socketMessage.message!!["email"] + email)
                                    emailService.sendVerificationEmail(email!!, session.restaurantId)
                                    password = socketMessage.message["password"]
                                }
                            }
                        }
                        "USER_INFO" -> {
                            println("STEP3" + socketMessage.message!!["name"] + socketMessage.message["phone_number"])
                            val saltedHash = hashingService.generateSaltedHash(password!!)
                            val user = User(
                                email = email!!,
                                password = saltedHash.hash,
                                salt = saltedHash.salt,
                                restaurantId = session.restaurantId,
                                userInformation = UserInformation(
                                    phoneNumber = socketMessage.message["phone_number"],
                                    name = socketMessage.message["name"],
                                    dateOfRegistration = LocalDateTime.now().toString()
                                )
                            )
                            launch(Dispatchers.Default) {
                                registrationRoomController.sendBoolean(session.sessionId, userService.insertUser(user))
                            }
                        }
                        "DISCONNECT" -> {
                            return@consumeEach
                        }
                        else -> {}
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            registrationRoomController.tryDisconnect(session.sessionId)
        }
    }

    get("/signUp") {

        val restaurantId = call.parameters["restaurantId"]
        val email = call.parameters["email"]

        if (email == null || restaurantId == null || email.isEmpty() || email.isEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Data is blank")
            return@get
        }
        val existingUsers = userService.getUsersForRestaurant(restaurantId)
        if (existingUsers.any { it.email == email }) {
            call.respond(HttpStatusCode.Conflict, "This email is already used")
            return@get
        }

        call.respond(HttpStatusCode.OK)
        launch(Dispatchers.Default) {
            emailService.sendVerificationEmail(email, restaurantId)
        }
    }

    get("/verifyEmail") {

        val code = call.parameters["code"]
        val email = call.parameters["email"]

        if (email == null || code == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        if (emailService.verifyCode(email, code)) {

            call.respond(HttpStatusCode.OK)

        } else {
            call.respond(HttpStatusCode.Forbidden)
            return@get
        }
    }

    post("/createAccount") {

        val request = call.receiveNullable<RegistrationRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            email = request.email,
            password = saltedHash.hash,
            salt = saltedHash.salt,
            restaurantId = request.restaurantId,
            userInformation = UserInformation(
                phoneNumber = request.phoneNumber,
                name = request.name
            )
        )
        val wasAcknowledged = userService.insertUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        } else {
            call.respond(HttpStatusCode.OK)
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