package com.ina_apps.poster.menu

import com.google.cloud.storage.Acl
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.ina_apps.data.getBucketOrCreate
import com.ina_apps.model.database_classes.Dish
import com.ina_apps.model.database_classes.PosterDishInformation
import com.ina_apps.model.services.DishesService
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class PosterMenuService(
    private val client: HttpClient,
    private val dishesService: DishesService,
    private val storage: Storage,
) {

    suspend fun insertMany(restaurantId: String, dishes: List<Dish>) : Boolean {

        return try {
            dishesService.deleteRedundantData(restaurantId, dishes.map { it.posterProductId })
            val bucket = getBucketOrCreate(restaurantId, storage)
            val fullImageList = storage.list(restaurantId).values.map { it.name }.toMutableSet()
            val idList = mutableListOf<String>()
            dishes.forEach { dish ->
                val id = dishesService.updateOrCreate(restaurantId, dish.copy(
                        image = null,
                        modificators = dish.modificators?.map { it.copy(image = null) },
                        groupModifications = dish.groupModifications?.map { group -> group.copy(modifications = group.modifications.map { it.copy(image = null) }) }
                    )
                )
                idList.add(id)
                if (dish.image != null) {
                    val folderBlobId = BlobId.of(bucket.name, "$id/")
                    val folderBlobInfo = BlobInfo.newBuilder(folderBlobId).build()
                    val blobId = BlobId.of(bucket.name, "$id/$id")
                    val blobInfo = BlobInfo.newBuilder(blobId)
                        .setContentType("image")
                        .setAcl(listOf(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))
                        .build()
                    storage.createFrom(blobInfo, dish.image.inputStream())
                    dish.modificators?.forEach { modificator ->
                        if (modificator.image != null) {
                            val modificatorBlobId = BlobId.of(bucket.name, "$id/modificator${modificator.modificatorId}")
                            val modificatorBlobInfo = BlobInfo.newBuilder(modificatorBlobId)
                                .setContentType("image")
                                .setAcl(listOf(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))
                                .build()
                            storage.createFrom(modificatorBlobInfo, modificator.image.inputStream())
                        }
                    }
                    dish.groupModifications?.forEachIndexed { index, groupModificator ->
                        groupModificator.modifications.forEach { modificator ->
                            if (modificator.image != null) {
                                val modificatorBlobId = BlobId.of(bucket.name, "$id/group$index/modificator${modificator.modificatorId}")
                                val modificatorBlobInfo = BlobInfo.newBuilder(modificatorBlobId)
                                    .setContentType("image")
                                    .setAcl(listOf(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))
                                    .build()
                                storage.createFrom(modificatorBlobInfo, modificator.image.inputStream())
                            }
                        }
                    }
                }
            }
            fullImageList.filter{ !idList.contains(it.substringBefore("/")) }
            fullImageList.forEach { id ->
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

    suspend fun getSources(token: String) : List<SourcesResponse> {

        return try {

            val response = client.get() {
                url("https://joinposter.com/api/settings.getOrderSources?token=$token")
                contentType(ContentType.Application.Json)
            }

            val sources = Json.decodeFromString<GetSourcesResponse>(response.bodyAsText())
            var hasOne0 = false
            val newList = mutableListOf<SourcesResponse>()
            sources.response.forEachIndexed() { index, source ->
                if (!hasOne0 && source.type == 0) {
                    hasOne0 = true
                    newList.add(source)
                } else if (source.type != 0) {
                    newList.add(source)
                }
            }
            return newList.toList()
        } catch (e: Exception) {
            e.printStackTrace()
            listOf<SourcesResponse>()
        }
    }

    suspend fun getCategories(token: String) : List<CategoryResponse> {

        return try {

            val response = client.get() {
                url("https://joinposter.com/api/menu.getCategories?token=$token")
                contentType(ContentType.Application.Json)
            }

            val sources = Json.decodeFromString<GetCategoriesResponse>(response.bodyAsText())
            return sources.response
        } catch (e: Exception) {
            e.printStackTrace()
            listOf<CategoryResponse>()
        }
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




