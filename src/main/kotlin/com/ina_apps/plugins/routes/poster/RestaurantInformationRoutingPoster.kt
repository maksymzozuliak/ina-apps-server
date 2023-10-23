package com.ina_apps.plugins.routes.poster

import com.ina_apps.model.services.RestaurantInformationService
import com.ina_apps.poster.account.PosterAccountService
import com.ina_apps.poster.orders.OrderRequest
import com.ina_apps.poster.orders.PosterOrderService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.restaurantInformationRoutingPoster(
    restaurantInformationService: RestaurantInformationService,
    posterAccountService: PosterAccountService
) {
    route("/restaurantInformation") {

        get("/get") {

            val code = call.parameters["code"]
            if (code == null) {
                call.respond(HttpStatusCode.Forbidden)
                return@get
            }
            val restaurantId = posterAccountService.getRestaurantIdFromCode(code)
            if (restaurantId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val restaurantInformation = restaurantInformationService.getRestaurantInformationById(restaurantId)
            if (restaurantInformation != null) {
                call.respond(HttpStatusCode.OK, restaurantInformation)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/update") {

            val code = call.parameters["code"]
            if (code == null) {
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }
            val restaurantId = posterAccountService.getRestaurantIdFromCode(code)
            if (restaurantId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val restaurantInformation = restaurantInformationService.getRestaurantInformationById(restaurantId)
            if (restaurantInformation != null) {
                call.respond(HttpStatusCode.OK, restaurantInformation)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
