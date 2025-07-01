package com.wutsi.koki.chatbot.messenger

import com.wutsi.koki.room.dto.RoomLocationMetric

object RoomFixtures {
    val metrics = RefDataFixtures.locations.reversed().map { location ->
        RoomLocationMetric(
            locationId = location.id,
            totalPublishedRentals = (100 * Math.random()).toInt()
        )
    }
}
