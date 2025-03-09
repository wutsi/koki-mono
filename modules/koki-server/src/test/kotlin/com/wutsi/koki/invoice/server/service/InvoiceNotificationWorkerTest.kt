package com.wutsi.koki.invoice.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.server.service.EmailService
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.event.InvoiceStatusChangedEvent
import com.wutsi.koki.invoice.server.command.SendInvoiceCommand
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.io.pdf.InvoicePdfExporter
import com.wutsi.koki.notification.server.service.NotificationConsumer
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals

class InvoiceNotificationWorkerTest {
    private val registry = mock<NotificationConsumer>()
    private val configurationService = mock<ConfigurationService>()
    private val invoiceService = mock<InvoiceService>()
    private val businessService = mock<BusinessService>()
    private val fileService = mock<FileService>()
    private val invoicePdfExporter = mock<InvoicePdfExporter>()
    private val logger = DefaultKVLogger()
    private val emailService = mock<EmailService>()
    private val url = URL("https://fff.com/foo.pdf")
    private val file = FileEntity(id = 555)

    private val worker = InvoiceNotificationWorker(
        registry = registry,
        configurationService = configurationService,
        invoiceService = invoiceService,
        emailService = emailService,
        businessService = businessService,
        fileService = fileService,
        invoicePdfExporter = invoicePdfExporter,
        logger = logger,
    )

    private val invoiceId = 111L
    private val business = BusinessEntity(companyName = "Olive Inc")

    private val configurations = listOf(
        ConfigurationEntity(name = ConfigurationName.INVOICE_EMAIL_OPENED_ENABLED, value = "1"),
        ConfigurationEntity(
            name = ConfigurationName.INVOICE_EMAIL_OPENED_SUBJECT, value = "New Invoice #{{invoiceNumber}}"
        ),
        ConfigurationEntity(name = ConfigurationName.INVOICE_EMAIL_OPENED_BODY, value = "You have a new invoice!"),

        ConfigurationEntity(name = ConfigurationName.INVOICE_EMAIL_PAID_ENABLED, value = "1"),
        ConfigurationEntity(
            name = ConfigurationName.INVOICE_EMAIL_PAID_SUBJECT,
            value = "Thank you for your payment - Invoice #{{invoiceNumber}}"
        ),
        ConfigurationEntity(name = ConfigurationName.INVOICE_EMAIL_PAID_BODY, value = "Thank you!"),
    )

