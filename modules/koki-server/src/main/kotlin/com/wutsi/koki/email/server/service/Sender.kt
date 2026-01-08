package com.wutsi.koki.email.server.service

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.platform.messaging.smtp.SMTPMessagingServiceBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.tika.language.detect.LanguageDetector
import org.springframework.stereotype.Service
import java.io.File

@Service
class Sender(
    private val filterSet: EmailFilterSet,
    private val tenantService: TenantService,
    private val configurationService: ConfigurationService,
    private val messagingServiceBuilder: MessagingServiceBuilder,
    private val languageDetector: LanguageDetector,
    private val logger: KVLogger,
) {
    fun send(
        recipient: UserEntity,
        subject: String,
        body: String,
        attachments: List<File>,
        tenantId: Long,
    ): Boolean {
        return send(
            recipient = Party(
                email = recipient.email ?: "",
                displayName = recipient.displayName,
            ),
            subject = subject,
            body = body,
            attachments = attachments,
            tenantId = tenantId
        )
    }

    fun send(
        recipient: Party,
        subject: String,
        body: String,
        attachments: List<File>,
        tenantId: Long,
    ): Boolean {
        logger.add("recipient_email", recipient.email)
        logger.add("recipient_display_name", recipient.displayName)

        if (recipient.email.isEmpty()) {
            return false
        }

        val language = detectLanguage(subject, body)
        logger.add("recipient_language", language)

        val config = configurationService.search(
            names = SMTPMessagingServiceBuilder.CONFIG_NAMES,
            tenantId = tenantId,
        ).map { cfg -> cfg.name to cfg.value }.toMap()

        val tenant = tenantService.get(tenantId)
        val message = createMessage(recipient, subject, body, attachments, config, language, tenant)
        messagingServiceBuilder.build(config).send(message)
        return true
    }

    private fun createMessage(
        recipient: Party,
        subject: String,
        body: String,
        attachments: List<File>,
        config: Map<String, String>,
        language: String,
        tenant: TenantEntity
    ): Message {
        val body = filterSet.filter(body, tenant.id ?: -1)
        return Message(
            subject = subject,
            body = body,
            mimeType = "text/html",
            language = language,
            recipient = recipient,
            sender = Party(
                email = config[ConfigurationName.SMTP_FROM_ADDRESS]?.ifEmpty { null } ?: "",
                displayName = config[ConfigurationName.SMTP_FROM_PERSONAL]?.ifEmpty { null } ?: tenant.name
            ),
            attachments = attachments
        )
    }

    private fun detectLanguage(subject: String, body: String): String {
        return languageDetector.detect("$subject.\n$body").language
    }
}
