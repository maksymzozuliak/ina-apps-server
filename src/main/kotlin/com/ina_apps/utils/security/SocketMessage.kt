package com.ina_apps.utils.security

import kotlinx.serialization.Serializable

@Serializable
data class SocketMessage(
    val type: String,
    val message: HashMap<String, String>?
)