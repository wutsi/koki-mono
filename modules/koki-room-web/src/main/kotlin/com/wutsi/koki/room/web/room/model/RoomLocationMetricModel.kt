package com.wutsi.koki.room.web.room.model

import com.wutsi.koki.room.web.refdata.model.LocationModel

data class RoomLocationMetricModel(
    val location: LocationModel = LocationModel(),
    val totalPublishedRentals: Int = 0,
)
