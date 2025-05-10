package com.wutsi.koki.room.dto

data class SearchRoomUnitResponse(
    val roomUnits: List<RoomUnitSummary> = emptyList()
)
