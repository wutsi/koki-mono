package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.tax.dto.CreateTaxRequest
import com.wutsi.koki.tax.dto.CreateTaxResponse
import com.wutsi.koki.tax.dto.GetTaxResponse
import com.wutsi.koki.tax.dto.SearchTaxResponse
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.dto.UpdateTaxRequest
import com.wutsi.koki.tax.dto.UpdateTaxStatusRequest
import com.wutsi.koki.tax.server.mapper.TaxMapper
import com.wutsi.koki.tax.server.service.TaxService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/taxes")
class TaxEndpoints(
    private val service: TaxService,
    private val mapper: TaxMapper,
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetTaxResponse {
        val tax = service.get(id, tenantId)
        return GetTaxResponse(mapper.toTax(tax))
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "tax-type-id") taxTypeIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "account-id") accountIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "accountant-id") accountantIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "created-by-id") createdByIds: List<Long> = emptyList(),
        @RequestParam(required = false, name = "status") statuses: List<TaxStatus> = emptyList(),
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchTaxResponse {
        val taxes = service.search(
            tenantId = tenantId,
            ids = ids,
            taxTypeIds = taxTypeIds,
            accountIds = accountIds,
            accountantIds = accountantIds,
            createdByIds = createdByIds,
            statuses = statuses,
            limit = limit,
            offset = offset
        )
        return SearchTaxResponse(
            taxes = taxes.map { tax -> mapper.toTaxSummary(tax) }
        )
    }

    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateTaxRequest,
    ): CreateTaxResponse {
        val tax = service.create(request, tenantId)
        return CreateTaxResponse(tax.id!!)
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateTaxRequest,
    ) {
        service.update(id, request, tenantId)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        service.delete(id, tenantId)
    }

    @PostMapping("/{id}/status")
    fun status(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateTaxStatusRequest,
    ) {
        service.status(id, request, tenantId)
    }
}
