package com.ina_apps.model.services

import com.ina_apps.model.database_classes.Order

interface OrdersService {

    suspend fun insertOrder(order: Order) : Boolean

    suspend fun updateOrder(order: Order): Boolean

    suspend fun getOrderById(id: String): Order?

}