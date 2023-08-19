package com.ina_apps.plugins

import com.ina_apps.data.services_implemintation.DishesServiceMongoDBImplementation
import com.ina_apps.data.services_implemintation.OrderServiceMongoDBImplementation
import com.ina_apps.data.services_implemintation.RestaurantInformationServiceMongoDBImplementation
import com.ina_apps.data.services_implemintation.UserServiceMongoDBImplementation
import com.ina_apps.plugins.routes.*
import com.ina_apps.room.RoomController
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    database: MongoDatabase,
) {

    val ordersService = OrderServiceMongoDBImplementation(database)
    val dishesService = DishesServiceMongoDBImplementation(database)
    val restaurantInformationService = RestaurantInformationServiceMongoDBImplementation(database)
    val userService = UserServiceMongoDBImplementation(database)

    routing {

        ordersRouting(ordersService)
        dishesRouting(dishesService)
        restaurantInformationRouting(restaurantInformationService)
        usersRouting(userService)
        menuSocketRouting(RoomController())

    }
}
