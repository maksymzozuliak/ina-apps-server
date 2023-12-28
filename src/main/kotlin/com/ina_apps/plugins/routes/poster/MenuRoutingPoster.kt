package com.ina_apps.plugins.routes.poster

import com.ina_apps.data.getBucketOrCreate
import com.ina_apps.model.database_classes.Dish
import com.ina_apps.model.services.DishesService
import com.ina_apps.model.services.RestaurantInformationService
import com.ina_apps.poster.menu.CategoryResponse
import com.ina_apps.poster.menu.PosterMenuService
import com.ina_apps.poster.menu.SourcesResponse
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
            val dishes = menuService.getProducts(restaurantId, restaurantInformation.posterInformation!!.accessToken)
            val sources = menuService.getSources(restaurantInformation.posterInformation.accessToken)
            val categories = menuService.getCategories(restaurantInformation.posterInformation.accessToken)
            call.respond(HttpStatusCode.OK, GetMenuResponse(sources, dishes, categories))
        }

        post("/insertMany") {

            val restaurantId = call.parameters["restaurantId"]

            val dishes = call.receive<List<Dish>>()
            if (restaurantId == null) {
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }
            val result = menuService.insertMany(restaurantId, dishes)
            if (result) {
                call.respond(HttpStatusCode.Created)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}

private data class GetMenuResponse(
    val sources: List<SourcesResponse>,
    val dishes: List<Dish>,
    val categories: List<CategoryResponse>
)

