package com.wutsi.koki.room.dto

import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Money
import java.util.Date

data class Room(
    val id: Long = -1,
    val type: RoomType = RoomType.UNKNOWN,
    val status: RoomStatus = RoomStatus.UNKNOWN,
    val title: String = "",
    val description: String? = null,
    val numberOfRooms: Int = -1,
    val numberOfBathrooms: Int = -1,
    val numberOfBeds: Int = -1,
    val maxGuests: Int = -1,
    val neighborhoodId: Long? = null,
    val address: Address? = null,
    val pricePerNight: Money = Money(),
    val checkinTime: String? = null,
    val checkoutTime: String? = null,
    val createdById: Long? = null,
    val createdAt: Date = Date(),
    val modifiedById: Long? = null,
    val modifiedAt: Date = Date(),
    val amenityIds: List<Long> = emptyList(),
)
