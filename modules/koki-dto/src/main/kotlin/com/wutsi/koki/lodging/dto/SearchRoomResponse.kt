package com.wutsi.koki.lodging.dto

data class SearchRoomResponse(
    val rooms: List<RoomSummary> = emptyList()
)
