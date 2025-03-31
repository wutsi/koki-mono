package com.wutsi.koki.invoice.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.event.InvoiceStatusChangedEvent
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.payment.dto.event.TransactionCompletedEvent
import com.wutsi.koki.payment.server.domain.TransactionEntity
import com.wutsi.koki.payment.server.service.TransactionService
import com.wutsi.koki.platform.mq.Publisher
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class InvoiceMQConsumerTest {
    private val transactionService = mock<TransactionService>()
    private val invoiceService = mock<InvoiceService>()
    private val publisher = mock<Publisher>()
    private val consumer = InvoiceMQConsumer(transactionService, invoiceService, publisher)

    private val transactionId = "1111"
    private val invoiceId = 10439L
    private val tenantId = 111L

    @Test
    fun payment() {
        createTransaction(TransactionStatus.SUCCESSFUL)
        val invoice = createInvoice(InvoiceStatus.OPENED)
        doReturn(invoice).whenever(invoiceService).get(any(), any())
        doReturn(invoice.copy(status = InvoiceStatus.PAID)).whenever(invoiceService).onPaymentReceived(any(), any())

        consumer.consume(createEvent(TransactionStatus.SUCCESSFUL))

        verify(invoiceService).onPaymentReceived(invoiceId, tenantId)

        val evt = argumentCaptor<InvoiceStatusChangedEvent>()
        verify(publisher).publish(evt.capture())
        assertEquals(invoiceId, evt.firstValue.invoiceId)
        assertEquals(tenantId, evt.firstValue.tenantId)
        assertEquals(InvoiceStatus.PAID, evt.firstValue.status)
    }

    @Test
    fun `partial payment`() {
        createTransaction(TransactionStatus.SUCCESSFUL)
        val invoice = createInvoice(InvoiceStatus.OPENED)
        doReturn(invoice).whenever(invoiceService).get(any(), any())
        doReturn(invoice).whenever(invoiceService).onPaymentReceived(any(), any())

        consumer.consume(createEvent(TransactionStatus.SUCCESSFUL))

        verify(invoiceService).onPaymentReceived(invoiceId, tenantId)

        verify(publisher, never()).publish(any())
    }

    @Test
    fun `pending payment`() {
        createTransaction(TransactionStatus.PENDING)
        val invoice = createInvoice(InvoiceStatus.OPENED)
        doReturn(invoice).whenever(invoiceService).get(any(), any())

        consumer.consume(createEvent(TransactionStatus.PENDING))

        verify(invoiceService, never()).onPaymentReceived(invoiceId, tenantId)
        verify(publisher, never()).publish(any())
    }

    @Test
    fun `failed payment`() {
        createTransaction(TransactionStatus.PENDING)
        val invoice = createInvoice(InvoiceStatus.OPENED)
        doReturn(invoice).whenever(invoiceService).get(any(), any())

        consumer.consume(createEvent(TransactionStatus.FAILED))

        verify(invoiceService, never()).onPaymentReceived(invoiceId, tenantId)
        verify(publisher, never()).publish(any())
    }

    private fun createEvent(status: TransactionStatus): TransactionCompletedEvent {
        return TransactionCompletedEvent(
            transactionId = transactionId,
            tenantId = tenantId,
            status = status,
        )
    }

    private fun createInvoice(status: InvoiceStatus): InvoiceEntity {
        val invoice = InvoiceEntity(
            id = invoiceId,
            tenantId = tenantId,
            status = status
        )
        doReturn(invoice).whenever(invoiceService).get(any(), any())
        return invoice
    }

    private fun createTransaction(status: TransactionStatus): TransactionEntity {
        val tx = TransactionEntity(
            id = "43049-3409403",
            tenantId = tenantId,
            invoiceId = invoiceId,
            status = status,
            type = TransactionType.PAYMENT,
        )
        doReturn(tx).whenever(transactionService).get(any(), any())
        return tx
    }
}
