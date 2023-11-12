package com.ina_apps.poster.menu

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.ina_apps.data.getBucketOrCreate
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
    private val dishesService: DishesService,
    private val storage: Storage,
) {

    suspend fun insertMany(restaurantId: String, dishes: List<Dish>) : Boolean {

        return try {
            dishesService.deleteRedundantData(restaurantId, dishes.map { it.posterProductId })
            val imageList = storage.list(restaurantId).values.map { it.name }.toMutableList()
            val idList = mutableListOf<String>()
            val bucket = getBucketOrCreate(restaurantId, storage)
            dishes.forEach { dish ->
                val temp = dish.image
                val id = dishesService.updateOrCreate(restaurantId, dish.copy(image = null))
                idList.add(id)
                if (temp != null) {
                    val blobId = BlobId.of(bucket.name, id)
                    val blobInfo = BlobInfo.newBuilder(blobId)
                        .setContentType("image")
                        .build()
                    storage.createFrom(blobInfo, dish.image.inputStream())
                }
            }
            imageList.removeAll(idList)
            imageList.forEach { id ->
                val blobId = BlobId.of(bucket.name, id)
                storage.delete(blobId)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getProducts(restaurantId: String, token: String) : List<Dish> {

        return try {

            val response = client.get() {
                url("https://joinposter.com/api/menu.getProducts?token=$token")
                contentType(ContentType.Application.Json)
            }

            val menu = Json.decodeFromString<GetProductsResponse>(response.bodyAsText())
            val mapMenu = menu.response.associate { Pair(it.productId, it.toPosterDishInformation()) }
            val resultList: MutableList<Dish> = mutableListOf()
            mapMenu.forEach { (productId, dishInformation) ->
                val newDish = Dish(
                    restaurantId = restaurantId,
                    posterProductId = productId,
                    poster = dishInformation,
                    price = dishInformation.price?.get("1"),
                    isActive = false,
                    position = dishesService.getDishesCount() + 1,
                    weight = if(dishInformation.type == 3) 1 else menu.response.find{ it.productId == productId }?.out,
                    unit = if(dishInformation.type == 3) "шт" else null,
                )
                resultList.add(newDish)
            }
            val dbDishes = dishesService.getDishesForRestaurant(restaurantId)
            resultList.forEachIndexed() { index, dish ->
                val temp = dbDishes.find { it.poster?.posterId == dish.posterProductId }
                if (temp != null) {
                    resultList[index] = temp.copy(poster = dish.poster)
                }
            }
            resultList
        } catch (e: Exception) {
            e.printStackTrace()
            listOf<Dish>()
        }
    }

    suspend fun getSourcesList(restaurantId: String) : List<Source> {

        return dishesService.getSourcesList(restaurantId)
    }

    private fun ProductResponse.toPosterDishInformation() : PosterDishInformation {

        var ingredients: String? = null
        if (this.ingredients != null) {
            ingredients = ""
            this.ingredients.forEachIndexed { index, ingredient ->
                ingredients += if (index+1 != this.ingredients.size) {
                    ingredient.ingredientName + ", "
                } else {
                    ingredient.ingredientName
                }
            }
        }

        return PosterDishInformation(
            posterId = this.productId,
            categoryName = this.categoryName,
            price = this.price,
            menuCategoryId = this.menuCategoryId,
            photo = this.photoOrigin,
            productName = this.productName,
            type = this.type,
            sources = this.sources,
            productIngredients = ingredients,
            modifications = this.modifications,
            groupModifications = this.groupModifications
        )
    }
}


