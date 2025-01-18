package com.wutsi.koki.portal.tenant.mapper

import com.wutsi.koki.module.dto.Module
import com.wutsi.koki.module.dto.Permission
import com.wutsi.koki.portal.tenant.model.ModuleModel
import com.wutsi.koki.portal.tenant.model.PermissionModel
import com.wutsi.koki.portal.tenant.model.TenantModel
import com.wutsi.koki.tenant.dto.Tenant
import org.springframework.stereotype.Service
import kotlin.text.ifEmpty

@Service
class TenantMapper {
    fun toTenantModel(entity: Tenant): TenantModel {
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
            currency = entity.locale,
            domainName = entity.domainName,
            iconUrl = entity.iconUrl?.ifEmpty { null },
            logoUrl = entity.logoUrl?.ifEmpty { null },
            portalUrl = entity.portalUrl,
            websiteUrl = entity.websiteUrl,
        )
    }

    fun toModuleModel(entity: Module): ModuleModel {
        return ModuleModel(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            title = entity.title,
            tabUrl = entity.tabUrl,
            homeUrl = entity.homeUrl,
            settingsUrl = entity.settingsUrl,
        )
    }

    fun toPermissionModel(entity: Permission): PermissionModel {
        return PermissionModel(
            id = entity.id,
            name = entity.name,
            description = entity.description,
        )
    }
}
