package com.ina_apps.model.services

import com.ina_apps.model.database_classes.Category
import com.ina_apps.model.database_classes.RestaurantInformation

interface RestaurantInformationService {

    suspend fun insertRestaurantInformation( restaurantInformation: RestaurantInformation) : Boolean

    suspend fun updateRestaurantInformation( restaurantInformation: RestaurantInformation) : Boolean

    suspend fun getRestaurantInformationById( id: String) : RestaurantInformation?

    suspend fun addCategory( id: String, category: Category) : Boolean

    suspend fun getRestaurantInformationByAccountName( accountName: String): RestaurantInformation?

    suspend fun getRestaurantInformationIdByAccountNumber( accountNumber: String): String?
}
