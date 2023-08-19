package com.ina_apps.model.classes

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class RestaurantInformation(

    @BsonId
    val id: String? = ObjectId().toString(),
    val address: String,
    val facebookURL: String?,
    val instagramURL: String?,
    val siteURL: String?,
    val latitude: Float,
    val longitude: Float,
    val zoom: Float,
    val number: String,
    val schedule: String,
    val deliverySettings: DeliverySettings
)

@Serializable
data class DeliverySettings(

    val restaurantId: String,
    val startHour: String,
    val lastHour: String,
    val price: Float,
    val nearestTimeInMinutes: Int
)
