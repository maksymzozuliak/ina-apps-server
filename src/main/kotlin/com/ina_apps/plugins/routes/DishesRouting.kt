package com.ina_apps.plugins.routes

import com.google.cloud.storage.*
import com.ina_apps.data.getBucketOrCreate
import com.ina_apps.model.database_classes.Dish
import com.ina_apps.model.services.DishesService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json


fun Route.dishesRouting(
    dishesService: DishesService,
    storage: Storage
) {
    route("/dishes") {

//        post("/insert") {
//
//            val multipart = call.receiveMultipart()
//            var dish: Dish? = null
//            var image: PartData.FileItem? = null
//
//            multipart.forEachPart { part ->
//                if (part is PartData.FileItem) {
//
//                    image = part
//                } else if (part is PartData.FormItem) {
//                    if (part.name == "dish") {
//                        val dishJson = part.value
//                        dish = Json.decodeFromString(dishJson)
//                    }
//                    part.dispose()
//                }
//            }
//            if (dish != null && image != null) {
//                val bucket = getBucketOrCreate(dish!!.restaurantId, storage)
//                val blobId = BlobId.of(bucket.name, image!!.originalFileName)
//                val blobInfo = BlobInfo.newBuilder(blobId)
//                    .setContentType(image!!.contentType?.contentType)
//                    .build()
//                storage.createFrom(blobInfo, image!!.streamProvider.invoke())
//                val result = dishesService.insertDish(
//                    dish!!.copy(imageName = image!!.originalFileName)
//                )
//                image!!.dispose()
//                if (result != null) {
//                    call.respond(HttpStatusCode.Created)
//                } else {
//                    call.respond(HttpStatusCode.BadRequest)
//                }
//            } else if (image != null) {
//                image!!.dispose()
//            }
//        }

        get("/getForRestaurant/{restaurantId}") {

            val restaurantId = call.parameters["restaurantId"]
            if (restaurantId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val dishesForRestaurant = dishesService.getDishesForRestaurant(id = restaurantId)
            call.respond(dishesForRestaurant)
        }

        authenticate {

            get("/getFavoriteForUser") {

                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class)
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val favoriteForUser = dishesService.getFavoriteForUser(id = userId)
                call.respond(favoriteForUser)
            }
        }
    }
}
