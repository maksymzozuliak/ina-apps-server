package com.ina_apps.model.database_classes

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Order(

    @BsonId
    val id: String? = ObjectId().toString(),
    val restaurantId: String,
    val address : Address,
    val change: Float?,
    val comment: String,
    val countOfCutlery: Int,
    val courierId: String,
    val date: String,
    val isDelivery: Boolean,
    val dishesIdList: List<String>,
    val forTime: String,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val timeHistory: TimeHistory,
    val userId: String?,
    val guestUser: GuestUser?

)

@Serializable
data class Address(

    val street: String,
    val building: String,
    val entrance: String,
    val apartment: String
)

@Serializable
data class TimeHistory(

    val timeOfOrdering: String,
    val timeOfProcessing: String,
    val timeOfCooking: String,
    val timeOfDelivering: String,
    val timeOfFinishing: String

)

@Serializable
data class GuestUser(

    val number: String,
    val name: String
)

@Serializable
enum class OrderStatus {
    REJECTED,
    PROCESSING,
    COOKING,
    DELIVERING,
    DONE;
}

@Serializable
enum class PaymentMethod {
    CARD,
    ONLINE,
    CASH;
}


