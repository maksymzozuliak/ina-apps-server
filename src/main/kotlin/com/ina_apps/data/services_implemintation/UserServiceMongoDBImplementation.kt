package com.ina_apps.data.services_implemintation

import com.ina_apps.model.database_classes.User
import com.ina_apps.model.services.UserService
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.litote.kmongo.eq

class UserServiceMongoDBImplementation(database: MongoDatabase): UserService {

    private val userCollection = database.getCollection<User>("User")

    override suspend fun insertUser(user: User): Boolean {

        val result = userCollection.insertOne(user)
        return result.wasAcknowledged()
    }

    override suspend fun getUsersForRestaurant(restaurantId: String): List<User> {

        return userCollection.find(User::restaurantId eq restaurantId).toList()
    }

    override suspend fun getUserById(id: String): User? {

        return userCollection.find(User::id eq id).firstOrNull()
    }


    override suspend fun updateUser(user: User): Boolean {

        return if( user.id != null) {
            val updateResult = userCollection.replaceOne(User::id eq user.id, user)
            updateResult.wasAcknowledged()
//                    && updateResult.modifiedCount > 0
        } else false
    }


}