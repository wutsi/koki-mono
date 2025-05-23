package com.wutsi.koki.invoice.server.service

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.Recipient
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.service.EmailService
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.event.InvoiceStatusChangedEvent
import com.wutsi.koki.invoice.server.command.SendInvoiceCommand
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.io.pdf.InvoicePdfExporter
import com.wutsi.koki.notification.server.service.AbstractNotificationWorker
import com.wutsi.koki.notification.server.service.NotificationMQConsumer
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.lang3.LocaleUtils
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date

/**
 * Send email notification about invoice
 * Email Variable:
 * - customerName
 * - businessName
 * - invoiceNumber
 * - invoiceAmountDue
 * - invoiceTotalAmount
 * - invoiceDate
 * - invoiceDueDate
 * - invoicePayUponReception
 * - paymentPortalUrl: URL where to pay online
 *
 * - paymentMethodInterac: TRUE if the merchant support Interact Transfer
 * - interacEmail: Email of the interac account
 * - interacQuestion: Secret question the customer should enter
 * - interacAnswer: Secret answer
 *
 * - paymentMethodCreditCard: TRUE if the merchant support CreditCard payment
 * - creditCardOfflineEnabled: TRUE if we can pay offline
 * - creditCardOfflinePhoneNumber: Phone number where to call for capturing the payment via phone
 *
 * - paymentMethodMobile: TRUE if the merchant support Mobile-Money payment
 * - mobileOfflinePhoneNumber: Phone number where to send mobile payment offline
 * - mobileOfflineAccountName: Name associate with the number
 * - mobileOfflinePhoneNumber: Phone number where to call for capturing the payment via phone
 * - mobileOfflineProvider: Phone number provider
 *
 * - paymentMethodPaypal: TRUE if the merchant support PayPal payment
 *
 * - paymentMethodCheck: TRUE if the merchant support payment via check
 * - checkPayTo
 * - checkInstructions
 *
 * - paymentMethodCash: TRUE if the merchant support payment in cach
 * - cashInstructions
 */
