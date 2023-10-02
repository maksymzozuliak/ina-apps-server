package com.ina_apps.poster.oauth2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("account_number")
    val accountNumber: String,
    val ownerInfo: OwnerInfo,
    val tariff: List<Tariff>,
    val user: User
)

@Serializable
data class User(
    val email: String,
    val id: Int,
    val name: String,
    @SerialName("role_id")
    val roleId: Int
)

@Serializable
data class OwnerInfo(
    val city: String,
    @SerialName("company_name")
    val companyName: String,
    val country: String,
    val email: String,
    val name: String,
    val phone: String
)

@Serializable
data class Tariff(
    val key: String,
    @SerialName("next_pay_date")
    val nextPayDate: String,
    val price: Int
)

fun OwnerInfo.toRestaurantInformationOwnerInfo(): com.ina_apps.model.database_classes.OwnerInfo {
    return com.ina_apps.model.database_classes.OwnerInfo(
        city = this.city,
        companyName = this.companyName,
        country = this.country,
        email = this.email,
        name = this.name,
        phone = this.phone
    )
}
