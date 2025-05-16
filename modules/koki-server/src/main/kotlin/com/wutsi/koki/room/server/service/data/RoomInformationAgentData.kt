package com.wutsi.koki.room.server.service.data

data class RoomPublisherAgentData(
    val title: String? = null,
    val description: String? = null,
    val heroImageIndex: Integer? = null,
    val heroImageReason: String? = null,
    val amenityIds: List<Long>? = null,
    val valid: Boolean = false,
)
