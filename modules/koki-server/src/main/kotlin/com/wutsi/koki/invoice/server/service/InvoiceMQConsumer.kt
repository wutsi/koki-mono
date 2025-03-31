package com.wutsi.koki.invoice.server.service

import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.event.InvoiceStatusChangedEvent
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.payment.dto.event.TransactionCompletedEvent
import com.wutsi.koki.payment.server.service.TransactionService
import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.stereotype.Service

@Service
class InvoiceMQConsumer(
    private val transactionService: TransactionService,
    private val invoiceService: InvoiceService,
    private val publisher: Publisher
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is TransactionCompletedEvent) {
            onTransactionCompleted(event)
        } else {
            return false
        }
        return true
    }

    fun onTransactionCompleted(event: TransactionCompletedEvent) {
        if (event.status == TransactionStatus.SUCCESSFUL) {
            val tx = transactionService.get(event.transactionId, event.tenantId)
            if (tx.type == TransactionType.PAYMENT) {
                // Previous status
                val previousInvoice = invoiceService.get(tx.invoiceId, event.tenantId)
                val previousStatus = previousInvoice.status

                // Next status
                val invoice = invoiceService.onPaymentReceived(tx.invoiceId, tx.tenantId)
                if (invoice.status == InvoiceStatus.PAID && previousStatus != InvoiceStatus.PAID) {
                    publisher.publish(
                        InvoiceStatusChangedEvent(
                            invoiceId = invoice.id!!,
                            tenantId = invoice.tenantId,
                            status = invoice.status,
                        )
                    )
                }
            }
        }
    }
}
