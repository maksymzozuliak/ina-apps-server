package com.ina_apps.model.database_classes

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
@Serializable
data class RestaurantInformation(

    @BsonId
    val id: String? = ObjectId().toString(),
    val posterInformation: PosterInformation? = null,
    val name: String? = null,
    val address: String?= null,
    val facebookURL: String?= null,
    val instagramURL: String?= null,
    val siteURL: String?= null,
    val latitude: Float?= null,
    val longitude: Float?= null,
    val zoom: Float?= null,
    val phoneNumber: String?= null,
    val deliverySettings: DeliverySettings?= null,
    val category: List<Category>?= null
)

@Serializable
data class PosterInformation(

    val account: String,
    val accessToken: String,
    val accountNumber: String,
    val ownerInfo: OwnerInfo,
    val paymentMethodId: Int
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

    val startHour: String,
    val lastHour: String,
    val price: Float,
    val nearestTimeInMinutes: Int? = null,
    val minPriceForFree: Float,
)

@Serializable
data class OwnerInfo(
    val city: String,
    val companyName: String,
    val country: String,
    val email: String,
    val name: String,
    val phone: String
)

