package com.ina_apps.model.database_classes

import com.ina_apps.poster.menu.DishModificationGroup
import com.ina_apps.poster.menu.Modificator
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
    val description: Map<String, String?>?= null,
    val additionalDishesId: List<String>? = null,
    val modificators: List<ReducedModificator>? = null,
    val groupModifications: List<ReducedGroupModifications>? = null,
    val image: ByteArray? = null
)

@Serializable
enum class Special{
    NEW, SPICY, POPULAR, VEGAN, VEGETARIAN, TOP
}

@Serializable
data class ReducedModificator(
    val modificatorId: String,
    val modificatorName: Map<String, String>?,
    val price: Float,
    val isActive: Boolean?,
    val image: ByteArray? = null
)

@Serializable
data class ReducedGroupModifications(
    val groupId: Int,
    val name: Map<String, String>?,
    val type: Int,
    val numMin: Int? = null,
    val numMax: Int? = null,
    val modifications: List<ReducedModificator>,
)

@Serializable
data class PosterDishInformation(
    val posterId: Long?,
    val categoryName: String,
    val price: Map<String, Long>?,
    val menuCategoryId: Long,
    val photo: String?,
    val productName: String,
    val type: Int,
    val sources: List<Source>?,
    val productIngredients: String?,
    val modifications: List<Modificator>? = null,
    val groupModifications: List<DishModificationGroup>? = null
)

