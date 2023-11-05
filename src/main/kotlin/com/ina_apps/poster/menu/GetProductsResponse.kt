package com.ina_apps.poster.menu

import com.ina_apps.model.database_classes.PosterDishInformation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetProductsResponse(
    val response: List<ProductResponse>
)
@Serializable
data class ProductResponse(
    val barcode: String,
    @SerialName("category_name")
    val categoryName: String,
    val hidden: Int,
    val unit: String,
    val cost: Long,
    @SerialName("cost_netto")
    val costNetto: Long? = null,
    val fiscal: Int,
    @SerialName("menu_category_id")
    val menuCategoryId: Long,
    val workshop: Long,
    @SerialName("nodiscount")
    val noDiscount: Int,
    val photo: String? = null,
    @SerialName("photo_origin")
    val photoOrigin: String? = null,
    @SerialName("product_code")
    val productCode: String,
    @SerialName("product_id")
    val productId: Long,
    @SerialName("product_name")
    val productName: String,
    @SerialName("sort_order")
    val sortOrder: Int,
    @SerialName("tax_id")
    val taxId: Long,
    @SerialName("product_tax_id")
    val productTaxId: Int,
    val type: Int,
    @SerialName("weight_flag")
    val weightFlag: Int,
    val color: String,
    val spots: List<Spot>,
    @SerialName("ingredient_id")
    val ingredientId: Long? = null,
    @SerialName("cooking_time")
    val cookingTime: Long? = null,
    @SerialName("different_spots_prices")
    val differentSpotsPrices: Int,
    val sources: List<Source>? = null,
    @SerialName("master_id")
    val masterId: Long,
    val out: Long,
    @SerialName("product_production_description")
    val productProductionDescription: String? = null,
    val ingredients: List<Ingredient>? = null,
    val price: Map<String, Long>,
    val profit: Map<String, Long>
)

@Serializable
data class Spot(
    @SerialName("spot_id")
    val spotId: Long,
    val price: Long,
    val profit: Long,
    @SerialName("profit_netto")
    val profitNetto: Long? = null,
    val visible: Int
)

@Serializable
data class Source(
    val id: Int,
    val name: String,
    val price: Double,
    val visible: Int
)

@Serializable
data class Ingredient(
    @SerialName("structure_id")
    val structureId: Long,
    @SerialName("ingredient_id")
    val ingredientId: Long,
    @SerialName("pr_in_clear")
    val prInClear: Int,
    @SerialName("pr_in_cook")
    val prInCook: Int,
    @SerialName("pr_in_fry")
    val prInFry: Int,
    @SerialName("pr_in_stew")
    val prInStew: Int,
    @SerialName("pr_in_bake")
    val prInBake: Int,
    @SerialName("structure_unit")
    val structureUnit: String,
    @SerialName("structure_type")
    val structureType: Int,
    @SerialName("structure_brutto")
    val structureBrutto: Double,
    @SerialName("structure_netto")
    val structureNetto: Double,
    @SerialName("structure_lock")
    val structureLock: Int,
    @SerialName("structure_selfprice")
    val structureSelfprice: Long,
    @SerialName("structure_selfprice_netto")
    val structureSelfpriceNetto: Long? = null,
    @SerialName("ingredient_name")
    val ingredientName: String,
    @SerialName("ingredient_unit")
    val ingredientUnit: String,
    @SerialName("ingredient_weight")
    val ingredientWeight: Long? = null,
    @SerialName("ingredients_losses_clear")
    val ingredientsLossesClear: Double,
    @SerialName("ingredients_losses_cook")
    val ingredientsLossesCook: Double,
    @SerialName("ingredients_losses_fry")
    val ingredientsLossesFry: Double,
    @SerialName("ingredients_losses_stew")
    val ingredientsLossesStew: Double,
    @SerialName("ingredients_losses_bake")
    val ingredientsLossesBake: Double
)
