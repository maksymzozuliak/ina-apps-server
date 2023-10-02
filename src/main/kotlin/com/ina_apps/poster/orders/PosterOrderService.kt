package com.ina_apps.poster.orders

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class PosterOrderService(
    private val client: HttpClient
) {

    suspend fun createOrder(token: String, orderRequest: OrderRequest): Boolean {

        return try {
            val response = client.post() {
                url("https://joinposter.com/api/incomingOrders.createIncomingOrder?token=$token")
                contentType(ContentType.Application.Json)
                setBody(orderRequest)
            }
            !response.bodyAsText().contains("error")
        } catch(e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
