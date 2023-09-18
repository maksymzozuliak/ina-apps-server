package com.ina_apps.data.services_implemintation

import com.ina_apps.model.rest_classes.Notification
import com.ina_apps.model.services.OneSignalService
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*

class OneSignalServiceImpl(
    private val client: HttpClient,
    private val apiKey: String
): OneSignalService {

    override suspend fun sendNotification(notification: Notification): Boolean {
        return try {
            val response = client.post() {
                url(OneSignalService.NOTIFICATIONS)
                contentType(ContentType.Application.Json)
                header("Authorization", "Basic $apiKey")
                setBody(notification)
            }
            println("Tagggg $response")
            true
        } catch(e: Exception) {
            e.printStackTrace()
            false
        }
    }
}