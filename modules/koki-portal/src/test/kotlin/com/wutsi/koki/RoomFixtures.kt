package com.wutsi.koki

import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Money
import com.wutsi.koki.room.dto.Room
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.room.dto.RoomType

object RoomFixtures {
    val rooms = listOf(
        RoomSummary(
            id = 1111,
            status = RoomStatus.PUBLISHED,
            type = RoomType.HOTEL,
            title = "Classic Double Room",
            maxGuests = 2,
            numberOfBeds = 1,
            numberOfBathrooms = 1,
            numberOfRooms = 1,
            pricePerNight = Money(amount = 75.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[0].id,
                country = RefDataFixtures.cities[0].country,
            )
        ),
        RoomSummary(
            id = 1112,
            status = RoomStatus.PUBLISHED,
            type = RoomType.HOTEL,
            title = "Comfort Double Room",
            maxGuests = 3,
            numberOfBeds = 2,
            numberOfBathrooms = 1,
            numberOfRooms = 1,
            pricePerNight = Money(amount = 100.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[0].id,
                country = RefDataFixtures.cities[0].country,
            )
        ),
        RoomSummary(
            id = 1113,
            status = RoomStatus.PUBLISHED,
            type = RoomType.HOTEL,
            title = "Deluxe Double Room",
            maxGuests = 3,
            numberOfBeds = 2,
            numberOfBathrooms = 1,
            numberOfRooms = 1,
            pricePerNight = Money(amount = 150.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[0].id,
                country = RefDataFixtures.cities[0].country,
            )
        ),
        RoomSummary(
            id = 1114,
            status = RoomStatus.UNAVAILABLE,
            type = RoomType.HOTEL,
            title = "Standard Single Room, 1 Queen Bed",
            maxGuests = 2,
            numberOfBeds = 2,
            numberOfBathrooms = 1,
            numberOfRooms = 1,
            pricePerNight = Money(amount = 100.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[1].id,
                country = RefDataFixtures.cities[1].country,
            )
        ),
        RoomSummary(
            id = 1115,
            status = RoomStatus.UNDER_REVIEW,
            type = RoomType.HOTEL,
            title = "Presidential Suite",
            maxGuests = 6,
            numberOfBeds = 3,
            numberOfBathrooms = 3,
            numberOfRooms = 3,
            pricePerNight = Money(amount = 500.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[1].id,
                country = RefDataFixtures.cities[1].country,
            )
        ),
        RoomSummary(
            id = 1116,
            status = RoomStatus.PUBLISHED,
            type = RoomType.APARTMENT,
            title = "Windmill in Ponta Delgada",
            maxGuests = 6,
            numberOfBeds = 3,
            numberOfBathrooms = 3,
            numberOfRooms = 3,
            pricePerNight = Money(amount = 350.0, currency = "CAD"),
            address = Address(
                cityId = RefDataFixtures.cities[1].id,
                country = RefDataFixtures.cities[1].country,
                stateId = RefDataFixtures.cities[1].parentId,
                street = "3030 Linton",
            ),
        ),
    )

    val room = Room(
        id = 1115,
        status = RoomStatus.PUBLISHED,
        type = RoomType.APARTMENT,
        title = "Windmill in Ponta Delgada",
        description = """
            Built in the 19th century, with a 360 degrees view over the sea and surroundings on the top floor.
            It features a Bedroom, a very well-decorated living room with kitchenette, and a WC.
            Free WiFi, air conditioning, Led TV and DVD player.
            Private parking inside the premises, providing extra security.
            Perfect for an unforgettable honeymoon experience.
        """.trimIndent(),
        maxGuests = 6,
        numberOfBeds = 3,
        numberOfBathrooms = 3,
        numberOfRooms = 3,
        pricePerNight = Money(amount = 350.0, currency = "CAD"),
        address = Address(
            cityId = RefDataFixtures.cities[1].id,
            country = RefDataFixtures.cities[1].country,
            stateId = RefDataFixtures.cities[1].parentId,
            street = "3030 Linton",
        ),
        createdById = UserFixtures.USER_ID,
        modifiedById = UserFixtures.USER_ID,
    )
}
