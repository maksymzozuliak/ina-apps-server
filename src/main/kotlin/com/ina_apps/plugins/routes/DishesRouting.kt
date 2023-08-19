package com.ina_apps.plugins.routes

import com.ina_apps.model.classes.Dish
import com.ina_apps.model.services.OrdersService
import com.ina_apps.model.classes.Order
import com.ina_apps.model.services.DishesService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.dishesRouting(
    dishesService: DishesService
) {
    route("/dishes") {

        post("/insert") {

            val dish = call.receive<Dish>()
            val result = dishesService.insertDish(dish)
            if (result) {
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/getForRestaurant/{restaurantId}") {
            val restaurantId = call.parameters["restaurantId"] ?: ""
            val dishesForRestaurant = dishesService.getDishesForRestaurant(id = restaurantId)
            call.respond(dishesForRestaurant)
        }
    }
}