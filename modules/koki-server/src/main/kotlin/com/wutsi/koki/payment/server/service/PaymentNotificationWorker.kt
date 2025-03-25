package com.wutsi.koki.payment.server.service

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.Recipient
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.service.EmailService
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.service.InvoiceService
import com.wutsi.koki.notification.server.service.AbstractNotificationWorker
import com.wutsi.koki.notification.server.service.NotificationConsumer
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.event.TransactionCompletedEvent
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.Locale

/**
 * - customerName
 * - businessName
 * - invoiceNumber
 * - paymentAmount
 * - paymentDate
 * - paymentMethod
 */
@Service
class PaymentNotificationWorker(
    registry: NotificationConsumer,
    private val configurationService: ConfigurationService,
    private val transactionService: TransactionService,
    private val invoiceService: InvoiceService,
    private val businessService: BusinessService,
    private val emailService: EmailService,
    private val tenantService: TenantService,
    private val messages: MessageSource,
    private val logger: KVLogger,
) : AbstractNotificationWorker(registry) {
    override fun notify(event: Any): Boolean {
        if (event is TransactionCompletedEvent) {
            onPayment(event)
            return true
        }
        return false
    }

    private fun onPayment(event: TransactionCompletedEvent) {
        logger.add("event_status", event.status)
        logger.add("event_transaction_id", event.transactionId)
        logger.add("event_tenant_id", event.tenantId)

        if (event.status != TransactionStatus.SUCCESSFUL) {
            logger.add("email_skipped_reason", "TransactionNotSuccessful")
            return
        }

        // Config
        val configs = configurationService.search(tenantId = event.tenantId, keyword = "payment.")
            .map { config -> config.name to config.value }
            .toMap()
        if (configs[ConfigurationName.PAYMENT_EMAIL_ENABLED] == null) {
            logger.add("email_skipped_reason", "NotificationDisabled")
            return
        }

        // Transaction
        val tx = transactionService.get(id = event.transactionId, tenantId = event.tenantId)
        logger.add("transaction_status", tx.status)

        // Invoice
        val invoice = invoiceService.get(id = tx.invoiceId, tenantId = event.tenantId)
        logger.add("invoice_email", invoice.customerEmail)
        if (invoice.customerEmail.isEmpty()) {
            logger.add("email_skipped_reason", "NoCustomerEmail")
            return
        }

        // Send
        val business = businessService.get(tenantId = event.tenantId)
        val tenant = tenantService.get(event.tenantId)
        emailService.send(
            request = SendEmailRequest(
                recipient = Recipient(
                    displayName = invoice.customerName,
                    email = invoice.customerEmail,
                    id = invoice.customerAccountId,
                    type = invoice.customerAccountId?.let { ObjectType.ACCOUNT } ?: ObjectType.UNKNOWN,
                ),

                subject = configs[ConfigurationName.PAYMENT_EMAIL_SUBJECT] ?: TenantPaymentInitializer.EMAIL_SUBJECT,
                body = configs[ConfigurationName.PAYMENT_EMAIL_BODY] ?: "",
                data = createData(tx, invoice, business, tenant),
                owner = ObjectReference(id = tx.invoiceId, type = ObjectType.INVOICE),
            ),
            tenantId = invoice.tenantId
        )
    }

    private fun createData(
        tx: TransactionEntity,
        invoice: InvoiceEntity,
        business: BusinessEntity,
        tenant: TenantEntity,
    ): Map<String, Any> {
        val moneyFormat = tenant.createMoneyFormat()
        val dateFormat = tenant.createDateFormat()
        return mapOf(
            "customerName" to invoice.customerName,
            "businessName" to business.companyName,
            "invoiceNumber" to invoice.number,
            "paymentAmount" to moneyFormat.format(tx.amount),
            "paymentDate" to dateFormat.format(tx.createdAt),
            "paymentMethod" to messages.getMessage(
                "payment-method-type.${tx.paymentMethodType}",
                emptyArray(),
                Locale("en")
            )
        )
    }
}
