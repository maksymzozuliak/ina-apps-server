package com.ina_apps.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.StorageOptions
import com.ina_apps.data.services_implemintation.*
import com.ina_apps.plugins.routes.*
import com.ina_apps.room.RegistrationRoomController
import com.ina_apps.room.RoomController
import com.ina_apps.utils.EmailService
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.zozuliak.security.hashing.HashingService
import com.zozuliak.security.token.TokenConfig
import com.zozuliak.security.token.TokenService
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import java.io.ByteArrayInputStream

fun Application.configureRouting(
    database: MongoDatabase,
    client: HttpClient,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {

    val oneSignalService = OneSignalServiceImpl(client, System.getenv("ONE_SIGNAL_REST_API_KEY"))

    val ordersService = OrderServiceMongoDBImplementation(database)
    val dishesService = DishesServiceMongoDBImplementation(database)
    val restaurantInformationService = RestaurantInformationServiceMongoDBImplementation(database)
    val userService = UserServiceMongoDBImplementation(database)

    val emailService = EmailService(restaurantInformationService)

    val registrationRoomController = RegistrationRoomController()

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
        oneSignalRouting(oneSignalService)
        authRouting(userService, hashingService, tokenService, tokenConfig, emailService,registrationRoomController)
    }
}
