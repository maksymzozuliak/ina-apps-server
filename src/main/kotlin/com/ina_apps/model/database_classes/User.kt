package com.ina_apps.model.database_classes

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(

    @BsonId
    val id: String? = ObjectId().toString(),
    val email: String,
    val password: String,
    val salt: String,
    val restaurantId: String,
    val userInformation: UserInformation? = null
)

@Serializable
data class UserInformation(

    val address: List<Address> = listOf(),
    val dateOfBirth: String? = null,
    val bonus: Int? = null,
    val dateOfRegistration: String? = null,
    val name: String?,
    val ordered: Int? = null,
    val favoriteDishesIdList: List<String> = listOf(),
    val paid: Float? = null,
    val phoneNumber: String?
)