@Service
class InvoiceNotificationWorker(
    registry: NotificationMQConsumer,
    private val configurationService: ConfigurationService,
    private val invoiceService: InvoiceService,
    private val businessService: BusinessService,
    private val logger: KVLogger,
    private val emailService: EmailService,
    private val fileService: FileService,
    private val invoicePdfExporter: InvoicePdfExporter,
    private val tenantService: TenantService,
) : AbstractNotificationWorker(registry) {
    override fun notify(event: Any): Boolean {
        if (event is InvoiceStatusChangedEvent) {
            onStatusChanged(event)
        } else if (event is SendInvoiceCommand) {
            onSend(event)
        } else {
            return false
        }
        return true
    }

    private fun onStatusChanged(event: InvoiceStatusChangedEvent) {
        logger.add("event_status", event.status)
        logger.add("event_invoice_id", event.invoiceId)
        logger.add("event_tenant_id", event.tenantId)
        if (event.status != InvoiceStatus.OPENED) {
            return
        }

        // Invoice
        val invoice = invoiceService.get(id = event.invoiceId, tenantId = event.tenantId)
        logger.add("invoice_status", invoice.status)
        if (invoice.status != event.status) {
            logger.add("email_skipped_reason", "StatusMismatch")
            return
        }
        if (invoice.customerEmail.isEmpty()) {
            logger.add("email_skipped_reason", "NoCustomerEmail")
            return
        }

        // Configs
        val configs = configurationService.search(tenantId = event.tenantId, keyword = "invoice.")
            .map { config -> config.name to config.value }
            .toMap()
        if (configs[ConfigurationName.INVOICE_EMAIL_ENABLED] == null) {
            logger.add("email_skipped_reason", "NotificationDisabled")
            return
        }

        // Send
        val business = businessService.get(tenantId = event.tenantId)
        val tenant = tenantService.get(event.tenantId)
        val file = pdfFile(invoice, business)
        emailService.send(
            request = SendEmailRequest(
                recipient = Recipient(
                    displayName = invoice.customerName,
                    email = invoice.customerEmail,
                    id = invoice.customerAccountId,
                    type = invoice.customerAccountId?.let { ObjectType.ACCOUNT } ?: ObjectType.UNKNOWN,
                    language = invoice.locale?.let { locale -> LocaleUtils.toLocale(locale).language }
                ),
                subject = configs[ConfigurationName.INVOICE_EMAIL_SUBJECT] ?: TenantInvoiceInitializer.EMAIL_SUBJECT,
                body = configs[ConfigurationName.INVOICE_EMAIL_BODY] ?: "",
                data = createData(invoice, business, tenant),
                owner = ObjectReference(id = invoice.id!!, type = ObjectType.INVOICE),
                attachmentFileIds = listOf(file.id!!),
            ),
            tenantId = invoice.tenantId
        )
    }

    private fun onSend(event: SendInvoiceCommand) {
        logger.add("event_invoice_id", event.invoiceId)
        logger.add("event_tenant_id", event.tenantId)

        val invoice = invoiceService.get(id = event.invoiceId, tenantId = event.tenantId)
        val business = businessService.get(tenantId = event.tenantId)
        val tenant = tenantService.get(event.tenantId)

        val configs = configurationService.search(tenantId = event.tenantId, keyword = "invoice.")
            .map { config -> config.name to config.value }
            .toMap()

        val file = pdfFile(invoice, business)
        emailService.send(
            request = SendEmailRequest(
                recipient = Recipient(
                    displayName = invoice.customerName,
                    email = invoice.customerEmail,
                    id = invoice.customerAccountId,
                    type = invoice.customerAccountId?.let { ObjectType.ACCOUNT } ?: ObjectType.UNKNOWN,
                    language = invoice.locale?.let { locale -> LocaleUtils.toLocale(locale).language }
                ),

                subject = configs[ConfigurationName.INVOICE_EMAIL_SUBJECT] ?: TenantInvoiceInitializer.EMAIL_SUBJECT,
                body = configs[ConfigurationName.INVOICE_EMAIL_BODY] ?: "",
                data = createData(invoice, business, tenant),
                owner = ObjectReference(id = invoice.id!!, type = ObjectType.INVOICE),
                attachmentFileIds = listOf(file.id!!),
            ),
            tenantId = invoice.tenantId
        )
    }

    private fun createData(
        invoice: InvoiceEntity,
        business: BusinessEntity,
        tenant: TenantEntity,
    ): Map<String, Any> {
        val configs = configurationService.search(tenantId = invoice.tenantId, keyword = "payment.")
            .map { config -> config.name to config.value }.toMap()

        val moneyFormat = tenant.createMoneyFormat()
        val dateFormat = tenant.createDateFormat()
        return mapOf(
            "customerName" to invoice.customerName,
            "businessName" to business.companyName,
            "invoiceNumber" to invoice.number,
            "invoiceDate" to dateFormat.format(invoice.invoicedAt),
            "invoiceDueDate" to dateFormat.format(invoice.dueAt),
            "invoicePayUponReception" to isSameDay(invoice.invoicedAt, invoice.dueAt),
            "invoiceAmountDue" to moneyFormat.format(invoice.amountDue),
            "invoiceTotalAmount" to moneyFormat.format(invoice.totalAmount),

            "paymentPortalUrl" to "${tenant.portalUrl}/paynow/${invoice.paynowId}.${invoice.id}",

            "paymentMethodInterac" to configs[ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED],
            "interacEmail" to configs[ConfigurationName.PAYMENT_METHOD_INTERAC_EMAIL],
            "interacQuestion" to configs[ConfigurationName.PAYMENT_METHOD_INTERAC_QUESTION],
            "interacAnswer" to configs[ConfigurationName.PAYMENT_METHOD_INTERAC_ANSWER],

            "paymentMethodCreditCard" to configs[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED],
            "creditCardOfflineEnabled" to configs[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_ENABLED],
            "creditCardOfflinePhoneNumber" to configs[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_PHONE_NUMBER],

            "paymentMethodMobile" to configs[ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED],
            "mobileOfflineEnabled" to configs[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_ENABLED],
            "mobileOfflinePhoneNumber" to configs[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PHONE_NUMBER],
            "mobileOfflineAccountName" to configs[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_ACCOUNT_NAME],
            "mobileOfflineProvider" to configs[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PROVIDER],

            "paymentMethodPaypal" to configs[ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED],

            "paymentMethodCheck" to configs[ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED],
            "checkPayTo" to configs[ConfigurationName.PAYMENT_METHOD_CHECK_PAYEE],
            "checkInstructions" to toHtml(configs[ConfigurationName.PAYMENT_METHOD_CHECK_INSTRUCTIONS]),

            "paymentMethodCash" to configs[ConfigurationName.PAYMENT_METHOD_CASH_ENABLED],
            "cashInstructions" to toHtml(configs[ConfigurationName.PAYMENT_METHOD_CASH_INSTRUCTIONS]),
        )
            .filter { entry -> entry.value != null } as Map<String, Any>
    }

    private fun isSameDay(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) {
            return false
        } else {
            return Math.abs(date1.time - date2.time) <= 86400000L
        }
    }

    private fun pdfFile(invoice: InvoiceEntity, business: BusinessEntity): FileEntity {
        val file = File.createTempFile("invoice-${invoice.id}", ".pdf")
        try {
            // Filename
            val filename = "Invoice-${invoice.number}.pdf"

            // Create PDF
            val output = FileOutputStream(file)
            invoicePdfExporter.export(invoice, business, output)

            // Store to the cloud
            val input = FileInputStream(file)
            val url = input.use {
                fileService.store(
                    filename = filename,
                    content = input,
                    contentType = "application/pdf",
                    contentLength = file.length(),
                    tenantId = invoice.tenantId,
                    ownerId = invoice.id,
                    ownerType = ObjectType.INVOICE,
                )
            }

            // Create the file
            return fileService.create(
                filename = filename,
                contentType = "application/pdf",
                contentLength = file.length(),
                userId = null,
                ownerId = null,
                ownerType = null,
                tenantId = invoice.tenantId,
                url = url,
                type = FileType.FILE,
            )
        } finally {
            file.delete()
        }
    }

    fun toHtml(str: String?): String? {
        if (str == null) {
            return null
        }

        return StringEscapeUtils.escapeHtml4(str).replace("\n", "<br/>")
    }
}
