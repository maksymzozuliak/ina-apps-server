package com.ina_apps.model.database_classes

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class RestaurantInformation(

    @BsonId
    val id: String? = ObjectId().toString(),
    val name: String,
    val address: String,
    val facebookURL: String?,
    val instagramURL: String?,
    val siteURL: String?,
    val latitude: Float,
    val longitude: Float,
    val zoom: Float,
    val number: String,
    val schedule: String,
    val deliverySettings: DeliverySettings,
    val category: List<Category>?
)

@Serializable
data class Category(

    val id: Int,
    val index: Int?,
    val name: Map<String, String>,
    val imageName: String?,
    val color: String?
)

@Serializable
data class DeliverySettings(

    val startHour: Int,
    val lastHour: Int,
    val price: Float,
    val nearestTimeInMinutes: Int
)
