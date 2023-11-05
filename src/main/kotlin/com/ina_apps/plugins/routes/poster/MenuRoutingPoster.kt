package com.ina_apps.plugins.routes.poster

import com.ina_apps.model.database_classes.RestaurantInformation
import com.ina_apps.model.services.RestaurantInformationService
import com.ina_apps.poster.menu.PosterMenuService
import com.ina_apps.poster.orders.OrderRequest
import com.ina_apps.poster.orders.PosterOrderService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.menuRoutingPoster(
    menuService: PosterMenuService,
    restaurantInformationService: RestaurantInformationService
) {
    route("/menu") {

        get("/getMenu") {

            val restaurantId = call.parameters["restaurantId"]
            if (restaurantId == null) {
                call.respond(HttpStatusCode.Forbidden)
                return@get
            }
            val restaurantInformation = restaurantInformationService.getRestaurantInformationById(restaurantId)
            if (restaurantInformation == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            val result = menuService.getProducts(restaurantId, restaurantInformation.posterInformation!!.accessToken)
            call.respond(HttpStatusCode.OK, result.toString())
        }
    }
}
