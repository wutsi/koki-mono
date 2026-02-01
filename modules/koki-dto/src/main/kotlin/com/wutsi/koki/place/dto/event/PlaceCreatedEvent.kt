package com.wutsi.koki.place.dto.event

data class PlaceCreatedEvent(
    val placeId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
