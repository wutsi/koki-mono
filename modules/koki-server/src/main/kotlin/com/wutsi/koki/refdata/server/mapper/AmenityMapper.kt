package com.wutsi.koki.refdata.server.mapper

import com.wutsi.koki.refdata.dto.Amenity
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import org.springframework.stereotype.Service

@Service
class AmenityMapper {
    fun toAmenity(entity: AmenityEntity): Amenity {
        return Amenity(
            id = entity.id,
            active = entity.active,
            categoryId = entity.categoryId,
            name = entity.name
        )
    }
}
