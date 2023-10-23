package com.ina_apps.utils

import com.ina_apps.model.database_classes.RestaurantInformation
import com.ina_apps.model.services.RestaurantInformationService
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.HtmlEmail
import java.io.File
import java.lang.NullPointerException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class EmailService(private val restaurantInformationService: RestaurantInformationService) {

    private val executorService = Executors.newScheduledThreadPool(1)

    private val users = ConcurrentHashMap<String, String>()

    private fun generateCode(targetEmail: String): String {

        val verificationCode = String.format("%04d", (0..9999).random())

        users[targetEmail] = verificationCode

        executorService.schedule({

            users.remove(targetEmail)
        }, 600.toLong(), TimeUnit.SECONDS)

        return verificationCode
    }

    suspend fun sendNewSubscriberEmail( restaurantInformation: RestaurantInformation) {

        try {

            val email = HtmlEmail()
            email.apply {
                hostName = "smtp.googlemail.com"
                setSmtpPort(465)
                setAuthenticator(
                    DefaultAuthenticator(
                        System.getenv("VERIFICATION_EMAIL"),
                        System.getenv("VERIFICATION_EMAIL_PASSWORD")
                    )
                )
                setFrom(System.getenv("VERIFICATION_EMAIL"), "I&A Apps")
                isSSLOnConnect = true
                subject = "New subscription"
                setHtmlMsg(
                    readAndReplacePlaceholder(
                        filePath = "src/main/kotlin/com/ina_apps/res/NewSubscriptionEmail.html",
                        pair = arrayOf(
                            Pair("*|ACCOUNT_NAME|*", restaurantInformation.account),
                            Pair("*|CITY|*", restaurantInformation.ownerInfo.city),
                            Pair("*|COMPANY_NAME|*", restaurantInformation.ownerInfo.companyName),
                            Pair("*|COUNTRY|*", restaurantInformation.ownerInfo.country),
                            Pair("*|EMAIL|*", restaurantInformation.ownerInfo.email),
                            Pair("*|OWNER_NAME|*", restaurantInformation.ownerInfo.name),
                            Pair("*|PHONE|", restaurantInformation.ownerInfo.phone),
                            Pair("*|DATABASE_ID|*",restaurantInformation.id?: "Not created"),
                        )
                    )
                )
                addTo("inaapps.contact@gmail.com")
            }
            email.send()

        } catch (e: NullPointerException) {}
    }

    suspend fun sendVerificationEmail(targetEmail: String, restaurantId: String) {

        try {

            val restaurant = restaurantInformationService.getRestaurantInformationById(restaurantId)
            if (restaurant != null) {
                val email = HtmlEmail()
                email.apply {
                    hostName = "smtp.googlemail.com"
                    setSmtpPort(465)
                    setAuthenticator(
                        DefaultAuthenticator(
                            System.getenv("VERIFICATION_EMAIL"),
                            System.getenv("VERIFICATION_EMAIL_PASSWORD")
                        )
                    )
                    setFrom(System.getenv("VERIFICATION_EMAIL"), restaurant.name)
                    isSSLOnConnect = true
                    subject = "Verification"
                    setHtmlMsg(
                        readAndReplacePlaceholder(
                            filePath = "src/main/kotlin/com/ina_apps/res/VerificationEmail.html",
                            pair = arrayOf(
                                Pair("*|RESTAURANT_NAME|*!", restaurant.name!!),
                                Pair("*|VERIFICATION_CODE|*", generateCode(targetEmail)),
                                Pair("*|FACEBOOK_LINK|*", restaurant.facebookURL ?: ""),
                                Pair("*|INSTAGRAM_LINK|*", restaurant.instagramURL ?: ""),
                                Pair("*|BOTTOM_INFO|*", "${restaurant.name}. ${restaurant.address}.")
                            )
                        )
                    )
                    addTo(targetEmail)
                }
                email.send()
            }
        } catch (e: NullPointerException) {}
    }

    fun verifyCode(email: String, code: String): Boolean {

        val verify = users[email] == code

        if (verify) {
            users.remove(email)
        }
        return verify
    }

    private fun readAndReplacePlaceholder(filePath: String, vararg pair: Pair<String, String>): String {
        val file = File(filePath)
        if (file.exists()) {
            var htmlContent = file.readText()
            pair.forEach {
                htmlContent = htmlContent.replace(it.first, it.second)
            }
            return htmlContent
        } else {
            throw IllegalArgumentException("File not found: $filePath")
        }
    }
}
