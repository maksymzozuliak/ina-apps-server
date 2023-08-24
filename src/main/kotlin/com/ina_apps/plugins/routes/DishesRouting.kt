package com.ina_apps.plugins.routes

import com.ina_apps.model.classes.Category
import com.ina_apps.model.classes.Dish
import com.ina_apps.model.services.OrdersService
import com.ina_apps.model.classes.Order
import com.ina_apps.model.services.DishesService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Route.dishesRouting(
    dishesService: DishesService
) {
    route("/dishes") {

        post("/insert") {

            val multipart = call.receiveMultipart()
            var dish: Dish? = null
            var byteArray: ByteArray? = null
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    byteArray = part.streamProvider().readBytes()
                } else if (part is PartData.FormItem) {
                    if (part.name == "dish") {
                        val dishJson = part.value
                        dish = Json.decodeFromString(dishJson)
                    }
                }
                part.dispose()
            }
            if (dish != null) {
                val result = dishesService.insertDish(
                    dish!!.copy(image = byteArray)
                )
                if (result) {
                    call.respond(HttpStatusCode.Created)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        get("/getForRestaurant/{restaurantId}") {

            val restaurantId = call.parameters["restaurantId"] ?: ""
            val dishesForRestaurant = dishesService.getDishesForRestaurant(id = restaurantId)
            call.respond(dishesForRestaurant)
        }

        get("/getFavoriteForUser/{userId}") {

            val userId = call.parameters["userId"] ?: ""
            val favoriteForUser = dishesService.getFavoriteForUser(id = userId)
            call.respond(favoriteForUser)
        }
    }
}