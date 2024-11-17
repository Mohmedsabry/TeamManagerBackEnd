package com.example.data.repositories

import java.util.*
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

fun sendVerificationEmail(recipientEmail: String, resetCode: String) {
    val username = "mayjak60@gmail.com"       // البريد الإلكتروني الخاص بك
    val password = "olmo ypye rebf okvo"          // كلمة مرور البريد الإلكتروني الخاص بك

    // إعدادات الخادم
    val props = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", "smtp.gmail.com")    // خادم Gmail
        put("mail.smtp.port", "587")               // منفذ SMTP
    }

    val session = Session.getInstance(props,
        object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

    try {
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(username))
            setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(recipientEmail)
            )
            subject = "Password Reset Code"
            setText("Your password reset code is: $resetCode")
        }

        Transport.send(message)
        println("Email sent successfully to $recipientEmail")

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
