package com.wutsi.koki.room.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UpdateRoomUnitRequest(
    val floor: Int = -1,
    @get:NotEmpty @get:Size(max = 10) val number: String = "",
    val status: RoomUnitStatus = RoomUnitStatus.UNKNOWN,
)
