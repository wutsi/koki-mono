package com.wutsi.koki.portal.room.form

import com.wutsi.koki.room.dto.RoomType

data class RoomForm(
    val type: RoomType = RoomType.UNKNOWN,
    val title: String = "",
    val numberOfRooms: Int = -1,
    val numberOfBathrooms: Int = -1,
    val numberOfBeds: Int = -1,
    val maxGuests: Int = -1,
    val pricePerNight: Double = 0.0,
    val currency: String = "",
    val country: String? = null,
    val cityId: Long? = null,
    val street: String? = null,
    val postalCode: String? = null,
    val description: String? = null,
)
