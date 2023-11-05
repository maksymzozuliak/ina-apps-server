package com.ina_apps.model.database_classes

import com.ina_apps.poster.menu.Source
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Dish(

    @BsonId
    val id: String? = ObjectId().toString(),
    val restaurantId: String,
    val posterProductId: Long,
    val poster: PosterDishInformation?,
    val name: Map<String, String>? = null,
//    val categoryId: Int,
    val special: List<Special>? = null,
    val position: Long? = null,
    val price: Long?,
//    val oldPrice: Float,
    val weight: Long? = null,
    val unit: String? = null,
    val isActive: Boolean? = null,
    val imageName: String? = null,
    val description: Map<String, String>?= null,
)

@Serializable
enum class Special{
    NEW, SPICY, POPULAR, HIT, VEGAN, VEGETARIAN, TOP, MEDIUM_SPICY
}

@Serializable
data class PosterDishInformation(
    val categoryName: String,
    val price: Map<String, Long>,
    val menuCategoryId: Long,
    val photo: String?,
    val productName: String,
    val type: Int,
    val sources: List<Source>?,
    val productIngredients: String?
)

