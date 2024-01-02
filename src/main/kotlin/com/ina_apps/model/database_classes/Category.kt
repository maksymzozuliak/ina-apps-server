package com.ina_apps.model.database_classes

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Category(

    @BsonId
    val id: String? = ObjectId().toString(),
    val restaurantId: String? = null,
    val categoryId: String,
    val categoryName: String,
    val color: String? = null,
    val image: ByteArray? = null
)
