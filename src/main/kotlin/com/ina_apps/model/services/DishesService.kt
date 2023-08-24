package com.ina_apps.model.services

import com.ina_apps.model.classes.Dish

interface DishesService {

    suspend fun insertDish(dish: Dish) : Boolean

    suspend fun getDishesForRestaurant(id: String) : List<Dish>

    suspend fun getFavoriteForUser(id: String) : List<Dish>
}