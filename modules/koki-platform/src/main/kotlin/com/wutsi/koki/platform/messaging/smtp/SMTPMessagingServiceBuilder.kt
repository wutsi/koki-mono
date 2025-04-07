package com.wutsi.koki.platform.messaging.smtp

import com.wutsi.koki.platform.messaging.MessagingNotConfiguredException
import com.wutsi.koki.tenant.dto.ConfigurationName
import jakarta.mail.Session
import kotlin.jvm.Throws

class SMTPMessagingServiceBuilder(
    private val session: Session,
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
        return SMTPMessagingService(
            session = session,
            fromAddress = from,
            fromPersonal = null
        )
    }

    private fun createExternalService(config: Map<String, String>): SMTPMessagingService {
        validate(config)

        return SMTPMessagingService(
            session = SMTPSessionBuilder().build(
                host = config.get(ConfigurationName.SMTP_HOST)!!,
                port = config.get(ConfigurationName.SMTP_PORT)!!.toInt(),
                username = config.get(ConfigurationName.SMTP_USERNAME)!!,
                password = config.get(ConfigurationName.SMTP_PASSWORD)!!
            ),
            fromAddress = config.get(ConfigurationName.SMTP_FROM_ADDRESS)!!,
            fromPersonal = config.get(ConfigurationName.SMTP_FROM_PERSONAL)
        )
    }

    private fun validate(config: Map<String, String>) {
        val required = CONFIG_NAMES.filter { name -> name != ConfigurationName.SMTP_TYPE }
            .filter { name -> name != ConfigurationName.SMTP_FROM_PERSONAL }

        val available = required.mapNotNull { name -> config[name] }
        if (available.size != required.size) {
            throw MessagingNotConfiguredException(
                "SMTP not configured. Missing config: " + required.filter { name -> config[name] == null }
            )
        }
    }
}
