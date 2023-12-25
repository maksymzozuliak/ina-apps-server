package com.ina_apps.model.services

import com.ina_apps.model.database_classes.Order
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface OrdersService {

    suspend fun insertOrder(order: Order) : Boolean

    suspend fun updateOrder(order: Order): Boolean

    suspend fun getOrderById(id: String): Order?

    suspend fun getOrdersForUser(userID: String): List<Order>

    suspend fun deleteOldOrders(months: Long)
}
