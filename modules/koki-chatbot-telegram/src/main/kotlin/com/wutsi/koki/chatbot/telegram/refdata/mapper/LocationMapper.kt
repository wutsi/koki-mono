package com.wutsi.koki.chatbot.telegram.refdata.mapper

import com.wutsi.koki.chatbot.telegram.refdata.model.LocationModel
import com.wutsi.koki.refdata.dto.Location
import org.springframework.stereotype.Service

@Service
class LocationMapper {
    fun toLocationModel(entity: Location): LocationModel {
        return LocationModel(
            id = entity.id,
            name = entity.name,
            parentId = entity.parentId,
            type = entity.type,
            country = entity.country,
        )
    }
}
