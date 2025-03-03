package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.GetTenantResponse
import com.wutsi.koki.tenant.dto.SearchTenantResponse
import com.wutsi.koki.tenant.server.mapper.TenantMapper
import com.wutsi.koki.tenant.server.service.TenantInitializer
import com.wutsi.koki.tenant.server.service.TenantService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/tenants")
class TenantEndpoints(
    private val service: TenantService,
    private val initializer: TenantInitializer,
    private val mapper: TenantMapper,
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): GetTenantResponse {
        return GetTenantResponse(
            tenant = mapper.toTenant(
                entity = service.get(id)
            )
        )
    }

    @GetMapping
    fun search(): SearchTenantResponse {
        val tenants = service.all()
        return SearchTenantResponse(
            tenants = tenants.map { tenant -> mapper.toTenant(tenant) }
        )
    }

    @PostMapping("/{id}/init")
    fun init(@PathVariable id: Long) {
        initializer.init(id)
    }
}
