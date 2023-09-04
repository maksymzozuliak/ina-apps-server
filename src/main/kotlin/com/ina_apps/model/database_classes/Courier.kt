package com.ina_apps.model.database_classes

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Courier(

    @BsonId
    val id: String? = ObjectId().toString(),
    val restaurantId: String,
    val name: String,
    val number: String,
    val isBusy: Boolean
)
