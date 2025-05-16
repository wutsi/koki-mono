package com.wutsi.koki.room.server.service

import com.wutsi.koki.room.server.domain.RoomEntity
import jakarta.validation.ValidationException

class RoomPublisherValidator(private val rules: List<PublishRule>) {
    @Throws(ValidationException::class)
    fun validate(room: RoomEntity) {
        rules.forEach { rule -> rule.validate(room) }
    }
}
