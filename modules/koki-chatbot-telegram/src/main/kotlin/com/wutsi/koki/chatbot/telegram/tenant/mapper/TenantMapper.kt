package com.wutsi.koki.chatbot.telegram.tenant.mapper

import com.wutsi.koki.chatbot.telegram.tenant.model.TenantModel
import com.wutsi.koki.tenant.dto.Tenant
import org.springframework.stereotype.Service

@Service
class TenantMapper {
    fun toTenantModel(
        entity: Tenant,
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
            portalUrl = entity.portalUrl,
            websiteUrl = entity.websiteUrl,
            clientPortalUrl = entity.clientPortalUrl,
            country = entity.country,
        )
    }
}
