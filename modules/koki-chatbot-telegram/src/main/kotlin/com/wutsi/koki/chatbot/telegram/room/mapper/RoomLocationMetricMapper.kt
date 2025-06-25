package com.wutsi.koki.chatbot.telegram.room.mapper

import com.wutsi.koki.chatbot.telegram.refdata.model.LocationModel
import com.wutsi.koki.chatbot.telegram.room.model.RoomLocationMetricModel
import com.wutsi.koki.room.dto.RoomLocationMetric
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
