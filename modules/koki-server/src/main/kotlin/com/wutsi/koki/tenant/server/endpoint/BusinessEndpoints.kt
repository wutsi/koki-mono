package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.tenant.dto.GetBusinessResponse
import com.wutsi.koki.tenant.dto.SaveBusinessRequest
import com.wutsi.koki.tenant.server.service.BusinessService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/businesses")
class BusinessEndpoints(
    private val service: BusinessService,
) {
    @GetMapping
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
    ): GetBusinessResponse {
        TODO()
    }

    @PostMapping
    fun save(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: SaveBusinessRequest,
    ) {
        service.save(request, tenantId)
    }
}
