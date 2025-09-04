package com.wutsi.koki.portal.tenant.mapper

import com.wutsi.koki.portal.module.model.ModuleModel
import com.wutsi.koki.portal.refdata.mapper.RefDataMapper
import com.wutsi.koki.portal.refdata.model.JuridictionModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.tenant.model.BusinessModel
import com.wutsi.koki.portal.tenant.model.TenantModel
import com.wutsi.koki.portal.tenant.model.TypeModel
import com.wutsi.koki.tenant.dto.Business
import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.tenant.dto.Type
import com.wutsi.koki.tenant.dto.TypeSummary
import org.springframework.stereotype.Service
import kotlin.text.ifEmpty

@Service
class TenantMapper(
    private val refDataMapper: RefDataMapper
) {
    fun toTenantModel(entity: Tenant, modules: Map<Long, ModuleModel>): TenantModel {
        return TenantModel(
            id = entity.id,
            numberFormat = entity.numberFormat,
            monetaryFormat = entity.monetaryFormat,
            dateTimeFormat = entity.dateTimeFormat,
            dateFormat = entity.dateFormat,
            timeFormat = entity.timeFormat,
            currencySymbol = entity.currencySymbol,
            country = entity.country,
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
            modules = modules.values.filter { module ->
                entity.moduleIds.contains(module.id)
            },
            clientPortalUrl = entity.clientPortalUrl,
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

    fun toBusinessModel(
        entity: Business,
        juridictions: Map<Long, JuridictionModel>,
        locations: Map<Long, LocationModel>,
    ): BusinessModel {
        return BusinessModel(
            id = entity.id,
            companyName = entity.companyName,
            website = entity.website?.trim()?.ifEmpty { null },
            phone = entity.phone?.trim()?.ifEmpty { null },
            fax = entity.fax?.trim()?.ifEmpty { null },
            email = entity.email?.trim()?.ifEmpty { null },
            juridictions = entity.juridictionIds.mapNotNull { id -> juridictions[id] },
            address = entity.address?.let { address ->
                refDataMapper.toAddressModel(address, locations)
            },
        )
    }
}
