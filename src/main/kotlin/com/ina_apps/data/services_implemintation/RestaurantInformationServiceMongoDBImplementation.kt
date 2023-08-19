package com.ina_apps.data.services_implemintation

import com.ina_apps.model.classes.Order
import com.ina_apps.model.classes.RestaurantInformation
import com.ina_apps.model.services.RestaurantInformationService
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection

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

}