package com.ina_apps.room

import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class RoomController() {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(
        userId: String,
        sessionId: String,
        socket: WebSocketSession
    ) {
        if(members.containsKey(userId)) {
            throw Exception()
        }
        members[userId] = Member(
            userId = userId,
            sessionId = sessionId,
            socket = socket
        )
    }

    suspend fun sendUpdatedDishesList(list: String) {
        members.values.forEach { member ->

            member.socket.send(Frame.Text(list))
        }
    }

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if(members.containsKey(username)) {
            members.remove(username)
        }
    }
}