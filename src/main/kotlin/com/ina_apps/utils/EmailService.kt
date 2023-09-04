package com.ina_apps.utils

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.HtmlEmail
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class EmailService {

    private val executorService = Executors.newScheduledThreadPool(1)

    private val users = ConcurrentHashMap<String, String>()

    private fun generateCode(targetEmail: String): String {

        val verificationCode = String.format("%04d", (0..9999).random())

        users[targetEmail] = verificationCode

        executorService.schedule({

            users.remove(targetEmail)
        }, 300.toLong(), TimeUnit.SECONDS)

        return verificationCode
    }

    suspend fun sendVerificationEmail(targetEmail: String) {

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
            setFrom(System.getenv("VERIFICATION_EMAIL"), "InA Apps")
            isSSLOnConnect = true
            subject = "Verification"
            setHtmlMsg(
                readAndReplacePlaceholder(
                filePath = "src/main/kotlin/com/ina_apps/res/VerificationEmail.html",
                placeholder = "{VERIFICATION_CODE}",
                replacement = generateCode(targetEmail)
                )
            )
            addTo(targetEmail)
        }
        email.send()
    }

    fun verifyCode(email: String, code: String): Boolean {

        val verify = users[email] == code

        if (verify) {
            users.remove(email)
        }
        return verify
    }

    private fun readAndReplacePlaceholder(filePath: String, placeholder: String, replacement: String): String {
        val file = File(filePath)
        if (file.exists()) {
            val htmlContent = file.readText()
            return htmlContent.replace(placeholder, replacement)
        } else {
            throw IllegalArgumentException("File not found: $filePath")
        }
    }
}