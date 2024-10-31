package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.tenant.server.domain.TenantEntity
import org.springframework.stereotype.Service

@Service
class TenantMapper {
    fun toTenant(entity: TenantEntity) = Tenant(
        id = entity.id ?: -1,
        name = entity.name,
        domainName = entity.domainName,
        locale = entity.locale,
        status = entity.status,
        currency = entity.currency,
        createdAt = entity.createdAt,
    )
}
