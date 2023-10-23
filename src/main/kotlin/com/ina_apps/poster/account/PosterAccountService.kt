package com.ina_apps.poster.account

import com.ina_apps.model.database_classes.RestaurantInformation
import com.ina_apps.model.services.RestaurantInformationService
import com.ina_apps.utils.EmailService
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json

class PosterAccountService(
    private val restaurantInformationService: RestaurantInformationService,
    private val client: HttpClient,
    private val emailService: EmailService
) {

    suspend fun getAccessToken(code: String, account: String): Boolean {

        if (restaurantInformationService.getRestaurantInformationByAccountName(account) != null) {
            return false
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

            val accessTokenResponse = Json.decodeFromString<PosterAccountInformationResponse>(response.bodyAsText())
            val restaurantInformation = RestaurantInformation(
                account = account,
                accessToken = accessTokenResponse.accessToken,
                ownerInfo = accessTokenResponse.ownerInfo.toRestaurantInformationOwnerInfo(),
                accountNumber = accessTokenResponse.accountNumber
            )
            emailService.sendNewSubscriberEmail(restaurantInformation)
            val successful = restaurantInformationService.insertRestaurantInformation(restaurantInformation)
            if (successful) {
                return true
            }

        } catch(e: Exception) {
            return false
        }
        return false
    }

    suspend fun getRestaurantIdFromCode(code: String) : String? {

        try {

            val response = client.post("https://joinposter.com/api/v2/auth/manage") {
                setBody(MultiPartFormDataContent(parts = formData {
                    append("code", code)
                    append("application_id", System.getenv("POSTER_APPLICATION_ID"))
                    append("application_secret", System.getenv("POSTER_APPLICATION_SECRET"))
                }))
            }

            val decodedResponse = Json.decodeFromString<PosterAccountInformationResponse>(response.bodyAsText())
            return restaurantInformationService.getRestaurantInformationIdByAccountNumber(decodedResponse.accountNumber)

        } catch(e: Exception) {
            return null
        }
    }
}
