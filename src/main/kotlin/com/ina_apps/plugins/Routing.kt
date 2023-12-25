package com.ina_apps.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.StorageOptions
import com.ina_apps.data.services_implemintation.*
import com.ina_apps.plugins.routes.*
import com.ina_apps.plugins.routes.poster.accessTokenRouting
import com.ina_apps.plugins.routes.poster.menuRoutingPoster
import com.ina_apps.plugins.routes.poster.ordersRoutingPoster
import com.ina_apps.plugins.routes.poster.restaurantInformationRoutingPoster
import com.ina_apps.poster.account.PosterAccountService
import com.ina_apps.poster.menu.PosterMenuService
import com.ina_apps.poster.orders.PosterOrderService
import com.ina_apps.room.RegistrationRoomController
import com.ina_apps.room.RoomController
import com.ina_apps.utils.CustomTimer
import com.ina_apps.utils.EmailService
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.zozuliak.security.hashing.HashingService
import com.zozuliak.security.token.TokenConfig
import com.zozuliak.security.token.TokenService
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

@OptIn(DelicateCoroutinesApi::class)
fun Application.configureRouting(
    database: MongoDatabase,
    client: HttpClient,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    timer: CustomTimer
) {

    val oneSignalService = OneSignalServiceImpl(client, System.getenv("ONE_SIGNAL_REST_API_KEY"))

    val jsonKey = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
    val projectId = System.getenv("GOOGLE_PROJECT_ID")
    val credentials = GoogleCredentials.fromStream(ByteArrayInputStream(jsonKey.toByteArray()))

    val storage = StorageOptions.newBuilder()
        .setCredentials(credentials)
        .setProjectId(projectId)
        .build()
        .service

    val ordersService = OrderServiceMongoDBImplementation(database)
    val dishesService = DishesServiceMongoDBImplementation(database)
    val restaurantInformationService = RestaurantInformationServiceMongoDBImplementation(database)
    val userService = UserServiceMongoDBImplementation(database)

    val emailService = EmailService(restaurantInformationService)

    val posterOrderService = PosterOrderService(client)
    val posterAccountService = PosterAccountService(restaurantInformationService, client, emailService)
    val posterMenuService = PosterMenuService(client, dishesService, storage)

    val registrationRoomController = RegistrationRoomController()

//    timer.createNewTimer(
//        24*60*60*1000L
//    ) {
//        GlobalScope.launch {
//            ordersService.deleteOldOrders(1)
//        }
//    }

    routing {

        ordersRouting(ordersService)
        dishesRouting(dishesService, storage)
        restaurantInformationRouting(restaurantInformationService, storage)
        usersRouting(userService)
        oneSignalRouting(oneSignalService)
        authRouting(userService, hashingService, tokenService, tokenConfig, emailService,registrationRoomController)

        //Poster
        route("/poster") {

            accessTokenRouting(posterAccountService)
            restaurantInformationRoutingPoster(restaurantInformationService)
            ordersRoutingPoster(posterOrderService)
            menuRoutingPoster(posterMenuService, restaurantInformationService)
        }
    }
}
