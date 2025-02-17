package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.tax.dto.CreateTaxProductRequest
import com.wutsi.koki.tax.dto.CreateTaxProductResponse
import com.wutsi.koki.tax.dto.GetTaxProductResponse
import com.wutsi.koki.tax.dto.SearchTaxProductResponse
import com.wutsi.koki.tax.dto.UpdateTaxProductRequest
import com.wutsi.koki.tax.server.mapper.TaxProductMapper
import com.wutsi.koki.tax.server.service.TaxProductService
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
@RequestMapping("/v1/tax-products")
class TaxProductEndpoints(
    private val service: TaxProductService,
    private val mapper: TaxProductMapper,
) {
    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetTaxProductResponse {
        val entity = service.get(id, tenantId)
        return GetTaxProductResponse(mapper.toTaxProduct(entity))
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(name = "tax-id") taxId: Long,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchTaxProductResponse {
        val entities = service.search(
            tenantId = tenantId,
            taxId = taxId,
            limit = limit,
            offset = offset
        )
        return SearchTaxProductResponse(entities.map { entity -> mapper.toTaxProduct(entity) })
    }

    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateTaxProductRequest,
    ): CreateTaxProductResponse {
        val taxProducts = service.create(request, tenantId)
        return CreateTaxProductResponse(taxProducts.mapNotNull { taxProduct -> taxProduct.id })
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateTaxProductRequest,
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
}
