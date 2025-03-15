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
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

class InvoiceNotificationWorkerTest {
    private val registry = mock<NotificationConsumer>()
    private val configurationService = mock<ConfigurationService>()
    private val invoiceService = mock<InvoiceService>()
    private val businessService = mock<BusinessService>()
    private val fileService = mock<FileService>()
    private val tenantService = mock<TenantService>()
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
        tenantService = tenantService,
        logger = logger,
        portalUrl = "http://localhost:8081"
    )

    private val invoiceId = 111L
    private val tenant = TenantEntity(id = 555L, dateFormat = "dd/MM/yyyy")
    private val business = BusinessEntity(companyName = "Olive Inc", tenantId = tenant.id!!)

    val config = mapOf(
        ConfigurationName.INVOICE_EMAIL_ENABLED to "1",
        ConfigurationName.INVOICE_EMAIL_SUBJECT to "New Invoice #{{invoiceNumber}} from {{businessName}}",
        ConfigurationName.INVOICE_EMAIL_BODY to "You have a new invoice!",

        ConfigurationName.INVOICE_EMAIL_PAID_SUBJECT to "Thank you for your payment - Invoice #{{invoiceNumber}}",
        ConfigurationName.INVOICE_EMAIL_PAID_BODY to "Thank you!",

        ConfigurationName.PAYMENT_METHOD_BANK_ENABLED to "1",

        ConfigurationName.PAYMENT_METHOD_CASH_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CASH_INSTRUCTIONS to "Instruction for cash payment",

        ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CHECK_PAYEE to "Ray Inc.",
        ConfigurationName.PAYMENT_METHOD_CHECK_INSTRUCTIONS to "Instruction for check payment",

        ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_INTERAC_QUESTION to "Color of the bench",
        ConfigurationName.PAYMENT_METHOD_INTERAC_ANSWER to "blue",
        ConfigurationName.PAYMENT_METHOD_INTERAC_EMAIL to "ray@gmail.com",

        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_PHONE_NUMBER to "5147580111",
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY to "STRIPE",
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY to "SRP.1234567890",

        ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_PAYPAL_CLIENT_ID to "PP.1234567890",

        ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PHONE_NUMBER to "5147580100",
        ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY to "FLUTTERWAVE",
        ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY_FLUTTERWAVE_SECRET_KEY to "FLT.1234567890",
    )

    private val configurations = config.entries.map { entry ->
        ConfigurationEntity(name = entry.key, value = entry.value)
    }

    @BeforeEach
    fun setUp() {
        doReturn(url).whenever(fileService).store(
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()
        )
        doReturn(file).whenever(fileService).create(
            anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()
        )

        doReturn(tenant).whenever(tenantService).get(any())

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
            totalAmount = 500.0,
            amountDue = 500.0,
            currency = "CAD",
            tenantId = 111L,
            invoicedAt = DateUtils.addDays(Date(), -10),
            dueAt = Date(),
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
        assertEquals(config[ConfigurationName.INVOICE_EMAIL_SUBJECT], request.firstValue.subject)
        assertEquals(config[ConfigurationName.INVOICE_EMAIL_BODY], request.firstValue.body)
        assertEquals(listOf(file.id), request.firstValue.attachmentFileIds)
        assertEquals(invoice.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.INVOICE, request.firstValue.owner?.type)

        val fmt = SimpleDateFormat(tenant.dateFormat)
        assertEquals(business.companyName, request.firstValue.data["businessName"])
        assertEquals(invoice.number, request.firstValue.data["invoiceNumber"])
        assertEquals(fmt.format(invoice.invoicedAt), request.firstValue.data["invoiceDate"])
        assertEquals(fmt.format(invoice.dueAt), request.firstValue.data["invoiceDueDate"])
        assertEquals(false, request.firstValue.data["invoicePayUponReception"])
        assertEquals("500,00 \$ CA", request.firstValue.data["invoiceTotalAmount"])
        assertEquals("500,00 \$ CA", request.firstValue.data["invoiceAmountDue"])
        assertEquals("http://localhost:8081/checkout/${invoice.id}", request.firstValue.data["portalPaymentURL"])

        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_CASH_ENABLED],
            request.firstValue.data["paymentMethodCash"]
        )
        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_CASH_INSTRUCTIONS],
            request.firstValue.data["cashInstructions"]
        )

        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED],
            request.firstValue.data["paymentMethodCheck"]
        )
        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_CHECK_PAYEE],
            request.firstValue.data["checkPayTo"]
        )
        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_CHECK_INSTRUCTIONS],
            request.firstValue.data["checkInstructions"]
        )

        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED],
            request.firstValue.data["paymentMethodInterac"]
        )
        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_INTERAC_EMAIL],
            request.firstValue.data["interacEmail"]
        )
        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_INTERAC_QUESTION],
            request.firstValue.data["interacQuestion"]
        )
        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_INTERAC_ANSWER],
            request.firstValue.data["interacAnswer"]
        )

        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED],
            request.firstValue.data["paymentMethodPaypal"]
        )

        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED],
            request.firstValue.data["paymentMethodCreditCard"]
        )
        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_PHONE_NUMBER],
            request.firstValue.data["creditCardOfflinePhoneNumber"]
        )

        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED],
            request.firstValue.data["paymentMethodMobile"]
        )
        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PHONE_NUMBER],
            request.firstValue.data["mobileOfflinePhoneNumber"]
        )
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
        assertEquals(config[ConfigurationName.INVOICE_EMAIL_SUBJECT], request.firstValue.subject)
        assertEquals(config[ConfigurationName.INVOICE_EMAIL_BODY], request.firstValue.body)
        assertEquals(listOf(file.id), request.firstValue.attachmentFileIds)
        assertEquals(invoice.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.INVOICE, request.firstValue.owner?.type)
    }

    @Test
    fun `notify opened invoice - not enabled`() {
        doReturn(configurations.filter { config -> config.name != ConfigurationName.INVOICE_EMAIL_ENABLED }).whenever(
            configurationService
        ).search(anyOrNull(), anyOrNull(), anyOrNull())

        val invoice = createInvoice(InvoiceStatus.OPENED)
        val event = createEvent(invoice.status)
        worker.notify(event)

        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `notify opened invoice - no config`() {
        doReturn(configurations.filter { config -> config.name == ConfigurationName.INVOICE_EMAIL_ENABLED })
            .whenever(configurationService).search(anyOrNull(), anyOrNull(), anyOrNull())

        val invoice = createInvoice(InvoiceStatus.OPENED, customerAccountId = null)
        val event = createEvent(invoice.status)
        worker.notify(event)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(invoice.tenantId))

        assertEquals(invoice.customerEmail, request.firstValue.recipient.email)
        assertEquals(invoice.customerName, request.firstValue.recipient.displayName)
        assertEquals(invoice.customerAccountId, request.firstValue.recipient.id)
        assertEquals(ObjectType.UNKNOWN, request.firstValue.recipient.type)
        assertEquals(TenantInvoiceInitializer.INVOICE_SUBJECT, request.firstValue.subject)
        assertEquals("", request.firstValue.body)
        assertEquals(listOf(file.id), request.firstValue.attachmentFileIds)
        assertEquals(invoice.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.INVOICE, request.firstValue.owner?.type)
    }

    @Test
    fun `never notify paid invoice`() {
        val invoice = createInvoice(InvoiceStatus.PAID)
        val event = createEvent(invoice.status)
        worker.notify(event)

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService, never()).send(any(), any())
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
