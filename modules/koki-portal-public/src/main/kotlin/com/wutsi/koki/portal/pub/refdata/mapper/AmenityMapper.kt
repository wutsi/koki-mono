package com.wutsi.koki.portal.pub.refdata.mapper

import com.wutsi.koki.portal.pub.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.pub.refdata.model.AmenityModel
import com.wutsi.koki.refdata.dto.Amenity
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class AmenityMapper : TenantAwareMapper() {
    fun toAmenityModel(entity: Amenity): AmenityModel {
        val language = LocaleContextHolder.getLocale().language
        return AmenityModel(
            id = entity.id,
            active = entity.active,
            categoryId = entity.categoryId,
            icon = entity.icon,
            top = entity.top,
            name = when (language) {
                "fr" -> entity.nameFr ?: entity.name
                else -> entity.name
            },
        )
    }
}
