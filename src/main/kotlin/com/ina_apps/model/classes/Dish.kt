package com.ina_apps.model.classes

import kotlinx.serialization.Serializable
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
    val image: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dish

        if (id != other.id) return false
        if (restaurantId != other.restaurantId) return false
        if (name != other.name) return false
        if (categoryId != other.categoryId) return false
        if (special != other.special) return false
        if (ingredients != other.ingredients) return false
        if (position != other.position) return false
        if (price != other.price) return false
        if (oldPrice != other.oldPrice) return false
        if (weight != other.weight) return false
        if (isActive != other.isActive) return false
        return image.contentEquals(other.image)
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + restaurantId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + categoryId
        result = 31 * result + (special?.hashCode() ?: 0)
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + position
        result = 31 * result + price.hashCode()
        result = 31 * result + oldPrice.hashCode()
        result = 31 * result + weight.hashCode()
        result = 31 * result + isActive.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }
}

@Serializable
enum class Special{
    NEW, HOT, TOP
}
