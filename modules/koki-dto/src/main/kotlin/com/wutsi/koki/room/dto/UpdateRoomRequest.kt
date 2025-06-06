package com.wutsi.koki.room.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.util.Date

data class UpdateRoomRequest(
    val type: RoomType = RoomType.UNKNOWN,
    @get:Size(max = 100) val title: String? = null,
    val description: String? = null,
    @get:Size(max = 255) val summary: String? = null,
    val numberOfRooms: Int = -1,
    val numberOfBathrooms: Int = -1,
    val numberOfBeds: Int = -1,
    val maxGuests: Int = -1,
    val area: Int = -1,

    val cityId: Long? = null,
    @get:Size(max = 30) val postalCode: String? = null,
    val street: String? = null,
    val neighborhoodId: Long? = null,

    val pricePerNight: Double? = null,
    val pricePerMonth: Double? = null,
    @get:NotEmpty @get:Size(max = 3) val currency: String? = null,

    @get:Size(max = 5) val checkinTime: String? = null,
    @get:Size(max = 5) val checkoutTime: String? = null,

    val furnishedType: FurnishedType = FurnishedType.UNKNOWN,
    val leaseType: LeaseType = LeaseType.UNKNOWN,
    val leaseTerm: LeaseTerm = LeaseTerm.UNKNOWN,
    val leaseTermDuration: Int? = null,
    val advanceRent: Int? = null,
    val visitFees: Double? = null,
    val yearOfConstruction: Int? = null,
    val dateOfAvailability: Date? = null,
    val categoryId: Long? = null,
)
