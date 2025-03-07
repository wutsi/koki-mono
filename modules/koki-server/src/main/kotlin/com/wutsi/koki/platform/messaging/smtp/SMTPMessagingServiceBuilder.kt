package com.wutsi.koki.platform.messaging.smtp

import com.wutsi.koki.platform.messaging.MessagingNotConfiguredException
import com.wutsi.koki.tenant.dto.ConfigurationName
import jakarta.mail.Authenticator
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import java.util.Properties
import kotlin.jvm.Throws

class SMTPMessagingServiceBuilder(
    private val host: String,
    private val port: Int,
    private val username: String,
    private val password: String,
    private val from: String,
) {
    companion object {
        val CONFIG_NAMES = listOf(
            ConfigurationName.SMTP_TYPE,
            ConfigurationName.SMTP_PORT,
            ConfigurationName.SMTP_HOST,
            ConfigurationName.SMTP_USERNAME,
            ConfigurationName.SMTP_PASSWORD,
            ConfigurationName.SMTP_FROM_ADDRESS,
            ConfigurationName.SMTP_FROM_PERSONAL
        )
    }

    @Throws(MessagingNotConfiguredException::class)
    fun build(config: Map<String, String>): SMTPMessagingService {
        val type = config[ConfigurationName.SMTP_TYPE]

        return when (type?.uppercase()) {
            SMTPType.EXTERNAL.name -> createExternalService(config)
            else -> createKokiService()
        }
    }

    private fun createKokiService(): SMTPMessagingService {
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

        return SMTPMessagingService(
            session = Session.getInstance(props, authenticator),
            fromAddress = from,
            fromPersonal = null
        )
    }

    private fun createExternalService(config: Map<String, String>): SMTPMessagingService {
        validate(config)

        val props = Properties()
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.smtp.port", config.get(ConfigurationName.SMTP_PORT))
        props.put("mail.smtp.host", config.get(ConfigurationName.SMTP_HOST))

        val authenticator = object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                val username = config.get(ConfigurationName.SMTP_USERNAME)
                val password = config.get(ConfigurationName.SMTP_PASSWORD)
                return PasswordAuthentication(username, password)
            }
        }

        return SMTPMessagingService(
            session = Session.getInstance(props, authenticator),
            fromAddress = config.get(ConfigurationName.SMTP_FROM_ADDRESS)!!,
            fromPersonal = config.get(ConfigurationName.SMTP_FROM_PERSONAL)!!
        )
    }

    private fun validate(config: Map<String, String>) {
        val missing = CONFIG_NAMES.filter { name ->
            name != ConfigurationName.SMTP_TYPE && config[name].isNullOrEmpty()
        }
        if (missing.isNotEmpty()) {
            throw MessagingNotConfiguredException("SMTP not configured. Missing config: $missing")
        }
    }
}
