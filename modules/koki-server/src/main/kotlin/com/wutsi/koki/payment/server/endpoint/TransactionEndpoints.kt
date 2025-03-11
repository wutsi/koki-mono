package com.wutsi.koki.payment.server.endpoint

import com.wutsi.koki.payment.dto.GetTransactionResponse
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.SearchTransactionResponse
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import com.wutsi.koki.payment.server.mapper.TransactionMapper
import com.wutsi.koki.payment.server.service.PaymentService
import com.wutsi.koki.payment.server.service.TransactionService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
@RequestMapping("/v1/transactions")
class TransactionEndpoints(
    private val service: TransactionService,
    private val paymentService: PaymentService,
    private val mapper: TransactionMapper,
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: String
    ): GetTransactionResponse {
        val tx = service.get(id, tenantId)
        return GetTransactionResponse(
            transaction = mapper.toTransaction(
                entity = tx,
                cash = if (tx.paymentMethodType == PaymentMethodType.CASH) {
                    paymentService.getCashByTransactionId(id)
                } else {
                    null
                },
                check = if (tx.paymentMethodType == PaymentMethodType.CHECK) {
                    paymentService.getCheckByTransactionId(id)
                } else {
                    null
                },
                interact = if (tx.paymentMethodType == PaymentMethodType.INTERAC) {
                    paymentService.getInteracByTransactionId(id)
                } else {
                    null
                },
            )
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<String> = emptyList(),
        @RequestParam(required = false, name = "invoice-id") invoiceId: Long? = null,
        @RequestParam(required = false, name = "type") types: List<TransactionType> = emptyList(),
        @RequestParam(required = false, name = "status") statuses: List<TransactionStatus> = emptyList(),

        @RequestParam(required = false, name = "created-at-from")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        createdAtFrom: Date? = null,

        @RequestParam(required = false, name = "created-at-to")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        createdAtTo: Date? = null,

        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchTransactionResponse {
        val transactions = service.search(
            tenantId = tenantId,
            ids = ids,
            invoiceId = invoiceId,
            types = types,
            statuses = statuses,
            createdAtFrom = createdAtFrom,
            createdAtTo = createdAtTo,
            limit = limit,
            offset = offset
        )
        return SearchTransactionResponse(
            transactions = transactions.map { tx -> mapper.toTransactionSummary(tx) }
        )
    }
}
