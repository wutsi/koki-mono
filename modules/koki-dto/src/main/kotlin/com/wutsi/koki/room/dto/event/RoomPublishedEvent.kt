package com.wutsi.koki.room.dto.event

data class RoomPublishedEvent(
    val roomId: Long = -1,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis()
)
