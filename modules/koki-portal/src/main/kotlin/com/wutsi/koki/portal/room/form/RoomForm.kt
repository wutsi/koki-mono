package com.wutsi.koki.portal.room.form

import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseTerm
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomType

data class RoomForm(
    val accountId: Long = -1,
    val type: RoomType = RoomType.UNKNOWN,
    val title: String? = null,
    val description: String? = null,
    val summary: String? = null,
    val numberOfRooms: Int = -1,
    val numberOfBathrooms: Int = -1,
    val numberOfBeds: Int = -1,
    val maxGuests: Int = -1,
    val pricePerNight: Double? = null,
    val pricePerMonth: Double? = null,
    val currency: String? = null,
    val country: String? = null,
    val cityId: Long? = null,
    val neighborhoodId: Long? = null,
    val street: String? = null,
    val postalCode: String? = null,
    val checkinTime: String? = null,
    val checkoutTime: String? = null,
    val furnishedType: FurnishedType = FurnishedType.UNKNOWN,
    val leaseType: LeaseType = LeaseType.UNKNOWN,
    val categoryId: Long? = null,
    val area: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    var leaseTerm: LeaseTerm = LeaseTerm.UNKNOWN,
    val leaseTermDuration: Int? = null,
    val advanceRent: Int? = null,
    val visitFees: Double? = null,
    val yearOfConstruction: Int? = null,
    val dateOfAvailability: String? = null,

)
