package com.ina_apps.poster.menu

import com.ina_apps.model.database_classes.Dish
import com.ina_apps.model.database_classes.PosterDishInformation
import com.ina_apps.model.services.DishesService
import com.ina_apps.model.services.RestaurantInformationService
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class PosterMenuService(
    private val client: HttpClient,
    private val dishesService: DishesService
) {

    suspend fun getProducts(restaurantId: String, token: String) : List<Dish> {

        return try {

            val response = client.get() {
                url("https://joinposter.com/api/menu.getProducts?token=$token")
                contentType(ContentType.Application.Json)
            }

            val menu = Json.decodeFromString<GetProductsResponse>(response.bodyAsText())
            val mapMenu = menu.response.associate { Pair(it.productId, it.toPosterDishInformation()) }
            val resultList: MutableList<Dish> = mutableListOf()
            dishesService.deleteRedundantData(menu.response.map { it.productId })
            mapMenu.forEach { (productId, dishInformation) ->
                var dish = dishesService.updatePosterDishInformation(productId, dishInformation)
                if (dish == null) {
                    val newDish = Dish(
                        restaurantId = restaurantId,
                        posterProductId = productId,
                        poster = dishInformation,
                        price = dishInformation.price["1"],
                        isActive = false,
                        position = dishesService.getDishesCount() + 1
                    )
                    val dishId = dishesService.insertDish(newDish)
                    dish = dishId?.let { dishesService.getOneById(it) }
                }
                if (dish != null) {
                    resultList.add(dish)
                }
            }
            resultList
        } catch (e: Exception) {
            e.printStackTrace()
            listOf<Dish>()
        }
    }

    private fun ProductResponse.toPosterDishInformation() : PosterDishInformation {

        var ingredients: String? = null
        if (this.ingredients != null) {
            ingredients = ""
            this.ingredients.forEach { ingredient ->
                ingredients += ingredient.ingredientName + " "
            }
        }

        return PosterDishInformation(
            categoryName = this.categoryName,
            price = this.price,
            menuCategoryId = this.menuCategoryId,
            photo = this.photoOrigin,
            productName = this.productName,
            type = this.type,
            sources = this.sources,
            productIngredients = ingredients
        )
    }
}


