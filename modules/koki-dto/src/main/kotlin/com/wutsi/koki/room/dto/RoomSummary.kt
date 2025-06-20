package com.wutsi.koki.room.dto

import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Money
import java.util.Date

data class RoomSummary(
    val id: Long = -1,
    val accountId: Long = -1,
    val heroImageId: Long? = null,
    val type: RoomType = RoomType.UNKNOWN,
    val status: RoomStatus = RoomStatus.UNKNOWN,
    val title: String? = null,
    val summary: String? = null,
    var titleFr: String? = null,
    var summaryFr: String? = null,
    val numberOfRooms: Int = -1,
    val numberOfBathrooms: Int = -1,
    val numberOfBeds: Int = -1,
    val maxGuests: Int = -1,
    val area: Int = -1,
    val pricePerNight: Money? = null,
    val pricePerMonth: Money? = null,
    val neighborhoodId: Long? = null,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val furnishedType: FurnishedType = FurnishedType.UNKNOWN,
    val leaseTerm: LeaseTerm = LeaseTerm.UNKNOWN,
    val leaseType: LeaseType = LeaseType.UNKNOWN,
    val address: Address? = null,
    val listingUrl: String? = null,
    val publishedAt: Date? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
