package com.wutsi.koki.payment.server.endpoint

import com.ibm.icu.text.SimpleDateFormat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.PrepareCheckoutRequest
import com.wutsi.koki.payment.dto.PrepareCheckoutResponse
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.payment.server.dao.TransactionRepository
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.payment.server.service.PaymentGatewayException
import com.wutsi.koki.payment.server.service.stripe.StripeGatewayService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/payment/PrepareCheckoutEndpoint.sql"])
class PrepareCheckoutEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TransactionRepository

    @MockitoBean
    private lateinit var stripe: StripeGatewayService

    @Test
    fun checkout() {
        doAnswer { inv ->
            val tx = inv.getArgument<TransactionEntity>(0)
            tx.status = TransactionStatus.PENDING
            tx.supplierTransactionId = "STR-140394309"
            tx.supplierStatus = "opened"
            tx.checkoutUrl = "https://localhost:3209329/checkout"
            tx
        }.whenever(stripe).checkout(any())

        val request = PrepareCheckoutRequest(
            invoiceId = 100L,
            paymentMethodType = PaymentMethodType.CREDIT_CARD,
        )
        val response = rest.postForEntity("/v1/payments/checkout", request, PrepareCheckoutResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val transactionId = response.body!!.transactionId
        val tx = dao.findById(transactionId).get()
        assertEquals(TENANT_ID, tx.tenantId)
        assertEquals(TransactionType.PAYMENT, tx.type)
        assertEquals(request.paymentMethodType, tx.paymentMethodType)
        assertEquals(PaymentGateway.STRIPE, tx.gateway)
        assertEquals(TransactionStatus.PENDING, tx.status)
        assertEquals(request.invoiceId, tx.invoiceId)
        assertEquals("Sample description", tx.description)
        assertEquals("CAD", tx.currency)
        assertEquals(820.0, tx.amount)
        assertEquals(null, tx.errorCode)
        assertEquals(null, tx.supplierErrorCode)
        assertEquals("STR-140394309", tx.supplierTransactionId)
        assertEquals("opened", tx.supplierStatus)
        assertEquals("https://localhost:3209329/checkout", tx.checkoutUrl)
        assertEquals(USER_ID, tx.createdById)
        assertEquals(fmt.format(Date()), fmt.format(tx.createdAt))
        assertEquals(fmt.format(Date()), fmt.format(tx.modifiedAt))
    }

    @Test
    fun error() {
        val ex = PaymentGatewayException(
            errorCode = "1111",
            supplierErrorCode = "xxxx",
            message = "Error"
        )
        doThrow(ex).whenever(stripe).checkout(any())

        val request = PrepareCheckoutRequest(
            invoiceId = 100L,
            paymentMethodType = PaymentMethodType.CREDIT_CARD,
        )
        val response = rest.postForEntity("/v1/payments/checkout", request, PrepareCheckoutResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val transactionId = response.body!!.transactionId
        val tx = dao.findById(transactionId).get()
        assertEquals(TENANT_ID, tx.tenantId)
        assertEquals(TransactionType.PAYMENT, tx.type)
        assertEquals(request.paymentMethodType, tx.paymentMethodType)
        assertEquals(PaymentGateway.STRIPE, tx.gateway)
        assertEquals(TransactionStatus.FAILED, tx.status)
        assertEquals(request.invoiceId, tx.invoiceId)
        assertEquals("Sample description", tx.description)
        assertEquals("CAD", tx.currency)
        assertEquals(820.0, tx.amount)
        assertEquals(ex.errorCode, tx.errorCode)
        assertEquals(ex.supplierErrorCode, tx.supplierErrorCode)
        assertEquals(null, tx.supplierTransactionId)
        assertEquals(null, tx.supplierStatus)
        assertEquals(null, tx.checkoutUrl)
        assertEquals(USER_ID, tx.createdById)
        assertEquals(fmt.format(Date()), fmt.format(tx.createdAt))
        assertEquals(fmt.format(Date()), fmt.format(tx.modifiedAt))
    }

    @Test
    fun `gateway not supported`() {
        val request = PrepareCheckoutRequest(
            invoiceId = 100L,
            paymentMethodType = PaymentMethodType.PAYPAL,
        )
        val response = rest.postForEntity("/v1/payments/checkout", request, PrepareCheckoutResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val transactionId = response.body!!.transactionId
        val tx = dao.findById(transactionId).get()
        assertEquals(TENANT_ID, tx.tenantId)
        assertEquals(TransactionType.PAYMENT, tx.type)
        assertEquals(request.paymentMethodType, tx.paymentMethodType)
        assertEquals(PaymentGateway.UNKNOWN, tx.gateway)
        assertEquals(TransactionStatus.FAILED, tx.status)
        assertEquals(request.invoiceId, tx.invoiceId)
        assertEquals("Sample description", tx.description)
        assertEquals("CAD", tx.currency)
        assertEquals(820.0, tx.amount)
        assertEquals(ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_SUPPORTED, tx.errorCode)
        assertEquals(null, tx.supplierErrorCode)
        assertEquals(null, tx.supplierTransactionId)
        assertEquals(null, tx.supplierStatus)
        assertEquals(null, tx.checkoutUrl)
        assertEquals(USER_ID, tx.createdById)
        assertEquals(fmt.format(Date()), fmt.format(tx.createdAt))
        assertEquals(fmt.format(Date()), fmt.format(tx.modifiedAt))
    }

    @Test
    fun `gateway not enabled`() {
        val request = PrepareCheckoutRequest(
            invoiceId = 100L,
            paymentMethodType = PaymentMethodType.MOBILE,
        )
        val response = rest.postForEntity("/v1/payments/checkout", request, PrepareCheckoutResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val transactionId = response.body!!.transactionId
        val tx = dao.findById(transactionId).get()
        assertEquals(TENANT_ID, tx.tenantId)
        assertEquals(TransactionType.PAYMENT, tx.type)
        assertEquals(request.paymentMethodType, tx.paymentMethodType)
        assertEquals(PaymentGateway.UNKNOWN, tx.gateway)
        assertEquals(TransactionStatus.FAILED, tx.status)
        assertEquals(request.invoiceId, tx.invoiceId)
        assertEquals("Sample description", tx.description)
        assertEquals("CAD", tx.currency)
        assertEquals(820.0, tx.amount)
        assertEquals(ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_SUPPORTED, tx.errorCode)
        assertEquals(null, tx.supplierErrorCode)
        assertEquals(null, tx.supplierTransactionId)
        assertEquals(null, tx.supplierStatus)
        assertEquals(null, tx.checkoutUrl)
        assertEquals(USER_ID, tx.createdById)
        assertEquals(fmt.format(Date()), fmt.format(tx.createdAt))
        assertEquals(fmt.format(Date()), fmt.format(tx.modifiedAt))
    }
}
