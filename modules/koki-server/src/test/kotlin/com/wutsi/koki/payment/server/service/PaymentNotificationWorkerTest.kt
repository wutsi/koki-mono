package com.wutsi.koki.payment.server.service

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
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.service.InvoiceService
import com.wutsi.koki.notification.server.service.NotificationConsumer
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.event.TransactionCompletedEvent
import com.wutsi.koki.payment.server.domain.TransactionEntity
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
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.context.support.ResourceBundleMessageSource
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

class PaymentNotificationWorkerTest {
    private val registry = mock<NotificationConsumer>()
    private val configurationService = mock<ConfigurationService>()
    private val invoiceService = mock<InvoiceService>()
    private val businessService = mock<BusinessService>()
    private val transactionService = mock<TransactionService>()
    private val tenantService = mock<TenantService>()
    private val logger = DefaultKVLogger()
    private val emailService = mock<EmailService>()
    private val worker = PaymentNotificationWorker(
        registry = registry,
        configurationService = configurationService,
        invoiceService = invoiceService,
        businessService = businessService,
        transactionService = transactionService,
        tenantService = tenantService,
        emailService = emailService,
        messages = createMessageSource(),
        logger = logger,
    )

    private val transactionId = "fdoifodi-fd"
    private val tenant = TenantEntity(id = 555L, dateFormat = "dd/MM/yyyy", monetaryFormat = "C\$ #,###,##0.00")
    private val business = BusinessEntity(companyName = "Olive Inc", tenantId = tenant.id!!)
    private val invoice = InvoiceEntity(
        id = 111L,
        number = 1445L,
        customerAccountId = 11L,
        customerEmail = "ray.sponsible2gmail.com",
        customerName = "Ray Sponsible",
        tenantId = tenant.id!!,
        currency = "CAD",
        totalAmount = 500.00,
    )

    val config = mapOf(
        ConfigurationName.PAYMENT_EMAIL_ENABLED to "1",
        ConfigurationName.PAYMENT_EMAIL_SUBJECT to "Thank you for your payment",
        ConfigurationName.PAYMENT_EMAIL_BODY to "Thank you!",
    )
    private val configurations = config.entries.map { entry ->
        ConfigurationEntity(name = entry.key, value = entry.value)
    }

    @BeforeEach
    fun setUp() {
        doReturn(tenant).whenever(tenantService).get(any())

        doReturn(business).whenever(businessService).get(any())

        doReturn(configurations).whenever(configurationService).search(anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(invoice).whenever(invoiceService).get(any(), any())
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
    fun `succesful payment`() {
        val tx = createTransaction(TransactionStatus.SUCCESSFUL)
        worker.notify(createEvent(TransactionStatus.SUCCESSFUL))

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), eq(invoice.tenantId))

        assertEquals(invoice.customerEmail, request.firstValue.recipient.email)
        assertEquals(invoice.customerName, request.firstValue.recipient.displayName)
        assertEquals(invoice.customerAccountId, request.firstValue.recipient.id)
        assertEquals(ObjectType.ACCOUNT, request.firstValue.recipient.type)
        assertEquals(config[ConfigurationName.PAYMENT_EMAIL_SUBJECT], request.firstValue.subject)
        assertEquals(config[ConfigurationName.PAYMENT_EMAIL_BODY], request.firstValue.body)
        assertEquals(emptyList(), request.firstValue.attachmentFileIds)
        assertEquals(invoice.id, request.firstValue.owner?.id)
        assertEquals(ObjectType.INVOICE, request.firstValue.owner?.type)

        val fmt = SimpleDateFormat(tenant.dateFormat)
        assertEquals(invoice.customerName, request.firstValue.data["customerName"])
        assertEquals(business.companyName, request.firstValue.data["businessName"])
        assertEquals(invoice.number, request.firstValue.data["invoiceNumber"])
        assertEquals(fmt.format(tx.createdAt), request.firstValue.data["paymentDate"])
        assertEquals("C\$ 500.00", request.firstValue.data["paymentAmount"])
        assertEquals("Credit Card", request.firstValue.data["paymentMethod"])
    }

    @Test
    fun `failed payment`() {
        worker.notify(createEvent(TransactionStatus.FAILED))

        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `pending payment`() {
        worker.notify(createEvent(TransactionStatus.PENDING))

        verify(emailService, never()).send(any(), any())
    }

    @Test
    fun `test email`() {
        createTransaction(TransactionStatus.SUCCESSFUL)
        worker.notify(createEvent(TransactionStatus.SUCCESSFUL))

        val request = argumentCaptor<SendEmailRequest>()
        verify(emailService).send(request.capture(), any())

        val body = IOUtils.toString(
            PaymentNotificationWorker::class.java.getResourceAsStream(TenantPaymentInitializer.EMAIL_BODY_PATH),
            "utf-8"
        )
        val template = MustacheTemplatingEngine(DefaultMustacheFactory())
        val xbody = template.apply(body, request.firstValue.data)

        println(xbody)
    }

    private fun createEvent(status: TransactionStatus): TransactionCompletedEvent {
        return TransactionCompletedEvent(
            tenantId = tenant.id!!,
            transactionId = transactionId,
            status = status,
        )
    }

    private fun createTransaction(status: TransactionStatus): TransactionEntity {
        val tx = TransactionEntity(
            id = transactionId,
            invoiceId = invoice.id!!,
            amount = invoice.totalAmount,
            currency = invoice.currency,
            status = status,
            createdAt = DateUtils.addDays(Date(), -1),
            paymentMethodType = PaymentMethodType.CREDIT_CARD,
        )
        doReturn(tx).whenever(transactionService).get(any(), any())
        return tx
    }

    fun createMessageSource(): ResourceBundleMessageSource {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasename("messages")
        return messageSource
    }
}
