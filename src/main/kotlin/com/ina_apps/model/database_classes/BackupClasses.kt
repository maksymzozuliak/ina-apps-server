package com.ina_apps.model.database_classes

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class HourlyBackup(
    @BsonId
    val id: String? = ObjectId().toString(),
    val timestamp: String,
    val orders: List<Order>
)

@Serializable
data class DailyBackup(
    @BsonId
    val id: String? = ObjectId().toString(),
    val timestamp: String,
    val orders: List<Order>,
    val restaurantInformation: List<RestaurantInformation>,
    val dishes: List<Dish>,
    val users: List<User>
)
