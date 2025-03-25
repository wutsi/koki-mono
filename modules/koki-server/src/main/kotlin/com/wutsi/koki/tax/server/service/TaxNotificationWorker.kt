package com.wutsi.koki.tax.server.service

import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.Recipient
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.service.EmailService
import com.wutsi.koki.invoice.server.service.TenantTaxInitializer
import com.wutsi.koki.notification.server.service.AbstractNotificationWorker
import com.wutsi.koki.notification.server.service.NotificationConsumer
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.tax.dto.event.TaxAssigneeChangedEvent
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import com.wutsi.koki.tenant.server.service.TypeService
import com.wutsi.koki.tenant.server.service.UserService
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class TaxNotificationWorker(
    registry: NotificationConsumer,

    private val configurationService: ConfigurationService,
    private val typeService: TypeService,
    private val accountService: AccountService,
    private val emailService: EmailService,
    private val taxService: TaxService,
    private val tenantService: TenantService,
    private val userService: UserService,
    private val messages: MessageSource,
    private val logger: KVLogger,
) : AbstractNotificationWorker(registry) {
    override fun notify(event: Any): Boolean {
        if (event is TaxAssigneeChangedEvent) {
            onAssigneeChanged(event)
        } else {
            return false
        }
        return true
    }

    private fun onAssigneeChanged(event: TaxAssigneeChangedEvent) {
        logger.add("event_assignee_id", event.assigneeId)
        logger.add("event_tax_id", event.taxId)
        logger.add("event_tenant_id", event.tenantId)

        // Assignee
        if (event.assigneeId == null) { // Unassigned
            logger.add("email_skipped_reason", "Unassigned")
            return
        }
        val assignee = userService.get(event.assigneeId!!, event.tenantId)

        // Config
        val configs = configurationService.search(tenantId = event.tenantId, keyword = "tax.")
            .map { config -> config.name to config.value }
            .toMap()
        if (configs[ConfigurationName.TAX_EMAIL_ASSIGNEE_ENABLED] == null) {
            logger.add("email_skipped_reason", "NotificationDisabled")
            return
        }

        // Tax
        val tax = taxService.get(event.taxId, event.tenantId)
        logger.add("tax_assignee_id", tax.assigneeId)
        if (tax.assigneeId != event.assigneeId) { // Assignee mismatch
            logger.add("email_skipped_reason", "AssigneeMismatch")
            return
        }

        // Tax Type
        val taxType = tax.taxTypeId?.let { id -> typeService.get(id, event.tenantId) }

        // Account
        val account = accountService.get(tax.accountId, event.tenantId)

        // Data
        val tenant = tenantService.get(event.tenantId)
        emailService.send(
            request = SendEmailRequest(
                subject = configs[ConfigurationName.TAX_EMAIL_ASSIGNEE_SUBJECT]
                    ?: TenantTaxInitializer.EMAIL_ASSIGNEE_SUBJECT,

                body = configs[ConfigurationName.TAX_EMAIL_ASSIGNEE_BODY] ?: "",

                owner = ObjectReference(id = event.taxId, type = ObjectType.TAX),

                recipient = Recipient(
                    displayName = assignee.displayName,
                    email = assignee.email,
                ),

                data = mapOf(
                    "recipientName" to assignee.displayName,
                    "accountName" to account.name,
                    "taxFiscalYear" to tax.fiscalYear,
                    "taxType" to taxType?.name,
                    "taxUrl" to "${tenant.portalUrl}/taxes/${tax.id}",
                    "taxStatus" to messages.getMessage(
                        "tax-status.${tax.status}",
                        emptyArray(),
                        LocaleContextHolder.getLocale()
                    ),
                ).filter { entry -> entry.value != null } as Map<String, Any>
            ),
            tenantId = event.tenantId
        )
    }
}
