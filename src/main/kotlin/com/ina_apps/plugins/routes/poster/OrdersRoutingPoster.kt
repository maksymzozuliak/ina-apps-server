package com.ina_apps.plugins.routes.poster

import com.ina_apps.poster.orders.OrderRequest
import com.ina_apps.poster.orders.PosterOrderService
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

fun Route.ordersRoutingPoster(
    posterOrderService: PosterOrderService
) {
    route("/orders") {

        post("/create/{token}") {

            val token = call.parameters["token"]
            if (token == null) {
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }
            val orderRequest = call.receive<OrderRequest>()
            val result = posterOrderService.createOrder(
                token = token,
                orderRequest = orderRequest
            )
            if (result) {
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
