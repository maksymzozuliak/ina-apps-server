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
    val deliverySettings: DeliverySettings,
    val category: List<Category>?
)

@Serializable
data class Category(

    val id: Int,
    val name: Map<String, String>,
    val image: ByteArray?,
    val color: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        if (name != other.name) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}

@Serializable
data class DeliverySettings(

    val restaurantId: String,
    val startHour: String,
    val lastHour: String,
    val price: Float,
    val nearestTimeInMinutes: Int
)
