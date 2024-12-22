package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.SearchTenantResponse
import com.wutsi.koki.tenant.server.mapper.TenantMapper
import com.wutsi.koki.tenant.server.service.TenantService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchTenantEndpoint(
    private val service: TenantService,
    private val mapper: TenantMapper,
) {
    @GetMapping("/v1/tenants")
    fun search(): SearchTenantResponse {
        return SearchTenantResponse(
            tenants = service.all().map { tenant -> mapper.toTenant(tenant) }
        )
    }
}
