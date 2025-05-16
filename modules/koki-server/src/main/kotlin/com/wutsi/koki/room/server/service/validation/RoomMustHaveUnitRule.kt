package com.wutsi.koki.room.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.room.dto.RoomUnitStatus
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.PublishRule
import com.wutsi.koki.room.server.service.RoomUnitService
import jakarta.validation.ValidationException

class RoomMustHaveUnitRule(private val roomUnitService: RoomUnitService) : PublishRule {
    override fun validate(room: RoomEntity) {
        val units = roomUnitService.search(
            tenantId = room.tenantId,
            roomId = room.id,
            status = RoomUnitStatus.AVAILABLE,
            limit = 1
        )
        if (units.isEmpty()) {
            throw ValidationException(ErrorCode.ROOM_UNIT_MISSING)
        }
    }
}
