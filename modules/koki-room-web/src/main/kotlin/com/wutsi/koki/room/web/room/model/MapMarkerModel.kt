package com.wutsi.koki.room.web.location.model

data class MapMarkerModel(
    val id: Long = -1,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val price: String? = null,
)
