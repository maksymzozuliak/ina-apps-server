package com.ina_apps.plugins.routes

import com.google.cloud.storage.*
import com.ina_apps.data.getBucketOrCreate
import com.ina_apps.model.database_classes.Category
import com.ina_apps.model.database_classes.RestaurantInformation
import com.ina_apps.model.services.RestaurantInformationService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun Route.restaurantInformationRouting(
    restaurantInformationService: RestaurantInformationService,
    storage: Storage
) {
    route("/restaurantInformation") {

        post("/insert") {

            val restaurantInformation = call.receive<RestaurantInformation>()
            val result = restaurantInformationService.insertRestaurantInformation(restaurantInformation)
            if (result) {
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        put("/update") {

            val restaurantInformation = call.receive<RestaurantInformation>()
            val result = restaurantInformationService.updateRestaurantInformation(restaurantInformation)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/getById/{id}") {

            val id = call.parameters["id"]
            if (id != null) {
                val restaurantInformation = restaurantInformationService.getRestaurantInformationById(id)
                if (restaurantInformation != null) {
                    call.respond(HttpStatusCode.OK, restaurantInformation)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        put("/addCategory/{restaurantId}") {

            val restaurantId = call.parameters["restaurantId"]
            val color = call.parameters["color"]
            val index = call.parameters["index"]?.toInt()
            val multipart = call.receiveMultipart()
            var categoryReceiver: CategoryReceiver? = null
            var image: PartData.FileItem? = null
            if (restaurantId != null) {
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {

                        image = part
                    } else if (part is PartData.FormItem) {
                        if (part.name == "name") {
                            val nameJson = part.value
                            categoryReceiver = Json.decodeFromString(nameJson)
                        }
                        part.dispose()
                    }
                }
                if (categoryReceiver != null) {
                    val bucket = getBucketOrCreate(restaurantId, storage)
                    val blobId = BlobId.of(bucket.name, "category-"+image!!.originalFileName)
                    val blobInfo = BlobInfo.newBuilder(blobId)
                        .setContentType(image!!.contentType?.contentType)
                        .build()
                    storage.createFrom(blobInfo, image!!.streamProvider.invoke())
                    image!!.dispose()
                    val result = restaurantInformationService.addCategory(restaurantId,
                        Category(
                            id = categoryReceiver!!.name.hashCode(),
                            name = categoryReceiver!!.name,
                            color = color,
                            index = index,
                            imageName = "category-"+image!!.originalFileName
                        )
                    )
                    if (result) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
            if (image != null) {
                image!!.dispose()
            }
        }
    }
}

@Serializable
data class CategoryReceiver(
    val name: Map<String, String>
)