package com.ina_apps.plugins.routes

import com.ina_apps.model.classes.Order
import com.ina_apps.model.classes.RestaurantInformation
import com.ina_apps.model.classes.User
import com.ina_apps.model.services.OrdersService
import com.ina_apps.model.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersRouting(
    userService: UserService
) {
    route("/users") {

        post("/insert") {

            val user = call.receive<User>()
            val result = userService.insertUser(user)
            if (result) {
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        put("/update") {

            val user = call.receive<User>()
            val result = userService.updateUser(user)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/getById/{id}") {

            val id = call.parameters["id"]
            if (id != null) {
                val user = userService.getUserById(id)
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

    }
}