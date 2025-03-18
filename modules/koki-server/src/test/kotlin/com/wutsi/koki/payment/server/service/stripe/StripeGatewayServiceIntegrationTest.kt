package com.wutsi.koki.payment.server.service.stripe

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.stripe.StripeClient
import com.stripe.exception.ApiException
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import com.stripe.service.CheckoutService
import com.stripe.service.checkout.SessionService
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.domain.InvoiceItemEntity
import com.wutsi.koki.invoice.server.domain.InvoiceTaxEntity
import com.wutsi.koki.invoice.server.service.InvoiceService
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.payment.server.service.PaymentGatewayException
import com.wutsi.koki.refdata.server.domain.JuridictionEntity
import com.wutsi.koki.refdata.server.domain.SalesTaxEntity
import com.wutsi.koki.refdata.server.service.SalesTaxService
import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class StripeGatewayServiceTest {
    private val salesTax = SalesTaxEntity(
        id = 555L,
        rate = 10.0,
        name = "HST",
        juridiction = JuridictionEntity(id = 111L)
    )

    private val tenant = TenantEntity(
        id = 555L,
        dateFormat = "dd/MM/yyyy",
        monetaryFormat = "C\$ #,###,##0.00",
        portalUrl = "http://localhost:8081",
    )

    private var transaction = TransactionEntity()
    private val invoice = InvoiceEntity(
        id = transaction.invoiceId,
        tenantId = tenant.id!!,
        currency = "CAD",
        totalAmount = transaction.amount - 50.0,
        totalTaxAmount = 50.0,
        items = listOf(
            InvoiceItemEntity(
                unitPriceId = 111L,
                unitPrice = 100.0,
                quantity = 3,
                subTotal = 300.0,
                description = "product 1",
                taxes = listOf(
                    InvoiceTaxEntity(
                        id = 12093209L,
                        salesTaxId = salesTax.id!!,
                        amount = 30.0,
                        rate = 5.0,
                        currency = transaction.currency,
                    )
                )
            ),
            InvoiceItemEntity(
                unitPriceId = 111L,
                unitPrice = 200.0,
                quantity = 1,
                subTotal = 200.0,
                description = "product 2"
            ),
        )
    )

    private val configs = mapOf(
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY to "STRIPE",
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY to "sk_kWkNGw85HwgjC8gxK90sWlZuLchMP"
    )

//    private val stripe: StripeClient = mock<StripeClient>()
//    private val checkoutService = mock<CheckoutService>()
//    private val sessionService = mock<SessionService>()

    private val stripeClientBuilder = mock<StripeClientBuilder>()
    private val invoiceService = mock<InvoiceService>()
    private val salesTaxService = mock<SalesTaxService>()
    private val tenantService = mock<TenantService>()
    private val configurationService = mock<ConfigurationService>()
    private val service = StripeGatewayService(
        stripeClientBuilder,
        invoiceService,
        salesTaxService,
        tenantService,
        timeout = 35L,
    )

    @BeforeEach
    fun setUp() {
        transaction = TransactionEntity(
            id = UUID.randomUUID().toString(),
            tenantId = invoice.tenantId,
            invoiceId = invoice.id!!,
            amount = invoice.totalAmount,
            currency = invoice.currency,
            status = TransactionStatus.PENDING,
        )

        doReturn(
            SearchConfigurationResponse(
                configurations = configs.map { entry -> Configuration(name = entry.key, value = entry.value) }
            )
        ).whenever(configurationService).search(any(), anyOrNull(), anyOrNull())

//        doReturn(checkoutService).whenever(stripe).checkout()
//        doReturn(sessionService).whenever(checkoutService).sessions()
//        doReturn(stripe).whenever(stripeClientBuilder).build(any())

        doReturn(tenant).whenever(tenantService).get(any())
        doReturn(invoice).whenever(invoiceService).get(any(), any())
        doReturn(listOf(salesTax)).whenever(salesTaxService)
            .search(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun checkout() {
        val now = System.currentTimeMillis()

        val session = setupSession("open")

        service.checkout(transaction)

        assertEquals(TransactionStatus.PENDING, transaction.status)
        assertEquals(session.id, transaction.supplierTransactionId)
        assertEquals(session.status, transaction.supplierStatus)
        assertEquals(session.url, transaction.checkoutUrl)

        val params = argumentCaptor<SessionCreateParams>()
//        verify(sessionService).create(params.capture())
        assertEquals(transaction.id, params.firstValue.metadata["transaction_id"])
        assertEquals(transaction.tenantId.toString(), params.firstValue.metadata["tenant_id"])
        assertEquals(transaction.invoiceId.toString(), params.firstValue.metadata["invoice_id"])
        assertEquals(transaction.currency, params.firstValue.currency)
        assertEquals(true, (params.firstValue.expiresAt * 1000 - now) / 1000L <= 35 * 60L)
        assertEquals(SessionCreateParams.Mode.PAYMENT, params.firstValue.mode)
        assertEquals(
            "http://localhost:8081/checkout/confirmation?transaction-id=${transaction.id}",
            params.firstValue.successUrl
        )
        assertEquals(params.firstValue.successUrl, params.firstValue.cancelUrl)
        assertEquals(invoice.items.size + 1, params.firstValue.lineItems.size)
        assertEquals(invoice.items[0].quantity.toLong(), params.firstValue.lineItems[0].quantity)
        assertEquals(invoice.items[0].currency, params.firstValue.lineItems[0].priceData.currency)
        assertEquals((invoice.items[0].unitPrice * 100).toLong(), params.firstValue.lineItems[0].priceData.unitAmount)
        assertEquals(invoice.items[0].description, params.firstValue.lineItems[0].priceData.productData.name)

        assertEquals(invoice.items[1].quantity.toLong(), params.firstValue.lineItems[1].quantity)
        assertEquals(invoice.items[1].currency, params.firstValue.lineItems[1].priceData.currency)
        assertEquals((invoice.items[1].unitPrice * 100).toLong(), params.firstValue.lineItems[1].priceData.unitAmount)
        assertEquals(invoice.items[1].description, params.firstValue.lineItems[1].priceData.productData.name)

        assertEquals(1L, params.firstValue.lineItems[2].quantity)
        assertEquals(invoice.currency, params.firstValue.lineItems[2].priceData.currency)
        assertEquals((invoice.totalTaxAmount * 100).toLong(), params.firstValue.lineItems[2].priceData.unitAmount)
        assertEquals(salesTax.name, params.firstValue.lineItems[2].priceData.productData.name)
    }

    @Test
    fun `checkout error`() {
        val ex = ApiException("Failed", "3209320", "ERR-04394039", 409, null)
//        doThrow(ex).whenever(sessionService).create(any<SessionCreateParams>())

        val result = assertThrows<PaymentGatewayException> { service.checkout(transaction) }

        assertEquals(ErrorCode.TRANSACTION_PAYMENT_FAILED, result.errorCode)
        assertEquals(ex.code, result.supplierErrorCode)
        assertEquals(ex.message, result.message)
        assertEquals(ex, result.cause)
    }

    @Test
    fun `sync - PENDING to SUCCESS`() {
        val session = setupSession("complete", "paid")

        val tx = transaction.copy(supplierTransactionId = "11111")
        service.sync(tx)

        assertEquals(TransactionStatus.SUCCESSFUL, tx.status)
        assertEquals(session.status, tx.supplierStatus)
    }

    @Test
    fun `sync - PENDING to FAILED`() {
        val session = setupSession("expired")

        val tx = transaction.copy(supplierTransactionId = "11111")
        service.sync(tx)

        assertEquals(TransactionStatus.FAILED, tx.status)
        assertEquals(session.status, tx.supplierStatus)
    }

    @Test
    fun `sync - open`() {
        setupSession("open")

        val tx = transaction.copy(supplierTransactionId = "11111")
        service.sync(tx)

        assertEquals(TransactionStatus.PENDING, tx.status)
    }

    @Test
    fun `sync - unpaid`() {
        setupSession("complete", "unpaid")

        val tx = transaction.copy(supplierTransactionId = "11111")
        service.sync(tx)

        assertEquals(TransactionStatus.PENDING, tx.status)
    }

    @Test
    fun `sync - unsupported status`() {
        setupSession("xxx")

        val tx = transaction.copy(supplierTransactionId = "11111")
        service.sync(tx)

        assertEquals(TransactionStatus.PENDING, tx.status)
    }

    @Test
    fun `sync - error`() {
        val ex = ApiException("Failed", "3209320", "ERR-04394039", 409, null)
        doThrow(ex).whenever(sessionService).retrieve(any<String>())

        val tx = transaction.copy(supplierTransactionId = "11111")
        val result = assertThrows<PaymentGatewayException> { service.sync(tx) }

        assertEquals(ErrorCode.TRANSACTION_PAYMENT_FAILED, result.errorCode)
        assertEquals(ex.code, result.supplierErrorCode)
        assertEquals(ex.message, result.message)
        assertEquals(ex, result.cause)
    }

    private fun setupSession(status: String, paymentStatus: String? = null): Session {
        val session = Session()
        session.id = UUID.randomUUID().toString()
        session.url = "https://stripe.com/c/540954054099"
        session.status = status
        session.paymentStatus = paymentStatus

//        doReturn(session).whenever(sessionService).create(any<SessionCreateParams>())
//        doReturn(session).whenever(sessionService).retrieve(any<String>())
        return session
    }
}
