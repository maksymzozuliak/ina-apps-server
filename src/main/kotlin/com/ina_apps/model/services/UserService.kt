package com.ina_apps.model.services

import com.ina_apps.model.database_classes.User
import com.ina_apps.model.database_classes.UserInformation

interface UserService {

    suspend fun getUserById(id: String) : User?

    suspend fun updateUser(user : User) : Boolean

    suspend fun insertUser(user: User) : Boolean

    suspend fun getUsersForRestaurant(restaurantId: String): List<User>
}