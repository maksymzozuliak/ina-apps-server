package com.ina_apps.plugins.routes.poster

import com.ina_apps.model.database_classes.DeliverySettings
import com.ina_apps.model.database_classes.RestaurantInformation
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
    restaurantInformationService: RestaurantInformationService
) {
    route("/restaurantInformation") {

        get("/get") {

            val restaurantId = call.parameters["restaurantId"]
            if (restaurantId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val restaurantInformation = restaurantInformationService.getRestaurantInformationById(restaurantId)
            if (restaurantInformation != null) {
                call.respond(HttpStatusCode.OK, restaurantInformation.copy(posterInformation = null))
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        put("/update") {

            val restaurantId = call.parameters["restaurantId"]
            if (restaurantId == null) {
                call.respond(HttpStatusCode.Forbidden)
                return@put
            }
            val restaurantInformation = call.receive<RestaurantInformation>()
            val fullRestaurantInformation = restaurantInformationService.getRestaurantInformationById(restaurantId)
            if (fullRestaurantInformation == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }
            val result = restaurantInformationService.updateRestaurantInformation(
                fullRestaurantInformation.copy(
                    name = restaurantInformation.name,
                    phoneNumber = restaurantInformation.phoneNumber,
                    address = restaurantInformation.address,
                    facebookURL = restaurantInformation.facebookURL,
                    instagramURL = restaurantInformation.instagramURL,
                    siteURL = restaurantInformation.siteURL,
                    longitude = restaurantInformation.longitude,
                    latitude = restaurantInformation.latitude,
                    zoom = restaurantInformation.zoom
                )
            )
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        put("/updateDeliverySettings") {

            val restaurantId = call.parameters["restaurantId"]
            if (restaurantId == null) {
                call.respond(HttpStatusCode.Forbidden)
                return@put
            }
            val deliverySettings = call.receive<DeliverySettings>()
            val result = restaurantInformationService.updateDeliverySettings(restaurantId, deliverySettings)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
