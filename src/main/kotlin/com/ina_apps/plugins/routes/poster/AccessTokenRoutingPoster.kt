package com.ina_apps.plugins.routes.poster

import com.ina_apps.model.database_classes.RestaurantInformation
import com.ina_apps.model.services.RestaurantInformationService
import com.ina_apps.poster.oauth2.AccessTokenResponse
import com.ina_apps.poster.oauth2.toRestaurantInformationOwnerInfo
import com.ina_apps.utils.EmailService
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.serialization.json.Json

fun Route.accessTokenRouting(
    restaurantInformationService: RestaurantInformationService,
    client: HttpClient,
    emailService: EmailService
) {

    get("/getAccessToken") {

        val code = call.parameters["code"]
        val account = call.parameters["account"]
        if (code == null || account == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        if (restaurantInformationService.getRestaurantInformationByAccountName(account) != null) {
            call.respond(HttpStatusCode.Conflict)
            return@get
        }

        try {

            val response = client.post("https://$account.joinposter.com/api/v2/auth/access_token") {
                setBody(MultiPartFormDataContent(parts = formData {
                    append("code", code)
                    append("application_id", System.getenv("POSTER_APPLICATION_ID"))
                    append("application_secret", System.getenv("POSTER_APPLICATION_SECRET"))
                    append("redirect_uri", System.getenv("POSTER_REDIRECT_URI"))
                    append("grant_type", "authorization_code")
                }))
            }

            val accessTokenResponse = Json.decodeFromString<AccessTokenResponse>(response.bodyAsText())
            val restaurantInformation = RestaurantInformation(
                account = account,
                accessToken = accessTokenResponse.accessToken,
                ownerInfo = accessTokenResponse.ownerInfo.toRestaurantInformationOwnerInfo()
            )
            emailService.sendNewSubscriberEmail(restaurantInformation)
            val successful = restaurantInformationService.insertRestaurantInformation(restaurantInformation)
            if (successful) {
                call.respondRedirect("https://$account.joinposter.com/manage/applications/i-a-apps")
            }

        } catch(e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e)
            return@get
        }

    }
}
