package com.ina_apps.model.services

import com.ina_apps.model.classes.User

interface UserService {

    suspend fun getUserById(id: String) : User?

    suspend fun updateUser(user : User) : Boolean

    suspend fun insertUser(user: User) : Boolean
}