package com.ina_apps.data.services_implemintation

import com.ina_apps.model.classes.Dish
import com.ina_apps.model.classes.Order
import com.ina_apps.model.services.DishesService
import com.ina_apps.model.services.OrdersService
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.toList
import org.litote.kmongo.eq

class DishesServiceMongoDBImplementation(database: MongoDatabase): DishesService {

    private val dishesCollection = database.getCollection<Dish>("Dish")

    override suspend fun insertDish(dish: Dish): Boolean {

        val result = dishesCollection.insertOne(dish)
        return result.wasAcknowledged()
    }

    override suspend fun getDishesForRestaurant(id: String): List<Dish> {

        val dishesForRestaurant = dishesCollection.find(Dish::restaurantId eq id)
        return dishesForRestaurant.toList()
    }

}