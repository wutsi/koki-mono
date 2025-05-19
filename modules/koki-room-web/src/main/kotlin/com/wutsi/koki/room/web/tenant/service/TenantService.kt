package com.wutsi.koki.room.web.tenant.service

import com.wutsi.koki.room.web.tenant.mapper.TenantMapper
import com.wutsi.koki.room.web.tenant.model.TenantModel
import com.wutsi.koki.sdk.KokiTenants
import org.springframework.stereotype.Service

@Service
class TenantService(
    private val koki: KokiTenants,
    private val mapper: TenantMapper,
) {
    fun tenants(): List<TenantModel> {
        return koki.tenants()
            .tenants
            .map { tenant ->
                mapper.toTenantModel(
                    entity = tenant,
                )
            }
    }
}
