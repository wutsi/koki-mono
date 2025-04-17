package com.wutsi.koki.account.server.service

import com.wutsi.koki.account.dto.event.InvitationCreatedEvent
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.Recipient
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.service.EmailService
import com.wutsi.koki.notification.server.service.AbstractNotificationWorker
import com.wutsi.koki.notification.server.service.NotificationMQConsumer
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service

@Service
class AccountNotificationWorker(
    registry: NotificationMQConsumer,

    private val invitationService: InvitationService,
    private val accountService: AccountService,
    private val configurationService: ConfigurationService,
    private val businessService: BusinessService,
    private val tenantService: TenantService,
    private val emailService: EmailService,
    private val logger: KVLogger,
) : AbstractNotificationWorker(registry) {
    override fun notify(event: Any): Boolean {
        if (event is InvitationCreatedEvent) {
            onInvitationCreated(event)
        } else {
            return false
        }
        return true
    }

    private fun onInvitationCreated(event: InvitationCreatedEvent) {
        logger.add("event_invitation_id", event.invitationId)
        logger.add("event_tenant_id", event.tenantId)

        val invitation = invitationService.get(event.invitationId, event.tenantId)
        val account = accountService.get(invitation.accountId, event.tenantId)
        if (account.userId != null) {
            logger.add("email_skipped_reason", "AccountHasUser")
            return
        }
        if (account.email.isNullOrEmpty()) {
            logger.add("email_skipped_reason", "AccountHasNoEmail")
            return
        }
        val business = businessService.get(event.tenantId)
        val tenant = tenantService.get(event.tenantId)

        val configs = configurationService.search(
            tenantId = event.tenantId,
            names = listOf(
                ConfigurationName.ACCOUNT_INVITATION_EMAIL_SUBJECT,
                ConfigurationName.ACCOUNT_INVITATION_EMAIL_BODY,
            )
        ).map { cfg -> cfg.name to cfg.value }
            .toMap()

        emailService.send(
            tenantId = event.tenantId,
            request = SendEmailRequest(
                owner = ObjectReference(id = invitation.accountId, type = ObjectType.ACCOUNT),
                recipient = Recipient(
                    id = invitation.accountId,
                    type = ObjectType.ACCOUNT,
                    displayName = account.name,
                    email = account.email!!,
                    language = account.language,
                ),

                subject = configs[ConfigurationName.ACCOUNT_INVITATION_EMAIL_SUBJECT]
                    ?: TenantAccountInitializer.INVITATION_EMAIL_SUBJECT,

                body = configs[ConfigurationName.ACCOUNT_INVITATION_EMAIL_BODY]
                    ?: IOUtils.toString(
                        this::class.java.getResourceAsStream(TenantAccountInitializer.INVITATION_EMAIL_BODY_PATH),
                        "utf-8",
                    ),

                data = mapOf(
                    "businessName" to business.companyName,
                    "recipientName" to account.name,
                    "invitationUrl" to "${tenant.clientPortalUrl}/invitations/${invitation.id}"
                ),
            )
        )
    }
}
