package com.wutsi.koki.lodging.dto

data class RoomSummary(
    val id: Long = -1,
    val type: RoomType = RoomType.UNKNOWN,
    val title: String = "",
    val numberOfRooms: Int? = null,
    val numberOfBathrooms: Int? = null,
    val numberOfBeds: Int? = null,
    val size: Int? = null,

    val ratings: Int? = null,
    val numberOfRatings: Int = 0,

    val amenityIds: List<Long> = emptyList(),
    val imageFileIds: List<Long> = emptyList(),
)
