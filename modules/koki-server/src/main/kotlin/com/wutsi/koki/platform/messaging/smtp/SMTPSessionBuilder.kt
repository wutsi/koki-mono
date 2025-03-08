package com.wutsi.koki.platform.messaging.smtp

import jakarta.mail.Authenticator
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import java.util.Properties

class SMTPSessionBuilder {
    fun build(
        host: String,
        port: Int,
        username: String,
        password: String,
    ): Session {
        val props = Properties()
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.smtp.port", port.toString())
        props.put("mail.smtp.host", host)

        val authenticator = object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                val username = username
                val password = password
                return PasswordAuthentication(username, password)
            }
        }
        return Session.getInstance(props, authenticator)
    }
}
