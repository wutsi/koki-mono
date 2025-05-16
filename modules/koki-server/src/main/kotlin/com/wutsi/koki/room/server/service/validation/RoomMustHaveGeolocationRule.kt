package com.wutsi.koki.room.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.PublishRule
import jakarta.validation.ValidationException

class RoomMustHaveGeolocationRule : PublishRule {
    override fun validate(room: RoomEntity) {
        if (room.longitude == null || room.latitude == null) {
            throw ValidationException(ErrorCode.ROOM_GEOLOCATION_MISSING)
        }
    }
}
