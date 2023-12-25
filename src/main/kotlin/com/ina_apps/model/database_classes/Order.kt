package com.ina_apps.model.database_classes

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

@Serializable
data class Order(

    @BsonId
    val id: String? = ObjectId().toString(),
    val restaurantId: String,
    val phoneNumber: String? = null,
    val name: String? = null,
    val address : Address? = null,
    val dishesIdList: List<String>,
    val comment: String? = null,
    val paymentMethod: PaymentMethod,
    val change: String? = null,
    val countOfCutlery: Int,
    val courierId: String? = null,
    val date: String? = null,
    val isDelivery: Boolean,
    val forTime: String? = null,
    val forDate: String? = null,
    val status: OrderStatus? = null,
    val timeHistory: TimeHistory? = null,
    val userId: String? = null,
    val number: Int? = null

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


