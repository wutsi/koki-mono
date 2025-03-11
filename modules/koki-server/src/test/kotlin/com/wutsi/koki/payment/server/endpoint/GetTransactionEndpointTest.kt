package com.wutsi.koki.payment.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.payment.dto.GetTransactionResponse
import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Sql(value = ["/db/test/clean.sql", "/db/test/payment/GetTransactionEndpoint.sql"])
class GetTransactionEndpointTest : AuthorizationAwareEndpointTest() {
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

        assertEquals(null, tx.paymentMethod.interact)
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

        assertEquals(null, tx.paymentMethod.interact)
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
    fun interact() {
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
        assertEquals("interact", tx.description)

        assertEquals(null, tx.paymentMethod.check)
        assertEquals(null, tx.paymentMethod.cash)
        assertEquals("1234", tx.paymentMethod.interact?.referenceNumber)
        assertEquals("TD Bank", tx.paymentMethod.interact?.bankName)
        assertNotNull(tx.paymentMethod.interact?.sentAt)
        assertNotNull(tx.paymentMethod.interact?.clearedAt)
    }

    @Test
    fun `interact without payment method details`() {
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
