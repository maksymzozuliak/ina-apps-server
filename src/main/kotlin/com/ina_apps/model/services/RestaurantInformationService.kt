package com.ina_apps.model.services

import com.ina_apps.model.classes.RestaurantInformation

interface RestaurantInformationService {

    suspend fun insertRestaurantInformation( restaurantInformation: RestaurantInformation) : Boolean

    suspend fun updateRestaurantInformation( restaurantInformation: RestaurantInformation) : Boolean

    suspend fun getRestaurantInformationById( id: String) : RestaurantInformation?
}
