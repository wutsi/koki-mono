package com.wutsi.koki.room.dto

data class SearchRoomResponse(
    val rooms: List<RoomSummary> = emptyList()
)
