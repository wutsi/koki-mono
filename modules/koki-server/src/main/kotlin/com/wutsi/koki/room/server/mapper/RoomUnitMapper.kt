package com.wutsi.koki.room.server.mapper

import com.wutsi.koki.room.dto.RoomUnit
import com.wutsi.koki.room.dto.RoomUnitSummary
import com.wutsi.koki.room.server.domain.RoomUnitEntity
import org.springframework.stereotype.Service

@Service
class RoomUnitMapper {
    fun toRoomUnit(entity: RoomUnitEntity): RoomUnit {
        return RoomUnit(
            id = entity.id ?: -1,
            status = entity.status,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            createdById = entity.createdById,
            modifiedById = entity.createdById,
            floor = entity.floor,
            number = entity.number,
            roomId = entity.roomId,
        )
    }

    fun toRoomUnitSummary(entity: RoomUnitEntity): RoomUnitSummary {
        return RoomUnitSummary(
            id = entity.id ?: -1,
            status = entity.status,
            floor = entity.floor,
            number = entity.number,
            roomId = entity.roomId,
        )
    }
}
