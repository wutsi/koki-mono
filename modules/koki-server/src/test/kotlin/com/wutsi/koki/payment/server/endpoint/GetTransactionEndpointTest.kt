package com.wutsi.koki.payment.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.payment.dto.GetTransactionResponse
import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.payment.dto.event.TransactionCompletedEvent
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.payment.server.service.PaymentGatewayException
import com.wutsi.koki.payment.server.service.stripe.StripeGatewayService
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/payment/GetTransactionEndpoint.sql"])
class GetTransactionEndpointTest : AuthorizationAwareEndpointTest() {
    @MockitoBean
    private lateinit var stripe: StripeGatewayService

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun `sync pending transaction`() {
        doAnswer { inv ->
            val tx = inv.getArgument<TransactionEntity>(0)
            tx.status = TransactionStatus.SUCCESSFUL
            tx
        }.whenever(stripe).sync(any())

        val response = rest.getForEntity("/v1/transactions/140?sync=true", GetTransactionResponse::class.java)

        val tx = response.body!!.transaction
        assertEquals(PaymentMethodType.CREDIT_CARD, tx.paymentMethodType)
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(TransactionStatus.SUCCESSFUL, tx.status)
        assertEquals(PaymentGateway.STRIPE, tx.gateway)
        assertEquals(500.0, tx.amount)
        assertEquals("CAD", tx.currency)
        assertEquals(null, tx.errorCode)
        assertEquals(null, tx.supplierErrorCode)
        assertEquals("credit-card PENDING", tx.description)

        assertEquals(null, tx.paymentMethod.interac)
        assertEquals(null, tx.paymentMethod.check)
        assertEquals(null, tx.paymentMethod.cash)

        val event = argumentCaptor<TransactionCompletedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(tx.id, event.firstValue.transactionId)
        assertEquals(1L, event.firstValue.tenantId)
        assertEquals(tx.status, event.firstValue.status)
    }

    @Test
    fun `sync pending transaction to error`() {
        val ex = PaymentGatewayException(
            errorCode = "1111",
            supplierErrorCode = "xxxx",
            message = "Error"
        )
        doThrow(ex).whenever(stripe).sync(any())

        val response = rest.getForEntity("/v1/transactions/141?sync=true", GetTransactionResponse::class.java)

        val tx = response.body!!.transaction
        assertEquals(PaymentMethodType.CREDIT_CARD, tx.paymentMethodType)
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(TransactionStatus.FAILED, tx.status)
        assertEquals(PaymentGateway.STRIPE, tx.gateway)
        assertEquals(500.0, tx.amount)
        assertEquals("CAD", tx.currency)
        assertEquals(ex.errorCode, tx.errorCode)
        assertEquals(ex.supplierErrorCode, tx.supplierErrorCode)
        assertEquals("credit-card PENDING", tx.description)

        assertEquals(null, tx.paymentMethod.interac)
        assertEquals(null, tx.paymentMethod.check)
        assertEquals(null, tx.paymentMethod.cash)

        val event = argumentCaptor<TransactionCompletedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(tx.id, event.firstValue.transactionId)
        assertEquals(1L, event.firstValue.tenantId)
        assertEquals(tx.status, event.firstValue.status)
    }

    @Test
    fun `sync successful transaction`() {
        val response = rest.getForEntity("/v1/transactions/142?sync=true", GetTransactionResponse::class.java)

        val tx = response.body!!.transaction
        assertEquals(PaymentMethodType.CREDIT_CARD, tx.paymentMethodType)
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(TransactionStatus.SUCCESSFUL, tx.status)
        assertEquals(PaymentGateway.STRIPE, tx.gateway)
        assertEquals(500.0, tx.amount)
        assertEquals("CAD", tx.currency)
        assertEquals(null, tx.errorCode)
        assertEquals(null, tx.supplierErrorCode)
        assertEquals("credit-card SUCCESSFUL", tx.description)

        assertEquals(null, tx.paymentMethod.interac)
        assertEquals(null, tx.paymentMethod.check)
        assertEquals(null, tx.paymentMethod.cash)

        verify(stripe, never()).sync(any())
        verify(publisher, never()).publish(any())
    }

