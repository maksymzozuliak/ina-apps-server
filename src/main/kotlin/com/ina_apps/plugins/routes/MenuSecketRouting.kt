package com.ina_apps.plugins.routes

import com.ina_apps.plugins.session.MenuSession
import com.ina_apps.room.RoomController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.litote.kmongo.json

fun Route.menuSocketRouting(roomController: RoomController) {

    webSocket("/menuSocket") {
        val session = call.sessions.get<MenuSession>()
        if(session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
            return@webSocket
        }
        try {
            roomController.onJoin(
                userId = session.userId,
                sessionId = session.sessionId,
                socket = this
            )
            incoming.consumeEach { frame ->
                if(frame is Frame.Text) {
                    roomController.sendUpdatedDishesList(frame.data.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            roomController.tryDisconnect(session.userId)
        }
    }
}
