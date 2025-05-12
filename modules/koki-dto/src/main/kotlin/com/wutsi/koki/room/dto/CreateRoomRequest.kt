package com.wutsi.koki.room.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreateRoomRequest(
    val type: RoomType = RoomType.UNKNOWN,
    @get:NotEmpty @get:Size(max = 100) val title: String = "",
    val description: String? = null,
    val numberOfRooms: Int = -1,
    val numberOfBathrooms: Int = -1,
    val numberOfBeds: Int = -1,
    val maxGuests: Int = -1,

    val cityId: Long? = null,
    @get:Size(max = 30) val postalCode: String? = null,
    val street: String? = null,
    val neighborhoodId: Long? = null,

    val pricePerNight: Double = 0.0,
    @get:NotEmpty @get:Size(max = 3) val currency: String = "",

    @get:Size(max = 5) val checkinTime: String? = null,
    @get:Size(max = 5) val checkoutTime: String? = null,
)
