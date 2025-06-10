package com.wutsi.koki.room.dto

import java.util.Date

data class RoomLocationMetric(
    val id: Long = -1,
    val locationId: Long = -1,
    val totalPublishedRentals: Int = 0,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
