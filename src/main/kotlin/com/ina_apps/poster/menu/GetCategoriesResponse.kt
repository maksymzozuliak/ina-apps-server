package com.ina_apps.poster.menu

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive


@Serializable
data class GetCategoriesResponse(
    val response: List<CategoryResponse>
)

@Serializable
data class CategoryResponse(
    @Serializable(with = CategoryIdSerializer::class)
    @SerialName("category_id")
    val categoryId: String,
    @SerialName("category_name")
    val categoryName: String,
    @SerialName("category_photo")
    val categoryPhoto: String? = null,
    @SerialName("category_photo_origin")
    val categoryPhotoOrigin: String? = null,
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
    val visible: List<Visible>? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = String::class)
object CategoryIdSerializer : KSerializer<String> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("category_id", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }

    override fun deserialize(decoder: Decoder): String {
        return when (val jsonToken = (decoder as? JsonDecoder)?.decodeJsonElement()) {
            is JsonPrimitive -> jsonToken.content
            else -> jsonToken.toString()
        }
    }
}

@Serializable
data class Visible(
    @SerialName("spot_id")
    val spotId: Int,
    val visible: Int
)
