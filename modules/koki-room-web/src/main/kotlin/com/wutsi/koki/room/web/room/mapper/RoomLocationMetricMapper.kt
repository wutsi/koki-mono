package com.wutsi.koki.room.web.room.mapper

import com.wutsi.koki.room.dto.RoomLocationMetric
import com.wutsi.koki.room.web.refdata.model.LocationModel
import com.wutsi.koki.room.web.room.model.RoomLocationMetricModel
import org.springframework.stereotype.Service

@Service
class RoomLocationMetricMapper {
    fun toRoomLocationMetricModel(
        entity: RoomLocationMetric,
        location: LocationModel
    ): RoomLocationMetricModel {
        return RoomLocationMetricModel(
            location = location,
            totalPublishedRentals = entity.totalPublishedRentals,
        )
    }
}
