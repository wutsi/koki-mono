package com.wutsi.koki.portal.payment.service

import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.portal.invoice.service.InvoiceService
import com.wutsi.koki.portal.payment.mapper.PaymentMapper
import com.wutsi.koki.portal.payment.model.TransactionModel
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.sdk.KokiPayments
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TransactionService(
    private val koki: KokiPayments,
    private val mapper: PaymentMapper,
    private val userService: UserService,
    private val invoiceService: InvoiceService,
) {
    fun transaction(
        id: String,
        fullGraph: Boolean = true,
    ): TransactionModel {
        val tx = koki.transaction(id).transaction
        val invoice = invoiceService.invoice(id = tx.invoiceId, fullGraph = false)

        val userIds = listOf(tx.createdById, tx.paymentMethod.cash?.collectedById).filterNotNull().distinct()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds,
                limit = userIds.size,
            ).associateBy { user -> user.id }
        }

        return mapper.toTransactionModel(
            entity = tx,
            invoices = mapOf(invoice.id to invoice),
            users = users
        )
    }

    fun transactions(
        ids: List<String> = emptyList(),
        invoiceId: Long? = null,
        types: List<TransactionType> = emptyList(),
        statuses: List<TransactionStatus> = emptyList(),
        createdAtFrom: Date? = null,
        createdAtTo: Date? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): List<TransactionModel> {
        val transactions = koki.transactions(
            ids = ids,
            invoiceId = invoiceId,
            types = types,
            statuses = statuses,
            createdAtFrom = createdAtFrom,
            createdAtTo = createdAtTo,
            limit = limit,
            offset = offset,
        ).transactions

        val invoiceIds = transactions.map { tx -> tx.invoiceId }.distinct()
        val invoices = if (invoiceIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            invoiceService.invoices(
                ids = invoiceIds,
                limit = invoiceIds.size
            ).associateBy { invoice -> invoice.id }
        }

        val userIds = transactions.mapNotNull { tx -> tx.createdById }.distinct()
        val users = if (userIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            userService.users(
                ids = userIds,
                limit = userIds.size,
            ).associateBy { user -> user.id }
        }

        return transactions.map { tx ->
            mapper.toTransactionModel(
                entity = tx,
                invoices = invoices,
                users = users
            )
        }
    }
}
