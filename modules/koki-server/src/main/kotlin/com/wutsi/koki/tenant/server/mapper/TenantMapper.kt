package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.tenant.server.domain.TenantEntity
import org.springframework.stereotype.Service

@Service
class TenantMapper {
    fun toTenant(entity: TenantEntity) = Tenant(
        id = entity.id!!,
        name = entity.name,
        domainName = entity.domainName,
        locale = entity.locale,
        status = entity.status,
        currency = entity.currency,
        createdAt = entity.createdAt,
        dateFormat = entity.dateFormat,
        timeFormat = entity.timeFormat,
        currencySymbol = entity.currencySymbol,
        dateTimeFormat = entity.dateTimeFormat,
        monetaryFormat = entity.monetaryFormat,
        numberFormat = entity.numberFormat,
        logoUrl = entity.logoUrl?.ifEmpty { null },
        iconUrl = entity.iconUrl?.ifEmpty { null },
        portalUrl = entity.portalUrl,
        websiteUrl = entity.websiteUrl,
        moduleIds = entity.modules.map { module -> module.id },
    )
}
