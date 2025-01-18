package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.module.service.ModuleService
import com.wutsi.koki.portal.tenant.mapper.TenantMapper
import com.wutsi.koki.portal.tenant.model.TenantModel
import com.wutsi.koki.sdk.KokiTenants
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TenantService(
    private val koki: KokiTenants,
    private val mapper: TenantMapper,
    private val moduleService: ModuleService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TenantService::class.java)
    }

    private var all: List<TenantModel>? = null

    fun tenants(): List<TenantModel> {
        if (all == null) {
            val modules = moduleService.modules()
                .associateBy { module -> module.id }

            all = koki.tenants()
                .tenants
                .map { tenant ->
                    mapper.toTenantModel(
                        entity = tenant,
                        modules = modules
                    )
                }
            LOGGER.info("${all?.size} Tenant(s) loaded")
        }
        return all!!
    }
}
