package com.wutsi.koki.invoice.server.endpoint

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.CreateInvoiceResponse
import com.wutsi.koki.invoice.dto.GetInvoiceResponse
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.SearchInvoiceResponse
import com.wutsi.koki.invoice.dto.UpdateInvoiceStatusRequest
import com.wutsi.koki.invoice.dto.event.InvoiceStatusChangedEvent
import com.wutsi.koki.invoice.server.command.SendInvoiceCommand
import com.wutsi.koki.invoice.server.io.pdf.InvoicePdfExporter
import com.wutsi.koki.invoice.server.mapper.InvoiceMapper
import com.wutsi.koki.invoice.server.service.InvoiceService
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.TenantService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
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
    private val tenantService: TenantService,
    private val businessService: BusinessService,
    private val mapper: InvoiceMapper,
    private val invoicePdfExporter: InvoicePdfExporter,
    private val publisher: Publisher,
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
        val tenant = tenantService.get(tenantId)
        return GetInvoiceResponse(
            invoice = mapper.toInvoice(invoice, tenant)
        )
    }

    @GetMapping("/{id}/send")
    fun send(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        val invoice = service.get(id, tenantId)
        if (invoice.status == InvoiceStatus.DRAFT) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.INVOICE_BAD_STATUS,
                    parameter = Parameter(value = invoice.status)
                )
            )
        }

        publisher.publish(
            SendInvoiceCommand(
                tenantId = tenantId,
                invoiceId = id,
            )
        )
    }

    @PostMapping("/{id}/statuses")
    fun status(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateInvoiceStatusRequest,
    ) {
        // Update status
        service.status(id, request, tenantId)

        // Publish event
        publisher.publish(
            InvoiceStatusChangedEvent(
                tenantId = tenantId,
                invoiceId = id,
                status = request.status,
            )
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

    @GetMapping("/pdf/{tenant-id}.{invoice-id}.pdf")
    fun invoicePdf(
        @PathVariable("tenant-id") tenantId: Long,
        @PathVariable("invoice-id") id: Long,
        response: HttpServletResponse
    ) {
        val invoice = service.get(id, tenantId)
        if (invoice.status == InvoiceStatus.DRAFT) {
            throw NotFoundException(
                error = Error(
                    code = ErrorCode.INVOICE_BAD_STATUS,
                    parameter = Parameter(value = invoice.status)
                )
            )
        }

        val filename = "invoice-$tenantId.$id.pdf"
        response.contentType = "application/pdf"
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename(filename).build().toString()
        )
        val business = businessService.get(tenantId)
        invoicePdfExporter.export(invoice, business, response.outputStream)
    }
}
