package com.ina_apps.plugins.routes

import com.ina_apps.model.database_classes.Order
import com.ina_apps.model.rest_classes.Notification
import com.ina_apps.model.rest_classes.NotificationMessage
import com.ina_apps.model.services.OneSignalService
import com.ina_apps.room.RoomController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.oneSignalRouting(service: OneSignalService) {

    route("/notification") {

        post("/send") {

            val notification = call.receive<Notification>()

            val successful = service.sendNotification(notification.copy(appId = OneSignalService.ONESIGNAL_APP_ID))

            if(successful) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }

}