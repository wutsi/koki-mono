package com.wutsi.koki.portal.service

import com.wutsi.koki.portal.mapper.TenantMapper
import com.wutsi.koki.sdk.KokiTenants
import com.wutsi.koki.tenant.dto.TenantModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TenantService(
    private val koki: KokiTenants,
    private val mapper: TenantMapper,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TenantService::class.java)
    }

    private var all: List<TenantModel>? = null

    fun tenants(): List<TenantModel> {
        if (all == null) {
            all = koki.tenants()
                .tenants
                .map { tenant -> mapper.toTenantModel(tenant) }
            LOGGER.info("${all?.size} Tenant(s) loaded")
        }
        return all!!
    }
}
