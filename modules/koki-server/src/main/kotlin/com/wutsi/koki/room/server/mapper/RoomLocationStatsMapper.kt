package com.wutsi.koki.room.server.mapper

import com.wutsi.koki.room.dto.RoomLocationMetric
import com.wutsi.koki.room.server.domain.RoomLocationMetricEntity
import org.springframework.stereotype.Service

@Service
class RoomLocationStatsMapper {
    fun toRoomLocationStats(entity: RoomLocationMetricEntity): RoomLocationMetric {
        return RoomLocationMetric(
            id = entity.id ?: -1,
            locationId = entity.location.id ?: -1,
            totalPublishedRentals = entity.totalPublishedRentals,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
