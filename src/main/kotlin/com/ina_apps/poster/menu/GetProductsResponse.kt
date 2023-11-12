package com.ina_apps.poster.menu

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
    @SerialName("modifications")
    val modifications: List<Modificator>? = null,
    @SerialName("group_modifications")
    val groupModifications: List<DishModificationGroup>? = null,
    @SerialName("tax_id")
    val taxId: Long,
    @SerialName("product_tax_id")
    val productTaxId: Int,
    val type: Int,
    @SerialName("weight_flag")
    val weightFlag: Int,
    val color: String,
    val spots: List<Spot>? = null,
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
    val price: Map<String, Long>? = null,
    val profit: Map<String, Long>? = null
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

@Serializable
data class Modificator(
    @SerialName("modificator_id")
    val modificatorId: String,
    @SerialName("modificator_name")
    val modificatorName: String,
    @SerialName("modificator_selfprice")
    val modificatorSelfprice: String,
    @SerialName("modificator_selfprice_netto")
    val modificatorSelfpriceNetto: String,
    @SerialName("order")
    val order: String,
    @SerialName("modificator_barcode")
    val modificatorBarcode: String,
    @SerialName("modificator_product_code")
    val modificatorProductCode: String,
    @SerialName("spots")
    val spots: List<ModificatorSpot>,
    @SerialName("ingredient_id")
    val ingredientId: String,
    @SerialName("fiscal_code")
    val fiscalCode: String,
    @SerialName("master_id")
    val masterId: String,
    val sources: List<Source>? = null
)

@Serializable
data class ModificatorSpot(
    @SerialName("spot_id")
    val spotId: String,
    @SerialName("price")
    val price: String,
    @SerialName("profit")
    val profit: String,
    @SerialName("profit_netto")
    val profitNetto: String,
    @SerialName("visible")
    val visible: String
)

@Serializable
data class DishModificationGroup(
    @SerialName("dish_modification_group_id")
    val dishModificationGroupId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("num_min")
    val numMin: Int,
    @SerialName("num_max")
    val numMax: Int,
    @SerialName("type")
    val type: Int,
    @SerialName("is_deleted")
    val isDeleted: Int,
    @SerialName("modifications")
    val modifications: List<DishModification>,
    val sources: List<Source>? = null
)

@Serializable
data class DishModification(
    @SerialName("dish_modification_id")
    val dishModificationId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("ingredient_id")
    val ingredientId: Int,
    @SerialName("type")
    val type: Int,
    @SerialName("brutto")
    val brutto: Int,
    @SerialName("price")
    val price: Int,
    @SerialName("photo_orig")
    val photoOrig: String,
    @SerialName("photo_large")
    val photoLarge: String,
    @SerialName("photo_small")
    val photoSmall: String,
    @SerialName("last_modified_time")
    val lastModifiedTime: String
)
