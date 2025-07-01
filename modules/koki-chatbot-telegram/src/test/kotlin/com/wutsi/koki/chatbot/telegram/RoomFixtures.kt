package com.wutsi.koki.chatbot.telegram

import com.wutsi.koki.refdata.dto.Money
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomLocationMetric
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.dto.RoomType

object RoomFixtures {
    val rooms = listOf(
        RoomSummary(
            id = 1111,
            status = RoomStatus.PUBLISHED,
            type = RoomType.ROOM,
            title = "Classic Double Room",
            summary = "Nice double room",
            heroImageId = FileFixtures.images[0].id,
            maxGuests = 2,
            numberOfBeds = 1,
            numberOfBathrooms = 1,
            numberOfRooms = 1,
            leaseType = LeaseType.LONG_TERM,
            pricePerMonth = Money(amount = 375.0, currency = "CAD"),
            listingUrl = "/rooms/1111/classic-double-room",
        ),
        RoomSummary(
            id = 1112,
            status = RoomStatus.PUBLISHED,
            type = RoomType.ROOM,
            title = "Comfort Single Room",
            summary = "Comfortable room with stunning view",
            heroImageId = FileFixtures.images[1].id,
            maxGuests = 3,
            numberOfBeds = 2,
            numberOfBathrooms = 1,
            numberOfRooms = 1,
            leaseType = LeaseType.LONG_TERM,
            pricePerNight = Money(amount = 100.0, currency = "CAD"),
            pricePerMonth = Money(amount = 1500.0, currency = "CAD"),
            listingUrl = "/rooms/1112/classic-single-room",
            latitude = 45.554082069423316,
            longitude = -73.66554873383372
        ),
        RoomSummary(
            id = 1113,
            status = RoomStatus.PUBLISHED,
            type = RoomType.ROOM,
            title = "Deluxe Double Room",
            heroImageId = FileFixtures.images[1].id,
            maxGuests = 3,
            numberOfBeds = 2,
            numberOfBathrooms = 1,
            numberOfRooms = 1,
            leaseType = LeaseType.SHORT_TERM,
            pricePerNight = Money(amount = 1850.0, currency = "CAD"),
            listingUrl = "/rooms/1113/deluxe-double-room",
            latitude = 45.55293917724604,
            longitude = -73.67104881166603
        ),
    )
    val metrics = RefDataFixtures.locations.reversed().map { location ->
        RoomLocationMetric(
            locationId = location.id,
            totalPublishedRentals = (100 * Math.random()).toInt()
        )
    }
}
