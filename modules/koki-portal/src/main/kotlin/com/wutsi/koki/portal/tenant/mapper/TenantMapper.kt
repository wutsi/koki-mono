package com.wutsi.koki.portal.tenant.mapper

import com.wutsi.koki.portal.module.model.ModuleModel
import com.wutsi.koki.portal.tenant.model.TenantModel
import com.wutsi.koki.tenant.dto.Tenant
import org.springframework.stereotype.Service
import kotlin.text.ifEmpty

@Service
class TenantMapper {
    fun toTenantModel(entity: Tenant, modules: Map<Long, ModuleModel>): TenantModel {
        return TenantModel(
            id = entity.id,
            numberFormat = entity.numberFormat,
            monetaryFormat = entity.monetaryFormat,
            dateTimeFormat = entity.dateTimeFormat,
            dateFormat = entity.dateFormat,
            timeFormat = entity.timeFormat,
            currencySymbol = entity.currencySymbol,
            createdAt = entity.createdAt,
            status = entity.status,
            name = entity.name,
            locale = entity.locale,
            currency = entity.currency,
            domainName = entity.domainName,
            iconUrl = entity.iconUrl?.ifEmpty { null },
            logoUrl = entity.logoUrl?.ifEmpty { null },
            portalUrl = entity.portalUrl,
            websiteUrl = entity.websiteUrl,
            modules = entity.moduleIds.mapNotNull { id -> modules[id] },
        )
    }
}
