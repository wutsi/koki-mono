package com.wutsi.koki.room.server.service.validation

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.PublishRule
import jakarta.validation.ValidationException

class RoomMustHavePriceRule : PublishRule {
    override fun validate(room: RoomEntity) {
        if (!isAvailable(room.pricePerMonth) && !isAvailable(room.pricePerNight)) {
            throw ValidationException(ErrorCode.ROOM_PRICE_MISSING)
        }
    }

    private fun isAvailable(price: Double?): Boolean {
        return price != null && price > 0
    }
}
