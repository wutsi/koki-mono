package com.wutsi.koki.portal.client.tenant.service

import com.wutsi.koki.portal.client.module.service.ModuleService
import com.wutsi.koki.portal.client.tenant.mapper.TenantMapper
import com.wutsi.koki.portal.tenant.model.TenantModel
import com.wutsi.koki.sdk.KokiTenants
import org.springframework.stereotype.Service

@Service
class TenantService(
    private val koki: KokiTenants,
    private val mapper: TenantMapper,
    private val moduleService: ModuleService,
) {
    fun tenants(): List<TenantModel> {
        val modules = moduleService.modules()
            .associateBy { module -> module.id }

        return koki.tenants()
            .tenants
            .map { tenant ->
                mapper.toTenantModel(
                    entity = tenant,
                    modules = modules,
                )
            }
    }
}
