package com.wutsi.koki.email.server.service

import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.platform.messaging.smtp.SMTPMessagingServiceBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.apache.tika.language.detect.LanguageDetector
import org.springframework.stereotype.Service
import java.io.File

@Service
class Sender(
    private val businessService: BusinessService,
    private val filterSet: EmailFilterSet,
    private val configurationService: ConfigurationService,
    private val messagingServiceBuilder: MessagingServiceBuilder,
    private val languageDetector: LanguageDetector,
) {
    fun send(
        recipient: UserEntity,
        subject: String,
        body: String,
        attachments: List<File>,
        tenantId: Long,
    ): Boolean {
        if (recipient.email.isNullOrEmpty()) {
            return false
        }

        val config = configurationService.search(
            names = SMTPMessagingServiceBuilder.CONFIG_NAMES,
            tenantId = tenantId,
        ).map { cfg -> cfg.name to cfg.value }.toMap()

        val business = businessService.getOrNull(tenantId)

        val message = createMessage(recipient, subject, body, attachments, config, business, tenantId)
        messagingServiceBuilder.build(config).send(message)
        return true
    }

    private fun createMessage(
        recipient: UserEntity,
        subject: String,
        body: String,
        attachments: List<File>,
        config: Map<String, String>,
        business: BusinessEntity?,
        tenantId: Long
    ): Message {
        val body = filterSet.filter(body, tenantId)
        return Message(
            subject = subject,
            body = body,
            mimeType = "text/html",
            language = detectLanguage(subject, body),
            recipient = Party(
                email = recipient.email!!,
                displayName = recipient.displayName,
            ),
            sender = Party(
                email = config[ConfigurationName.SMTP_FROM_ADDRESS]?.ifEmpty { null } ?: "",
                displayName = config[ConfigurationName.SMTP_FROM_PERSONAL]?.ifEmpty { null } ?: business?.companyName
            ),
            attachments = attachments
        )
    }

    private fun detectLanguage(subject: String, body: String): String {
        return languageDetector.detect("$subject.\n$body").language
    }
}
