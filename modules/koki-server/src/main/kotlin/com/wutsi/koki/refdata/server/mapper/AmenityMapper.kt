package com.wutsi.koki.refdata.server.mapper

import com.wutsi.koki.refdata.dto.Amenity
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class AmenityMapper {
    fun toAmenity(entity: AmenityEntity): Amenity {
        val language = LocaleContextHolder.getLocale().language
        return Amenity(
            id = entity.id,
            active = entity.active,
            categoryId = entity.categoryId,
            name = when (language) {
                "fr" -> entity.nameFr ?: entity.name
                else -> entity.name
            },
            icon = entity.icon,
        )
    }
}
