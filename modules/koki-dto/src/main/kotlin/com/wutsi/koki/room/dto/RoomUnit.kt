package com.wutsi.koki.room.dto

import java.util.Date

data class RoomUnit(
    val id: Long = -1,
    val roomId: Long = -1,
    val floor: Int = 0,
    val number: String = "",
    val status: RoomUnitStatus = RoomUnitStatus.UNKNOWN,
    val createdById: Long? = null,
    val createdAt: Date = Date(),
    val modifiedById: Long? = null,
    val modifiedAt: Date = Date(),
)
