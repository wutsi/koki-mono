package com.wutsi.koki.room.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.PublishRule
import jakarta.validation.ValidationException

class RoomMustHavePriceRule : PublishRule {
    override fun validate(room: RoomEntity) {
        if (room.pricePerNight == null || room.pricePerNight == 0.0) {
            throw ValidationException(ErrorCode.ROOM_PRICE_MISSING)
        }
    }
}
