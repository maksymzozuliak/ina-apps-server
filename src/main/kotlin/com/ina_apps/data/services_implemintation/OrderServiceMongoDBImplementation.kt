package com.ina_apps.data.services_implemintation

import com.ina_apps.model.database_classes.Order
import com.ina_apps.model.services.OrdersService
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.litote.kmongo.eq
import org.litote.kmongo.gte
import org.litote.kmongo.lt
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

    override suspend fun getOrdersForUser(userID: String): List<Order> {

        return ordersCollection.find(Order::userId eq userID).toList()
    }

    override suspend fun deleteOldOrders(months: Long) {

        val currentDate = LocalDate.now()
        val threeMonthsAgo = currentDate.minusMonths(months)
        val formatter = DateTimeFormatter.ISO_INSTANT
        val startDate = threeMonthsAgo.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
        ordersCollection.deleteMany(
            Order::date lt formatter.format(startDate)
        )
    }

}
