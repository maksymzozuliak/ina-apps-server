package com.ina_apps.model.services

import com.ina_apps.model.database_classes.Category
import com.ina_apps.model.database_classes.Dish
import com.ina_apps.model.database_classes.PosterDishInformation
import com.ina_apps.poster.menu.Source
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.nin

interface DishesService {

    suspend fun insertDish(dish: Dish) : String?

    suspend fun getDishesForRestaurant(id: String) : List<Dish>

    suspend fun getFavoriteForUser(id: String) : List<Dish>

    suspend fun getOneById(id: String) : Dish?

    suspend fun updatePosterDishInformation(posterProductId: Long, posterDishInformation: PosterDishInformation) : Dish?

    suspend fun deleteRedundantData(restaurantId: String, idList: List<Long>)

    suspend fun getDishesCount() : Long

    suspend fun replaceAll(restaurantId: String, dishes: List<Dish>): Boolean

    suspend fun updateOrCreate(restaurantId: String, dish: Dish): String

    suspend fun updateOrCreateCategory(category: Category)

    suspend fun getCategoriesForRestaurant(restaurantId: String): List<Category>

    suspend fun deleteRedundantCategoryData(restaurantId: String, idList: List<String>)
}
