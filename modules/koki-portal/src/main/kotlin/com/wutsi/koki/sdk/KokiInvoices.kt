package com.wutsi.koki.sdk

import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.CreateInvoiceResponse
import com.wutsi.koki.invoice.dto.GetInvoiceResponse
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.SearchInvoiceResponse
import com.wutsi.koki.invoice.dto.UpdateInvoiceStatusRequest
import org.springframework.web.client.RestTemplate

class KokiInvoices(
    private val urlBuilder: URLBuilder,
    private val tenantProvider: TenantProvider,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val INVOICE_PATH_PREFIX = "/v1/invoices"
    }

    fun create(request: CreateInvoiceRequest): CreateInvoiceResponse {
        val url = urlBuilder.build(INVOICE_PATH_PREFIX)
        return rest.postForEntity(url, request, CreateInvoiceResponse::class.java).body
    }

    fun invoice(id: Long): GetInvoiceResponse {
        val url = urlBuilder.build("$INVOICE_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetInvoiceResponse::class.java).body
    }

    fun invoices(
        ids: List<Long>,
        number: Long?,
        statuses: List<InvoiceStatus>,
        accountId: Long?,
        taxId: Long?,
        orderId: Long?,
        limit: Int,
        offset: Int,
    ): SearchInvoiceResponse {
        val url = urlBuilder.build(
            INVOICE_PATH_PREFIX,
            mapOf(
                "id" to ids,
                "number" to number,
                "status" to statuses,
                "account-id" to accountId,
                "tax-id" to taxId,
                "order-id" to orderId,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchInvoiceResponse::class.java).body
    }

    fun setStatus(id: Long, request: UpdateInvoiceStatusRequest) {
        val url = urlBuilder.build("$INVOICE_PATH_PREFIX/$id/statuses")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun url(id: Long): String {
        return urlBuilder.build("$INVOICE_PATH_PREFIX/pdf/${tenantProvider.id()}.$id.pdf")
    }

    fun send(id: Long) {
        val url = urlBuilder.build("$INVOICE_PATH_PREFIX/$id/send")
        rest.getForEntity(url, Any::class.java)
    }
}
