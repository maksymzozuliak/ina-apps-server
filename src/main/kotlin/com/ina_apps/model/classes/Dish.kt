package com.ina_apps.model.classes

import kotlinx.serialization.*
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Dish(

    @BsonId
    val id: String? = ObjectId().toString(),
    val restaurantId: String,
    val name: Map<String, String>,
    val categoryId: Int,
    val special: List<Special>?,
    val ingredients: Map<String,List<String>>,
    val position: Int,
    val price: Float,
    val oldPrice: Float,
    val weight: Float,
    val isActive: Boolean,
    val imageName: String? = null
)

@Serializable
enum class Special{
    NEW, HOT, TOP
}


