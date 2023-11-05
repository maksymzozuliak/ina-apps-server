package com.ina_apps.data.services_implemintation

import com.ina_apps.model.database_classes.*
import com.ina_apps.model.services.DishesService
import com.mongodb.client.model.DeleteOptions
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonString
import org.litote.kmongo.*
import org.litote.kmongo.util.idValue


class DishesServiceMongoDBImplementation(database: MongoDatabase): DishesService {

    private val dishesCollection = database.getCollection<Dish>("Dish")
    private val userCollection = database.getCollection<User>("User")

    override suspend fun insertDish(dish: Dish): String? {

        val result = dishesCollection.insertOne(dish)
        val id = result.insertedId as BsonString
        return id.value
    }

    override suspend fun getDishesForRestaurant(id: String): List<Dish> {

        val dishesForRestaurant = dishesCollection.find(Dish::restaurantId eq id)
        return dishesForRestaurant.toList()
    }

    override suspend fun getFavoriteForUser(id: String): List<Dish> {

        val user = userCollection.find(User::id eq id).firstOrNull()
        val favoriteList: List<String> = user?.userInformation?.favoriteDishesIdList ?: listOf<String>()
        return dishesCollection.find(Dish::id `in` favoriteList).toList()
    }

    override suspend fun getOneById(id: String): Dish? {

        return dishesCollection.find(Dish::id eq id).firstOrNull()
    }

    override suspend fun updatePosterDishInformation(posterProductId: Long, posterDishInformation: PosterDishInformation): Dish? {

        val update = Updates.combine(
            Updates.set("posterProductId", posterProductId),
            Updates.set("poster", posterDishInformation)
        )
        return dishesCollection.findOneAndUpdate(Dish::posterProductId eq posterProductId, update, options = FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER))
    }

    override suspend fun deleteRedundantData(idList: List<Long>) {

        dishesCollection.deleteMany(Dish::posterProductId nin idList)

    }

    override suspend fun getDishesCount(): Long {

        return dishesCollection.countDocuments()
    }

}
