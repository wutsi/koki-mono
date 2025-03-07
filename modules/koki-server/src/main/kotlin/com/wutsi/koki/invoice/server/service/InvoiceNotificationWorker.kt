package com.wutsi.koki.invoice.server.service

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.Recipient
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.service.EmailService
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.event.InvoiceStatusChangedEvent
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.io.pdf.InvoicePdfExporter
import com.wutsi.koki.invoice.server.io.pdf.ReceiptPdfExporter
import com.wutsi.koki.notification.server.service.AbstractNotificationWorker
import com.wutsi.koki.notification.server.service.NotificationConsumer
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.util.CurrencyUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Send email notification about invoice
 * Email Variable:
 * - invoiceNumber
 * - invoiceAmountDue
 * - invoiceTotalAmount
 * - businessName
 */
@Service
class InvoiceNotificationWorker(
    registry: NotificationConsumer,
    private val configurationService: ConfigurationService,
    private val invoiceService: InvoiceService,
    private val businessService: BusinessService,
    private val logger: KVLogger,
    private val emailService: EmailService,
    private val fileService: FileService,
    private val invoicePdfExporter: InvoicePdfExporter,
    private val receiptPdfExporter: ReceiptPdfExporter,
) : AbstractNotificationWorker(registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(InvoiceNotificationWorker::class.java)
    }

    override fun notify(event: Any): Boolean {
        if (event is InvoiceStatusChangedEvent) {
            onStatusChanged(event)
        } else {
            return false
        }
        return true
    }

    private fun onStatusChanged(event: InvoiceStatusChangedEvent) {
        logger.add("event_status", event.status)
        logger.add("event_invoice_id", event.invoiceId)
        logger.add("event_tenant_id", event.tenantId)

        if (event.status == InvoiceStatus.PAID || event.status == InvoiceStatus.OPENED) {
            send(event)
        }
    }

    private fun send(event: InvoiceStatusChangedEvent) {
        val invoice = invoiceService.get(id = event.invoiceId, tenantId = event.tenantId)
        logger.add("invoice_status", invoice.status)
        if (invoice.status != event.status) {
            logger.add("email_sent", false)
            logger.add("email_reason", "StatusMismatch")
            return
        }
        if (invoice.customerEmail.isEmpty()) {
            logger.add("email_sent", false)
            logger.add("email_reason", "NoEmail")
            return
        }

        val configs = configurationService.search(tenantId = event.tenantId, keyword = "invoice.")
            .map { config -> config.name to config.value }.toMap()
        if (!isEnabled(configs, event)) {
            if (LOGGER.isDebugEnabled) {
                LOGGER.debug("Email notification not enabled for status: ${event.status}")
            }
            return
        }

        val business = businessService.get(tenantId = event.tenantId)
        val file = pdfFile(invoice, business, event)
        emailService.send(request = SendEmailRequest(
            recipient = Recipient(
                displayName = invoice.customerName,
                email = invoice.customerEmail,
                id = invoice.customerAccountId,
                type = invoice.customerAccountId?.let { ObjectType.ACCOUNT } ?: ObjectType.UNKNOWN,
            ),
            subject = getSubject(configs, event),
            body = getBody(configs, event),
            data = createData(invoice, business),
            owner = ObjectReference(id = invoice.id!!, type = ObjectType.INVOICE),
            attachmentFileIds = listOf(file.id!!),
        ), tenantId = invoice.tenantId)
    }

    private fun createData(invoice: InvoiceEntity, business: BusinessEntity): Map<String, Any> {
        val fmt = CurrencyUtil.getNumberFormat(invoice.currency)
        return mapOf(
            "businessName" to business.companyName,
            "invoiceNumber" to invoice.number,
            "invoiceAmountDue" to fmt.format(invoice.amountDue),
            "invoiceTotalAmount" to fmt.format(invoice.totalAmount),
        )
    }

    private fun isEnabled(configs: Map<String, String>, event: InvoiceStatusChangedEvent): Boolean {
        return when (event.status) {
            InvoiceStatus.OPENED -> configs[ConfigurationName.INVOICE_EMAIL_ENABLED] != null
            InvoiceStatus.PAID -> configs[ConfigurationName.INVOICE_EMAIL_RECEIPT_ENABLED] != null
            else -> false
        }
    }

    private fun getSubject(configs: Map<String, String>, event: InvoiceStatusChangedEvent): String {
        return when (event.status) {
            InvoiceStatus.OPENED -> configs[ConfigurationName.INVOICE_EMAIL_SUBJECT]
                ?: TenantInvoiceInitializer.INVOICE_SUBJECT

            InvoiceStatus.PAID -> configs[ConfigurationName.INVOICE_EMAIL_RECEIPT_SUBJECT]
                ?: TenantInvoiceInitializer.RECEIPT_SUBJECT

            else -> throw IllegalStateException("Not supported: ${event.status}")
        }
    }

    private fun getBody(configs: Map<String, String>, event: InvoiceStatusChangedEvent): String {
        return when (event.status) {
            InvoiceStatus.OPENED -> configs[ConfigurationName.INVOICE_EMAIL_BODY] ?: ""

            InvoiceStatus.PAID -> configs[ConfigurationName.INVOICE_EMAIL_RECEIPT_BODY] ?: ""

            else -> throw IllegalStateException("Not supported: ${event.status}")
        }
    }

    private fun pdfFile(
        invoice: InvoiceEntity, business: BusinessEntity, event: InvoiceStatusChangedEvent
    ): FileEntity {
        val file = File.createTempFile("invoice-${invoice.id}", ".pdf")
        try {
            // Filename
            val filename = when (event.status) {
                InvoiceStatus.OPENED -> "Invoice-${invoice.number}.pdf"
                else -> "Invoice-${invoice.number}-Receipt.pdf"
            }

            // Create PDF
            val output = FileOutputStream(file)
            when (event.status) {
                InvoiceStatus.OPENED -> invoicePdfExporter.export(invoice, business, output)
                else -> receiptPdfExporter.export(invoice, business, output)
            }

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
            )
        } finally {
            file.delete()
        }
    }
}
