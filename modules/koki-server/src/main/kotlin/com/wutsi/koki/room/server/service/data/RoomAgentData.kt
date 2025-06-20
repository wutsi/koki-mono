package com.wutsi.koki.room.server.service.data

data class RoomAgentData(
    val title: String = "",
    val summary: String? = null,
    val description: String? = null,
    val titleFr: String? = null,
    val summaryFr: String? = null,
    val descriptionFr: String? = null,
    val heroImageIndex: Int = 0,
    val heroImageReason: String? = null,
    val numberOfBeds: Int = 0,
    val numberOfBedrooms: Int = 0,
    val numberOfBathrooms: Int = 0,
    val amenityIds: List<Long> = emptyList(),
    val valid: Boolean = false,
)
