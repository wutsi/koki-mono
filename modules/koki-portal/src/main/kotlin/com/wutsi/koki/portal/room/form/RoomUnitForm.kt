package com.wutsi.koki.portal.room.form

import com.wutsi.koki.room.dto.RoomUnitStatus

data class RoomUnitForm(
    val roomId: Long = -1,
    val floor: Int = 0,
    val number: String = "",
    val status: RoomUnitStatus = RoomUnitStatus.UNKNOWN,
)
