package com.ina_apps.plugins.routes

import com.ina_apps.model.classes.Order
import com.ina_apps.model.classes.RestaurantInformation
import com.ina_apps.model.services.OrdersService
import com.ina_apps.model.services.RestaurantInformationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.restaurantInformationRouting(
    restaurantInformationService: RestaurantInformationService
) {
    route("/restaurantInformation") {

        post("/insert") {

            val restaurantInformation = call.receive<RestaurantInformation>()
            val result = restaurantInformationService.insertRestaurantInformation(restaurantInformation)
            if (result) {
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        put("/update") {

            val restaurantInformation = call.receive<RestaurantInformation>()
            val result = restaurantInformationService.updateRestaurantInformation(restaurantInformation)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/getById/{id}") {

            val id = call.parameters["id"]
            if (id != null) {
                val restaurantInformation = restaurantInformationService.getRestaurantInformationById(id)
                if (restaurantInformation != null) {
                    call.respond(HttpStatusCode.OK, restaurantInformation)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}