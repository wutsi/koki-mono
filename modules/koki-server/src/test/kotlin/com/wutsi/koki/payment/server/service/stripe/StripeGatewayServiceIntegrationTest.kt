package com.wutsi.koki.payment.server.service.stripe

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.domain.InvoiceItemEntity
import com.wutsi.koki.invoice.server.domain.InvoiceTaxEntity
import com.wutsi.koki.invoice.server.service.InvoiceService
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.refdata.server.domain.JuridictionEntity
import com.wutsi.koki.refdata.server.domain.SalesTaxEntity
import com.wutsi.koki.refdata.server.service.SalesTaxService
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StripeGatewayServiceIntegrationTest {
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

    private var transaction = TransactionEntity(
        id = UUID.randomUUID().toString(),
        currency = "CAD",
        status = TransactionStatus.PENDING,
    )
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
                currency = transaction.currency,
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
                description = "product 2",
                currency = transaction.currency,
            ),
        )
    )

    private val configs = mapOf(
        ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY to "sk_kWkNGw85HwgjC8gxK90sWlZuLchMP"
    )

    private val invoiceService = mock<InvoiceService>()
    private val salesTaxService = mock<SalesTaxService>()
    private val tenantService = mock<TenantService>()
    private val configurationService = mock<ConfigurationService>()
    private val stripeClientBuilder = StripeClientBuilder(configurationService)
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
            configs.map { entry -> ConfigurationEntity(name = entry.key, value = entry.value) }
        ).whenever(configurationService).search(any(), anyOrNull(), anyOrNull())

        doReturn(tenant).whenever(tenantService).get(any())
        doReturn(invoice).whenever(invoiceService).get(any(), any())
        doReturn(listOf(salesTax)).whenever(salesTaxService)
            .search(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun checkout() {
        // Checkout
        service.checkout(transaction)
        assertEquals(TransactionStatus.PENDING, transaction.status)
        assertNotNull(transaction.supplierTransactionId)
        assertEquals("open", transaction.supplierStatus)
        assertEquals(true, transaction.checkoutUrl?.startsWith("https://checkout.stripe.com/c/pay/"))

        // Sync
        service.sync(transaction)
        assertEquals(TransactionStatus.PENDING, transaction.status)
    }
}
