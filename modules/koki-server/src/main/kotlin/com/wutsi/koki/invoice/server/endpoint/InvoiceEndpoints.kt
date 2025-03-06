package com.wutsi.koki.invoice.server.endpoint

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.CreateInvoiceResponse
import com.wutsi.koki.invoice.dto.GetInvoiceResponse
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.SearchInvoiceResponse
import com.wutsi.koki.invoice.dto.UpdateInvoiceStatusRequest
import com.wutsi.koki.invoice.dto.event.InvoiceStatusChangedEvent
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.invoice.server.io.pdf.InvoicePdfExporter
import com.wutsi.koki.invoice.server.mapper.InvoiceMapper
import com.wutsi.koki.invoice.server.service.InvoiceService
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.security.server.service.SecurityService
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
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

@RestController
@RequestMapping("/v1/invoices")
class InvoiceEndpoints(
    private val service: InvoiceService,
    private val tenantService: TenantService,
    private val businessService: BusinessService,
    private val mapper: InvoiceMapper,
    private val pdfExporter: InvoicePdfExporter,
    private val fileService: FileService,
    private val publisher: Publisher,
    private val securityService: SecurityService,
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

    @PostMapping("/{id}/statuses")
    fun status(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateInvoiceStatusRequest,
    ) {
        // Update status
        val invoice = service.status(id, request, tenantId)

        // Create Invoice PDF
        if (request.status == InvoiceStatus.OPENED || request.status == InvoiceStatus.PAID || request.status == InvoiceStatus.VOIDED) {
            val file = pdfFile(id, tenantId)
            service.linkPdfFile(invoice, file)
        }

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
    fun pdf(
        @PathVariable("tenant-id") tenantId: Long,
        @PathVariable("invoice-id") id: Long,
        response: HttpServletResponse
    ) {
        response.contentType = "application/pdf"
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename("$tenantId.$id.pdf").build().toString()
        )
        try {
            pdf(
                id = id,
                tenantId = tenantId,
                output = response.outputStream
            )
        } catch (ex: NotFoundException) {
            response.contentType = "application/json"
            response.sendError(404, "Invoice not found")
        }
    }

    private fun pdf(id: Long, tenantId: Long, output: OutputStream): InvoiceEntity {
        val invoice = service.get(id, tenantId)
        val business = businessService.get(tenantId)
        pdfExporter.export(invoice, business, output)
        return invoice
    }

    private fun pdfFile(id: Long, tenantId: Long): FileEntity {
        val file = File.createTempFile("invoice-$id", ".pdf")
        try {
            // Create PDF
            val output = FileOutputStream(file)
            val invoice = output.use {
                pdf(id, tenantId, output)
            }

            // Store to the cloud
            val filename = "Invoice-${invoice.number}.pdf"
            val input = FileInputStream(file)
            val url = input.use {
                fileService.store(
                    filename = filename,
                    content = input,
                    contentType = "application/pdf",
                    contentLength = file.length(),
                    tenantId = invoice.tenantId,
                    ownerId = invoice.id,
                    ownerType = ObjectType.INVOICE,
                )
            }

            // Create the file
            return fileService.create(
                filename = filename,
                contentType = "application/pdf",
                contentLength = file.length(),
                userId = securityService.getCurrentUserIdOrNull(),
                ownerId = null,
                ownerType = null,
                tenantId = invoice.tenantId,
                url = url,
            )
        } finally {
            file.delete()
        }
    }
}
