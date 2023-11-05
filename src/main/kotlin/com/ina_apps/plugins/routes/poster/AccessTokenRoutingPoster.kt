package com.ina_apps.plugins.routes.poster

import com.ina_apps.model.database_classes.RestaurantInformation
import com.ina_apps.model.services.RestaurantInformationService
import com.ina_apps.poster.account.PosterAccountInformationResponse
import com.ina_apps.poster.account.PosterAccountService
import com.ina_apps.poster.account.toRestaurantInformationOwnerInfo
import com.ina_apps.utils.EmailService
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Route.accessTokenRouting(
    posterAccountService: PosterAccountService
) {

    get("/getAccessToken") {

        val code = call.parameters["code"]
        val account = call.parameters["account"]
        if (code == null || account == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val successful = posterAccountService.getAccessToken(code, account)
        if (successful) {
            call.respondRedirect("https://$account.joinposter.com/manage/applications/i-a-apps")
        } else {
            call.respond(HttpStatusCode.Conflict)
        }
    }

    get("/getRestaurantId") {

        val code = call.parameters["code"]
        if (code == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val restaurantId = posterAccountService.getRestaurantIdFromCode(code)
        if (restaurantId != null) {
            call.respond(HttpStatusCode.OK, restaurantId)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
