package com.wutsi.koki.sdk

import com.wutsi.koki.payment.dto.CreateCashPaymentRequest
import com.wutsi.koki.payment.dto.CreateCheckPaymentRequest
import com.wutsi.koki.payment.dto.CreateInteracPaymentRequest
import com.wutsi.koki.payment.dto.CreatePaymentResponse
import com.wutsi.koki.payment.dto.GetTransactionResponse
import com.wutsi.koki.payment.dto.SearchTransactionResponse
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.payment.dto.TransactionType
import org.springframework.web.client.RestTemplate
import java.text.SimpleDateFormat
import java.util.Date

class KokiPayments(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val TRANSACTION_PATH_PREFIX = "/v1/transactions"
        private const val PAYMENT_PATH_PREFIX = "/v1/payments"
    }

    fun cashPayment(request: CreateCashPaymentRequest): CreatePaymentResponse {
        val url = urlBuilder.build("$PAYMENT_PATH_PREFIX/cash")
        return rest.postForEntity(url, request, CreatePaymentResponse::class.java).body
    }

    fun checkPayment(request: CreateCheckPaymentRequest): CreatePaymentResponse {
        val url = urlBuilder.build("$PAYMENT_PATH_PREFIX/check")
        return rest.postForEntity(url, request, CreatePaymentResponse::class.java).body
    }

    fun interacPayment(request: CreateInteracPaymentRequest): CreatePaymentResponse {
        val url = urlBuilder.build("$PAYMENT_PATH_PREFIX/interac")
        return rest.postForEntity(url, request, CreatePaymentResponse::class.java).body
    }

    fun transaction(id: String): GetTransactionResponse {
        val url = urlBuilder.build("$TRANSACTION_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetTransactionResponse::class.java).body
    }

    fun transactions(
        ids: List<String>,
        invoiceId: Long?,
        types: List<TransactionType>,
        statuses: List<TransactionStatus>,
        createdAtFrom: Date?,
        createdAtTo: Date?,
        limit: Int,
        offset: Int,
    ): SearchTransactionResponse {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val url = urlBuilder.build(
            TRANSACTION_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "invoice-id" to invoiceId,
                "type" to types,
                "status" to statuses,
                "created-at-from" to createdAtFrom?.let { date -> fmt.format(date) },
                "created-at-to" to createdAtTo?.let { date -> fmt.format(date) },
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchTransactionResponse::class.java).body
    }
}
