package com.ina_apps.plugins.routes

import com.ina_apps.model.classes.Category
import com.ina_apps.model.classes.RestaurantInformation
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
    restaurantInformationService: RestaurantInformationService
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

        put("/addCategory/{id}") {

            val id = call.parameters["id"]
            val color = call.parameters["color"]
            val multipart = call.receiveMultipart()
            var categoryReceiver: CategoryReceiver? = null
            var byteArray: ByteArray? = null
            if (id != null) {
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        byteArray = part.streamProvider().readBytes()
                    } else if (part is PartData.FormItem) {
                        if (part.name == "name") {
                            val nameJson = part.value
                            categoryReceiver = Json.decodeFromString(nameJson)
                    }
                }
                    part.dispose()
                }
                if (categoryReceiver != null) {
                    val result = restaurantInformationService.addCategory(id,
                        Category(
                            id = categoryReceiver!!.name.hashCode(),
                            name = categoryReceiver!!.name,
                            color = color,
                            image = byteArray
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
        }
    }
}

@Serializable
data class CategoryReceiver(
    val name: Map<String, String>
)