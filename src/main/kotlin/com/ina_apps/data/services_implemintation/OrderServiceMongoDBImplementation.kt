package com.ina_apps.data.services_implemintation

import com.ina_apps.model.database_classes.Order
import com.ina_apps.model.services.OrdersService
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.litote.kmongo.eq

class OrderServiceMongoDBImplementation(database: MongoDatabase): OrdersService {

    private val ordersCollection = database.getCollection<Order>("Order")

    override suspend fun insertOrder(order: Order): Boolean {

        val result = ordersCollection.insertOne(order)
        return result.wasAcknowledged()
    }

    override suspend fun updateOrder(order: Order): Boolean {

        return if( order.id != null) {
            val updateResult = ordersCollection.replaceOne(Order::id eq order.id, order)
            updateResult.wasAcknowledged() && updateResult.modifiedCount > 0
        } else false
    }

    override suspend fun getOrderById(id: String): Order? {

        return ordersCollection.find(Order::id eq id).firstOrNull()
    }

}