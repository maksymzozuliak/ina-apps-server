package com.ina_apps.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.StorageOptions
import com.ina_apps.data.services_implemintation.DishesServiceMongoDBImplementation
import com.ina_apps.data.services_implemintation.OrderServiceMongoDBImplementation
import com.ina_apps.data.services_implemintation.RestaurantInformationServiceMongoDBImplementation
import com.ina_apps.data.services_implemintation.UserServiceMongoDBImplementation
import com.ina_apps.plugins.routes.*
import com.ina_apps.room.RoomController
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.routing.*
import java.io.ByteArrayInputStream

fun Application.configureRouting(
    database: MongoDatabase,
) {

    val ordersService = OrderServiceMongoDBImplementation(database)
    val dishesService = DishesServiceMongoDBImplementation(database)
    val restaurantInformationService = RestaurantInformationServiceMongoDBImplementation(database)
    val userService = UserServiceMongoDBImplementation(database)

    val jsonKey = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
    val projectId = System.getenv("GOOGLE_PROJECT_ID")
    val credentials = GoogleCredentials.fromStream(ByteArrayInputStream(jsonKey.toByteArray()))
    val storage = StorageOptions.newBuilder()
        .setCredentials(credentials)
        .setProjectId(projectId)
        .build()
        .service

    routing {

        ordersRouting(ordersService)
        dishesRouting(dishesService, storage)
        restaurantInformationRouting(restaurantInformationService, storage)
        usersRouting(userService)
        menuSocketRouting(RoomController())

    }
}
