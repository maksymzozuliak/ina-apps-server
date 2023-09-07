package com.ina_apps.utils

import org.apache.commons.mail.Email

data class RegistrationSession(
    val sessionId: String,
    val restaurantId: String
)
