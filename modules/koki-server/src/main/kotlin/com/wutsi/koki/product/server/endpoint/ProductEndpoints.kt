package com.wutsi.koki.product.server.endpoint

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.product.dto.CreateProductRequest
import com.wutsi.koki.product.dto.CreateProductResponse
import com.wutsi.koki.product.dto.GetProductResponse
import com.wutsi.koki.product.dto.ProductType
import com.wutsi.koki.product.dto.SearchProductResponse
import com.wutsi.koki.product.dto.UpdateProductRequest
import com.wutsi.koki.product.server.mapper.ProductMapper
import com.wutsi.koki.product.server.service.ProductService
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
@RequestMapping("/v1/products")
class ProductEndpoints(
    private val service: ProductService,
    private val mapper: ProductMapper,
    private val taxProductService: TaxProductService,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateProductRequest,
    ): CreateProductResponse {
        val product = service.create(request, tenantId)
        return CreateProductResponse(product.id!!)
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateProductRequest,
    ) {
        service.update(id, request, tenantId)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        if (isUsed(id, tenantId)) {
            throw ConflictException(
                error = Error(code = ErrorCode.PRODUCT_IN_USE)
            )
        }
        service.delete(id, tenantId)
    }

    private fun isUsed(id: Long, tenantId: Long): Boolean {
        return taxProductService.search(tenantId = tenantId, productIds = listOf(id), limit = 1).isNotEmpty()
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetProductResponse {
        val product = service.get(id, tenantId)
        return GetProductResponse(
            product = mapper.toProduct(product)
        )
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "type") types: List<ProductType> = emptyList(),
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchProductResponse {
        val products = service.search(
            tenantId = tenantId,
            ids = ids,
            types = types,
            keyword = keyword,
            active = active,
            limit = limit,
            offset = offset
        )
        return SearchProductResponse(
            products = products.map { product -> mapper.toProductSummary(product) }
        )
    }
}
