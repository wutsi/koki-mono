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
import org.springframework.stereotype.Service

@Service
class EmailSender(
    private val businessService: BusinessService,
    private val filterSet: EmailFilterSet,
    private val configurationService: ConfigurationService,
    private val messagingServiceBuilder: MessagingServiceBuilder,
) {
    fun send(
        recipient: UserEntity,
        subject: String,
        body: String,
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

        val message = createMessage(recipient, subject, body, config, business, tenantId)
        messagingServiceBuilder.build(config).send(message)
        return true
    }

    private fun createMessage(
        recipient: UserEntity,
        subject: String,
        body: String,
        config: Map<String, String>,
        business: BusinessEntity?,
        tenantId: Long
    ): Message {
        return Message(
            subject = subject,
            body = filterSet.filter(body, tenantId),
            mimeType = "text/html",
            recipient = Party(
                email = recipient.email!!,
                displayName = recipient.displayName,
            ),
            sender = Party(
                displayName = config[ConfigurationName.SMTP_FROM_PERSONAL] ?: business?.companyName
            )
        )
    }
}
