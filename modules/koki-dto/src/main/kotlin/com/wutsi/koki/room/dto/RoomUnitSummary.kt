package com.wutsi.koki.room.dto

data class RoomUnitSummary(
    val id: Long = -1,
    val roomId: Long = -1,
    val floor: Int = 0,
    val number: String = "",
    val status: RoomUnitStatus = RoomUnitStatus.UNKNOWN,
)
