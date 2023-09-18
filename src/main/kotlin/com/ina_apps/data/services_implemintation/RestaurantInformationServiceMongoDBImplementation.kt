package com.ina_apps.data.services_implemintation

import com.ina_apps.model.database_classes.Category
import com.ina_apps.model.database_classes.RestaurantInformation
import com.ina_apps.model.services.RestaurantInformationService
import com.mongodb.client.model.Updates.addToSet
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.litote.kmongo.eq

class RestaurantInformationServiceMongoDBImplementation(database: MongoDatabase): RestaurantInformationService {

    private val restaurantInformationCollection = database.getCollection<RestaurantInformation>("RestaurantInformation")

    override suspend fun insertRestaurantInformation(restaurantInformation: RestaurantInformation): Boolean {

        val result = restaurantInformationCollection.insertOne(restaurantInformation)
        return result.wasAcknowledged()
    }

    override suspend fun updateRestaurantInformation(restaurantInformation: RestaurantInformation): Boolean {

        return if( restaurantInformation.id != null) {
            val updateResult = restaurantInformationCollection.replaceOne(RestaurantInformation::id eq restaurantInformation.id, restaurantInformation)
            updateResult.wasAcknowledged() && updateResult.modifiedCount > 0
        } else false
    }

    override suspend fun getRestaurantInformationById(id: String): RestaurantInformation? {

        return restaurantInformationCollection.find(RestaurantInformation::id eq id).firstOrNull()
    }

    override suspend fun addCategory(id: String, category: Category): Boolean {

        val result: RestaurantInformation? = if (category.index == null) {
            val categories = getRestaurantInformationById(id)?.category
            restaurantInformationCollection.findOneAndUpdate(
                RestaurantInformation::id eq id, addToSet("category", category.copy(index = categories?.size))
            )
        } else {
            restaurantInformationCollection.findOneAndUpdate(
                RestaurantInformation::id eq id, addToSet("category", category)
            )
        }
        return result != null
    }
}