package com.wutsi.koki.portal.room.model

import com.wutsi.koki.room.dto.RoomUnitStatus
import java.util.Date

data class RoomUnitModel(
    val id: Long = -1,
    val roomId: Long = -1,
    val floor: Int = 0,
    val floorText: String = "",
    val number: String = "",
    val status: RoomUnitStatus = RoomUnitStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
)
