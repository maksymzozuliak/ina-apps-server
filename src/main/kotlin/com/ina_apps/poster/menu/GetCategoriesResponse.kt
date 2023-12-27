package com.ina_apps.poster.menu

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable
data class GetCategoriesResponse(
    val response: List<CategoryResponse>
)

@Serializable
data class CategoryResponse(
//    @Serializable(with = CategoryIdSerializer::class)
    @SerialName("category_id")
    val categoryId: String,
    @SerialName("category_name")
    val categoryName: String,
    @SerialName("category_photo")
    val categoryPhoto: String? = null,
    @SerialName("parent_category")
    val parentCategory: String? = null,
    @SerialName("category_color")
    val categoryColor: String? = null,
    @SerialName("category_hidden")
    val categoryHidden: String? = null,
    @SerialName("sort_order")
    val sortOrder: String? = null,
    val fiscal: String? = null,
    val nodiscount: String? = null,
    @SerialName("tax_id")
    val taxId: String? = null,
    val left: String? = null,
    val right: String? = null,
    val level: String? = null,
    @SerialName("category_tag")
    val categoryTag: String? = null,
    val visible: Visible? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = CategoryResponse::class)
object CategoryIdSerializer : KSerializer<Any> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("CategoryId", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any) {
        when (value) {
            is String -> encoder.encodeString(value)
            is Int -> encoder.encodeInt(value)
            else -> throw SerializationException("Unsupported type for MyData.myField: $value")
        }
    }

    override fun deserialize(decoder: Decoder): Any {
        val input = decoder.decodeString()
        return try {
            input.toInt()
        } catch (e: NumberFormatException) {
            input
        }
    }
}

@Serializable
data class Visible(
    @SerialName("spot_id")
    val spotId: Int,
    val visible: Int
)
