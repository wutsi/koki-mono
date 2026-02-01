package com.wutsi.koki.place.dto.event

data class PlaceUpdatedEvent(
    val placeId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
