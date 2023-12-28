package com.ina_apps.data.services_implemintation

import com.ina_apps.model.database_classes.Category
import com.ina_apps.model.database_classes.DeliverySettings
import com.ina_apps.model.database_classes.PosterInformation
import com.ina_apps.model.database_classes.RestaurantInformation
import com.ina_apps.model.services.RestaurantInformationService
import com.mongodb.client.model.Updates.*
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.litote.kmongo.div
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
            updateResult.wasAcknowledged() /* && updateResult.modifiedCount > 0 */
        } else false
    }

    override suspend fun getRestaurantInformationById(id: String): RestaurantInformation? {

        return restaurantInformationCollection.find(RestaurantInformation::id eq id).firstOrNull()
    }

    override suspend fun getRestaurantInformationByAccountName(accountName: String): RestaurantInformation? {

        return restaurantInformationCollection.find(RestaurantInformation::posterInformation / PosterInformation::account eq accountName).firstOrNull()
    }

    override suspend fun getRestaurantInformationIdByAccountNumber(accountNumber: String): String? {

        return restaurantInformationCollection.find(RestaurantInformation::posterInformation / PosterInformation::accountNumber eq accountNumber).firstOrNull()?.id
    }

    override suspend fun updateDeliverySettings(id: String, deliverySettings: DeliverySettings): Boolean {

        val update = combine(
            set("deliverySettings.startHour", deliverySettings.startHour),
            set("deliverySettings.lastHour", deliverySettings.lastHour),
            set("deliverySettings.price", deliverySettings.price),
            set("deliverySettings.minPriceForFree", deliverySettings.minPriceForFree)
        )
        val updateResult = restaurantInformationCollection.updateOne(RestaurantInformation::id eq id, update)
        return updateResult.wasAcknowledged()
    }
}