    @BeforeEach
    fun setUp() {
        doReturn(url).whenever(fileService).store(
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()
        )
        doReturn(file).whenever(fileService).create(
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()
        )
        doReturn(business).whenever(businessService).get(any())
        doReturn(configurations).whenever(configurationService).search(anyOrNull(), anyOrNull(), anyOrNull())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    private fun createEvent(status: InvoiceStatus): InvoiceStatusChangedEvent {
        return InvoiceStatusChangedEvent(
            tenantId = 111L,
            invoiceId = invoiceId,
            status = status,
        )
    }

    private fun createInvoice(
        status: InvoiceStatus, customerAccountId: Long? = 555L, email: String = "ray.sponsible@gmail.com"
    ): InvoiceEntity {
        val invoice = InvoiceEntity(
            id = invoiceId,
            number = 1445L,
            customerAccountId = customerAccountId,
            customerEmail = email,
            customerName = "Ray Sponsible",
            status = status,
            currency = "CAD",
            tenantId = 111L,
        )
        doReturn(invoice).whenever(invoiceService).get(any(), any())
        return invoice
    }

    @Test
    fun postConstruct() {
        worker.setUp()
        verify(registry).register(worker)
    }

    @Test
    fun preDestroy() {
        worker.tearDown()
        verify(registry).unregister(worker)
    }

    @Test
    fun `send invoice`() {
        val invoice = createInvoice(InvoiceStatus.OPENED)
        worker.notify(
            SendInvoiceCommand(invoiceId = invoiceId, tenantId = invoice.tenantId)
        )

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(invoice.tenantId))

        assertEquals(invoice.customerEmail, request.firstValue.recipient.email)
        assertEquals(invoice.customerName, request.firstValue.recipient.displayName)
        assertEquals(invoice.customerAccountId, request.firstValue.recipient.id)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)
        assertEquals("New Invoice #{{invoiceNumber}}", request.firstValue.subject)
        assertEquals("You have a new invoice!", request.firstValue.body)
        assertEquals(listOf(file.id), request.firstValue.attachmentFileIds)
        assertEquals(invoice.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.INVOICE, request.firstValue.owner?.type)
    }

    @Test
    fun `notify opened invoice`() {
        val invoice = createInvoice(InvoiceStatus.OPENED)
        val event = createEvent(invoice.status)
        worker.notify(event)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(invoice.tenantId))

        assertEquals(invoice.customerEmail, request.firstValue.recipient.email)
        assertEquals(invoice.customerName, request.firstValue.recipient.displayName)
        assertEquals(invoice.customerAccountId, request.firstValue.recipient.id)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)
        assertEquals("New Invoice #{{invoiceNumber}}", request.firstValue.subject)
        assertEquals("You have a new invoice!", request.firstValue.body)
        assertEquals(listOf(file.id), request.firstValue.attachmentFileIds)
        assertEquals(invoice.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.INVOICE, request.firstValue.owner?.type)
    }

    @Test
    fun `notify opened invoice - not enabled`() {
        doReturn(configurations.filter { config -> config.name != ConfigurationName.INVOICE_EMAIL_OPENED_ENABLED }).whenever(
            configurationService
        ).search(anyOrNull(), anyOrNull(), anyOrNull())

        val invoice = createInvoice(InvoiceStatus.OPENED)
        val event = createEvent(invoice.status)
        worker.notify(event)

        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `notify opened invoice - no config`() {
        doReturn(configurations.filter { config -> config.name == ConfigurationName.INVOICE_EMAIL_OPENED_ENABLED }).whenever(
            configurationService
        ).search(anyOrNull(), anyOrNull(), anyOrNull())

        val invoice = createInvoice(InvoiceStatus.OPENED, customerAccountId = null)
        val event = createEvent(invoice.status)
        worker.notify(event)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(invoice.tenantId))

        assertEquals(invoice.customerEmail, request.firstValue.recipient.email)
        assertEquals(invoice.customerName, request.firstValue.recipient.displayName)
        assertEquals(null, request.firstValue.recipient.id)
        assertEquals(ObjectType.UNKNOWN, request.firstValue.recipient.type)
        assertEquals(TenantInvoiceInitializer.INVOICE_SUBJECT, request.firstValue.subject)
        assertEquals("", request.firstValue.body)
        assertEquals(true, request.firstValue.attachmentFileIds.isNotEmpty())
        assertEquals(invoice.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.INVOICE, request.firstValue.owner?.type)
    }

    @Test
    fun `notify paid invoice`() {
        val invoice = createInvoice(InvoiceStatus.PAID)
        val event = createEvent(invoice.status)
        worker.notify(event)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(invoice.tenantId))

        assertEquals(invoice.customerEmail, request.firstValue.recipient.email)
        assertEquals(invoice.customerName, request.firstValue.recipient.displayName)
        assertEquals(invoice.customerAccountId, request.firstValue.recipient.id)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)
        assertEquals("Thank you for your payment - Invoice #{{invoiceNumber}}", request.firstValue.subject)
        assertEquals("Thank you!", request.firstValue.body)
        assertEquals(true, request.firstValue.attachmentFileIds.isNotEmpty())
        assertEquals(invoice.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.INVOICE, request.firstValue.owner?.type)
    }

    @Test
    fun `notify paid invoice - not enabled`() {
        doReturn(configurations.filter { config -> config.name != ConfigurationName.INVOICE_EMAIL_PAID_ENABLED }).whenever(
            configurationService
        ).search(anyOrNull(), anyOrNull(), anyOrNull())

        val invoice = createInvoice(InvoiceStatus.PAID)
        val event = createEvent(invoice.status)
        worker.notify(event)

        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `notify paid invoice - no config`() {
        doReturn(configurations.filter { config -> config.name == ConfigurationName.INVOICE_EMAIL_PAID_ENABLED }).whenever(
            configurationService
        ).search(anyOrNull(), anyOrNull(), anyOrNull())

        val invoice = createInvoice(InvoiceStatus.PAID, customerAccountId = null)
        val event = createEvent(invoice.status)
        worker.notify(event)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(invoice.tenantId))

        assertEquals(invoice.customerEmail, request.firstValue.recipient.email)
        assertEquals(invoice.customerName, request.firstValue.recipient.displayName)
        assertEquals(null, request.firstValue.recipient.id)
        assertEquals(ObjectType.UNKNOWN, request.firstValue.recipient.type)
        assertEquals(TenantInvoiceInitializer.RECEIPT_SUBJECT, request.firstValue.subject)
        assertEquals("", request.firstValue.body)
        assertEquals(true, request.firstValue.attachmentFileIds.isNotEmpty())
        assertEquals(invoice.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.INVOICE, request.firstValue.owner?.type)
    }

    @Test
    fun `never notify draft invoice`() {
        val invoice = createInvoice(InvoiceStatus.DRAFT)
        val event = createEvent(invoice.status)
        worker.notify(event)

        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `never notify voided invoice`() {
        val invoice = createInvoice(InvoiceStatus.VOIDED)
        val event = createEvent(invoice.status)
        worker.notify(event)

        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `status mismatch`() {
        createInvoice(InvoiceStatus.VOIDED)
        val event = createEvent(InvoiceStatus.OPENED)
        worker.notify(event)

        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `customer with no email`() {
        createInvoice(InvoiceStatus.OPENED, email = "")
        val event = createEvent(InvoiceStatus.OPENED)
        worker.notify(event)

        verify(emailService, never()).send(any(), any())
    }
}
