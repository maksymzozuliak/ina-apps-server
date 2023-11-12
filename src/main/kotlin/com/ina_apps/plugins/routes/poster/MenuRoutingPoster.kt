package com.ina_apps.plugins.routes.poster

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.ina_apps.data.getBucketOrCreate
import com.ina_apps.model.database_classes.Dish
import com.ina_apps.model.services.DishesService
import com.ina_apps.model.services.RestaurantInformationService
import com.ina_apps.poster.menu.PosterMenuService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.json
import java.io.InputStream

fun Route.menuRoutingPoster(
    menuService: PosterMenuService,
    restaurantInformationService: RestaurantInformationService
) {
    route("/menu") {

        get("/getMenu") {

            val restaurantId = call.parameters["restaurantId"]
            if (restaurantId == null) {
                call.respond(HttpStatusCode.Forbidden)
                return@get
            }
            val restaurantInformation = restaurantInformationService.getRestaurantInformationById(restaurantId)
            if (restaurantInformation == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            val result = menuService.getProducts(restaurantId, restaurantInformation.posterInformation!!.accessToken)
            val sources = menuService.getSourcesList(restaurantId)
            call.respond(HttpStatusCode.OK, Pair(sources, result))
        }

        post("/insertMany") {

            val restaurantId = call.parameters["restaurantId"]

            val dishes = call.receive<List<Dish>>()
            if (restaurantId == null) {
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }
//            val newDishes = mutableListOf<Dish>()
//            dishes.forEach { dish ->
//                if (dish.image != null) {
//                    val bucket = getBucketOrCreate(restaurantId, storage)
//                    val blobId = BlobId.of(bucket.name, dish.poster?.posterId.toString())
//                    val blobInfo = BlobInfo.newBuilder(blobId)
//                        .setContentType("image")
//                        .build()
//                    storage.createFrom(blobInfo, dish.image.inputStream())
//                }
//                newDishes.add(dish.copy(image = null))
//            }
//            val result = dishesService.replaceAll(restaurantId ,newDishes)
            val result = menuService.insertMany(restaurantId, dishes)
            if (result) {
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
