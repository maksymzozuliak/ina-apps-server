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
import org.litote.kmongo.json

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

        post("/sendText") {

            val headings = call.parameters["headings"]
            val contents = call.parameters["contents"]

            if (headings == null || contents == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val notification = Notification(
                includedSegments = listOf("All"),
                contents = NotificationMessage(contents),
                headings = NotificationMessage(headings),
                appId = OneSignalService.ONESIGNAL_APP_ID
            )

            val successful = service.sendNotification(notification)

            if(successful) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }

}
