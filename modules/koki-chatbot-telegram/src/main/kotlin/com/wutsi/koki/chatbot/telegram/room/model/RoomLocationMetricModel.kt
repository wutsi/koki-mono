package com.wutsi.koki.chatbot.telegram.room.model

import com.wutsi.koki.chatbot.telegram.refdata.model.LocationModel

data class RoomLocationMetricModel(
    val location: LocationModel = LocationModel(),
    val totalPublishedRentals: Int = 0,
)
