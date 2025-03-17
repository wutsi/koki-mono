package com.wutsi.koki.payment.server.job

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.stripe.model.checkout.Session
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.event.TransactionCompletedEvent
import com.wutsi.koki.payment.server.dao.TransactionRepository
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.payment.server.service.stripe.StripeGatewayService
import com.wutsi.koki.platform.mq.Publisher
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/payment/SyncPendingTransaction.sql"])
class SyncPendingTransactionTest {
    @MockitoBean
    private lateinit var stripe: StripeGatewayService

    @MockitoBean
    private lateinit var publisher: Publisher

    @Autowired
    private lateinit var jobs: TransactionCronJobs

    @Autowired
    private lateinit var dao: TransactionRepository

    @Test
    fun run() {
        // GIVEN
        doAnswer { inv ->
            val tx = inv.arguments[0] as TransactionEntity
            tx.status = if (tx.id == "120") {
                TransactionStatus.SUCCESSFUL
            } else if (tx.id == "130") {
                TransactionStatus.FAILED
            } else {
                TransactionStatus.PENDING
            }
        }.whenever(stripe).sync(any())

        // WHEN
        jobs.pending()

        // THEN
        val tx100 = dao.findById("100").get()
        assertEquals(TransactionStatus.PENDING, tx100.status)

        val tx120 = dao.findById("120").get()
        assertEquals(TransactionStatus.SUCCESSFUL, tx120.status)

        val tx130 = dao.findById("130").get()
        assertEquals(TransactionStatus.FAILED, tx130.status)

        val tx140 = dao.findById("140").get()
        assertEquals(TransactionStatus.PENDING, tx140.status)

        val tx150 = dao.findById("150").get()
        assertEquals(TransactionStatus.PENDING, tx150.status)

        val event = argumentCaptor<TransactionCompletedEvent>()
        verify(publisher, times(2)).publish(event.capture())

        assertEquals(tx120.id, event.firstValue.transactionId)
        assertEquals(tx120.tenantId, event.firstValue.tenantId)
        assertEquals(tx120.status, event.firstValue.status)

        assertEquals(tx130.id, event.secondValue.transactionId)
        assertEquals(tx130.tenantId, event.secondValue.tenantId)
        assertEquals(tx130.status, event.secondValue.status)
    }

    private fun createStipeSession(status: String, paymentStatus: String) {
        val session = Session()
        session.status = status
        session.paymentStatus = paymentStatus
    }
}
