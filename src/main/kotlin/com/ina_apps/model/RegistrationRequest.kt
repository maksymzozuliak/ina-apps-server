package com.ina_apps.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val email: String,
    val password: String,
    val restaurantId: String,
    val phoneNumber: String,
    val name: String
)
