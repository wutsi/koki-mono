package com.wutsi.koki.invoice.server.endpoint

import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.CreateInvoiceResponse
import com.wutsi.koki.invoice.dto.GetInvoiceResponse
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.SearchInvoiceResponse
import com.wutsi.koki.invoice.server.mapper.InvoiceMapper
import com.wutsi.koki.invoice.server.service.InvoiceService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/invoices")
class InvoiceEndpoints(
    private val service: InvoiceService,
    private val mapper: InvoiceMapper,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateInvoiceRequest,
    ): CreateInvoiceResponse {
        val invoice = service.create(request, tenantId)
        return CreateInvoiceResponse(invoice.id!!)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetInvoiceResponse {
        val invoice = service.get(id, tenantId)
        return GetInvoiceResponse(
            invoice = mapper.toInvoice(invoice)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false) number: Long? = null,
        @RequestParam(required = false, name = "status") statuses: List<InvoiceStatus> = emptyList(),
        @RequestParam(required = false, name = "account-id") accountId: Long? = null,
        @RequestParam(required = false, name = "tax-id") taxId: Long? = null,
        @RequestParam(required = false, name = "order-id") orderId: Long? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchInvoiceResponse {
        val invoices = service.search(
            tenantId = tenantId,
            ids = ids,
            number = number,
            statuses = statuses,
            accountId = accountId,
            taxId = taxId,
            orderId = orderId,
            limit = limit,
            offset = offset,
        )
        return SearchInvoiceResponse(invoices = invoices.map { invoice -> mapper.toInvoiceSummary(invoice) })
    }
}
