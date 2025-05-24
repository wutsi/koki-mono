package com.wutsi.koki.payment.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.payment.dto.CreateCashPaymentRequest
import com.wutsi.koki.payment.dto.CreatePaymentResponse
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.payment.dto.event.TransactionCompletedEvent
import com.wutsi.koki.payment.server.dao.PaymentMethodCashRepository
import com.wutsi.koki.payment.server.dao.TransactionRepository
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql"])
class CreateCashPaymentEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: TransactionRepository

    @Autowired
    private lateinit var cashDao: PaymentMethodCashRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun create() {
        val request = CreateCashPaymentRequest(
            invoiceId = 111L,
            description = "This is the description",
            collectedAt = Date(),
            collectedById = 111L,
            amount = 500.0,
            currency = "CAD",
        )
        val response = rest.postForEntity("/v1/payments/cash", request, CreatePaymentResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")
        val transactionId = response.body!!.transactionId
        val tx = dao.findById(transactionId).get()
        assertEquals(TENANT_ID, tx.tenantId)
        assertEquals(TransactionType.PAYMENT, tx.type)
        assertEquals(PaymentMethodType.CASH, tx.paymentMethodType)
        assertEquals(TransactionStatus.SUCCESSFUL, tx.status)
        assertEquals(request.invoiceId, tx.invoiceId)
        assertEquals(request.description, tx.description)
        assertEquals(request.currency, tx.currency)
        assertEquals(request.amount, tx.amount)
        assertEquals(null, tx.errorCode)
        assertEquals(null, tx.supplierErrorCode)
        assertEquals(USER_ID, tx.createdById)
        assertEquals(fmt.format(Date()), fmt.format(tx.createdAt))
        assertEquals(fmt.format(Date()), fmt.format(tx.modifiedAt))

        val paymentMethod = cashDao.findByTransactionId(transactionId)
        assertEquals(transactionId, paymentMethod?.transactionId)
        assertEquals(fmt.format(request.collectedAt), fmt.format(paymentMethod?.collectedAt))
        assertEquals(request.collectedById, paymentMethod?.collectedById)

        val event = argumentCaptor<TransactionCompletedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(tx.id, event.firstValue.transactionId)
        assertEquals(tx.tenantId, event.firstValue.tenantId)
        assertEquals(tx.status, event.firstValue.status)
    }
}
