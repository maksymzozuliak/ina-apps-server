package com.ina_apps.plugins.routes

import com.google.api.client.util.DateTime
import com.ina_apps.model.services.OrdersService
import com.ina_apps.model.database_classes.Order
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

fun Route.ordersRouting(
    ordersService: OrdersService
) {
    route("/orders") {

        post("/insert") {

            val order = call.receive<Order>()
            val result = ordersService.insertOrder(order.copy(date = LocalDateTime.now().toString()))
            if (result) {
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        put("/update") {

            val order = call.receive<Order>()
            val result = ordersService.updateOrder(order)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/getById/{id}") {

            val id = call.parameters["id"]
            if (id != null) {
                val order = ordersService.getOrderById(id)
                if (order != null) {
                    call.respond(HttpStatusCode.OK, order)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/getForUser/{userId}") {

            val userId = call.parameters["userId"]
            if (userId != null) {
                val ordersForUser = ordersService.getOrdersForUser(userId)
                call.respond(HttpStatusCode.OK, ordersForUser)

            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}