package com.ina_apps.room

import io.ktor.server.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

data class RegistrationMember(
    val sessionId: String,
    val socket: WebSocketSession
)

class RegistrationRoomController {
    private val members = ConcurrentHashMap<String, RegistrationMember>()

    fun onJoin(
        sessionId: String,
        socket: WebSocketSession
    ) {
        if(members.containsKey(sessionId)) {
            throw Exception()
        }
        members[sessionId] = RegistrationMember(
            sessionId = sessionId,
            socket = socket
        )
    }

    suspend fun sendBoolean(sessionId: String, success: Boolean) {

        val successJson = Json.encodeToString(value = success)
        members[sessionId]?.socket?.send(Frame.Text(successJson))
    }

    suspend fun tryDisconnect(sessionId: String) {
        members[sessionId]?.socket?.close()
        if(members.containsKey(sessionId)) {
            members.remove(sessionId)
        }
    }
}