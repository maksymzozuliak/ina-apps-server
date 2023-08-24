package com.ina_apps.data.services_implemintation

import com.ina_apps.model.classes.Dish
import com.ina_apps.model.classes.User
import com.ina_apps.model.services.DishesService
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class DishesServiceMongoDBImplementation(database: MongoDatabase): DishesService {

    private val dishesCollection = database.getCollection<Dish>("Dish")
    private val userCollection = database.getCollection<User>("User")

    override suspend fun insertDish(dish: Dish): Boolean {

        val result = dishesCollection.insertOne(dish)
        return result.wasAcknowledged()
    }

    override suspend fun getDishesForRestaurant(id: String): List<Dish> {

        val dishesForRestaurant = dishesCollection.find(Dish::restaurantId eq id)
        return dishesForRestaurant.toList()
    }

    override suspend fun getFavoriteForUser(id: String): List<Dish> {

        val user = userCollection.find(User::id eq id).firstOrNull()
        val favoriteList: List<String> = user?.favoriteDishesIdList ?: listOf<String>()
        return dishesCollection.find(Dish::id `in` favoriteList).toList()
    }

}