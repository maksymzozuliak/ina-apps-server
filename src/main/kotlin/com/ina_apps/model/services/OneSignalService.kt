package com.ina_apps.model.services

import com.ina_apps.model.rest_classes.Notification

interface OneSignalService {

    suspend fun sendNotification(notification: Notification): Boolean

    companion object {
        const val ONESIGNAL_APP_ID = "d3b8be9c-85cf-4bdf-bd79-90dfb0c7ab0f"

        const val NOTIFICATIONS = "https://onesignal.com/api/v1/notifications"
    }
}