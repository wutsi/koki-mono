package com.wutsi.koki.tenant.server.mapper

import com.wutsi.koki.platform.core.image.Dimension
import com.wutsi.koki.platform.core.image.Focus
import com.wutsi.koki.platform.core.image.Format
import com.wutsi.koki.platform.core.image.ImageService
import com.wutsi.koki.platform.core.image.Transformation
import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.tenant.server.domain.TenantEntity
import org.springframework.stereotype.Service

@Service
class TenantMapper(
    private val imageService: ImageService,
) {
    fun toTenant(entity: TenantEntity): Tenant {
        val logoUrl = entity.logoUrl?.ifEmpty { null }
        val iconUrl = entity.iconUrl?.ifEmpty { null }
        return Tenant(
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
            logoUrl = logoUrl,
            logoTinyUrl = logoUrl?.let { url -> transform(url = url, width = null, height = 100) },
            iconUrl = iconUrl,
            iconTinyUrl = iconUrl?.let { url -> transform(url = url, width = 64, height = 64) },
            qrCodeIconUrl = entity.qrCodeIconUrl?.ifEmpty { null },
            portalUrl = entity.portalUrl,
            websiteUrl = entity.websiteUrl,
            moduleIds = entity.modules.map { module -> module.id },
            clientPortalUrl = entity.clientPortalUrl,
            country = entity.country,
        )
    }

    private fun transform(url: String, width: Int?, height: Int?, format: Format = Format.WEBP): String {
        return imageService.transform(
            url,
            Transformation(
                focus = Focus.AUTO,
                format = format,
                dimension = Dimension(width, height),
            )
        )
    }
}
