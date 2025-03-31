package com.wutsi.koki.invoice.server.service

import com.github.mustachejava.DefaultMustacheFactory
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
import com.wutsi.koki.notification.server.service.NotificationMQConsumer
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class InvoiceNotificationWorkerTest {
    private val registry = mock<NotificationMQConsumer>()
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
    )

    private val invoiceId = 111L
    private val tenant = TenantEntity(
        id = 555L,
        dateFormat = "dd/MM/yyyy",
        monetaryFormat = "C\$ #,###,##0.00",
        portalUrl = "http://localhost:8081",
    )
    private val business = BusinessEntity(companyName = "Olive Inc", tenantId = tenant.id!!)

    val config = mapOf(
        ConfigurationName.INVOICE_EMAIL_ENABLED to "1",
        ConfigurationName.INVOICE_EMAIL_SUBJECT to "New Invoice #{{invoiceNumber}} from {{businessName}}",
        ConfigurationName.INVOICE_EMAIL_BODY to "You have a new invoice!",

        ConfigurationName.PAYMENT_METHOD_BANK_ENABLED to "1",

        ConfigurationName.PAYMENT_METHOD_CASH_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CASH_INSTRUCTIONS to """
            Cash payment are done at our office at the address:
            3030 Linton
            Montreal, H7K 1L1
            Quebec, Canada

            Open hours: Monday-Friday, 9:00AM to 5:30PM
        """.trimIndent(),

        ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CHECK_PAYEE to "Ray Inc.",
        ConfigurationName.PAYMENT_METHOD_CHECK_INSTRUCTIONS to """
            You can send you check via mail at the following address:
            RAY INC
            3030 Linton
            Montreal, H7K 1L1
            Quebec, Canada
        """.trimIndent(),

        ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_INTERAC_QUESTION to "Color of the bench",
        ConfigurationName.PAYMENT_METHOD_INTERAC_ANSWER to "blue",
        ConfigurationName.PAYMENT_METHOD_INTERAC_EMAIL to "ray@gmail.com",

        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_PHONE_NUMBER to "5147580111",
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY to "STRIPE",
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY to "SRP.1234567890",

        ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_PAYPAL_CLIENT_ID to "PP.1234567890",

        ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_ENABLED to "1",
        ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PHONE_NUMBER to "5147580100",
        ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_ACCOUNT_NAME to "Ray Sponsible",
        ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PROVIDER to "MTN",
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
            tenantId = tenant.id!!,
            invoiceId = invoiceId,
            status = status,
        )
    }

    private fun createInvoice(
        status: InvoiceStatus, customerAccountId: Long? = 555L, email: String = "ray.sponsible@gmail.com"
    ): InvoiceEntity {
        val invoice = InvoiceEntity(
            id = invoiceId,
            paynowId = UUID.randomUUID().toString(),
            number = 1445L,
            customerAccountId = customerAccountId,
            customerEmail = email,
            customerName = "Ray Sponsible",
            status = status,
            totalAmount = 500.0,
            amountDue = 500.0,
            currency = "CAD",
            tenantId = tenant.id!!,
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
        assertEquals(invoice.customerName, request.firstValue.data["customerName"])
        assertEquals(business.companyName, request.firstValue.data["businessName"])
        assertEquals(invoice.number, request.firstValue.data["invoiceNumber"])
        assertEquals(fmt.format(invoice.invoicedAt), request.firstValue.data["invoiceDate"])
        assertEquals(fmt.format(invoice.dueAt), request.firstValue.data["invoiceDueDate"])
        assertEquals(false, request.firstValue.data["invoicePayUponReception"])
        assertEquals("C\$ 500.00", request.firstValue.data["invoiceTotalAmount"])
        assertEquals("C\$ 500.00", request.firstValue.data["invoiceAmountDue"])
        assertEquals(
            "${tenant.portalUrl}/paynow/${invoice.paynowId}.${invoice.id}",
            request.firstValue.data["paymentPortalUrl"]
        )

        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_CASH_ENABLED],
            request.firstValue.data["paymentMethodCash"]
        )
        assertEquals(
            worker.toHtml(config[ConfigurationName.PAYMENT_METHOD_CASH_INSTRUCTIONS]),
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
            worker.toHtml(config[ConfigurationName.PAYMENT_METHOD_CHECK_INSTRUCTIONS]),
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
            config[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_ENABLED],
            request.firstValue.data["creditCardOfflineEnabled"]
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
            config[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_ENABLED],
            request.firstValue.data["mobileOfflineEnabled"]
        )
        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PHONE_NUMBER],
            request.firstValue.data["mobileOfflinePhoneNumber"]
        )
        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_PROVIDER],
            request.firstValue.data["mobileOfflineProvider"]
        )
        assertEquals(
            config[ConfigurationName.PAYMENT_METHOD_MOBILE_OFFLINE_ACCOUNT_NAME],
            request.firstValue.data["mobileOfflineAccountName"]
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
        assertEquals(TenantInvoiceInitializer.EMAIL_SUBJECT, request.firstValue.subject)
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

    @Test
    fun `test email`() {
        val invoice = createInvoice(InvoiceStatus.OPENED)
        worker.notify(SendInvoiceCommand(invoiceId = invoiceId, tenantId = invoice.tenantId))

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), any())

        val body = IOUtils.toString(
            InvoiceNotificationWorker::class.java.getResourceAsStream(TenantInvoiceInitializer.EMAIL_BODY_PATH),
            "utf-8"
        )
        val template = MustacheTemplatingEngine(DefaultMustacheFactory())
        val xbody = template.apply(body, request.firstValue.data)

        println(xbody)
    }
}
