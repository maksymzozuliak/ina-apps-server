package com.ina_apps.plugins.routes

import com.ina_apps.model.database_classes.User
import com.ina_apps.model.database_classes.UserInformation
import com.ina_apps.model.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.usersRouting(
    userService: UserService
) {
    route("/users") {

//        post("/insert") {
//
//            val user = call.receive<User>()
//            val result = userService.insertUser(user)
//            if (result) {
//                call.respond(HttpStatusCode.Created)
//            } else {
//                call.respond(HttpStatusCode.BadRequest)
//            }
//        }

        authenticate {

            put("/updateInformation") {

                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class)
                if (userId != null) {

                    val userInformation = call.receive<UserInformation>()
                    val result = userService.updateUserInformation(userId, userInformation)
                    if (result) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get("/get") {

                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class)
                if (userId != null) {
                    val userInformation = userService.getUserInformationById(userId)
                    if (userInformation != null) {
                        call.respond(HttpStatusCode.OK, userInformation)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
