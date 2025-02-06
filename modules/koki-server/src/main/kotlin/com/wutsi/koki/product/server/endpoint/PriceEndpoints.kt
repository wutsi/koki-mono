package com.wutsi.koki.product.server.endpoint

import com.wutsi.koki.product.dto.CreatePriceRequest
import com.wutsi.koki.product.dto.CreatePriceResponse
import com.wutsi.koki.product.dto.GetPriceResponse
import com.wutsi.koki.product.dto.SearchPriceResponse
import com.wutsi.koki.product.dto.UpdatePriceRequest
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
import java.util.Date

@RestController
@RequestMapping("/v1/prices")
class PriceEndpoints {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreatePriceRequest,
    ): CreatePriceResponse {
        TODO()
    }

    @PostMapping("/{id}")
    fun update(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdatePriceRequest,
    ) {
        TODO()
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        TODO()
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetPriceResponse {
        TODO()
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "product-id") productIds: List<Long> = emptyList(),
        @RequestParam(required = false) currency: String? = null,
        @RequestParam(required = false) date: Date? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchPriceResponse {
        TODO()
    }
}
