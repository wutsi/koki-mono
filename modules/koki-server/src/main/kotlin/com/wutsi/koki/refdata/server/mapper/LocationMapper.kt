package com.wutsi.koki.refdata.server.mapper

import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.refdata.server.domain.LocationEntity
import org.springframework.stereotype.Service

@Service
class LocationMapper {
    fun toLocation(entity: LocationEntity): Location {
        return Location(
            id = entity.id!!,
            name = entity.name,
            country = entity.country,
            type = entity.type,
            parentId = entity.parentId,
            population = entity.population ?: 0L,
        )
    }
}
