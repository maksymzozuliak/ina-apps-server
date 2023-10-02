package com.ina_apps.poster.orders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientAddress(
    val address1: String,
    val address2: String,
    val comment: String? = null,
    val lat: Double? = null,
    val lng: Double? = null
)

@Serializable
data class Payment(
    val type: Int = 0,
    val sum: Int,
    val currency: String
)

@Serializable
data class Modification(
    val m: String,
    val a: Int
)

@Serializable
data class Product(
    @SerialName("product_id")
    val productId: Int,
    @SerialName("modificator_id")
    val modificatorId: String? = null,
    val modification: List<Modification>? = null,
    val count: Int,
    val price: Int? = null
)

@Serializable
data class Promotion(
    val id: String,
    @SerialName("involved_products")
    val involvedProducts: List<InvolvedProduct>,
    @SerialName("result_products")
    val resultProducts: List<ResultProduct>? = null
)

@Serializable
data class InvolvedProduct(
    val id: String,
    val count: Int,
    val modification: List<Modification>? = null
)

@Serializable
data class ResultProduct(
    @SerialName("product_id")
    val productId: String,
    val count: Int,
    val modification: List<Modification>? = null
)

@Serializable
data class OrderRequest(
    @SerialName("spot_id")
    val spotId: String,
    @SerialName("client_id")
    val clientId: String? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val sex: Int? = null,
    val birthday: String? = null,
    @SerialName("client_address_id")
    val clientAddressId: String? = null,
    @SerialName("client_address")
    val clientAddress: ClientAddress? = null,
    @SerialName("service_mode")
    val serviceMode: Int,
    @SerialName("delivery_price")
    val deliveryPrice: Int? = null,
    val comment: String? = null,
    val products: List<Product>,
    val payment: Payment? = null,
    val promotion: List<Promotion>? = null,
    @SerialName("delivery_time")
    val deliveryTime: String? = null
)
