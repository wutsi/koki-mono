package com.wutsi.koki.portal.client.tenant.mapper

import com.wutsi.koki.portal.client.module.model.ModuleModel
import com.wutsi.koki.portal.client.tenant.model.TypeModel
import com.wutsi.koki.portal.tenant.model.TenantModel
import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.tenant.dto.Type
import com.wutsi.koki.tenant.dto.TypeSummary
import org.springframework.stereotype.Service

@Service
class TenantMapper {
    fun toTenantModel(
        entity: Tenant,
        modules: Map<Long, ModuleModel>,
    ): TenantModel {
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
            clientPortalUrl = entity.clientPortalUrl,
            modules = modules.values.filter { module -> entity.moduleIds.contains(module.id) },
        )
    }

    fun toTypeModel(entity: Type): TypeModel {
        return TypeModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: entity.name,
            active = entity.active,
            objectType = entity.objectType,
            description = entity.description,
        )
    }

    fun toTypeModel(entity: TypeSummary): TypeModel {
        return TypeModel(
            id = entity.id,
            name = entity.name,
            title = entity.title ?: entity.name,
            objectType = entity.objectType,
            active = entity.active,
        )
    }
}
