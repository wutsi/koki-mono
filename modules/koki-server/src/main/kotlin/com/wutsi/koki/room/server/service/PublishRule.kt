package com.wutsi.koki.room.server.service

import com.wutsi.koki.room.server.domain.RoomEntity
import jakarta.validation.ValidationException

interface PublishRule {
    @Throws(ValidationException::class)
    fun validate(room: RoomEntity)
}
