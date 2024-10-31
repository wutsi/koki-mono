package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.GetTenantResponse
import com.wutsi.koki.tenant.server.mapper.TenantMapper
import com.wutsi.koki.tenant.server.service.TenantService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GetTenantEndpoint(
    private val service: TenantService,
    private val mapper: TenantMapper,
) {
    @GetMapping("/v1/tenants/{id}")
    fun get(@PathVariable id: Long): GetTenantResponse =
        GetTenantResponse(
            tenant = mapper.toTenant(
                entity = service.get(id)
            )
        )
}
