package com.ina_apps.poster.account

import com.ina_apps.model.database_classes.PosterInformation
import com.ina_apps.model.database_classes.RestaurantInformation
import com.ina_apps.model.services.RestaurantInformationService
import com.ina_apps.utils.EmailService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.MessageDigest

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

            val paymentResponse = client.post("https://joinposter.com/api/settings.createPaymentMethod?token=${accessTokenResponse.accessToken}") {
                setBody(MultiPartFormDataContent(parts = formData {
                    append("title", "I&A Apps")
                    append("money_type", 2)
                    append("color", "navy-blue")
                }))
            }

            val paymentMethodIdResponse = Json.decodeFromString<ResponseData>(paymentResponse.bodyAsText())

            val restaurantInformation = RestaurantInformation(
                posterInformation = PosterInformation(
                    account = account,
                    accessToken = accessTokenResponse.accessToken,
                    ownerInfo = accessTokenResponse.ownerInfo.toRestaurantInformationOwnerInfo(),
                    accountNumber = accessTokenResponse.accountNumber,
                    paymentMethodId = paymentMethodIdResponse.response
                ),
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
                    append("application_id", System.getenv("POSTER_APPLICATION_ID"))
                    append("application_secret", System.getenv("POSTER_APPLICATION_SECRET"))
                    append("code", code)
                    append("verify", createMD5Hash(code))
                }))
            }
            val decodedResponse = Json.decodeFromString<PosterAccountInformationResponse>(response.bodyAsText())
            return restaurantInformationService.getRestaurantInformationIdByAccountNumber(decodedResponse.accountNumber)

        } catch(e: Exception) {
            return null
        }
    }

    private fun createMD5Hash(code: String): String {
        val concatenatedString = "${System.getenv("POSTER_APPLICATION_ID")}:${System.getenv("POSTER_APPLICATION_SECRET")}:$code"

        val md5 = MessageDigest.getInstance("MD5")
        val byteArray = concatenatedString.toByteArray(Charsets.UTF_8)
        val md5Bytes = md5.digest(byteArray)

        val md5Hex = md5Bytes.joinToString("") {
            String.format("%02x", it)
        }

        return md5Hex
    }

    @Serializable
    private data class ResponseData(val response: Int)
}
