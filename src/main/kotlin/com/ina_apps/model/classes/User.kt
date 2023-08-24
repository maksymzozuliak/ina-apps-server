package com.ina_apps.model.classes

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(

    @BsonId
    val id: String? = ObjectId().toString(),
    val restaurantId: String,
    val address: List<Address>,
    val dateOfBirth: String,
    val bonus: Int,
    val dateOfRegistration: String,
    val name: String,
    val ordered: Int,
    val favoriteDishesIdList: List<String>?,
    val paid: Float,
    val phoneNumber: String
)
