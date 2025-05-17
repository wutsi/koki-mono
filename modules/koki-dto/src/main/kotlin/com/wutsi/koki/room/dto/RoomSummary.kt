package com.wutsi.koki.room.dto

import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Money

data class RoomSummary(
    val id: Long = -1,
    val heroImageId: Long? = null,
    val type: RoomType = RoomType.UNKNOWN,
    val status: RoomStatus = RoomStatus.UNKNOWN,
    val title: String = "",
    val summary: String? = null,
    val numberOfRooms: Int = -1,
    val numberOfBathrooms: Int = -1,
    val numberOfBeds: Int = -1,
    val maxGuests: Int = -1,
    val pricePerNight: Money = Money(),
    val neighborhoodId: Long? = null,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val address: Address? = null,
)
