package com.wutsi.koki.product.server.endpoint

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.price.server.service.PriceService
import com.wutsi.koki.product.dto.CreatePriceRequest
import com.wutsi.koki.product.dto.CreatePriceResponse
import com.wutsi.koki.product.dto.GetPriceResponse
import com.wutsi.koki.product.dto.SearchPriceResponse
import com.wutsi.koki.product.dto.UpdatePriceRequest
import com.wutsi.koki.product.server.mapper.PriceMapper
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
@RequestMapping("/v1/prices")
class PriceEndpoints(
    private val service: PriceService,
    private val mapper: PriceMapper,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreatePriceRequest,
    ): CreatePriceResponse {
        val price = service.create(request, tenantId)
        return CreatePriceResponse(price.id!!)
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdatePriceRequest,
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

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetPriceResponse {
        val price = service.get(id, tenantId)
        return GetPriceResponse(mapper.toPrice(price))
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "product-id") productIds: List<Long> = emptyList(),
        @RequestParam(required = false) currency: String? = null,
        @RequestParam(required = false, name = "account-type-id") accountTypeIds: List<Long> = emptyList(),

        @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        date: Date? = null,

        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchPriceResponse {
        val prices = service.search(
            tenantId = tenantId,
            ids = ids,
            productIds = productIds,
            accountTypeIds = accountTypeIds,
            currency = currency,
            date = date,
            active = active,
            limit = limit,
            offset = offset
        )
        return SearchPriceResponse(
            prices = prices.map { price -> mapper.toPriceSummary(price) }
        )
    }
}
