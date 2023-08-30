package com.ina_apps.plugins.routes

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.*
import com.google.common.base.Preconditions
import com.ina_apps.data.getBucketOrCreate
import com.ina_apps.model.classes.Dish
import com.ina_apps.model.services.DishesService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.nio.file.Paths


fun Route.dishesRouting(
    dishesService: DishesService,
    storage: Storage
) {
    route("/dishes") {

        post("/insert") {

            val multipart = call.receiveMultipart()
            var dish: Dish? = null
            var image: PartData.FileItem? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {

                    image = part
                } else if (part is PartData.FormItem) {
                    if (part.name == "dish") {
                        val dishJson = part.value
                        dish = Json.decodeFromString(dishJson)
                    }
                    part.dispose()
                }
            }
            if (dish != null && image != null) {
                val bucket = getBucketOrCreate(dish!!.restaurantId, storage)
                val blobId = BlobId.of(bucket.name, image!!.originalFileName)
                val blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(image!!.contentType?.contentType)
                    .build()
                storage.createFrom(blobInfo, image!!.streamProvider.invoke())
                val result = dishesService.insertDish(
                    dish!!.copy(imageName = image!!.originalFileName)
                )
                image!!.dispose()
                if (result) {
                    call.respond(HttpStatusCode.Created)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            } else if (image != null) {
                image!!.dispose()
            }
        }

        get("/getForRestaurant/{restaurantId}") {

            val restaurantId = call.parameters["restaurantId"] ?: ""
            val dishesForRestaurant = dishesService.getDishesForRestaurant(id = restaurantId)
            call.respond(dishesForRestaurant)
        }

        get("/getFavoriteForUser/{userId}") {

            val userId = call.parameters["userId"] ?: ""
            val favoriteForUser = dishesService.getFavoriteForUser(id = userId)
            call.respond(favoriteForUser)
        }
    }
}