    @Test
    fun `sync successful failed`() {
        val response = rest.getForEntity("/v1/transactions/143?sync=true", GetTransactionResponse::class.java)

        val tx = response.body!!.transaction
        assertEquals(PaymentMethodType.CREDIT_CARD, tx.paymentMethodType)
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(TransactionStatus.FAILED, tx.status)
        assertEquals(PaymentGateway.STRIPE, tx.gateway)
        assertEquals(500.0, tx.amount)
        assertEquals("CAD", tx.currency)
        assertEquals(null, tx.errorCode)
        assertEquals(null, tx.supplierErrorCode)
        assertEquals("credit-card FAILED", tx.description)

        assertEquals(null, tx.paymentMethod.interac)
        assertEquals(null, tx.paymentMethod.check)
        assertEquals(null, tx.paymentMethod.cash)

        verify(stripe, never()).sync(any())
        verify(publisher, never()).publish(any())
    }

    @Test
    fun cash() {
        val response = rest.getForEntity("/v1/transactions/110", GetTransactionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val tx = response.body!!.transaction
        assertEquals(PaymentMethodType.CASH, tx.paymentMethodType)
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(TransactionStatus.FAILED, tx.status)
        assertEquals(PaymentGateway.UNKNOWN, tx.gateway)
        assertEquals(500.0, tx.amount)
        assertEquals("CAD", tx.currency)
        assertEquals("1111", tx.errorCode)
        assertEquals("insufisant-funds", tx.supplierErrorCode)
        assertEquals("cash", tx.description)

        assertEquals(null, tx.paymentMethod.interac)
        assertEquals(null, tx.paymentMethod.check)
        assertEquals(555, tx.paymentMethod.cash?.collectedById)
        assertNotNull(tx.paymentMethod.cash?.collectedAt)
    }

    @Test
    fun `cash without payment method details`() {
        val response = rest.getForEntity("/v1/transactions/119", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun check() {
        val response = rest.getForEntity("/v1/transactions/120", GetTransactionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val tx = response.body!!.transaction
        assertEquals(PaymentMethodType.CHECK, tx.paymentMethodType)
        assertEquals(TransactionType.DONATION, tx.type)
        assertEquals(TransactionStatus.SUCCESSFUL, tx.status)
        assertEquals(PaymentGateway.UNKNOWN, tx.gateway)
        assertEquals(500.0, tx.amount)
        assertEquals("CAD", tx.currency)
        assertEquals(null, tx.errorCode)
        assertEquals(null, tx.supplierErrorCode)
        assertEquals("check", tx.description)

        assertEquals(null, tx.paymentMethod.interac)
        assertEquals(null, tx.paymentMethod.cash)
        assertEquals("1234", tx.paymentMethod.check?.checkNumber)
        assertEquals("TD Bank", tx.paymentMethod.check?.bankName)
        assertNotNull(tx.paymentMethod.check?.clearedAt)
    }

    @Test
    fun `check without payment method details`() {
        val response = rest.getForEntity("/v1/transactions/129", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun interac() {
        val response = rest.getForEntity("/v1/transactions/130", GetTransactionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val tx = response.body!!.transaction
        assertEquals(PaymentMethodType.INTERAC, tx.paymentMethodType)
        assertEquals(TransactionType.PAYMENT, tx.type)
        assertEquals(TransactionStatus.PENDING, tx.status)
        assertEquals(PaymentGateway.UNKNOWN, tx.gateway)
        assertEquals(500.0, tx.amount)
        assertEquals("CAD", tx.currency)
        assertEquals(null, tx.errorCode)
        assertEquals(null, tx.supplierErrorCode)
        assertEquals("interac", tx.description)

        assertEquals(null, tx.paymentMethod.check)
        assertEquals(null, tx.paymentMethod.cash)
        assertEquals("1234", tx.paymentMethod.interac?.referenceNumber)
        assertEquals("TD Bank", tx.paymentMethod.interac?.bankName)
        assertNotNull(tx.paymentMethod.interac?.sentAt)
        assertNotNull(tx.paymentMethod.interac?.clearedAt)
    }

    @Test
    fun `interac without payment method details`() {
        val response = rest.getForEntity("/v1/transactions/139", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.TRANSACTION_PAYMENT_METHOD_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `not found`() {
        val response = rest.getForEntity("/v1/transactions/9999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, response.body?.error?.code)
    }
}
